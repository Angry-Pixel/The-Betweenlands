package thebetweenlands.world.feature.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;

public class WorldGenMinableBetweenlands extends WorldGenerator {
	private Block toGen;
	private int numberOfBlocks;
	private Block toReplace;
	private int mineableBlockMeta;
	private boolean findBlockToReplace = false;

	public WorldGenMinableBetweenlands prepare(Block block, int meta, int numberOfBlocks, Block blockToReplace, boolean findBlockToReplace) {
		this.toGen = block;
		this.numberOfBlocks = numberOfBlocks;
		this.toReplace = blockToReplace;
		return this;
	}

	public WorldGenMinableBetweenlands prepare(Block blockToReplace) {
		this.toReplace = blockToReplace;
		return this;
	}

	public boolean isFindingBlock() {
		return this.findBlockToReplace;
	}

	public boolean generate(World world, Random rand, int x, int y, int z) {
		if(this.isFindingBlock()) {
			BiomeGenBaseBetweenlands biome = (BiomeGenBaseBetweenlands) world.getBiomeGenForCoords(x, z);
			this.prepare(biome.getBaseBlock(y));
		}

		float f = rand.nextFloat() * (float)Math.PI;
		double d0 = x + 8 + MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
		double d1 = x + 8 - MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
		double d2 = z + 8 + MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
		double d3 = z + 8 - MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
		double d4 = y + rand.nextInt(3) - 2;
		double d5 = y + rand.nextInt(3) - 2;

		for (int l = 0; l <= this.numberOfBlocks; ++l) {
			double d6 = d0 + (d1 - d0) * l / this.numberOfBlocks;
			double d7 = d4 + (d5 - d4) * l / this.numberOfBlocks;
			double d8 = d2 + (d3 - d2) * l / this.numberOfBlocks;
			double d9 = rand.nextDouble() * this.numberOfBlocks / 16.0D;
			double d10 = (MathHelper.sin(l * (float)Math.PI / this.numberOfBlocks) + 1.0F) * d9 + 1.0D;
			double d11 = (MathHelper.sin(l * (float)Math.PI / this.numberOfBlocks) + 1.0F) * d9 + 1.0D;
			int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
			int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
			int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
			int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
			int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
			int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

			for (int k2 = i1; k2 <= l1; ++k2) {
				double d12 = (k2 + 0.5D - d6) / (d10 / 2.0D);

				if (d12 * d12 < 1.0D) {
					for (int l2 = j1; l2 <= i2; ++l2) {
						double d13 = (l2 + 0.5D - d7) / (d11 / 2.0D);

						if (d12 * d12 + d13 * d13 < 1.0D) {
							for (int i3 = k1; i3 <= j2; ++i3) {
								double d14 = (i3 + 0.5D - d8) / (d10 / 2.0D);

								if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && world.getBlock(k2, l2, i3).isReplaceableOreGen(world, k2, l2, i3, this.toReplace) && this.canGenerate(world, k2, l2, i3)) {
									world.setBlock(k2, l2, i3, this.toGen, this.mineableBlockMeta, 2);
								}
							}
						}
					}
				}
			}
		}

		return true;
	}
	
	protected boolean canGenerate(World world, int x, int y, int z) {
		return true;
	}
}