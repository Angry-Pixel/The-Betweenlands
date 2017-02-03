package thebetweenlands.common.block;

import net.minecraft.block.state.IBlockState;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class SoilHelper {
	public static boolean canSustainPlant(IBlockState state) {
		return SurfaceType.GRASS.matches(state) || SurfaceType.DIRT.matches(state) || state.getBlock() instanceof BlockGenericDugSoil;
	}

	public static boolean canSustainUnderwaterPlant(IBlockState state) {
		return state.getBlock() == BlockRegistry.MUD || canSustainPlant(state);
	}

	public static boolean canSustainCrop(IBlockState state) {
		return state.getBlock() instanceof BlockGenericDugSoil && (state.getValue(BlockGenericDugSoil.COMPOSTED) || state.getValue(BlockGenericDugSoil.DECAYED));
	}
}
