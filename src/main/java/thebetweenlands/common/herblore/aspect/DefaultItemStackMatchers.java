package thebetweenlands.common.herblore.aspect;

import net.minecraft.item.ItemStack;

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
			return original.getItem() == stack.getItem() && original.getItemDamage() == stack.getItemDamage();
		}
	};
}