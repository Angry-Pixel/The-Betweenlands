package thebetweenlands.blocks.structure;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockSwordStone extends Block {
	public BlockSwordStone() {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 0);
		setLightLevel(0.8F);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.swordStone");
		setBlockTextureName("thebetweenlands:swordStone");
	}

	@Override
	public int getRenderType() {
		return 0;
	}

}
