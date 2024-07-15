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
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.CommonHooks;
import thebetweenlands.common.registries.FluidRegistry;

public class SwampReedBlock extends PlantBlock implements SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public SwampReedBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.isEmptyBlock(pos.above()) || level.getFluidState(pos.above()).is(FluidRegistry.SWAMP_WATER_STILL.get())) {
			int i = 1;

			while (level.getBlockState(pos.below(i)).is(this)) {
				i++;
			}

			if (i < 4) {
				if (CommonHooks.canCropGrow(level, pos, state, true)) {
					CommonHooks.fireCropGrowPost(level, pos.above(), this.defaultBlockState());
					level.setBlock(pos, state.setValue(WATERLOGGED, level.getFluidState(pos.above()).is(FluidRegistry.SWAMP_WATER_STILL.get())), 4);
				}
			}
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
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
		return state.getValue(WATERLOGGED) ? FluidRegistry.SWAMP_WATER_STILL.get().getSource(false) : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}
}
