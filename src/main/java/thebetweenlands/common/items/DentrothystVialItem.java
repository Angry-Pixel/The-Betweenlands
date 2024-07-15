package thebetweenlands.common.items;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

public class DentrothystVialItem extends Item {

	private final Holder<Item> fullBottle;

	public DentrothystVialItem(Holder<Item> fullBottle, Properties properties) {
		super(properties);
		this.fullBottle = fullBottle;
	}

	public Holder<Item> getFullBottle() {
		return this.fullBottle;
	}
}
