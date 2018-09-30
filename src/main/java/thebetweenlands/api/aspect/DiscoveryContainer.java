package thebetweenlands.api.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.aspect.DiscoveryContainer.AspectDiscovery.EnumDiscoveryResult;
import thebetweenlands.api.item.IDiscoveryProvider;
import thebetweenlands.common.herblore.aspect.AspectManager;

public class DiscoveryContainer<T> {
	private final Map<AspectItem, List<IAspectType>> discoveredStaticAspects = new HashMap<AspectItem, List<IAspectType>>();
	private final IDiscoveryProvider<T> provider;
	private final T providerObj;

	public DiscoveryContainer(IDiscoveryProvider<T> provider, T providerObj) {
		this.provider = provider;
		this.providerObj = providerObj;
	}

	private DiscoveryContainer() {
		this.provider = null;
		this.providerObj = null;
	}

	/**
	 * Returns a new empty discovery container
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static DiscoveryContainer<?> empty() {
		return new DiscoveryContainer();
	}

	/**
	 * Reads a discovery container from an NBT
	 *
	 * @param nbt
	 * @return
	 */
	public static DiscoveryContainer<?> readFromNBT(NBTTagCompound nbt) {
		DiscoveryContainer<?> container = empty();
		container.updateFromNBT(nbt, false);
		return container;
	}

	/**
	 * Returns how many aspects of the specified item are already discovered
	 * @param item
	 * @return
	 */
	private int getDiscoveryCount(AspectItem item) {
		return !this.discoveredStaticAspects.containsKey(item) ? 0 : this.discoveredStaticAspects.get(item).size();
	}

	private DiscoveryContainer<T> saveContainer() {
		if(this.provider != null && this.providerObj != null)
			this.provider.saveContainer(this.providerObj, this);
		return this;
	}

	/**
	 * Discovers a new aspect of an item and returns the discovery result
	 * @param manager
	 * @param aspectItem
	 * @return
	 */
	public AspectDiscovery discover(AspectManager manager, AspectItem aspectItem) {
		List<Aspect> staticAspects = manager.getStaticAspects(aspectItem);
		if(staticAspects.isEmpty()) {
			return new AspectDiscovery(EnumDiscoveryResult.NONE, null, false);
		}
		int discoveryCount = this.getDiscoveryCount(aspectItem) + 1;
		if(discoveryCount > staticAspects.size()) {
			return new AspectDiscovery(EnumDiscoveryResult.END, null, false);
		}
		Aspect undiscovered = this.getUndiscoveredAspect(staticAspects, this.discoveredStaticAspects.get(aspectItem));
		this.addDiscovery(aspectItem, undiscovered.type);
		if(discoveryCount == staticAspects.size()) {
			this.saveContainer();
			return new AspectDiscovery(EnumDiscoveryResult.LAST, undiscovered, true);
		} else {
			this.saveContainer();
			return new AspectDiscovery(EnumDiscoveryResult.NEW, undiscovered, true);
		}
	}

	public boolean haveDiscoveredAll(AspectManager manager) {
		for(Entry<AspectItem, List<Aspect>> e : manager.getMatchedAspects().entrySet()) {
			List<Aspect> staticAspects = manager.getStaticAspects(e.getKey());
			if (getDiscoveryCount(e.getKey()) < staticAspects.size())
				return false;
		}
		return true;
	}

	/**
	 * Discovers all aspects of all aspect items
	 * @param manager
	 */
	public void discoverAll(AspectManager manager) {
		for(Entry<AspectItem, List<Aspect>> e : manager.getMatchedAspects().entrySet()) {
			for(Aspect a : e.getValue())
				this.addDiscovery(e.getKey(), a.type);
		}
		this.saveContainer();
	}

	/**
	 * Removes all discoveries of the specified item
	 * @param item
	 */
	public void resetDiscovery(AspectItem item) {
		this.discoveredStaticAspects.remove(item);
		this.saveContainer();
	}

	/**
	 * Resets all aspect discoveries of all aspect items
	 */
	public void resetAllDiscovery() {
		this.discoveredStaticAspects.clear();
		this.saveContainer();
	}

	/**
	 * Adds a single aspect discovery
	 * @param item
	 * @param discovered
	 */
	public void addDiscovery(AspectItem item, IAspectType discovered) {
		List<IAspectType> discoveredAspects = this.discoveredStaticAspects.get(item);
		if(discoveredAspects == null) {
			this.discoveredStaticAspects.put(item, discoveredAspects = new ArrayList<IAspectType>());
		}
		if(!discoveredAspects.contains(discovered))
			discoveredAspects.add(discovered);
		this.saveContainer();
	}

	private Aspect getUndiscoveredAspect(List<Aspect> all, List<IAspectType> discovered) {
		if(discovered == null) {
			return all.size() == 0 ? null : all.get(0);
		}
		for(Aspect a : all) {
			if(!discovered.contains(a.type))
				return a;
		}
		return null;
	}

