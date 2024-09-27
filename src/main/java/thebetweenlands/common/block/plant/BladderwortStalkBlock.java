package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.common.CommonHooks;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

public class BladderwortStalkBlock extends UnderwaterPlantBlock {

	public static final IntegerProperty AGE = BlockStateProperties.AGE_15;

	public BladderwortStalkBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(AGE, 0));
	}

	@Override
	public boolean isFarmable(Level level, BlockPos pos, BlockState state) {
		return false;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos blockpos = pos.relative(Direction.DOWN);
		BlockState blockstate = level.getBlockState(blockpos);
		return blockstate.is(this) || blockstate.isFaceSturdy(level, blockpos, Direction.UP);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.canSurvive(level, pos) ? state : BlockRegistry.SWAMP_WATER.get().defaultBlockState();
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.randomTick(state, level, pos, random);

		if (CommonHooks.canCropGrow(level, pos, state, random.nextFloat() <= 0.5F)) {
			int currentAge = state.getValue(AGE);

			if (currentAge >= 15) {
				int height;
				for (height = 1; level.getBlockState(pos.below(height)).is(this); ++height) ;

				if (this.canGrowUp(level, pos)) {
					this.growUp(level, pos);
				}

				level.setBlockAndUpdate(pos, state.setValue(AGE, 0));
			} else {
				level.setBlockAndUpdate(pos, state.setValue(AGE, currentAge + 1));
			}

			CommonHooks.fireCropGrowPost(level, pos, state);
		}
	}

	protected boolean canGrowUp(Level level, BlockPos pos) {
		return level.getBlockState(pos.above()).getBlock() != this &&
			(level.getBlockState(pos.above()).is(BlockRegistry.SWAMP_WATER) || (level.getFluidState(pos).is(FluidRegistry.SWAMP_WATER_STILL.get()) && level.isEmptyBlock(pos.above())));
	}

	protected void growUp(Level level, BlockPos pos) {
		if (!level.getBlockState(pos.above()).liquid()) {
			level.setBlockAndUpdate(pos.above(), BlockRegistry.BLADDERWORT_FLOWER.get().defaultBlockState());
		} else {
			level.setBlockAndUpdate(pos.above(), this.defaultBlockState());
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(AGE));
	}
}
