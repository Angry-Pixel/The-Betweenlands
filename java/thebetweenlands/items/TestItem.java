package thebetweenlands.items;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class TestItem extends ItemSword {

	public TestItem() {
		super(Item.ToolMaterial.IRON);
	}

	@Override
	public boolean getIsRepairable(ItemStack itemStack1, ItemStack itemStack2) {
		return Items.iron_ingot == itemStack2.getItem() ? true : super.getIsRepairable(itemStack1, itemStack2);
	}
}
