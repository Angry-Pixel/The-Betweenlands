package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.SurfaceType;
import thebetweenlands.common.world.gen.feature.config.SimulacrumConfiguration;

public class RootmanSimulacrumFeature extends SimulacrumFeature {

	public RootmanSimulacrumFeature(Codec<SimulacrumConfiguration> codec) {
		super(codec);
	}

	@Override
	protected boolean shouldGenerateOfferingTable(RandomSource rand) {
		return false;
	}

	@Override
	protected boolean canGenerateHere(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		if (level.getBlockState(pos.below()).is(BlockRegistry.GIANT_ROOT)) {
			BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
			return surface.getY() > level.getSeaLevel() + 24 && SurfaceType.GRASS_AND_DIRT.matches(level, surface.below());
		}
		return false;
	}
}
