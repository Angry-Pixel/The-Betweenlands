package thebetweenlands.common.block.plant;

import net.minecraft.block.state.IBlockState;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockCaveGrass extends BlockPlant {
	@Override
	protected boolean canSustainBush(IBlockState state) {
		return SoilHelper.canSustainPlant(state) || state.getBlock() == BlockRegistry.BETWEENSTONE || state.getBlock() == BlockRegistry.PITSTONE;
	}
}
