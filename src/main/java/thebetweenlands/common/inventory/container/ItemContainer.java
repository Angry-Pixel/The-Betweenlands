package thebetweenlands.common.inventory.container;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import thebetweenlands.common.registries.DataComponentRegistry;

public class ItemContainer implements Container {

	// handle edgecases with modded compound containers
	protected static final Set<ItemContainer> OPEN_CONTAINERS = new HashSet<>();//Collections.newSetFromMap(new WeakHashMap<ItemContainer, Boolean>());
	protected final Set<UUID> trackingPlayers = new HashSet<UUID>();
	
	private final UUID stackUUID;
	private final ItemStack stack;
	private final NonNullList<ItemStack> contents;
	private boolean changed = false;

	public ItemContainer(ItemStack stack, int slots) {
		this.stack = stack;
		if(!stack.has(DataComponentRegistry.INVENTORY_ITEM_UUID)) {
			stack.set(DataComponentRegistry.INVENTORY_ITEM_UUID, UUID.randomUUID());
		}
		this.stackUUID = stack.get(DataComponentRegistry.INVENTORY_ITEM_UUID);
		this.contents = NonNullList.withSize(slots, ItemStack.EMPTY);
		ItemContainerContents container = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		for (int i = 0; i < slots; i++) {
			if (container.getSlots() > i) {
				this.contents.set(i, container.getStackInSlot(i));
			} else {
				this.contents.set(i, ItemStack.EMPTY);
			}
		}
	}

	public ItemStack getContainerStack() {
		return this.stack;
	}

	public ItemStack getContainerStack(Player player) {
		return getContainerStackFromPlayerAndStackUUIDOrFallback(player, this.stackUUID, this.stack);
	}

	@Override
	public int getContainerSize() {
		return this.contents.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.contents) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.contents.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack itemstack = ContainerHelper.removeItem(this.contents, slot, amount);
		if (!itemstack.isEmpty()) {
			this.setChanged();
		}

		return itemstack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(this.contents, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.contents.set(slot, stack);
		stack.limitSize(this.getMaxStackSize(stack));
		this.setChanged();
	}

	@Override
	public void setChanged() {
		this.changed = true;
	}
	
	@Override
	public boolean canTakeItem(Container target, int slot, ItemStack stack) {
		if(Container.super.canTakeItem(target, slot, stack)) {
			if(stack == this.stack || (stack.has(DataComponentRegistry.INVENTORY_ITEM_UUID) && stack.get(DataComponentRegistry.INVENTORY_ITEM_UUID).equals(this.stackUUID))) {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean stillValid(Player player) {
		return getContainerStackFromPlayerAndStackUUID(player, this.stackUUID, null) != null;
	}

	@Override
	public void clearContent() {
		this.contents.clear();
		this.setChanged();
	}

	@Override
	public void startOpen(Player player) {
		Container.super.startOpen(player);
		this.trackingPlayers.add(player.getUUID());
		OPEN_CONTAINERS.add(this);
	}
	
	public void saveTo(ItemStack stack) {
		if (this.changed) {
			stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.contents));
		}
	}
	
	public void releaseStack(ItemStack stack, boolean force) {
		saveTo(stack);
		if((force || this.trackingPlayers.isEmpty()) && stack.has(DataComponentRegistry.INVENTORY_ITEM_UUID) && stack.get(DataComponentRegistry.INVENTORY_ITEM_UUID).equals(this.stackUUID)) {
			stack.remove(DataComponentRegistry.INVENTORY_ITEM_UUID);
		}
	}
	
	@Override
	public void stopOpen(Player player) {
		this.trackingPlayers.remove(player.getUUID());
		ItemStack stack = getContainerStackFromPlayerAndStackUUIDOrFallback(player, stackUUID, this.stack);
		releaseStack(stack, false);
		if(this.trackingPlayers.isEmpty()) {
			OPEN_CONTAINERS.remove(this);
		}
		Container.super.stopOpen(player);
	}


	public static ItemStack getContainerStackFromPlayerAndStackUUIDOrFallback(Player player, UUID stackUUID, ItemStack targetStack) {
		if(player == null || stackUUID == null) return targetStack;
		final ItemStack stack = getContainerStackFromPlayerAndStackUUID(player, stackUUID, targetStack);
		return stack == null ? targetStack : stack;
	}
	
	/**
	 * Tries to fetch the target stack from the player's inventory/equipment. Falls back to checking via stack UUID if the exact pointer isn't found
	 * @param player
	 * @param stackUUID
	 * @param targetStack
	 * @return
	 */
	@Nullable
	public static ItemStack getContainerStackFromPlayerAndStackUUID(Player player, UUID stackUUID, @Nullable ItemStack targetStack) {
		if(player == null) return null;
		
		ItemStack foundStack = null;
		
//		//Check if pouch is in equipment
//		IEquipmentCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
//		if (cap != null) {
//			Inventory inv = cap.getInventory(EnumEquipmentInventory.MISC);
//
//			for (int i = 0; i < inv.getContainerSize(); i++) {
//				if (inv.getItem(i) == this.pouch.getContainerStack()) {
//					return true;
//				}
//			}
//		}
		
		Inventory inventory = player.getInventory();
		for(int i = 0; i < inventory.getContainerSize(); ++i) {
			ItemStack stack = inventory.getItem(i);
			if(stack == targetStack) return stack;
			else if(foundStack == null && stack.has(DataComponentRegistry.INVENTORY_ITEM_UUID) && stack.get(DataComponentRegistry.INVENTORY_ITEM_UUID).equals(stackUUID)) {
				foundStack = stack;
				if(targetStack == null) break;
			}
		}
		return foundStack;
	}
	
	@Nullable
	public static ItemContainer getOpenItemContainer(Player player) {
//		if(player == null || !player.hasContainerOpen()) return null;
		if(player == null) return null;
		final UUID playerUUID = player.getUUID();
		for(ItemContainer container : OPEN_CONTAINERS) {
			if(container.trackingPlayers.contains(playerUUID)) {
				return container;
			}
		}
		return null;
	}
}
