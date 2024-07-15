package thebetweenlands.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface FarmablePlant {
	/**
	 * Returns whether the plant is farmable
	 * @param level
	 * @param pos
	 * @param state
	 * @return
	 */
	boolean isFarmable(Level level, BlockPos pos, BlockState state);

	/**
	 * Returns whether this plant can spread to the target position
	 * @param level
	 * @param pos
	 * @param state
	 * @param targetPos
	 * @param random
	 * @return
	 */
	boolean canSpreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random);

	/**
	 * Returns the spreading chance between 0 and 1
	 * @param level
	 * @param pos
	 * @param state
	 * @param targetPos
	 * @param random
	 * @return
	 */
	default float getSpreadChance(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return 1;
	}

	/**
	 * Returns how much compost is consumed when this plant spreads
	 * @param level
	 * @param pos
	 * @param state
	 * @param random
	 * @return
	 */
	int getCompostCost(Level level, BlockPos pos, BlockState state, RandomSource random);

	/**
	 * Called when the plant is decaying
	 * @param level
	 * @param pos
	 * @param state
	 * @param random
	 */
	void decayPlant(Level level, BlockPos pos, BlockState state, RandomSource random);

	/**
	 * Spreads this plant to the specified target position
	 * @param level
	 * @param pos
	 * @param state
	 * @param targetPos
	 * @param random
	 */
	void spreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random);

	/**
	 * Returns whether this plant can be destroyed by rain
	 * @param level
	 * @param pos
	 * @param state
	 * @return
	 */
	default boolean canBeDestroyedByPuddles(LevelReader level, BlockPos pos, BlockState state) {
		return false;
	}
}
