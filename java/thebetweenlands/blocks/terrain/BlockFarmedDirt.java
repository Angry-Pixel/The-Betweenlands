package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockFarmedDirt extends Block {
	public BlockFarmedDirt() {
		super(Material.ground);
		setHardness(0.5F);
		setStepSound(soundTypeGravel);
		setHarvestLevel("shovel", 0);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.farmedDirt");
	}
}
