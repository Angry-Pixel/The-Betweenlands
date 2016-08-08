package thebetweenlands.client.tab;

import net.minecraft.item.Item;
import thebetweenlands.common.registries.BlockRegistry;

public class TabPlants extends CreativeTabBetweenlands {
	public TabPlants() {
		super("thebetweenlands.plants");
	}

	@Override
	public Item getTabIconItem() {
		return Item.getItemFromBlock(BlockRegistry.MIRE_CORAL);
	}
}
