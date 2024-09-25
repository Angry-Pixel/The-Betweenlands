package thebetweenlands.common.block.waterlog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import javax.annotation.Nullable;

import java.util.Optional;

public class SwampSlabBlock extends SlabBlock implements SwampWaterLoggable {
	public SwampSlabBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(TYPE, SlabType.BOTTOM).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		BlockState blockstate = context.getLevel().getBlockState(blockpos);
		if (blockstate.is(this)) {
			return blockstate.setValue(TYPE, SlabType.DOUBLE).setValue(WATER_TYPE, WaterType.NONE);
		} else {
			FluidState fluidstate = context.getLevel().getFluidState(blockpos);
			BlockState blockstate1 = this.defaultBlockState()
				.setValue(TYPE, SlabType.BOTTOM)
				.setValue(WATER_TYPE, WaterType.getFromFluid(fluidstate.getType()));
			Direction direction = context.getClickedFace();
			return direction != Direction.DOWN && (direction == Direction.UP || !(context.getClickLocation().y - (double) blockpos.getY() > 0.5))
				? blockstate1
				: blockstate1.setValue(TYPE, SlabType.TOP);
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(currentPos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return state;
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(TYPE, WATER_TYPE);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		if (type == PathComputationType.WATER) {
			return !state.getFluidState().isEmpty();
		}
		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		return state.getValue(TYPE) != SlabType.DOUBLE && SwampWaterLoggable.super.placeLiquid(level, pos, state, fluidState);
	}

	@Override
	public boolean canPlaceLiquid(@javax.annotation.Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getValue(TYPE) != SlabType.DOUBLE && SwampWaterLoggable.super.canPlaceLiquid(player, level, pos, state, fluid);
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
