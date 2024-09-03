package thebetweenlands.common.block.waterlog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SwampTrapdoorBlock extends TrapDoorBlock implements SwampWaterLoggable {
	public SwampTrapdoorBlock(BlockSetType type, Properties properties) {
		super(type, properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HALF, Half.BOTTOM).setValue(POWERED, false).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	public void toggle(BlockState state, Level level, BlockPos pos, @Nullable Player player) {
		BlockState blockstate = state.cycle(OPEN);
		level.setBlock(pos, blockstate, 2);
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		this.playSound(player, level, pos, blockstate.getValue(OPEN));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(currentPos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return state;
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!level.isClientSide()) {
			boolean flag = level.hasNeighborSignal(pos);
			if (flag != state.getValue(POWERED)) {
				if (state.getValue(OPEN) != flag) {
					state = state.setValue(OPEN, flag);
					this.playSound(null, level, pos, flag);
				}

				level.setBlock(pos, state.setValue(POWERED, flag), 2);
				if (state.getValue(WATER_TYPE) != WaterType.NONE) {
					level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
				}
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = this.defaultBlockState();
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		Direction direction = context.getClickedFace();
		if (!context.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
			blockstate = blockstate.setValue(FACING, direction)
				.setValue(HALF, context.getClickLocation().y - (double)context.getClickedPos().getY() > 0.5 ? Half.TOP : Half.BOTTOM);
		} else {
			blockstate = blockstate.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
		}

		if (context.getLevel().hasNeighborSignal(context.getClickedPos())) {
			blockstate = blockstate.setValue(OPEN, true).setValue(POWERED, true);
		}

		return blockstate.setValue(WATER_TYPE, WaterType.getFromFluid(fluidstate.getType()));
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, HALF, POWERED, WATER_TYPE);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return switch (pathComputationType) {
			case LAND, AIR -> state.getValue(OPEN);
			case WATER -> state.getValue(WATER_TYPE) != WaterType.NONE;
		};
	}

	@Override
	public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return SwampWaterLoggable.super.canPlaceLiquid(player, level, pos, state, fluid);
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		return SwampWaterLoggable.super.placeLiquid(level, pos, state, fluidState);
	}

	@Override
	public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
		return SwampWaterLoggable.super.pickupBlock(player, level, pos, state);
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return SwampWaterLoggable.super.getPickupSound();
	}
}
