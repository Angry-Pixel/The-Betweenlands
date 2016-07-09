package thebetweenlands.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class SoilHelper {
	public static boolean canSustainPlant(IBlockState state) {
		return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.FARMLAND;
	}
}
