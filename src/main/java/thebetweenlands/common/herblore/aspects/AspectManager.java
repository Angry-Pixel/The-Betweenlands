package thebetweenlands.common.herblore.aspects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.world.storage.world.BetweenlandsWorldData;
import thebetweenlands.utils.NBTHelper;

public class AspectManager {
	public static enum AspectTier {
		COMMON, UNCOMMON, RARE
	}

	public static enum AspectGroup {
		HERB, GEM_BYRGINAZ, GEM_FIRNALAZ, GEM_FERGALAZ
	}

	public static class AspectEntry {
		public final IAspectType aspect;
		public final AspectTier tier;
		public final AspectGroup type;
		public final float baseAmount;
		public final String aspectName;

		public AspectEntry(IAspectType aspect, AspectTier tier, AspectGroup type, float baseAmount) {
			this.aspect = aspect;
			this.tier = tier;
			this.type = type;
			this.baseAmount = baseAmount;
			this.aspectName = this.aspect.getName();
		}

		public boolean matchEntry(AspectItemEntry itemEntry) {
			return itemEntry.tier == this.tier && itemEntry.type == this.type;
		}
	}

	public static final class AspectItemEntry {
		public final AspectItem item;
		public final AspectTier tier;
		public final AspectGroup type;
		public final float amountMultiplier, amountVaration;
		public final String itemName;

		public AspectItemEntry(AspectItem item, AspectTier tier, AspectGroup type, float amountMultiplier, float amountVariation) {
			this.item = item;
			this.tier = tier;
			this.type = type;
			this.amountMultiplier = amountMultiplier;
			this.amountVaration = amountVariation;
			this.itemName = this.item.item.getUnlocalizedName();
		}
	}

	public static final class AspectItem {
		public final Item item;
		public final int damage;

		public AspectItem(Item item, int damage) {
			this.item = item;
			this.damage = damage;
		}

		public AspectItem(Item item) {
			this(item, -1);
		}

		public AspectItem(ItemStack itemStack) {
			this(itemStack.getItem(), itemStack.getItemDamage());
		}

		public boolean matchItemStack(ItemStack itemStack) {
			return itemStack.getItem().equals(this.item) && (this.damage == -1 || itemStack.getItemDamage() == this.damage);
		}

		public boolean matchItem(Item item) {
			return this.item.equals(item);
		}

		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt.setString("item", this.item.getUnlocalizedName());
			nbt.setInteger("damage", this.damage);
			return nbt;
		}

		public static AspectItem readFromNBT(NBTTagCompound nbt) {
			String itemName = nbt.getString("item");
			int itemDamage = nbt.getInteger("damage");
			AspectItem aspectItem = getItemEntryFromName(itemName, itemDamage);
			return aspectItem;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.damage;
			result = prime * result + ((this.item == null) ? 0 : this.item.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AspectItem other = (AspectItem) obj;
			if (this.damage != other.damage)
				return false;
			if (this.item == null) {
				if (other.item != null)
					return false;
			} else if (!this.item.equals(other.item))
				return false;
			return true;
		}
	}

	private static final List<AspectEntry> registeredAspects = new ArrayList<AspectEntry>();
	private static final Map<AspectItem, List<AspectItemEntry>> registeredItems = new LinkedHashMap<AspectItem, List<AspectItemEntry>>();
	private final Map<AspectItem, List<Aspect>> matchedAspects = new LinkedHashMap<AspectItem, List<Aspect>>();

	public Map<AspectItem, List<Aspect>> getMatchedAspects() {
		return Collections.unmodifiableMap(this.matchedAspects);
	}

	public static Map<AspectItem, List<AspectItemEntry>> getRegisteredItems(){
		return registeredItems;
	}

	public static void registerAspect(AspectEntry entry) {
		registeredAspects.add(entry);
	}

	public static void addStaticAspectsToItem(AspectItemEntry entry) {
		addStaticAspectsToItem(entry, 1);
	}

	public static void addStaticAspectsToItem(AspectItemEntry entry, int aspectCount) {
		AspectItem itemEntry = entry.item;
		for(Entry<AspectItem, List<AspectItemEntry>> e : registeredItems.entrySet()) {
			if(e.getKey().equals(itemEntry)) {
				itemEntry = e.getKey();
				break;
			}
		}
		List<AspectItemEntry> entryList = registeredItems.get(itemEntry);
		if(entryList == null) {
			entryList = new ArrayList<AspectItemEntry>();
			registeredItems.put(entry.item, entryList);
		}
		for(int i = 0; i < aspectCount; i++) {
			entryList.add(entry);
		}
	}

