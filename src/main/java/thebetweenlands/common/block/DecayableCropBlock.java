package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.TriState;
import thebetweenlands.api.block.FarmablePlant;
import thebetweenlands.common.block.entity.DugSoilBlockEntity;

import javax.annotation.Nullable;

public abstract class DecayableCropBlock extends CropBlock implements FarmablePlant {

	public static final BooleanProperty DECAYED = BooleanProperty.create("decayed");
	public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 15);

	public DecayableCropBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(DECAYED, false).setValue(AGE, 0));
	}

	public abstract int getMaxHeight();

	@Override
	protected IntegerProperty getAgeProperty() {
		return STAGE;
	}

	@Override
	public int getMaxAge() {
		return 3;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos blockpos = pos.below();
		BlockState belowBlockState = level.getBlockState(blockpos);
		TriState soilDecision = level.getBlockState(pos.below()).canSustainPlant(level, pos.below(), Direction.UP, state);
		if (!soilDecision.isDefault()) return soilDecision.isTrue();
		return this.mayPlaceOn(belowBlockState, level, blockpos);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return super.mayPlaceOn(state, level, pos) &&
			!(state.getBlock() instanceof DugSoilBlock && state.getValue(DugSoilBlock.DECAYED)) &&
			!(state.getBlock() instanceof DecayableCropBlock && state.getValue(AGE) < 15);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(DECAYED, this.isDecayed(context.getLevel(), context.getClickedPos()));
	}

	/**
	 * Returns whether the soil is decayed
	 *
	 * @param level
	 * @param pos
	 * @return
	 */
	public boolean isDecayed(LevelReader level, BlockPos pos) {
		for (int i = 0; i < this.getMaxHeight() + 1; i++) {
			BlockState blockState = level.getBlockState(pos.below(i));
			if (blockState.getBlock() instanceof DugSoilBlock) {
				return blockState.getValue(DugSoilBlock.DECAYED);
			}
		}
		return false;
	}

	/**
	 * Returns whether the soil is composted
	 *
	 * @param level
	 * @param pos
	 * @return
	 */
	public boolean isComposted(LevelReader level, BlockPos pos) {
		for (int i = 0; i < this.getMaxHeight() + 1; i++) {
			BlockState blockState = level.getBlockState(pos.below(i));
			if (blockState.getBlock() instanceof DugSoilBlock) {
				return blockState.getValue(DugSoilBlock.COMPOSTED);
			}
		}
		return false;
	}

	/**
	 * Returns whether the soil is fogged
	 *
	 * @param level
	 * @param pos
	 * @return
	 */
	public boolean isFogged(LevelReader level, BlockPos pos) {
		for (int i = 0; i < this.getMaxHeight() + 1; i++) {
			BlockState blockState = level.getBlockState(pos.below(i));
			if (blockState.getBlock() instanceof DugSoilBlock) {
				return blockState.getValue(DugSoilBlock.FOGGED);
			}
		}
		return false;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		boolean removed = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
		if (removed && this.getAge(state) >= this.getMaxAge()) {
			this.harvestAndUpdateSoil(level, pos, 10);
		}
		return removed;
	}

	/**
	 * Called when the crop is harvested. Updates the soil and e.g. consumes compost if the block below is dug soil
	 *
	 * @param level
	 * @param pos
	 * @param compost
	 */
	protected void harvestAndUpdateSoil(Level level, BlockPos pos, int compost) {
		BlockState stateDown = level.getBlockState(pos.below());
		if (stateDown.getBlock() instanceof DugSoilBlock soil) {
			if (level.getBlockEntity(pos.below()) instanceof DugSoilBlockEntity te && te.isComposted()) {
				te.setCompost(level, pos, Math.max(te.getCompost() - compost, 0));
				if (soil.isPurified(level, pos.below(), stateDown)) {
					te.setPurifiedHarvests(level, pos, te.getPurifiedHarvests() + 1);
				}
			}
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isAreaLoaded(pos, 1)) return;
		if (this.canGrow(level, pos, state)) {
			int i = this.getAge(state);
			if (CommonHooks.canCropGrow(level, pos, state, random.nextFloat() <= this.getGrowthChance(level, pos, state, random))) {
				level.setBlock(pos, this.getStateForAge(Math.min(i + this.getGrowthSpeed(level, pos, state, random), this.getMaxAge())), 2);

				if (i >= this.getMaxAge()) {
					int height;
					for (height = 1; level.getBlockState(pos.below(height)).is(this); ++height) ;

					if (this.canGrowUp(level, pos, state, height)) {
						this.growUp(level, pos);
					}

					level.setBlock(pos, this.defaultBlockState(), 2);
				}
				CommonHooks.fireCropGrowPost(level, pos, state);
			}
		}
	}

	/**
	 * Returns by how many steps it should age per growth chance
	 *
	 * @param level
	 * @param pos
	 * @param state
	 * @param random
	 * @return
	 */
	protected int getGrowthSpeed(Level level, BlockPos pos, BlockState state, RandomSource random) {
		return 1 + (this.isFogged(level, pos) ? random.nextInt(3) + 2 : 0);
	}

	/**
	 * Returns the growth chance
	 *
	 * @param level
	 * @param pos
	 * @param state
	 * @param random
	 * @return
	 */
	protected float getGrowthChance(Level level, BlockPos pos, BlockState state, RandomSource random) {
		return this.isFogged(level, pos) ? 1 : 0.5F;
	}

	/**
	 * Returns whether the plant can grow
	 *
	 * @param level
	 * @param pos
	 * @param state
	 * @return
	 */
	protected boolean canGrow(LevelReader level, BlockPos pos, BlockState state) {
		return !state.getValue(DECAYED) && this.isComposted(level, pos);
	}

	/**
	 * Returns whether the plant can grow a block higher
	 *
	 * @param level
	 * @param pos
	 * @param state
	 * @param height
	 * @return
	 */
	protected boolean canGrowUp(LevelReader level, BlockPos pos, BlockState state, int height) {
		return level.isEmptyBlock(pos.above()) && (this.getMaxHeight() == -1 || height < this.getMaxHeight());
	}

	/**
	 * Grows the plant one block higher
	 *
	 * @param level
	 * @param pos   Position of the currently highest block of the plant
	 */
	protected void growUp(Level level, BlockPos pos) {
		level.setBlockAndUpdate(pos.above(), this.defaultBlockState().setValue(DECAYED, level.getBlockState(pos).getValue(DECAYED)));
	}

	@Override
	public boolean isFarmable(Level level, BlockPos pos, BlockState state) {
		return false;
	}

	@Override
	public boolean canBeDestroyedByPuddles(LevelReader level, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean canSpreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return false;
	}

	@Override
	public int getCompostCost(Level level, BlockPos pos, BlockState state, RandomSource random) {
		return 4;
	}

	@Override
	public void decayPlant(Level level, BlockPos pos, BlockState state, RandomSource random) {
		level.setBlockAndUpdate(pos, state.setValue(DECAYED, true));
	}

	@Override
	public void spreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {

	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(AGE, DECAYED));
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
		if (!this.canGrow(level, pos, state)) {
			return false;
		}
		if (state.getValue(AGE) < 15) {
			return true;
		}
		int height;
		for (height = 1; level.getBlockState(pos.below(height)).getBlock() == this; ++height) ;
		return this.canGrowUp(level, pos, state, height);
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
		int age = state.getValue(AGE) + Mth.nextInt(random, 2, 5);
		if (age > 15) {
			age = 15;
			int height;
			for (height = 1; level.getBlockState(pos.below(height)).getBlock() == this; ++height) ;
			if (this.canGrowUp(level, pos, state, height)) {
				this.growUp(level, pos);
			}
		}
		level.setBlockAndUpdate(pos, state.setValue(AGE, age));
	}
}
