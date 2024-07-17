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

public class SwampReedClusterFeature extends Feature<NoneFeatureConfiguration> {
	public SwampReedClusterFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return generate(context.level(), context.random(), context.origin());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		BlockPos.MutableBlockPos mutable = pos.mutable();
		boolean generated = false;

		for (BlockState state = level.getBlockState(mutable); (state.isAir() || state.is(BlockTags.LEAVES)) && mutable.getY() > level.getMinBuildHeight(); state = level.getBlockState(mutable)) {
			mutable.below();
		}

		for (int i = 0; i < 128; ++i) {
			mutable.offset(rand.nextInt(10) - rand.nextInt(10), rand.nextInt(8) - rand.nextInt(8), rand.nextInt(10) - rand.nextInt(10));

			if (level.isAreaLoaded(mutable, 1)) {
				if (SurfaceType.WATER.matches(level, mutable.above()) && level.getBlockState(mutable).is(BlockRegistry.MUD) && level.getBlockState(mutable.above(2)).isAir()) {
					this.generateReedStack(level, rand, mutable.above());
					generated = true;
				} else if (SurfaceType.MIXED_GROUND.matches(level, mutable) && BlockRegistry.SWAMP_REED.get().defaultBlockState().canSurvive(level, mutable.above())) {
					this.generateReedStack(level, rand, mutable.above());
					generated = true;
				}
			}
		}
		return generated;
	}

	private void generateReedStack(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		BlockPos.MutableBlockPos mutable = pos.mutable();
		int height = rand.nextInt(4) + 2;
		for (int yo = 0; yo < height; ++yo) {
			BlockPos offset = mutable.offset(0, yo, 0);
			BlockState state = level.getBlockState(offset);
			if (!state.isAir() && !SurfaceType.WATER.matches(state))
				break;
			level.setBlock(offset, BlockRegistry.SWAMP_REED.get().defaultBlockState(), 2 | 16);
		}
	}
}
