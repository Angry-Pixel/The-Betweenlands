package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockPitstone extends Block {
	public BlockPitstone() {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.pitstone");
		setBlockTextureName("thebetweenlands:pitstone");
	}
}
