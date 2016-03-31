package thebetweenlands.client.tabs;

import java.util.Comparator;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TabBlocks extends CreativeTabBetweenlands {
	Comparator<ItemStack> sortedBlocks;

	public TabBlocks() {
		super("thebetweenlands.block");
	}

	@Override
	public Item getTabIconItem() {
		return /*Item.getItemFromBlock(BLBlockRegistry.swampGrass)*/Item.getItemFromBlock(Blocks.stone);
	}
}