	public static List<AspectEntry> getAspectEntriesFromName(String name) {
		List<AspectEntry> aspectEntries = new ArrayList<AspectEntry>();
		for(AspectEntry aspect : registeredAspects) {
			if(aspect.aspect.getName().equals(name)) {
				aspectEntries.add(aspect);
			}
		}
		return aspectEntries;
	}

	public static AspectItem getItemEntryFromName(String name, int damage) {
		for(AspectItem e : registeredItems.keySet()) {
			if(e.item.getUnlocalizedName().equals(name) && e.damage == damage) {
				return e;
			}
		}
		return null;
	}

	public static long getAspectsSeed(long worldSeed) {
		Random rnd = new Random();
		rnd.setSeed(worldSeed);
		return rnd.nextLong();
	}

	/**
	 * Returns the aspect manager for the specified world
	 * @param world
	 * @return
	 */
	public static AspectManager get(World world) {
		return BetweenlandsWorldData.forWorld(world).getAspectManager();
	}

	/**
	 * Loads all static aspects from an NBT and complements any missing data
	 * @param nbt
	 * @param aspectSeed
	 */
	public void loadAndPopulateStaticAspects(NBTTagCompound nbt, long aspectSeed) {
		if(nbt != null && nbt.hasKey("aspects")) {
			NBTTagCompound aspectCompound = nbt.getCompoundTag("aspects");
			this.loadStaticAspects(aspectCompound);
			//System.out.println("Loaded aspects: ");
			/*for(Entry<ItemEntry, List<ItemAspect>> entry : this.matchedAspects.entrySet()) {
				System.out.println(entry.getKey().item.getUnlocalizedName() + " ");
			}*/
			this.updateAspects(aspectSeed);
			//System.out.println("Updated aspects: ");
			/*for(Entry<ItemEntry, List<ItemAspect>> entry : this.matchedAspects.entrySet()) {
				System.out.println(entry.getKey().item.getUnlocalizedName() + " ");
			}*/
		} else {
			this.generateStaticAspects(aspectSeed);
		}
	}

	private void updateMatchedAspects(AspectItem item, List<Aspect> aspects) {
		Collections.sort(aspects);
		this.matchedAspects.put(item, aspects);
	}

	/**
	 * Loads all static aspects from an NBT
	 * @param nbt
	 */
	public void loadStaticAspects(NBTTagCompound nbt) {
		this.matchedAspects.clear();
		NBTTagList entryList = (NBTTagList) nbt.getTag("entries");
		entryIT:
			for(int i = 0; i < entryList.tagCount(); i++) {
				NBTTagCompound entryCompound = entryList.getCompoundTagAt(i);
				//System.out.println("Getting item entry: " + itemName);
				AspectItem itemEntry = AspectItem.readFromNBT(entryCompound);
				if(itemEntry == null) {
					//System.out.println("Failed getting item entry");
					continue;
				}
				NBTTagList aspectList = (NBTTagList) entryCompound.getTag("aspects");
				List<Aspect> itemAspects = new ArrayList<Aspect>();
				for(int c = 0; c < aspectList.tagCount(); c++) {
					NBTTagCompound aspectCompound = aspectList.getCompoundTagAt(c);
					Aspect aspect = Aspect.readFromNBT(aspectCompound);
					if(aspect == null) {
						//System.out.println("Failed getting aspect");
						continue entryIT;
					}
					itemAspects.add(aspect);
				}
				this.updateMatchedAspects(itemEntry, itemAspects);
			}
	}

	/**
	 * Saves all static aspects to an NBT
	 * @param nbt
	 */
	public void saveStaticAspects(NBTTagCompound nbt) {
		NBTTagCompound aspectCompound = new NBTTagCompound();
		NBTTagList entryList = new NBTTagList();
		for(Entry<AspectItem, List<Aspect>> entry : this.matchedAspects.entrySet()) {
			AspectItem itemEntry = entry.getKey();
			List<Aspect> itemAspects = entry.getValue();
			NBTTagCompound entryCompound = new NBTTagCompound();
			itemEntry.writeToNBT(entryCompound);
			NBTTagList aspectList = new NBTTagList();
			for(Aspect aspect : itemAspects) {
				aspectList.appendTag(aspect.writeToNBT(new NBTTagCompound()));
			}
			entryCompound.setTag("aspects", aspectList);
			entryList.appendTag(entryCompound);
		}
		aspectCompound.setTag("entries", entryList);
		nbt.setTag("aspects", aspectCompound);
	}

