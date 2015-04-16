package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockStairsBL extends BlockStairs {

	public BlockStairsBL(Block block, int meta) {
		super(block, meta);
		setLightOpacity(0);
		setCreativeTab(ModCreativeTabs.blocks);
	}
}