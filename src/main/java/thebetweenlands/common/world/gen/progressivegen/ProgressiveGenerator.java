package thebetweenlands.common.world.gen.progressivegen;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ProgressiveGenerator {
	/**
	 * Generates the structure bulk.
	 * <p><b>Do not use {@link World#rand}</b>
	 * @param world
	 * @param rand
	 * @param pos
	 */
	public void generateBulk(World world, Random rand, BlockPos pos);

	/**
	 * Called after the whole structure bulk has been generated
	 * @param world
	 * @param rand
	 * @param pos
	 */
	public default void post(World world, Random rand, BlockPos pos) {

	}
}