	/**
	 * Resets all static aspects
	 * @param aspectSeed
	 */
	public void resetStaticAspects(long aspectSeed) {
		this.generateStaticAspects(aspectSeed);
	}

	private void generateStaticAspects(long seed) {
		this.matchedAspects.clear();
		this.updateAspects(seed);
	}

	private void updateAspects(long seed) {
		Random rnd = new Random();
		rnd.setSeed(seed);

		List<AspectEntry> availableAspects = new ArrayList<AspectEntry>(this.registeredAspects.size());
		availableAspects.addAll(this.registeredAspects);

		List<AspectEntry> possibleAspects = new ArrayList<AspectEntry>();

		for(Entry<AspectItem, List<AspectItemEntry>> item : this.registeredItems.entrySet()) {
			AspectItem itemStack = item.getKey();
			if(this.matchedAspects.containsKey(itemStack)) {
				continue;
			}
			List<AspectItemEntry> itemEntries = item.getValue();
			List<Aspect> itemAspects = new ArrayList<Aspect>(itemEntries.size());
			if(!this.fillItemAspects(itemAspects, itemEntries.size(), itemEntries, possibleAspects, availableAspects, rnd)) {
				this.fillItemAspects(itemAspects, itemEntries.size(), itemEntries, possibleAspects, this.registeredAspects, rnd);
			}
			for(Aspect itemAspect : itemAspects) {
				this.removeAvailableAspect(itemAspect, availableAspects);
			}
			List<Aspect> mergedAspects = new ArrayList<Aspect>(itemAspects.size());
			for(Aspect aspect : itemAspects) {
				Aspect mergedAspect = null;
				for(Aspect ma : mergedAspects) {
					if(ma.type == aspect.type) {
						mergedAspect = ma;
						break;
					}
				}
				if(mergedAspect == null) {
					mergedAspects.add(aspect);
				} else {
					mergedAspects.remove(mergedAspect);
					mergedAspects.add(new Aspect(mergedAspect.type, mergedAspect.getAmount() + aspect.getAmount()));
				}
			}
			this.updateMatchedAspects(itemStack, mergedAspects);
		}
	}

	private void removeAvailableAspect(Aspect itemAspect, List<AspectEntry> availableAspects) {
		Iterator<AspectEntry> it = availableAspects.iterator();
		AspectEntry availableAspect = null;
		while(it.hasNext() && (availableAspect = it.next()) != null) {
			if(availableAspect.aspect.equals(itemAspect.type)) {
				it.remove();
			}
		}
	}

	private boolean fillItemAspects(List<Aspect> itemAspects, int itemAspectsSize, List<AspectItemEntry> itemEntries, List<AspectEntry> possibleAspects,
			List<AspectEntry> availableAspects, Random rnd) {
		boolean foundMatches = false;
		int possibleAspectCount = this.fillPossibleAspects(itemEntries, possibleAspects, availableAspects, null);
		if(possibleAspectCount == 0) return false;
		for(int i = 0; i < itemAspectsSize; i++) {
			if(possibleAspects.size() == 0) {
				this.fillPossibleAspects(itemEntries, possibleAspects, availableAspects, itemAspectsSize < possibleAspectCount ? itemAspects : null);
			}
			AspectEntry randomAspect = possibleAspects.get(rnd.nextInt(possibleAspects.size()));
			possibleAspects.remove(randomAspect);
			AspectItemEntry matchingItemEntry = null;
			Collections.shuffle(itemEntries, rnd);
			for(AspectItemEntry itemEntry : itemEntries) {
				if(randomAspect.matchEntry(itemEntry)) {
					matchingItemEntry = itemEntry;
					break;
				}
			}
			float baseAmount = randomAspect.baseAmount * matchingItemEntry.amountMultiplier;
			itemAspects.add(new Aspect(randomAspect.aspect, baseAmount + baseAmount * matchingItemEntry.amountVaration * (rnd.nextFloat() * 2.0F - 1.0F)));
			foundMatches = true;
		}
		return foundMatches;
	}

