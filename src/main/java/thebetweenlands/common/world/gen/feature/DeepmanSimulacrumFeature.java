package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.config.SimulacrumConfiguration;

public class DeepmanSimulacrumFeature extends SimulacrumFeature {

	public DeepmanSimulacrumFeature(Codec<SimulacrumConfiguration> codec) {
		super(codec);
	}

	@Override
	protected boolean canGenerateHere(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		return level.getBlockState(pos.below()).is(BlockRegistry.BETWEENSTONE);
	}
}
