package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.material.Fluids;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.config.SimulacrumConfiguration;

public class LakeCavernSimulacrumFeature extends SimulacrumFeature {

	public LakeCavernSimulacrumFeature(Codec<SimulacrumConfiguration> codec) {
		super(codec);
	}

	@Override
	protected boolean canGenerateHere(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		if (!level.getBlockState(pos.below()).is(BlockRegistry.PITSTONE)) {
			return false;
		}

		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockPos offset = pos.relative(direction, 3);

			if (level.getBlockState(offset.below()).getFluidState().is(Fluids.WATER) || level.getBlockState(offset.below()).getFluidState().is(Fluids.WATER)) {
				return true;
			}
		}

		return false;
	}
}
