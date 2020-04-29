package thebetweenlands.common.world.gen.feature.structure;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenChiromawNest extends WorldGenerator {

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		generateRockPile(world, rand, pos);
		generateNest(world, rand, pos.up(6));

		return false;
	}

	public void generateNest(World world, Random rand, BlockPos pos) {
		for (int xx = -3; xx <= 3; xx++) {
			for (int zz = -3; zz <= 3; zz++) {
				for (int yy = 0; yy > -3; yy--) {
					double dSqDome = Math.pow(xx, 2.0D) + Math.pow(zz, 2.0D) + Math.pow(yy, 2.0D);

					if (Math.round(Math.sqrt(dSqDome)) < 4) {
						setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), rand.nextInt(4) == 0 ? BlockRegistry.NESTING_BLOCK_BONES.getDefaultState() : BlockRegistry.NESTING_BLOCK_STICKS.getDefaultState());

						if (yy == 0 && Math.round(Math.sqrt(dSqDome)) == 1)
							setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), Blocks.AIR.getDefaultState());
					}

					setBlockAndNotifyAdequately(world, pos.add(0, yy, 0), BlockRegistry.BETWEENSTONE.getDefaultState());
				}
			}
		}
	}

	public void generateRockPile(World world, Random rand, BlockPos pos) {
		for (int xx = -5; xx <= 5; xx++) {
			for (int zz = -5; zz <= 5; zz++) {
				for (int yy = 0; yy < 4; yy++) {
					double dSqDome = Math.pow(xx, 2.0D) + Math.pow(zz, 2.0D) + Math.pow(yy, 2.0D);

					if (yy == 0 && rand.nextBoolean() && Math.round(Math.sqrt(dSqDome)) == 5)
						setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), BlockRegistry.BETWEENSTONE.getDefaultState());

					if (Math.round(Math.sqrt(dSqDome)) < 5)
						setBlockAndNotifyAdequately(world, pos.add(xx, yy, zz), BlockRegistry.BETWEENSTONE.getDefaultState());
				}
			}
		}
	}

}