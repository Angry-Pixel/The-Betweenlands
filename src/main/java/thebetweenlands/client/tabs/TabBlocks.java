package thebetweenlands.client.tabs;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.client.tab.CreativeTabBetweenlands;

import java.util.Comparator;

public class TabBlocks extends CreativeTabBetweenlands {
	Comparator<ItemStack> sortedBlocks;

	public TabBlocks() {
		super("thebetweenlands.block");
	}

	@Override
	public Item getTabIconItem() {
		return /*Item.getItemFromBlock(BLBlockRegistry.swampGrass)*/Item.getItemFromBlock(Blocks.STONE);
	}
}
