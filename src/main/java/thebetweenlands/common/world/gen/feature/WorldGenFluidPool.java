package thebetweenlands.common.world.gen.feature;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenFluidPool extends WorldGenerator {
	private final Block block;
	private final List<Block> blackListedBlocks = ImmutableList.of(
			BlockRegistry.BETWEENSTONE_TILES, 
			BlockRegistry.BETWEENSTONE_BRICK_STAIRS, 
			BlockRegistry.BETWEENSTONE_BRICKS, 
			BlockRegistry.BETWEENSTONE_BRICK_SLAB
			);
	private final double size;
	private int minY = 0;

	public WorldGenFluidPool(Block blockIn, double size) {
		super(true);
		this.block = blockIn;
		this.size = size;
	}

	public WorldGenFluidPool(Block blockIn) {
		this(blockIn, 1.0D);
	}
	
	public WorldGenFluidPool setMinY(int minY) {
		this.minY = minY;
		return this;
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		for (position = position.add(-8, 0, -8); position.getY() > 5 && worldIn.isAirBlock(position); position = position.down()) {
			;
		}

		if (position.getY() <= 4 + this.minY) {
			return false;
		} else {
			position = position.down(4);

			for (int xx = 0; xx < 16; ++xx)
				for (int zz = 0; zz < 16; ++zz)
					for (int yy = 0; yy < 8; ++yy)
						if(this.blackListedBlocks.contains(worldIn.getBlockState(position.add(xx, yy, zz)).getBlock()))
							return false;

			boolean[] isInPool = new boolean[2048];
			int blobs = rand.nextInt(4) + 4;

			for (int blob = 0; blob < blobs; ++blob) {
				double sx = (rand.nextDouble() * 6.0D + 3.0D) * this.size;
				double sy = (rand.nextDouble() * 4.0D + 2.0D) * this.size;
				double sz = (rand.nextDouble() * 6.0D + 3.0D) * this.size;
				double bx = rand.nextDouble() * (16.0D - sx - 2.0D) + 1.0D + sx / 2.0D;
				double by = rand.nextDouble() * (8.0D - sy - 4.0D) + 2.0D + sy / 2.0D;
				double bz = rand.nextDouble() * (16.0D - sz - 2.0D) + 1.0D + sz / 2.0D;

				for (int ox = 1; ox < 15; ++ox) {
					for (int oz = 1; oz < 15; ++oz) {
						for (int oy = 1; oy < 7; ++oy) {
							double dx = ((double)ox - bx) / (sx / 2.0D);
							double dy = ((double)oy - by) / (sy / 2.0D);
							double dz = ((double)oz - bz) / (sz / 2.0D);
							double dst = dx * dx + dy * dy + dz * dz;

							if (dst < 1.0D) {
								isInPool[(ox * 16 + oz) * 8 + oy] = true;
							}
						}
					}
				}
			}

			for (int ox = 0; ox < 16; ++ox) {
				for (int oz = 0; oz < 16; ++oz) {
					for (int oy = 0; oy < 8; ++oy) {
						boolean isOuterBlock = !isInPool[(ox * 16 + oz) * 8 + oy] && (ox < 15 && isInPool[((ox + 1) * 16 + oz) * 8 + oy] || ox > 0 && isInPool[((ox - 1) * 16 + oz) * 8 + oy] || oz < 15 && isInPool[(ox * 16 + oz + 1) * 8 + oy] || oz > 0 && isInPool[(ox * 16 + (oz - 1)) * 8 + oy] || oy < 7 && isInPool[(ox * 16 + oz) * 8 + oy + 1] || oy > 0 && isInPool[(ox * 16 + oz) * 8 + (oy - 1)]);

						if (isOuterBlock) {
							Material material = worldIn.getBlockState(position.add(ox, oy, oz)).getMaterial();

							if (oy >= 4 && material.isLiquid()) {
								return false;
							}

							if (oy < 4 && !material.isSolid() && worldIn.getBlockState(position.add(ox, oy, oz)).getBlock() != this.block) {
								return false;
							}
						}
					}
				}
			}

			for (int ox = 0; ox < 16; ++ox) {
				for (int oz = 0; oz < 16; ++oz) {
					for (int oy = 0; oy < 8; ++oy) {
						if (isInPool[(ox * 16 + oz) * 8 + oy]) {
							this.setBlockAndNotifyAdequately(worldIn, position.add(ox, oy, oz), oy >= 4 ? Blocks.AIR.getDefaultState() : this.block.getDefaultState());
						}
					}
				}
			}

			return true;
		}
	}
}