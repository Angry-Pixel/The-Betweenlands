package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockSlimyDirt
        extends Block
{
	public BlockSlimyDirt() {
		super(Material.ground);
		setHardness(0.5F);
		setStepSound(soundTypeGravel);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.slimyDirt");
		setBlockTextureName("thebetweenlands:slimyDirt");
	}
}
