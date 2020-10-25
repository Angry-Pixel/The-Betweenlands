package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class WorldGenRootmanSimulacrum extends WorldGenSimulacrum {
	public WorldGenRootmanSimulacrum() {
		super(ImmutableList.of(BlockRegistry.SIMULACRUM_ROOTMAN), LootTableRegistry.ROOTMAN_SIMULACRUM_OFFERINGS);
	}

	@Override
	protected boolean shouldGenerateOfferingTable(Random rand) {
		return false;
	}

	@Override
	protected boolean canGenerateHere(World world, Random rand, BlockPos pos) {
		if(world.getBlockState(pos.down()).getBlock() == BlockRegistry.GIANT_ROOT) {
			BlockPos surface = world.getHeight(pos);
			return surface.getY() > WorldProviderBetweenlands.LAYER_HEIGHT + 24 && SurfaceType.GRASS_AND_DIRT.matches(world, surface.down());
		}
		return false;
	}
}
