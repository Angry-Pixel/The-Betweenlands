package thebetweenlands.common.block.container;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.entity.MudFlowerPotBlockEntity;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

public class MudFlowerPotBlock extends BaseEntityBlock implements SwampWaterLoggable {

	protected static final VoxelShape SHAPE = Shapes.or(
		Block.box(6.0D, 0.0D, 6.0D, 10.0D, 2.0D, 10.0D),
		Block.box(6.5D, 2.0D, 6.5D, 9.5D, 3.0D, 9.5D),
		Block.box(5.5D, 3.0D, 5.5D, 10.5D, 7.0D, 10.5D));

	public MudFlowerPotBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		ItemInteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
		if (result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
			return result;
		}

		if (level.getBlockEntity(pos) instanceof MudFlowerPotBlockEntity be) {
			if (!be.hasFlowerBlock() && stack.is(BlockRegistry.SULFUR_TORCH.asItem())) {
				level.setBlockAndUpdate(pos, BlockRegistry.MUD_FLOWER_POT_CANDLE.get().defaultBlockState());
				level.playSound(null, pos, SoundType.WOOD.getPlaceSound(), SoundSource.BLOCKS, (SoundType.WOOD.getVolume() + 1.0F) / 2.0F, SoundType.WOOD.getPitch() * 0.8F);
				stack.consume(1, player);
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			} else {
				boolean isFlower = stack.getItem() instanceof BlockItem item && !getFlowerPotState(item.getBlock()).isAir();

				if (isFlower != be.hasFlowerBlock()) {
					if (!level.isClientSide()) {
						if (isFlower && !be.hasFlowerBlock()) {
							be.setFlowerBlock(((BlockItem) stack.getItem()).getBlock());
							player.awardStat(Stats.POT_FLOWER);
							stack.consume(1, player);
						} else {
							ItemStack flowerStack = new ItemStack(be.getFlowerBlock());
							if (stack.isEmpty()) {
								player.setItemInHand(hand, flowerStack);
							} else if (!player.addItem(flowerStack)) {
								player.drop(flowerStack, false);
							}

							be.setFlowerBlock(Blocks.AIR);
						}
					}

					level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
					return ItemInteractionResult.sidedSuccess(level.isClientSide());
				} else {
					return ItemInteractionResult.CONSUME;
				}
			}
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}


	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		return level.getBlockEntity(pos) instanceof MudFlowerPotBlockEntity pot && pot.hasFlowerBlock() ? new ItemStack(pot.getFlowerBlock()) : super.getCloneItemStack(level, pos, state);
	}

	public static BlockState getFlowerPotState(Block flower) {
		Map<ResourceLocation, Supplier<? extends Block>> fullPots = ((FlowerPotBlock) Blocks.FLOWER_POT).getFullPotsView();
		return fullPots.getOrDefault(BuiltInRegistries.BLOCK.getKey(flower), () -> Blocks.AIR).get().defaultBlockState();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MudFlowerPotBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATER_TYPE);
	}
}
