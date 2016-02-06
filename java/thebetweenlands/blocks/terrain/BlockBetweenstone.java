package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockBetweenstone
        extends Block
{
	public BlockBetweenstone() {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.betweenstone");
		setBlockTextureName("thebetweenlands:betweenstone");
	}
}
