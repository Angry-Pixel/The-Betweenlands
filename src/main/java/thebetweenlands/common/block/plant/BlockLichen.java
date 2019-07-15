package thebetweenlands.common.block.plant;

import net.minecraft.block.state.IBlockState;

public class BlockLichen extends BlockMoss {
	public BlockLichen(boolean spreading) {
		super(spreading);
	}

	@Override
	public int getColorMultiplier(IBlockState state, net.minecraft.world.IBlockAccess worldIn, net.minecraft.util.math.BlockPos pos, int tintIndex) {
		return 0xFFFFFF;
	}
}
