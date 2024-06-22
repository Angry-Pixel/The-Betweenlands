package thebetweenlands.common.world.gen.feature.legacy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.Random;

public class WorldGenDebug extends WorldGenHelper {

	@Override
	public boolean generate(WorldGenLevel world, Random rand, BlockPos pos) {
		world.setBlock(pos, BlockRegistry.LIMESTONE.get().defaultBlockState(), 0);
		return true;
	}
}
