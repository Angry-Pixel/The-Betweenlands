package thebetweenlands.items.misc;

import net.minecraft.item.Item;
import thebetweenlands.gemcircle.CircleGem;

public class ItemGem extends Item {
	private final CircleGem circleGem;

	public ItemGem(String name, CircleGem circleGem) {
		this.circleGem = circleGem;
		this.setUnlocalizedName("thebetweenlands." + name);
		this.setTextureName("thebetweenlands:" + name);
	}

	public CircleGem getCircleGem() {
		return this.circleGem;
	}
}
