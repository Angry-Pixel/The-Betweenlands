package thebetweenlands.common.item.misc;

import net.minecraft.world.item.Item;
import thebetweenlands.util.BLDyeColor;

public class BLDyeItem extends Item {

	private final BLDyeColor color;

	public BLDyeItem(BLDyeColor color, Properties properties) {
		super(properties);
		this.color = color;
	}

	public BLDyeColor getColor() {
		return this.color;
	}
}
