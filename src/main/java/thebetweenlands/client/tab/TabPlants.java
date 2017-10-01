package thebetweenlands.client.tab;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.registries.BlockRegistry;

public class TabPlants extends CreativeTabBetweenlands {
	public TabPlants() {
		super("thebetweenlands.plants");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(Item.getItemFromBlock(BlockRegistry.MIRE_CORAL));
	}
}
