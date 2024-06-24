package thebetweenlands.api.aspect;

import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.aspect.IItemStackMatcher;

/**
 * Used as an identifier for items that carry aspects.
 * Instances can be obtained from {@link AspectManager#getAspectItem(ItemStack)}
 */
public class AspectItem {
	private final ItemStack original;
	private final IItemStackMatcher matcher;

	/**
	 * Creates a new aspect item
	 * @param stack
	 * @param matcher
	 */
	public AspectItem(ItemStack stack, IItemStackMatcher matcher) {
		this.original = stack;
		this.matcher = matcher;
	}

	/**
	 * Returns a copy of the original item stack
	 * @return
	 */
	public ItemStack getOriginal() {
		return this.original.copy();
	}

	/**
	 * Returns the item stack matcher
	 * @return
	 */
	public IItemStackMatcher getMatcher() {
		return this.matcher;
	}

	/**
	 * Returns whether the specified item matches
	 * @param stack
	 * @return
	 */
	public boolean matches(ItemStack stack) {
		return this.matcher.matches(this.original, stack);
	}
}
