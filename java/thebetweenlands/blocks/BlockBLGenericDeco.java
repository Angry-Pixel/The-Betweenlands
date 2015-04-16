package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.StatCollector;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockBLGenericDeco extends Block {
	
	private String type;

	public BlockBLGenericDeco(String blockName, Material material) {
		super(material);
		setCreativeTab(ModCreativeTabs.blocks);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + type));
	}

}