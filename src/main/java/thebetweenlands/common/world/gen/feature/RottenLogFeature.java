package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.config.RottenLogConfiguration;

public class RottenLogFeature extends Feature<RottenLogConfiguration> {

	public RottenLogFeature(Codec<RottenLogConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<RottenLogConfiguration> context) {
		return generate(context.level(), context.random(), context.origin(), context.config());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos, RottenLogConfiguration config) {
		int length = rand.nextInt(config.lengthOffset()) + config.length();
		int radius = rand.nextInt(config.baseRadius()) + config.baseRadius();
		Direction.Axis axis = rand.nextInt(2) == 0 ? Direction.Axis.X : Direction.Axis.Z;
		BlockState log = BlockRegistry.ROTTEN_BARK.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		if (axis == Direction.Axis.Z) {
			for (int xx = x - radius; radius + x >= xx; xx++) {
				for (int zz = z - length; length + z - 1 >= zz; zz++) {
					for (int yy = y + 1; yy <= y + radius * 2; yy++) {
						BlockPos offset = new BlockPos(xx, yy, zz);
						if (!level.isAreaLoaded(offset, 1) || !level.getBlockState(offset).canBeReplaced()) {
							return false;
						}
					}
				}
			}

			for (int zz = z - length; length + z - 1 >= zz; zz++) {
				for (int i = radius * -1; i <= radius; i++) {
					for (int j = radius * -1; j <= radius; j++) {
						double distSq = i * i + j * j;
						BlockPos offset = new BlockPos(x + i, y + j + radius, zz);
						if (Math.round(Math.sqrt(distSq)) == radius) {
							level.setBlock(offset, log, 2 | 16);
							if (rand.nextInt(12) == 0) {
								level.setBlock(offset, Blocks.AIR.defaultBlockState(), 2 | 16);
							}
							if (zz == z - length && rand.nextInt(2) == 0 || zz == z + length - 1 && rand.nextInt(2) == 0) {
								level.setBlock(offset, Blocks.AIR.defaultBlockState(), 2 | 16);
							}
						} else {
							level.setBlock(offset, Blocks.AIR.defaultBlockState(), 2 | 16);
						}
					}
				}
			}
		} else {
			for (int xx = x - length; length + x - 1 >= xx; xx++) {
				for (int zz = z - radius; radius + z >= zz; zz++) {
					for (int yy = y + 1; yy <= y + radius * 2; yy++) {
						BlockPos offset = new BlockPos(xx, yy, zz);
						if (!level.isAreaLoaded(offset, 1) || !level.getBlockState(offset).canBeReplaced()) {
							return false;
						}
					}
				}
			}

			for (int xx = x - length; length + x - 1 >= xx; xx++) {
				for (int i = radius * -1; i <= radius; ++i) {
					for (int j = radius * -1; j <= radius; ++j) {
						double dSq = i * i + j * j;
						BlockPos offset = new BlockPos(xx, y + j + radius, z + i);
						if (Math.round(Math.sqrt(dSq)) == radius) {
							level.setBlock(offset, log, 2 | 16);
							if (rand.nextInt(12) == 0) {
								level.setBlock(offset, Blocks.AIR.defaultBlockState(), 2 | 16);
							}
							if (xx == x - length && rand.nextInt(2) == 0 || xx == x + length - 1 && rand.nextInt(2) == 0) {
								level.setBlock(offset, Blocks.AIR.defaultBlockState(), 2 | 16);
							}
						} else {
							level.setBlock(offset, Blocks.AIR.defaultBlockState(), 2 | 16);
						}
					}
				}
			}
		}
		return true;
	}
}
