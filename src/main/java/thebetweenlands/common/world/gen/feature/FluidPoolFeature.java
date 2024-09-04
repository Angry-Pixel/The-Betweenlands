package thebetweenlands.common.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.FluidState;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.config.PoolConfiguration;

import java.util.List;

public class FluidPoolFeature extends Feature<PoolConfiguration> {

	private static final List<Block> BLOCK_BLACKLIST = ImmutableList.of(
		BlockRegistry.BETWEENSTONE_TILES.get(),
		BlockRegistry.BETWEENSTONE_BRICK_STAIRS.get(),
		BlockRegistry.BETWEENSTONE_BRICKS.get(),
		BlockRegistry.BETWEENSTONE_BRICK_SLAB.get());

	public FluidPoolFeature(Codec<PoolConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<PoolConfiguration> context) {
		return generate(context.level(), context.random(), context.origin(), context.config());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos, PoolConfiguration config) {
		pos = pos.offset(-8, 0, -8);
		while (pos.getY() > 5 && level.getBlockState(pos).isAir()) {
			pos = pos.below();
		}

		if (pos.getY() <= 4 + config.minY()) {
			return false;
		} else {
			pos = pos.below();

			for (int xx = 0; xx < 16; xx++) {
				for (int zz = 0; zz < 16; zz++) {
					for (int yy = 0; yy < 8; yy++) {
						if (BLOCK_BLACKLIST.contains(level.getBlockState(pos.offset(xx, yy, zz)).getBlock())) {
							return false;
						}
					}
				}
			}

			boolean[] isInPool = new boolean[2048];
			int blobs = rand.nextInt(4) + 4;

			for (int blob = 0; blob < blobs; blob++) {
				double sx = (rand.nextDouble() * 6.0D + 3.0D) * config.size();
				double sy = (rand.nextDouble() * 4.0D + 2.0D) * config.size();
				double sz = (rand.nextDouble() * 6.0D + 3.0D) * config.size();
				double bx = rand.nextDouble() * (16.0D - sx - 2.0D) + 1.0D + sx / 2.0D;
				double by = rand.nextDouble() * (8.0D - sy - 4.0D) + 2.0D + sy / 2.0D;
				double bz = rand.nextDouble() * (16.0D - sz - 2.0D) + 1.0D + sz / 2.0D;

				for (int ox = 1; ox < 15; ox++) {
					for (int oz = 1; oz < 15; oz++) {
						for (int oy = 1; oy < 7; oy++) {
							double dx = (ox - bx) / (sx / 2.0D);
							double dy = (oy - by) / (sy / 2.0D);
							double dz = (oz - bz) / (sz / 2.0D);
							double dist = dx * dx + dy * dy + dz + dz;

							if (dist < 1.0D) {
								isInPool[(ox * 16 + oz) * 8 + oy] = true;
							}
						}
					}
				}
			}

			for (int ox = 0; ox < 16; ox++) {
				for (int oz = 0; oz < 16; oz++) {
					for (int oy = 0; oy < 8; oy++) {
						boolean isOuterBlock = !isInPool[(ox * 16 + oz) * 8 + oy] &&
							(ox < 15 && isInPool[((ox + 1) * 16 + oz) * 8 + oy] || ox > 0 &&
								isInPool[((ox - 1) * 16 + oz) * 8 + oy] || oz < 15 &&
								isInPool[(ox * 16 + oz + 1) * 8 + oy] || oz > 0 &&
								isInPool[(ox * 16 + (oz - 1)) * 8 + oy] || oy < 7 &&
								isInPool[(ox * 16 + oz) * 8 + oy + 1] || oy > 0 &&
								isInPool[(ox * 16 + oz) * 8 + (oy - 1)]);

						if (isOuterBlock) {
							BlockState state = level.getBlockState(pos.offset(ox, oy, oz));
							FluidState fluid = level.getFluidState(pos.offset(ox, oy, oz));

							if (oy >= 4 && !fluid.isEmpty()) {
								return false;
							}

							if (oy < 4 && state.isSolid() && state != config.fluid()) {
								return false;
							}
						}
					}
				}
			}

			for (int ox = 0; ox < 16; ox++) {
				for (int oz = 0; oz < 16; oz++) {
					for (int oy = 0; oy < 8; oy++) {
						if (isInPool[(ox * 16 + oz) * 8 + oy]) {
							this.setBlock(level, pos.offset(ox, oy, oz), oy >= 4 ? Blocks.AIR.defaultBlockState() : config.fluid());
						}
					}
				}
			}

			return true;
		}
	}
}
