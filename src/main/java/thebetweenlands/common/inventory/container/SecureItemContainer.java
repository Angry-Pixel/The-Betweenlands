package thebetweenlands.common.inventory.container;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.util.ZeroCullingObject2IntHashMap;

/**
 * Hardened version of {@linkplain ItemContainer} against duplication bugs
 * Use for anything the player can directly interact with. {@linkplain ItemContainer} is sufficient otherwise
 */
public class SecureItemContainer extends ItemContainer {

	// Separate tracker for remote (client) worlds, to prevent any race conditions where the server erroneously thinks there are open containers
	// TODO Look into switching to a non-static method of achieving this same thing
	// Use a ThreadLocal so we have a different tracker on both the singleplayer client and server (ThreadLocal is used in a bunch of places in vanilla too)
	public static final ThreadLocal<ContainerTracker> TRACKER = ThreadLocal.<SecureItemContainer.ContainerTracker>withInitial(ContainerTracker::new);

	public static final ContainerTracker getTrackerUnchecked() { return TRACKER.get(); }
	public static final ContainerTracker getTracker() {
		TheBetweenlands.ensureOnGameThread();
		return getTrackerUnchecked();
	}
//	public static final ContainerTracker getTracker(boolean isClientSide) { return getTracker(); }
	public static final ContainerTracker getTracker(Player player) { return getTracker(); }
	
	// For handling edgecases with modded compound containers
	public static class ContainerTracker implements Iterable<SecureItemContainer> {
		// Track number of containers opened on a single bag uuid, the map clears keys when their value is 0
		public final ZeroCullingObject2IntHashMap<UUID> OPEN_CONTAINERS_BY_UUID = new ZeroCullingObject2IntHashMap<UUID>();
		public final Set<SecureItemContainer> OPEN_CONTAINERS = new HashSet<>();
	
		/**
		 * Adds the container to tracking.
		 * @param container the SecureContainer to add to tracking.
		 * @return the total number of open containers that refer to the same pouch (including this one).
		 */
		public int trackContainer(SecureItemContainer container) {
			OPEN_CONTAINERS.add(container);
			return OPEN_CONTAINERS_BY_UUID.increment(container.getContainerStackUUID());
		}

		/**
		 * Removes the container from tracking.
		 * @param container the SecureContainer to remove from tracking.
		 * @return the number of other open containers that refer to the same pouch
		 */
		public int untrackContainer(SecureItemContainer container) {
			OPEN_CONTAINERS.remove(container);
			return OPEN_CONTAINERS_BY_UUID.decrement(container.getContainerStackUUID());
		}

		/**
		 * Returns the number of other containers tracking a UUID, excluding the passed container.
		 * Does not modify the tracked containers
		 * @param container the container to check against.
		 * @return the number of other open containers that refer to the same pouch
		 */
		public int otherTrackingContainers(SecureItemContainer container) {
			int count = OPEN_CONTAINERS_BY_UUID.getInt(container.getContainerStackUUID());
			if(OPEN_CONTAINERS.contains(container)) count--;
			return count;
		}
		
		/**
		 * @param uuid the stack UUID to check the number of open containers for
		 * @return the number of currently tracked containers for stack with the UUID
		 */
		public int getOpenContainerCount(UUID uuid) {
			return OPEN_CONTAINERS_BY_UUID.getInt(uuid);
		}
		
		/**
		 * @return the total number of currently tracked containers
		 */
		public int getOpenContainerCount() {
			return OPEN_CONTAINERS.size();
		}

		@Override
		public Iterator<SecureItemContainer> iterator() {
			return OPEN_CONTAINERS.iterator();
		}
	}
	
	
	
	// Track multiple players accessing the same gui in case of fake (simulated) players or admin menu spectator tools
	protected final Set<UUID> trackingPlayers = new HashSet<UUID>();
	protected Map<UUID, ContainerListener> playerListeners = new HashMap<UUID, ContainerListener>();
	// The ContainerTracker
	protected final ContainerTracker tracker;
	// UUID of the ItemStack, used to identify any copies (ItemStack#copy()) in the player's inventory
	protected UUID stackUUID;
	// Used to inform whether or not to strip the UUID component from the item when the container closes
	private boolean isTracked = false;
	
