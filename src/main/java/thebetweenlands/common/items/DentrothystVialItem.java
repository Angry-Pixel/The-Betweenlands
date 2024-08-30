package thebetweenlands.common.items;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

public class DentrothystVialItem extends Item {

	private final Holder<Item> fullAspectBottle;
	private final Holder<Item> fullElixirBottle;

	public DentrothystVialItem(Holder<Item> fullAspectBottle, Holder<Item> fullElixirBottle, Properties properties) {
		super(properties);
		this.fullAspectBottle = fullAspectBottle;
		this.fullElixirBottle = fullElixirBottle;
	}

	public Holder<Item> getFullAspectBottle() {
		return this.fullAspectBottle;
	}

	public Holder<Item> getFullElixirBottle() {
		return this.fullElixirBottle;
	}
}
