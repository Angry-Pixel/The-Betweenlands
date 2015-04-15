package thebetweenlands.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.StatCollector;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockMudBrick extends Block {
	
	private String type;

	public BlockMudBrick(String mudBrick) {
		super(Material.rock);
		setCreativeTab(ModCreativeTabs.blocks);
		type = mudBrick;
		setBlockName("thebetweenlands.mudBrick");
		setBlockTextureName("thebetweenlands:mudBrick");
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands.mudBrick"));
	}

}