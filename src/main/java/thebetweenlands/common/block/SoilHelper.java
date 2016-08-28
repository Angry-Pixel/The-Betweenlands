package thebetweenlands.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class SoilHelper {
	public static boolean canSustainPlant(IBlockState state) {
		return SurfaceType.GRASS.matches(state) || SurfaceType.DIRT.matches(state);
	}

	public static boolean canSustainUnderwaterPlant(IBlockState state) {
		return state.getBlock() == BlockRegistry.MUD || canSustainPlant(state);
	}
}
