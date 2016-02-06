package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockBLStairs extends BlockStairs {

	public BlockBLStairs(Block block, int meta) {
		super(block, meta);
		setLightOpacity(0);
		setCreativeTab(BLCreativeTabs.blocks);
	}
}