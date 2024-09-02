package thebetweenlands.common.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class FilteredSlot extends Slot {

	private final Predicate<ItemStack> stack;

	public FilteredSlot(Container container, int slot, int x, int y, Predicate<ItemStack> stack) {
		super(container, slot, x, y);
		this.stack = stack;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return this.stack.test(stack);
	}
}
