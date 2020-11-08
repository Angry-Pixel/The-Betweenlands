package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

public class WorldGenDeepmanSimulacrum extends WorldGenSimulacrum {
	public WorldGenDeepmanSimulacrum() {
		super(ImmutableList.of(BlockRegistry.SIMULACRUM_DEEPMAN), LootTableRegistry.DEEPMAN_SIMULACRUM_OFFERINGS);
	}

	@Override
	protected boolean canGenerateHere(World world, Random rand, BlockPos pos) {
		return world.getBlockState(pos.down()).getBlock() == BlockRegistry.BETWEENSTONE;
	}

}
