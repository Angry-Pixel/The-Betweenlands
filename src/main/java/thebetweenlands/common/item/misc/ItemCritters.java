package thebetweenlands.common.item.misc;

import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.item.tools.ItemNet;

public class ItemCritters extends ItemMob {
	public ItemCritters() {
		super(1, null, null);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
			for(Entry<Class<? extends Entity>, Pair<Supplier<? extends ItemMob>, Predicate<Entity>>> entry : ItemNet.CATCHABLE_ENTITIES.entries()) {
				if(entry.getValue().getLeft().get() == this) {
					items.add(this.capture(entry.getKey()));
				}
			}
		}
	}
}
