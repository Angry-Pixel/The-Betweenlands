package thebetweenlands.blocks;

import thebetweenlands.creativetabs.ModCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.util.StatCollector;

public class BlockStairsBL extends BlockStairs {

	public BlockStairsBL(int id, Block block, int meta) {
		super(block, meta);
		setHardness(2.0F);
		setLightOpacity(0);
	}

	public BlockStairsBL(int id, Block block) {
		this(id, block, 0);
	}
}