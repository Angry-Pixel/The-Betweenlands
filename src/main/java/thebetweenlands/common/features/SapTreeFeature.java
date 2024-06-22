package thebetweenlands.common.features;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.generation.trees.SapTree;
import thebetweenlands.common.registries.BlockRegistry;

public class SapTreeFeature extends Feature<SapTreeConfig> {

	public SapTreeFeature(Codec<SapTreeConfig> p_65786_) {
		super(p_65786_);
	}

	@Override
	public boolean place(FeaturePlaceContext<SapTreeConfig> p_159749_) {
		WorldGenLevel level = p_159749_.level();
		Random rand = p_159749_.random();
		BlockPos pos = p_159749_.origin().north(rand.nextInt(8) - 4).east(rand.nextInt(8) - 4);
		int depth = 0;

		// Random chance to not gen
		if (rand.nextBoolean()) {
			return true;
		}

		// Do find surface
		boolean surface = false;
		for (int y = 0; y <= 40; y++) {
			BlockPos translatedpos = pos.below(y);
			BlockState scanstate = level.getBlockState(translatedpos);
			if (!scanstate.is(Blocks.AIR) && !(scanstate.getFluidState().is(FluidTags.WATER) && scanstate.getDestroySpeed(level, translatedpos) == 100)) {
				surface = true;
				depth = y - 1;

				// Check surface
				if (!scanstate.is(BlockRegistry.SWAMP_GRASS.get()) &&
					!scanstate.is(BlockRegistry.DEAD_SWAMP_GRASS.get()) &&
					!scanstate.is(BlockRegistry.SWAMP_DIRT.get()) &&
					!scanstate.is(BlockRegistry.MUD.get())) {
					return false;
				}

				break;
			}
		}

		// if no surface
		if (!surface) {
			return false;
		}

		// Place
		return SapTree.generate(level, pos.below(depth), rand);
	}
}