	public SecureItemContainer(ItemStack stack, int slots) {
		this(stack, slots, getTracker());
	}
	
	public SecureItemContainer(ItemStack stack, int slots, ContainerTracker tracker) {
		super(stack, slots);
		if(!stack.has(DataComponentRegistry.INVENTORY_ITEM_UUID)) {
			stack.set(DataComponentRegistry.INVENTORY_ITEM_UUID, UUID.randomUUID());
		}
		this.stackUUID = stack.get(DataComponentRegistry.INVENTORY_ITEM_UUID);
		this.tracker = tracker;
	}
	
	@Nonnull
	public UUID getContainerStackUUID() {
		return this.stackUUID;
	}

	// Should only be called on the client when it recieves info from the server, so we don't need to worry about adjusting the other trackers
	public void setContainerStackUUID(UUID dataUUID) {
		final ZeroCullingObject2IntHashMap<UUID> containerTracker = tracker.OPEN_CONTAINERS_BY_UUID;
		containerTracker.decrement(this.getContainerStackUUID());
		this.stackUUID = dataUUID;
		this.stack.set(DataComponentRegistry.INVENTORY_ITEM_UUID, dataUUID);
		containerTracker.increment(this.getContainerStackUUID());
	}

	protected void startTracking() {
		tracker.trackContainer(this);
		isTracked = true;
	}
	
	protected void stopTracking() {
		tracker.untrackContainer(this);
		isTracked = false;
	}
	
	@Override
	@Nonnull
	public ItemStack getContainerStackFromPlayer(Player player) {
		return getContainerStackFromPlayerAndStackUUIDOrFallback(player, this.getContainerStackUUID(), super.getContainerStackFromPlayer(player));
	}

	public boolean isStackOrCopyOfStack(ItemStack stack) {
		return stack == this.stack || (stack.has(DataComponentRegistry.INVENTORY_ITEM_UUID) && stack.get(DataComponentRegistry.INVENTORY_ITEM_UUID).equals(this.getContainerStackUUID()));
	}
	
	@Override
	public boolean stillValid(Player player) {
		return super.stillValid(player) && this.trackingPlayers.contains(player.getUUID()) && getContainerStackFromPlayerAndStackUUID(player, this.getContainerStackUUID(), null) != null;
	}
	
	@Override
	public void startOpen(final Player player) {
		super.startOpen(player);
		final UUID playerUUID = player.getUUID();
		this.trackingPlayers.add(playerUUID);
		this.startTracking();
		if(!this.playerListeners.containsKey(playerUUID)) {
			ContainerListener listener = (container) -> {
				this.saveTo(getContainerStackFromPlayer(player));
			};
			this.addListener(listener);
			this.playerListeners.put(playerUUID, listener);
		}
	}

	protected boolean canReleaseUUIDComponent() {
		return this.trackingPlayers.isEmpty() && !this.isTracked && this.tracker.otherTrackingContainers(this) == 0;
	}
	
	@Override
	public void releaseStack(ItemStack stack, boolean force) {
		super.releaseStack(stack, force);
		if((force || canReleaseUUIDComponent()) && isStackOrCopyOfStack(stack)) {
			stack.remove(DataComponentRegistry.INVENTORY_ITEM_UUID);
		}
	}
	
	@Override
	public void stopOpen(Player player) {
		// Has to go before the super.stopOpen(player)
		final UUID playerUUID = player.getUUID();
		this.trackingPlayers.remove(player.getUUID());
		if(this.playerListeners.containsKey(playerUUID)) {
			this.removeListener(this.playerListeners.get(playerUUID));
		}
		if(this.trackingPlayers.isEmpty()) {
			this.stopTracking();
		}
		super.stopOpen(player);
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
	public static SecureItemContainer getOpenItemContainer(Player player) {
//		if(player == null || !player.hasContainerOpen()) return null;
		if(player == null) return null;
		final UUID playerUUID = player.getUUID();
		for(SecureItemContainer container : getTracker(player)) {
			if(container.trackingPlayers.contains(playerUUID)) {
				return container;
			}
		}
		return null;
	}

}
