package thebetweenlands.util;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * A class used to wrap any other slot; exists to be extended.
 */
public class SlotWrapper<T extends Slot> extends Slot {

	/**
	 * Wraps a slot. Exists because generics.
	 * @param <T> The type of the wrapped slot.
	 * @param slot The slot to wrap.
	 * @return A wrapped version of the slot
	 */
	public static <T extends Slot> SlotWrapper<T> wrap(T slot) {
		return new SlotWrapper<>(Objects.requireNonNull(slot));
	}

	protected final T delegate;

	public SlotWrapper(T slot) {
		super(slot.container, slot.getSlotIndex(), slot.x, slot.y);
		this.delegate = slot;
	}

	public T getDelegate() {
		return this.delegate;
	}

	@Override
	public void onQuickCraft(ItemStack oldStack, ItemStack newStack) {
		this.getDelegate().onQuickCraft(oldStack, newStack);
	}

	@Override
	public void onTake(Player player, ItemStack stack) {
		this.getDelegate().onTake(player, stack);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return this.getDelegate().mayPlace(stack);
	}

	@Override
	public ItemStack getItem() {
		return this.getDelegate().getItem();
	}

	@Override
	public boolean hasItem() {
		return this.getDelegate().hasItem();
	}

	@Override
	public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
		this.getDelegate().setByPlayer(newStack, oldStack);
	}

	@Override
	public void set(ItemStack stack) {
		this.getDelegate().set(stack);
	}

	@Override
	public void setChanged() {
		this.getDelegate().setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return this.getDelegate().getMaxStackSize();
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return this.getDelegate().getMaxStackSize(stack);
	}

	@Nullable
	@Override
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return this.getDelegate().getNoItemIcon();
	}

	@Override
	public ItemStack remove(int amount) {
		return this.getDelegate().remove(amount);
	}

	@Override
	public boolean mayPickup(Player player) {
		return this.getDelegate().mayPickup(player);
	}

	@Override
	public boolean isActive() {
		return this.getDelegate().isActive();
	}

	@Override
	public int getSlotIndex() {
		return this.getDelegate().getSlotIndex();
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return this.getDelegate().isSameInventory(other);
	}

	@Override
	public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
		this.getDelegate().setBackground(atlas, sprite);
		return this;
	}

	@Override
	public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
		if (!this.mayPickup(player)) {
			return Optional.empty();
		} else if (!this.allowModification(player) && decrement < this.getItem().getCount()) {
			return Optional.empty();
		} else {
			return this.getDelegate().tryRemove(count, decrement, player);
		}
	}

	@Override
	public ItemStack safeTake(int count, int decrement, Player player) {
		if (!this.mayPickup(player)) {
			return ItemStack.EMPTY;
		} else if (!this.allowModification(player) && decrement < this.getItem().getCount()) {
			return ItemStack.EMPTY;
		} else {
			return this.getDelegate().safeTake(count, decrement, player);
		}
	}

	@Override
	public ItemStack safeInsert(ItemStack stack) {
		if (!this.mayPlace(stack)) {
			return stack;
		} else {
			return this.getDelegate().safeInsert(stack);
		}
	}

	@Override
	public ItemStack safeInsert(ItemStack stack, int increment) {
		if (!this.mayPlace(stack)) {
			return stack;
		} else {
			return this.getDelegate().safeInsert(stack, increment);
		}
	}

	@Override
	public boolean allowModification(Player player) {
		// super.allowModification(player) checks this.mayPickup(player) & this.mayPlace(player, stack)
		return super.allowModification(player) && this.getDelegate().allowModification(player);
	}

	@Override
	public int getContainerSlot() {
		return this.getDelegate().getContainerSlot();
	}

	@Override
	public boolean isHighlightable() {
		return this.getDelegate().isHighlightable();
	}

	@Override
	public boolean isFake() {
		return this.getDelegate().isFake();
	}
}
