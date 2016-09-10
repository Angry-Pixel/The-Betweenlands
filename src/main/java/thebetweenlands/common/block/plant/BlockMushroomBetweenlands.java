package thebetweenlands.common.block.plant;

import net.minecraft.block.state.IBlockState;

public class BlockMushroomBetweenlands extends BlockPlant {
	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.isOpaqueCube();
	}
}
