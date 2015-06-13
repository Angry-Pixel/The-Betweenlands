package thebetweenlands.items;

import thebetweenlands.creativetabs.ModCreativeTabs;
import net.minecraft.item.Item;

public class ItemWeedwoodBucketRubber extends Item {
	public ItemWeedwoodBucketRubber() {
		this.setMaxStackSize(1);
		this.setUnlocalizedName("thebetweenlands.bucketOfRubber");
		this.setTextureName("thebetweenlands:weedwoodBucketRubber");
		this.setCreativeTab(ModCreativeTabs.items);
	}
}
