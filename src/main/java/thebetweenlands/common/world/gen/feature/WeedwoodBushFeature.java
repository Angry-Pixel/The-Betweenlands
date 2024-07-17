package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.SurfaceType;

public class WeedwoodBushFeature extends Feature<NoneFeatureConfiguration> {

	public WeedwoodBushFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return generate(context.level(), context.random(), context.origin());
	}

	private boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		BlockPos.MutableBlockPos offset = pos.mutable();
		int y = pos.getY();
		BlockState state;
		do {
			offset.setY(y);
			state = level.getBlockState(offset);
			if (!state.is(BlockTags.LEAVES) || state.isAir()) {
				break;
			}
			--y;
		} while (y > level.getMinBuildHeight());
		++y;
		offset.setY(y);

		this.setBlock(level, offset, BlockRegistry.WEEDWOOD_BUSH.get().defaultBlockState());

		int startRadius = rand.nextInt(6) + 3;
		int height = rand.nextInt(2) + 1;

		for (int by = y; by < y + height; ++by) {
			int yo = by - y;
			int radius = (int)((startRadius - yo) / (0.5 * (yo * 2 + 1) + 1));

			for (int bx = pos.getX() - radius; bx <= pos.getX() + radius; ++bx) {
				int xo = bx - pos.getX();

				for (int bz = pos.getZ() - radius; bz <= pos.getZ() + radius; ++bz) {
					int zo = bz - pos.getZ();

					offset.set(bx, by, bz);
					BlockState checkstate = level.getBlockState(offset);

					if (checkstate.is(BlockTags.REPLACEABLE_BY_TREES) && rand.nextInt((int)((xo * xo + zo * zo) * 1.25 + 1)) < 2) {
						BlockState belowstate = level.getBlockState(offset.below());
						if (belowstate.is(BlockRegistry.WEEDWOOD_BUSH.get()) || SurfaceType.GRASS_AND_DIRT.matches(belowstate)) {
							this.setBlock(level, offset, BlockRegistry.WEEDWOOD_BUSH.get().defaultBlockState());
						}
					}
				}
			}
		}

		return true;
	}
}
