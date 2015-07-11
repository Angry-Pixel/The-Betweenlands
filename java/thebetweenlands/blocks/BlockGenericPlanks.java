package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.StatCollector;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockGenericPlanks extends Block {

	private String type;

	public BlockGenericPlanks(String blockName, Material material) {
		super(material);
		setCreativeTab(ModCreativeTabs.blocks);
		setHardness(2F);
		setResistance(5.0F);
		setStepSound(Block.soundTypeWood);
		type = blockName;
		setBlockName("thebetweenlands." + blockName);
		setBlockTextureName("thebetweenlands:"+ blockName);
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + type));
	}
}