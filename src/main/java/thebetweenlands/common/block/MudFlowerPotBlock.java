package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.entity.MudFlowerPotBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

public class MudFlowerPotBlock extends BaseEntityBlock {

	protected static final VoxelShape SHAPE = Block.box(5.5D, 0.0D, 5.5D, 11.5D, 7.0D, 11.5D);

	public MudFlowerPotBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
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
}
