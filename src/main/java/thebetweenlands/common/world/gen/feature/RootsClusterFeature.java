package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.SurfaceType;
import thebetweenlands.common.world.gen.WorldGenUtil;

public class RootsClusterFeature extends Feature<NoneFeatureConfiguration> {

	public RootsClusterFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return generateRootsStack(context.level(), context.random(), context.origin());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		boolean generated = false;
		pos = WorldGenUtil.loopUntilSolid(level, pos);

		for (int i = 0; i < 128; i++) {
			BlockPos offset = WorldGenUtil.randomOffset(rand, pos, 10, 8, 10);

			if (SurfaceType.MIXED_GROUND.matches(level, offset)) {
				if (generateRootsStack(level, rand, offset.above())) {
					generated = true;
				}
			}
		}

		return generated;
	}

	private boolean generateRootsStack(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		int height = 6;
		BlockPos.MutableBlockPos check  = new BlockPos.MutableBlockPos();
		for (int yo = 0; yo < 6; yo++) {
			check.setY(pos.getY() + yo);
			if (!level.getBlockState(check).isAir()) {
				height = yo;
				break;
			}
		}

		if (height < 2) {
			return false;
		}

		height = rand.nextInt(height) + 1 + rand.nextInt(4);
		for (int yo = 0; yo < height; yo++) {
			BlockPos offset = pos.offset(0, yo, 0);
			level.setBlock(offset, BlockRegistry.ROOT.get().defaultBlockState(), 2 | 16);
		}
		return true;
	}
}
