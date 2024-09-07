package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.SurfaceType;
import thebetweenlands.util.WorldGenUtil;

public class BladderwortClusterFeature extends Feature<NoneFeatureConfiguration> {

	public BladderwortClusterFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return generate(context.level(), context.random(), context.origin());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		boolean generated = false;
		pos = WorldGenUtil.loopUntilSolid(level, pos);

		for (int i = 0; i < 128; i++) {
			BlockPos offset = WorldGenUtil.randomOffset(rand, pos, 10, 8, 10);

			if (SurfaceType.WATER.matches(level, offset.above()) && SurfaceType.DIRT.matches(level, offset)) {
				if (generateBladderwortStack(level, offset.above()))
					generated = true;
			}
		}

		return generated;
	}

	private boolean generateBladderwortStack(WorldGenLevel level, BlockPos pos) {
		int height = 0;
		BlockPos.MutableBlockPos mutable = pos.mutable();

		for (int yo = 0; yo < 128; yo++) {
			mutable.setY(pos.getY() + yo);
			if (!level.getBlockState(mutable).isAir() && !SurfaceType.WATER.matches(level, mutable))
				return false;
			if (level.getBlockState(mutable).isAir() && level.getBlockState(mutable.setY(pos.getY() + yo + 1)).isAir()) {
				height = yo;
				break;
			}
		}

		if (height < 4)
			return false;

		for (int yo = 0; yo <= height; yo++) {
			BlockPos offset = pos.offset(0, yo, 0);
			BlockState state = level.getBlockState(offset);
			if (!SurfaceType.WATER.matches(state)) {
				level.setBlock(offset, BlockRegistry.BLADDERWORT_FLOWER.get().defaultBlockState(), 2 | 16);
				break;
			} else {
				level.setBlock(offset, BlockRegistry.BLADDERWORT_STALK.get().defaultBlockState(), 2 | 16);
			}
		}

		return true;
	}
}