	/**
	 * Writes this discovery container to an NBT
	 * @param nbt
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList discoveryList = new NBTTagList();
		Iterator<Entry<AspectItem, List<IAspectType>>> discoveryIT = this.discoveredStaticAspects.entrySet().iterator();
		while(discoveryIT.hasNext()) {
			Entry<AspectItem, List<IAspectType>> e = discoveryIT.next();
			if(e.getKey() == null || e.getValue() == null || e.getValue().size() == 0) {
				discoveryIT.remove();
				continue;
			}
			NBTTagCompound discoveryEntry = new NBTTagCompound();
			AspectManager.writeAspectItemToNbt(e.getKey(), discoveryEntry);
			NBTTagList aspectListCompound = new NBTTagList();
			for(IAspectType type : e.getValue()) {
				aspectListCompound.appendTag(type.writeToNBT(new NBTTagCompound()));
			}
			discoveryEntry.setTag("aspects", aspectListCompound);
			discoveryList.appendTag(discoveryEntry);
		}
		nbt.setTag("discoveries", discoveryList);
		return nbt;
	}

	/**
	 * Updates this discovery container from an NBT
	 * @param nbt
	 * @return
	 */
	public DiscoveryContainer<T> updateFromNBT(NBTTagCompound nbt, boolean save) {
		this.discoveredStaticAspects.clear();
		NBTTagList discoveryList = nbt.getTagList("discoveries", Constants.NBT.TAG_COMPOUND);
		int discoveryEntries = discoveryList.tagCount();
		for (int i = 0; i < discoveryEntries; i++) {
			NBTTagCompound discoveryEntry = discoveryList.getCompoundTagAt(i);
			AspectItem item = AspectManager.readAspectItemFromNBT(discoveryEntry);
			List<IAspectType> aspectTypeList = new ArrayList<IAspectType>();
			NBTTagList aspectListCompound = discoveryEntry.getTagList("aspects", Constants.NBT.TAG_COMPOUND);
			for (int c = 0; c < aspectListCompound.tagCount(); c++) {
				NBTTagCompound aspectTypeCompound = aspectListCompound.getCompoundTagAt(c);
				aspectTypeList.add(IAspectType.readFromNBT(aspectTypeCompound));
			}
			this.discoveredStaticAspects.put(item, aspectTypeList);
		}
		if (save)
			this.saveContainer();
		return this;
	}

	/**
	 * Merges the discoveries of another container into this container
	 * @param other
	 * @return
	 */
	public DiscoveryContainer<T> mergeDiscoveries(DiscoveryContainer<?> other) {
		boolean changed = false;
		for (Entry<AspectItem, List<IAspectType>> entry : other.discoveredStaticAspects.entrySet()) {
			AspectItem otherItem = entry.getKey();
			List<IAspectType> otherTypes = entry.getValue();
			if (!this.discoveredStaticAspects.containsKey(otherItem)) {
				this.discoveredStaticAspects.put(otherItem, otherTypes);
				changed = true;
			} else {
				List<IAspectType> aspectTypes = this.discoveredStaticAspects.get(otherItem);
				for (IAspectType otherType : otherTypes) {
					if (!aspectTypes.contains(otherType)) {
						aspectTypes.add(otherType);
						changed = true;
					}
				}
			}
		}
		if(changed)
			this.saveContainer();
		return this;
	}

	/**
	 * Returns the list of all the discovered aspects of the specified item.
	 * @param manager
	 * @param item
	 * @return
	 */
	public List<Aspect> getDiscoveredStaticAspects(AspectManager manager, AspectItem item) {
		List<Aspect> discoveredStaticAspects = new ArrayList<Aspect>();
		if(this.discoveredStaticAspects.containsKey(item)) {
			List<IAspectType> discoveredAspects = this.discoveredStaticAspects.get(item);
			List<Aspect> staticAspects = manager.getStaticAspects(item);
			for(Aspect a : staticAspects) {
				if(discoveredAspects.contains(a.type))
					discoveredStaticAspects.add(a);
			}
		}
		return discoveredStaticAspects;
	}

	public static class AspectDiscovery {
		public final EnumDiscoveryResult result;
		public final boolean successful;
		public final Aspect discovered;

		private AspectDiscovery(EnumDiscoveryResult result, Aspect discovered, boolean successful) {
			this.result = result;
			this.discovered = discovered;
			this.successful = successful;
		}

		public static enum EnumDiscoveryResult {
			NONE, NEW, LAST, END
		}
	}

	/**
	 * Returns whether a player has a discovery provider to write to
	 * @param player
	 * @return
	 */
	public static boolean hasDiscoveryProvider(EntityPlayer player) {
		InventoryPlayer inventory = player.inventory;
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem() instanceof IDiscoveryProvider)
				return true;
		}
		return false;
	}

	/**
	 * Returns a list of all writable discovery containers in the inventory of a player
	 * @param player
	 * @return
	 */
	public static List<DiscoveryContainer<?>> getWritableDiscoveryContainers(EntityPlayer player) {
		List<DiscoveryContainer<?>> containerList = new ArrayList<DiscoveryContainer<?>>();
		InventoryPlayer inventory = player.inventory;
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem() instanceof IDiscoveryProvider) {
				@SuppressWarnings("unchecked")
				IDiscoveryProvider<ItemStack> provider = (IDiscoveryProvider<ItemStack>) stack.getItem();
				DiscoveryContainer<?> container = provider.getContainer(stack);
				if(container != null)
					containerList.add(container);
			}
		}
		return containerList;
	}

	/**
	 * Merges all discovery containers in the inventory of a player into one discovery container.
	 * Mostly used to get the combined knowledge of the player.
	 * @param player
	 * @return
	 */
	public static DiscoveryContainer<?> getMergedDiscoveryContainer(EntityPlayer player) {
		List<DiscoveryContainer<?>> containerList = getWritableDiscoveryContainers(player);
		DiscoveryContainer<?> merged = DiscoveryContainer.empty();
		for(DiscoveryContainer<?> container : containerList) {
			if(container != null)
				merged.mergeDiscoveries(container);
		}
		return merged;
	}

	/**
	 * Adds a discovered aspect to all discovery containers of the player
	 * @param player
	 * @param item
	 * @param type
	 */
	public static void addDiscoveryToContainers(EntityPlayer player, AspectItem item, IAspectType type) {
		List<DiscoveryContainer<?>> discoveryContainers = getWritableDiscoveryContainers(player);
		for(DiscoveryContainer<?> container : discoveryContainers)
			container.addDiscovery(item, type);
	}
}
