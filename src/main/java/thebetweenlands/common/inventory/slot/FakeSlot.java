package thebetweenlands.common.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FakeSlot extends Slot {
	public FakeSlot(Container container, int slot, int x, int y) {
		super(container, slot, x, y);
	}

	@Override
	public boolean isHighlightable() {
		return false;
	}

	@Override
	public void set(ItemStack stack) {

	}

	@Override
	public ItemStack remove(int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isFake() {
		return true;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	public boolean mayPickup(Player player) {
		return false;
	}
}
