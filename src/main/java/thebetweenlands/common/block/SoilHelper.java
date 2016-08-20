package thebetweenlands.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import thebetweenlands.common.registries.BlockRegistry;

public class SoilHelper {
	public static boolean canSustainPlant(IBlockState state) {
		return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.FARMLAND ||
				state.getBlock() == BlockRegistry.SWAMP_DIRT || state.getBlock() == BlockRegistry.SWAMP_GRASS ||
				state.getBlock() == BlockRegistry.DEAD_GRASS || state.getBlock() == BlockRegistry.SLUDGY_DIRT;
	}

	public static boolean canSustainUnderwaterPlant(IBlockState state) {
		return state.getBlock() == BlockRegistry.MUD || canSustainPlant(state);
	}
}
