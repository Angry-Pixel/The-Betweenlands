package thebetweenlands.common.block;

import net.minecraft.block.state.IBlockState;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.block.terrain.BlockCragrock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class SoilHelper {
	public static boolean canSustainPlant(IBlockState state) {
		return SurfaceType.GRASS.matches(state) || SurfaceType.DIRT.matches(state) || state.getBlock() instanceof BlockGenericDugSoil || state.getBlock() == BlockRegistry.MUD_BRICKS || state.getBlock() == BlockRegistry.GIANT_ROOT ||
				(state.getBlock() == BlockRegistry.CRAGROCK && state.getValue(BlockCragrock.VARIANT) != BlockCragrock.EnumCragrockType.DEFAULT) || state.getBlock() == BlockRegistry.MUD_BRICK_SHINGLES || state.getBlock() == BlockRegistry.MUD_BRICK_SHINGLE_SLAB
				|| state.getBlock() == BlockRegistry.MUD_TILES || state.getBlock() == BlockRegistry.COMPACTED_MUD;
	}

	public static boolean canSustainUnderwaterPlant(IBlockState state) {
		return state.getBlock() == BlockRegistry.MUD || canSustainPlant(state);
	}

	public static boolean canSustainCrop(IBlockState state) {
		return state.getBlock() instanceof BlockGenericDugSoil && (state.getValue(BlockGenericDugSoil.COMPOSTED) || state.getValue(BlockGenericDugSoil.DECAYED));
	}
}
