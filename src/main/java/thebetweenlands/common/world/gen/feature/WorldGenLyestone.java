package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenLyestone  extends WorldGenerator {
	public WorldGenLyestone() {
		super(false);
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() == BlockRegistry.LIMESTONE && world.isAirBlock(pos.up())) {
			boolean genOre = rand.nextInt(1 + BetweenlandsConfig.WORLD_AND_DIMENSION.oreGenBlockChanceLyestone) == 0;
			if (genOre) {
				for (int xx = -2; xx <= 2; xx++) {
					for (int zz = -2; zz <= 2; zz++) {
						for (int yy = -1; yy <= 1; yy++) {
							double dst = Math.sqrt(xx * xx + yy * yy + zz * zz);
							if (rand.nextInt(MathHelper.ceil(dst / 1.4D) + 1) == 0) {
								BlockPos offsetPos = pos.add(xx, yy, zz);
								if (world.isAreaLoaded(pos, 2) && world.isAirBlock(offsetPos.up())) {
									IBlockState surfaceBlock = world.getBlockState(offsetPos);
									if (surfaceBlock.getBlock() == BlockRegistry.LIMESTONE) 
										this.setBlockAndNotifyAdequately(world, offsetPos, BlockRegistry.LYESTONE.getDefaultState());
									
								}
							}
						}
					}
				}
				return true;	
			}
		}
		return false;
	}
}
