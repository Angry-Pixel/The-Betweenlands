package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.SimulacrumEffect;
import thebetweenlands.common.block.entity.simulacrum.SimulacrumBlockEntity;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.SimulacrumEffectRegistry;

import java.util.List;

public class SimulacrumBlock extends HorizontalBaseEntityBlock implements SwampWaterLoggable {

	public static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public SimulacrumBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof SimulacrumBlockEntity simulacrum) {
			if (stack.has(DataComponentRegistry.SIMULACRUM_EFFECT)) {
				simulacrum.setEffect(BLRegistries.SIMULACRUM_EFFECTS.get(stack.get(DataComponentRegistry.SIMULACRUM_EFFECT)));
			}
			simulacrum.setActive(level, pos, state, true);
			if (stack.has(DataComponents.CUSTOM_NAME)) {
				simulacrum.setCustomName(stack.getDisplayName());
			}
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (player.isCreative() && player.isCrouching()) {

			if (!level.isClientSide()) {
				if (level.getBlockEntity(pos) instanceof SimulacrumBlockEntity simulacrum) {
					SimulacrumEffect nextEffect = BLRegistries.SIMULACRUM_EFFECTS.byId(BLRegistries.SIMULACRUM_EFFECTS.getId(simulacrum.getEffect()) + 1 % BLRegistries.SIMULACRUM_EFFECTS.size());

					simulacrum.setEffect(nextEffect);
					player.displayClientMessage(Component.translatable("chat.simulacrum.changed_effect", Component.translatable(nextEffect.getDescriptionId())), true);
				}
			}

			return InteractionResult.sidedSuccess(level.isClientSide());
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(WATER_TYPE, SwampWaterLoggable.WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != SwampWaterLoggable.WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SimulacrumBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.SIMULACRUM.get(), SimulacrumBlockEntity::tick);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		ItemStack stack = new ItemStack(this);

		if (level.getBlockEntity(pos) instanceof SimulacrumBlockEntity simulacrum) {
			stack.set(DataComponentRegistry.SIMULACRUM_EFFECT, BLRegistries.SIMULACRUM_EFFECTS.getKey(simulacrum.getEffect()));
		}

		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (flag.isCreative()) {
			tooltip.add(Component.translatable("block.thebetweenlands.simulacrum.effect", Component.translatable(BLRegistries.SIMULACRUM_EFFECTS.get(stack.getOrDefault(DataComponentRegistry.SIMULACRUM_EFFECT, BLRegistries.SIMULACRUM_EFFECTS.getKey(SimulacrumEffectRegistry.NONE.get()))).getDescriptionId())));
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(WATER_TYPE));
	}
}
