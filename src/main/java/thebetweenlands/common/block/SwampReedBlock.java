package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.CommonHooks;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;

public class SwampReedBlock extends PlantBlock implements SwampWaterLoggable {

	public SwampReedBlock(Properties properties) {
		super(PlantBlock.GRASS_SHAPE, false, properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.isEmptyBlock(pos.above()) || !level.getFluidState(pos.above()).is(Fluids.EMPTY)) {
			int i = 1;

			while (level.getBlockState(pos.below(i)).is(this)) {
				i++;
			}

			if (i < 4) {
				if (CommonHooks.canCropGrow(level, pos, state, true)) {
					CommonHooks.fireCropGrowPost(level, pos.above(), this.defaultBlockState());
					level.setBlock(pos, state.setValue(WATER_TYPE, WaterType.getFromFluid(level.getFluidState(pos.above()).getType())), 4);
				}
			}
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(currentPos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}
		return state.canSurvive(level, currentPos) ? super.updateShape(state, facing, facingState, level, currentPos, facingPos) : Blocks.AIR.defaultBlockState();
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState blockstate = level.getBlockState(pos.below());
		if (blockstate.is(this)) {
			return true;
		} else {
			net.neoforged.neoforge.common.util.TriState soilDecision = blockstate.canSustainPlant(level, pos.below(), Direction.UP, state);
			if (!soilDecision.isDefault()) return soilDecision.isTrue();
			if (blockstate.is(BlockTags.DIRT) || blockstate.is(BlockTags.SAND)) {
				BlockPos blockpos = pos.below();

				for (Direction direction : Direction.Plane.HORIZONTAL) {
					BlockState blockstate1 = level.getBlockState(blockpos.relative(direction));
					FluidState fluidstate = level.getFluidState(blockpos.relative(direction));
					if (state.canBeHydrated(level, pos, fluidstate, blockpos.relative(direction)) || blockstate1.is(Blocks.FROSTED_ICE)) {
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATER_TYPE);
	}
}
