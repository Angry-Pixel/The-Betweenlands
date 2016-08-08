package thebetweenlands.client.tab;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.herblore.ItemCrushed.EnumItemCrushed;

public class TabHerblore extends CreativeTabBetweenlands {
	public TabHerblore() {
		super("thebetweenlands.herbLore");
	}

	@Override
	public ItemStack getIconItemStack() {  
		return EnumItemCrushed.GROUND_GENERIC_LEAF.create(1);  
		} 
}
