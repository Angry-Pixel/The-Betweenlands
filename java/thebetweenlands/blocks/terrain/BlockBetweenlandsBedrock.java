package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockBetweenlandsBedrock extends Block {
	public BlockBetweenlandsBedrock() {
		super(Material.rock);
		setResistance(6000000.0F);
		setStepSound(soundTypePiston);
		disableStats();
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.bedrock");
		setBlockTextureName("thebetweenlands:bedrock");
		setBlockUnbreakable();
	}
}