	private int fillPossibleAspects(List<AspectItemEntry> itemEntries, List<AspectEntry> possibleAspects, List<AspectEntry> availableAspects, List<Aspect> takenAspects) {
		possibleAspects.clear();
		for(AspectItemEntry itemEntry : itemEntries) {
			for(AspectEntry availableAspect : availableAspects) {
				if(availableAspect.matchEntry(itemEntry) && !possibleAspects.contains(availableAspect) && (takenAspects == null || !takenAspects.contains(availableAspect))) {
					possibleAspects.add(availableAspect);
				}
			}
		}
		return possibleAspects.size();
	}

	/**
	 * Returns a list of all static aspects on an item
	 * @param item
	 * @return
	 */
	public List<Aspect> getStaticAspects(AspectItem item) {
		for(Entry<AspectItem, List<Aspect>> e : this.matchedAspects.entrySet()) {
			if(e.getKey().equals(item)) {
				return e.getValue();
			}
		}
		return new ArrayList<Aspect>();
	}

	/**
	 * Returns a list of all dynamic aspects on an item stack
	 * @param stack
	 * @return
	 */
	public static List<Aspect> getDynamicAspects(ItemStack stack) {
		List<Aspect> aspects = new ArrayList<Aspect>();
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("herbloreAspects")) {
			NBTTagList lst = stack.getTagCompound().getTagList("herbloreAspects", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < lst.tagCount(); i++) {
				NBTTagCompound aspectCompound = lst.getCompoundTagAt(i);
				Aspect itemAspect = Aspect.readFromNBT(aspectCompound);
				if(itemAspect != null)
					aspects.add(itemAspect);
			}
		}
		return aspects;
	}

	/**
	 * Adds dynamic aspects to an item stack
	 * @param stack
	 * @param aspects
	 * @return
	 */
	public static ItemStack addDynamicAspects(ItemStack stack, Aspect... aspects) {
		if(!NBTHelper.getStackNBTSafe(stack).hasKey("herbloreAspects")) {
			stack.getTagCompound().setTag("herbloreAspects", new NBTTagList());
		}
		NBTTagList lst = stack.getTagCompound().getTagList("herbloreAspects", Constants.NBT.TAG_COMPOUND);
		for(Aspect aspect : aspects) {
			NBTTagCompound aspectCompound = new NBTTagCompound();
			aspect.writeToNBT(aspectCompound);
			lst.appendTag(aspectCompound);
		}
		return stack;
	}

	/**
	 * Removes all dynamic aspects from an item stack
	 * @param stack
	 * @return
	 */
	public static ItemStack removeDynamicAspects(ItemStack stack) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("herbloreAspects")) {
			stack.getTagCompound().removeTag("herbloreAspects");
		}
		return stack;
	}

	/**
	 * Removes all dynamic aspects from an item stack that match with one of the specified aspect types
	 * @param stack
	 * @param type
	 * @return
	 */
	public static ItemStack removeDynamicAspects(ItemStack stack, IAspectType... types) {
		for(IAspectType type : types) {
			if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("herbloreAspects")) {
				NBTTagList lst = stack.getTagCompound().getTagList("herbloreAspects", Constants.NBT.TAG_COMPOUND);
				int count = lst.tagCount();
				for(int i = 0; i < count; i++) {
					NBTTagCompound aspectCompound = lst.getCompoundTagAt(i);
					Aspect itemAspect = Aspect.readFromNBT(aspectCompound);
					if(itemAspect != null && itemAspect.type.equals(type)) {
						lst.removeTag(i);
						break;
					}
				}
			}
		}
		return stack;
	}

	/**
	 * Removes all dynamic aspects from an item stack that match with one of the specified aspects
	 * @param stack
	 * @param aspects
	 * @return
	 */
	public static ItemStack removeDynamicAspects(ItemStack stack, Aspect... aspects) {
		for(Aspect aspect : aspects) {
			if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("herbloreAspects")) {
				NBTTagList lst = stack.getTagCompound().getTagList("herbloreAspects", Constants.NBT.TAG_COMPOUND);
				int count = lst.tagCount();
				for(int i = 0; i < count; i++) {
					NBTTagCompound aspectCompound = lst.getCompoundTagAt(i);
					Aspect itemAspect = Aspect.readFromNBT(aspectCompound);
					if(itemAspect != null && itemAspect.equals(aspect)) {
						lst.removeTag(i);
						break;
					}
				}
			}
		}
		return stack;
	}

	/**
	 * Returns a list of all discovered and dynamic aspects on an item. If you specify a player
	 * this will only return the aspects that the player has discovered.
	 * If the player is null this will return all aspects on an item.
	 * @param stack
	 * @param player
	 * @return
	 */
	public List<Aspect> getDiscoveredAspects(ItemStack stack, DiscoveryContainer discoveryContainer) {
		List<Aspect> aspects = new ArrayList<Aspect>();
		if(discoveryContainer == null) {
			aspects.addAll(this.getStaticAspects(new AspectItem(stack)));
		} else {
			aspects.addAll(discoveryContainer.getDiscoveredStaticAspects(this, new AspectItem(stack)));
		}
		aspects.addAll(getDynamicAspects(stack));
		return aspects;
	}

	/**
	 * Returns a list of all discovered and dynamic aspect types on an item. If you specify a player
	 * this will only return the aspect types that the player has discovered.
	 * If the player is null this will return all aspect types on an item.
	 * @param stack
	 * @param player
	 * @return
	 */
	public List<IAspectType> getDiscoveredAspectTypes(ItemStack stack, DiscoveryContainer discoveryContainer) {
		List<IAspectType> aspects = new ArrayList<IAspectType>();
		for(Aspect aspect : this.getDiscoveredAspects(stack, discoveryContainer)) {
			aspects.add(aspect.type);
		}
		return aspects;
	}

	/**
	 * Returns a list of all discovered aspects on an item. If you specify a player
	 * this will only return the aspects that the player has discovered.
	 * If the player is null this will return all aspects on an item.
	 * @param item
	 * @param player
	 * @return
	 */
	public List<Aspect> getDiscoveredAspects(AspectItem item, DiscoveryContainer discoveryContainer) {
		List<Aspect> aspects = new ArrayList<Aspect>();
		if(discoveryContainer == null) {
			aspects.addAll(this.getStaticAspects(item));
		} else {
			aspects.addAll(discoveryContainer.getDiscoveredStaticAspects(this, item));
		}
		return aspects;
	}

	/**
	 * Returns a list of all discovered aspect types on an item. If you specify a player
	 * this will only return the aspect types that the player has discovered.
	 * If the player is null this will return all aspect types on an item.
	 * @param item
	 * @param player
	 * @return
	 */
	public List<IAspectType> getDiscoveredAspectTypes(AspectItem item, DiscoveryContainer discoveryContainer) {
		List<IAspectType> aspects = new ArrayList<IAspectType>();
		for(Aspect aspect : this.getDiscoveredAspects(item, discoveryContainer)) {
			aspects.add(aspect.type);
		}
		return aspects;
	}

	/**
	 * Writes an aspect type to the specified nbt compound
	 * @param type
	 * @param nbt
	 * @return
	 */
	public static NBTTagCompound writeAspectTypeNBT(IAspectType type, NBTTagCompound nbt) {
		nbt.setString("type", type.getName());
		return nbt;
	}

	/**
	 * Reads an aspect type from the specified compound
	 * @param nbt
	 * @return
	 */
	public static IAspectType readAspectTypeFromNBT(NBTTagCompound nbt) {
		return AspectRegistry.getAspectTypeFromName(nbt.getString("type"));
	}

	/**
	 * Returns whether a player has a HL book to write to
	 * @param player
	 * @return
	 */
	public static boolean hasDiscoveryProvider(EntityPlayer player) {
		InventoryPlayer inventory = player.inventory;
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack != null && stack.getItem() instanceof IDiscoveryProvider)
				return true;
		}
		return false;
	}

	/**
	 * Returns a list of all writable discovery containers in the inventory of a player
	 * @param player
	 * @return
	 */
	public static List<DiscoveryContainer> getWritableDiscoveryContainers(EntityPlayer player) {
		List<DiscoveryContainer> containerList = new ArrayList<DiscoveryContainer>();
		InventoryPlayer inventory = player.inventory;
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if(stack != null && stack.getItem() instanceof IDiscoveryProvider) {
				IDiscoveryProvider<ItemStack> provider = (IDiscoveryProvider<ItemStack>) stack.getItem();
				ItemStack providerObj = stack;
				DiscoveryContainer container = provider.getContainer(stack);
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
	public static DiscoveryContainer getMergedDiscoveryContainer(EntityPlayer player) {
		List<DiscoveryContainer> containerList = getWritableDiscoveryContainers(player);
		DiscoveryContainer merged = new DiscoveryContainer();
		for(DiscoveryContainer container : containerList) {
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
		List<DiscoveryContainer> discoveryContainers = AspectManager.getWritableDiscoveryContainers(player);
		for(DiscoveryContainer container : discoveryContainers)
			container.addDiscovery(item, type);
	}
}
