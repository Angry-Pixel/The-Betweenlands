package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

public class BlockSlope extends BlockStairs {
	public BlockSlope(Block block, int meta) {
		super(block, meta);
		this.setLightOpacity(0);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.SLOPE.id();
	}
}