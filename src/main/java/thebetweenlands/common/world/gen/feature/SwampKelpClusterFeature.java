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

public class SwampKelpClusterFeature extends Feature<NoneFeatureConfiguration> {
	public SwampKelpClusterFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return false;
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		boolean generated = false;

		for (BlockState state = level.getBlockState(pos); (state.isAir() || state.is(BlockTags.LEAVES)) && pos.getY() > level.getMinBuildHeight(); level.getBlockState(pos)) {
			pos = pos.below();
		}

		for (int i = 0; i < 128; ++i) {
			BlockPos offset = pos.mutable().offset(rand.nextInt(10) - rand.nextInt(10), rand.nextInt(8) - rand.nextInt(8), rand.nextInt(10) - rand.nextInt(10));

			if (SurfaceType.WATER.matches(level, offset.above()) && SurfaceType.DIRT.matches(level, offset)) {
				if (this.generateSwampKelpStack(level, rand, offset.above())) {
					generated = true;
				}
			}
		}
		return generated;
	}

	private boolean generateSwampKelpStack(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		int height = 0;
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for (int yo = 0; yo < 128; ++yo) {
			mutable.set(pos.getX(), pos.getY() + yo, pos.getZ());
			if (!SurfaceType.WATER.matches(level, mutable)) {
				height = yo;
				break;
			}
		}
		if (height < 2)
			return false;
		height = Math.min(height, 7);
		height = rand.nextInt(height) + 1;
		for (int yo = 0; yo < height; ++yo) {
			mutable.set(pos.getX(), pos.getY() + yo, pos.getZ());
			BlockState state = level.getBlockState(mutable);
			if (SurfaceType.WATER.matches(state)) {
				level.setBlock(mutable, BlockRegistry.SWAMP_KELP.get().defaultBlockState(), 2 | 16);
			}
		}
		return true;
	}
}
