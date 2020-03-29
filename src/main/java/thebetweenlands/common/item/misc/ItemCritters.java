package thebetweenlands.common.item.misc;

import java.util.Map.Entry;
import java.util.function.Supplier;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.item.tools.ItemNet;

public class ItemCritters extends ItemMob {
	public ItemCritters() {
		super(1, null);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
			for(Entry<Class<? extends Entity>, Supplier<? extends ItemMob>> entry : ItemNet.CATCHABLE_ENTITIES.entrySet()) {
				if(entry.getValue().get() == this) {
					items.add(this.capture(entry.getKey()));
				}
			}
		}
	}
}
