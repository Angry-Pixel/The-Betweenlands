package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.config.ChanceConfiguration;

public class LyestoneFeature extends Feature<ChanceConfiguration> {

	public LyestoneFeature(Codec<ChanceConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<ChanceConfiguration> context) {
		return generate(context.level(), context.random(), context.origin(), context.config());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos, ChanceConfiguration config) {
		if (level.getBlockState(pos).is(BlockRegistry.LIMESTONE) && level.getBlockState(pos.above()).isAir()) {
			boolean gen = rand.nextInt(1 + config.chance()) == 0;
			if (gen) {
				for (int xx = -2; xx <= 2; xx++) {
					for (int zz = -2; zz <= 2; zz++) {
						for (int yy = -1; yy <= 1; yy++) {
							double dist = Mth.sqrt(xx * xx + yy * yy + zz * zz);
							if (rand.nextInt(Mth.ceil(dist / 1.4D) + 1) == 0) {
								BlockPos offset = pos.offset(xx, yy, zz);
								if (level.isAreaLoaded(pos, 2) && level.getBlockState(offset.above()).isAir()) {
									BlockState state = level.getBlockState(offset);
									if (state.is(BlockRegistry.LIMESTONE)) {
										this.setBlock(level, offset, BlockRegistry.LYESTONE.get().defaultBlockState());
									}
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
