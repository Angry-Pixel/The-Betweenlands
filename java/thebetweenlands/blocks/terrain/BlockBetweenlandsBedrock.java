package thebetweenlands.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockBetweenlandsBedrock
        extends Block
{
	public BlockBetweenlandsBedrock() {
		super(Material.rock);
		setResistance(6000000.0F);
		setStepSound(soundTypePiston);
		disableStats();
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.bedrock");
		setBlockTextureName("thebetweenlands:bedrock");
		setBlockUnbreakable();
	}
}
