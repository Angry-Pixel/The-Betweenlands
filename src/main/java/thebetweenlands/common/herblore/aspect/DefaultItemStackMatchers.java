package thebetweenlands.common.herblore.aspect;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class DefaultItemStackMatchers {
	public static final IItemStackMatcher ITEM = new IItemStackMatcher() {
		@Override
		public boolean matches(ItemStack original, ItemStack stack) {
			return original.getItem() == stack.getItem();
		}
	};

	public static final IItemStackMatcher ITEM_DAMAGE = new IItemStackMatcher() {
		@Override
		public boolean matches(ItemStack original, ItemStack stack) {
			return original.getItem() == stack.getItem() && (original.getItemDamage() == OreDictionary.WILDCARD_VALUE || original.getItemDamage() == stack.getItemDamage());
		}
	};
}