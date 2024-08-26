package thebetweenlands.common.herblore.aspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectItem;
import thebetweenlands.api.aspect.AspectType;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class AspectManager {
	public enum AspectTier {
		COMMON(0),
		UNCOMMON(1),
		RARE(2);

		public final int id;

		AspectTier(int id) {
			this.id = id;
		}
	}

	public enum AspectGroup {
		HERB(0),
		GEM_BYRGINAZ(1),
		GEM_FIRNALAZ(2),
		GEM_FERGALAZ(3),
		SAP_SPIT(4),
		SLUDGE_WORM_DUNGEON(5);

		public final int id;

		AspectGroup(int id) {
			this.id = id;
		}
	}

	public record AspectEntry(Holder<AspectType> aspect, int tier, int group, int baseAmount) {

		private AspectEntry(Holder<AspectType> aspect, AspectTier tier, AspectGroup group, int baseAmount) {
			this(aspect, tier.id, group.id, baseAmount);
		}

		@Override
		public String toString() {
			return "AspectEntry[Aspect=" + this.aspect().getKey().location() + ", Tier=" + this.tier() + ", Group=" + this.group() + ", BaseAmount=" + this.baseAmount() + "]";
		}
	}

	public static final class AspectItemEntry {
		public final AspectItem item;
		public final int tier;
		public final int group;
		public final float amountMultiplier, amountVaration;
		public final ResourceLocation itemName;

		private AspectItemEntry(ItemStack item, IItemStackMatcher matcher, AspectTier tier, AspectGroup group, float amountMultiplier, float amountVariation) {
			this(item, matcher, tier.id, group.id, amountMultiplier, amountVariation);
		}

		private AspectItemEntry(ItemStack item, IItemStackMatcher matcher, int tier, int group, float amountMultiplier, float amountVariation) {
			this.item = new AspectItem(item, matcher);
			this.tier = tier;
			this.group = group;
			this.amountMultiplier = amountMultiplier;
			this.amountVaration = amountVariation;
			this.itemName = BuiltInRegistries.ITEM.getKey(item.getItem());
		}

		/**
		 * Returns whether the tiers and groups are matching
		 * @param aspectEntry
		 * @return
		 */
		public boolean matchEntry(AspectEntry aspectEntry) {
			return aspectEntry.tier == this.tier && aspectEntry.group == this.group;
		}
	}

	private static final List<AspectEntry> REGISTERED_ASPECTS = new ArrayList<>();
	private static final Map<AspectItem, List<AspectItemEntry>> REGISTERED_ITEMS = new LinkedHashMap<>();
	private static final Map<Item, List<AspectItem>> ITEM_TO_ASPECT_ITEMS = new HashMap<>();

	private final Map<AspectItem, List<Aspect>> matchedAspects = new LinkedHashMap<>();

	/**
	 * Returns a list of all generated and matched aspects
	 * @return
	 */
	public Map<AspectItem, List<Aspect>> getMatchedAspects() {
		return Collections.unmodifiableMap(this.matchedAspects);
	}

	/**
	 * Returns all registered aspect items and their possible aspects
	 * @return
	 */
	public static Map<AspectItem, List<AspectItemEntry>> getRegisteredItems(){
		return REGISTERED_ITEMS;
	}

	/**
	 * Registers an aspect
	 * @param aspect
	 * @param tier
	 * @param type
	 * @param baseAmount
	 */
	public static void registerAspect(Holder<AspectType> aspect, AspectTier tier, AspectGroup type, int baseAmount) {
		registerAspect(aspect, tier.id, type.id, baseAmount);
	}

	/**
	 * Registers an aspect
	 * @param aspect
	 * @param tier
	 * @param type
	 * @param baseAmount
	 */
	public static void registerAspect(Holder<AspectType> aspect, int tier, int type, int baseAmount) {
		REGISTERED_ASPECTS.add(new AspectEntry(aspect, tier, type, baseAmount));
	}

	/**
	 * Adds static aspects to the specified entry
	 * @param item Item
	 * @param matcher The matcher returns whether an aspect is applicable to a certain item stack
	 * @param tier Aspect tier
	 * @param type Aspect type
	 * @param amountMultiplier Aspect amount multiplier
	 * @param amountVariation Random aspect amount variation
	 */
	public static void addStaticAspectsToItem(ItemStack item, IItemStackMatcher matcher, AspectTier tier, AspectGroup type, float amountMultiplier, float amountVariation) {
		addStaticAspectsToItem(item, matcher, tier, type, amountMultiplier, amountVariation, 1);
	}

	/**
	 * Adds static aspects to the specified entry
	 * @param item Item
	 * @param matcher The matcher returns whether an aspect is applicable to a certain item stack
	 * @param tier Aspect tier
	 * @param type Aspect type
	 * @param amountMultiplier Aspect amount multiplier
	 * @param amountVariation Random aspect amount variation
	 * @param aspectCount How often the item should have this entry
	 */
	public static void addStaticAspectsToItem(ItemStack item, IItemStackMatcher matcher, AspectTier tier, AspectGroup type, float amountMultiplier, float amountVariation, int aspectCount) {
		addStaticAspectsToItem(item, matcher, tier.id, type.id, amountMultiplier, amountVariation, aspectCount);
	}

	/**
	 * Adds static aspects to the specified entry
	 * @param item Item
	 * @param matcher The matcher returns whether an aspect is applicable to a certain item stack
	 * @param tier Aspect tier
	 * @param type Aspect type
	 * @param amountMultiplier Aspect amount multiplier
	 * @param amountVariation Random aspect amount variation
	 */
	public static void addStaticAspectsToItem(ItemStack item, IItemStackMatcher matcher, int tier, int type, float amountMultiplier, float amountVariation) {
		addStaticAspectsToItem(item, matcher, tier, type, amountMultiplier, amountVariation, 1);
	}

	/**
	 * Adds static aspects to the specified entry
	 * @param item Item
	 * @param matcher The matcher returns whether an aspect is applicable to a certain item stack
	 * @param tier Aspect tier
	 * @param type Aspect type
	 * @param amountMultiplier Aspect amount multiplier
	 * @param amountVariation Random aspect amount variation
	 * @param aspectCount How often the item should have this entry
	 */
	public static void addStaticAspectsToItem(ItemStack item, IItemStackMatcher matcher, int tier, int type, float amountMultiplier, float amountVariation, int aspectCount) {
		AspectItemEntry entry = new AspectItemEntry(item, matcher, tier, type, amountMultiplier, amountVariation);
		AspectItem itemEntry = entry.item;

		//Check if aspect item already exists, if so use that
		List<AspectItem> aspectItems = ITEM_TO_ASPECT_ITEMS.get(item.getItem());
		if(aspectItems != null) {
			for(AspectItem aspectItem : aspectItems) {
				if(aspectItem.matches(item)) {
					itemEntry = aspectItem;
					break;
				}
			}
		}

		//Register item and possible aspects
		List<AspectItemEntry> entryList = REGISTERED_ITEMS.computeIfAbsent(itemEntry, k -> new ArrayList<>());
		for(int i = 0; i < aspectCount; i++) {
			entryList.add(entry);
		}

		//Register aspect item and matcher
		if(aspectItems == null) {
			ITEM_TO_ASPECT_ITEMS.put(item.getItem(), aspectItems = new ArrayList<>());
		}
		aspectItems.add(entry.item);
	}

	/**
	 * Returns the aspect seed based on the world seed
	 * @param worldSeed
	 * @return
	 */
	public static long getAspectsSeed(long worldSeed) {
		Random rnd = new Random();
		rnd.setSeed(worldSeed);
		return rnd.nextLong();
	}

	/**
	 * Returns the aspect manager for the specified world
	 * @param level
	 * @return
	 */
	public static AspectManager get(Level level) {
		return BetweenlandsWorldStorage.getOrThrow(level).getAspectManager();
	}

	/**
	 * Loads all static aspects from an NBT and complements any missing data
	 * @param nbt
	 * @param aspectSeed
	 */
	public void loadAndPopulateStaticAspects(CompoundTag nbt, HolderLookup.Provider provider, long aspectSeed) {
		if(nbt != null && nbt.contains("entries")) {
			this.loadStaticAspects(nbt, provider);
			//System.out.println("Loaded aspects: ");
			//int loaded = this.matchedAspects.size();
			//for(Entry<AspectItem, List<Aspect>> entry : this.matchedAspects.entrySet()) {
			//	System.out.println(entry.getKey().original.getTranslationKey() + " " + entry.getValue());
			//}
			this.updateAspects(aspectSeed);
			//System.out.println("Updated aspects: ");
			//for(Entry<AspectItem, List<Aspect>> entry : this.matchedAspects.entrySet()) {
			//	System.out.println(entry.getKey().original.getTranslationKey() + " " + entry.getValue());
			//}
			//System.out.println("Total inserted: " + (this.matchedAspects.size() - loaded));
		} else {
			this.generateStaticAspects(aspectSeed);
		}
	}

	private void updateMatchedAspects(AspectItem item, List<Aspect> aspects) {
//		Collections.sort(aspects);
		this.matchedAspects.put(item, aspects);
	}

	/**
	 * Loads all static aspects from an NBT
	 * @param nbt
	 */
	public void loadStaticAspects(CompoundTag nbt, HolderLookup.Provider provider) {
		this.matchedAspects.clear();
		ListTag entryList = (ListTag) nbt.get("entries");
		entryIT:
			for(int i = 0; i < entryList.size(); i++) {
				CompoundTag entryCompound = entryList.getCompound(i);
				//System.out.println("Getting aspect item: " + entryCompound);
				AspectItem itemEntry = readAspectItemFromNBT(entryCompound, provider);
				if(itemEntry == null) {
					//System.out.println("Failed getting aspect item");
					continue;
				}
				//System.out.println("Getting aspect list for item: " + itemEntry);
				ListTag aspectList = (ListTag) entryCompound.get("aspects");
				List<Aspect> itemAspects = new ArrayList<>();
				for(int c = 0; c < aspectList.size(); c++) {
					CompoundTag aspectCompound = aspectList.getCompound(c);
					Aspect aspect = Aspect.readFromNBT(aspectCompound, provider);
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
	public void saveStaticAspects(CompoundTag nbt, HolderLookup.Provider provider) {
		ListTag entryList = new ListTag();
		for(Entry<AspectItem, List<Aspect>> entry : this.matchedAspects.entrySet()) {
			AspectItem itemEntry = entry.getKey();
			List<Aspect> itemAspects = entry.getValue();
			CompoundTag entryCompound = new CompoundTag();
			writeAspectItemToNbt(itemEntry, entryCompound, provider);
			ListTag aspectList = new ListTag();
			for(Aspect aspect : itemAspects) {
				aspectList.add(aspect.writeToNBT(new CompoundTag(), provider));
			}
			entryCompound.put("aspects", aspectList);
			entryList.add(entryCompound);
			//System.out.println("Saved item aspects: " + entryCompound);
		}
		nbt.put("entries", entryList);
	}

	/**
	 * Writes an aspect item to the specified NBT
	 * @param tag
	 * @return
	 */
	public static CompoundTag writeAspectItemToNbt(AspectItem aspectItem, CompoundTag tag, HolderLookup.Provider provider) {
		tag.put("item", aspectItem.getOriginal().save(provider));
		return tag;
	}

	/**
	 * Reads an aspect item from the specified NBT
	 * @param tag
	 * @return
	 */
	@Nullable
	public static AspectItem readAspectItemFromNBT(CompoundTag tag, HolderLookup.Provider provider) {
		ItemStack item = tag.contains("item") ? ItemStack.parseOptional(provider, tag.getCompound("item")) : null;
		if(item == null)
			return null;
		return AspectManager.getAspectItem(item);
	}

	/**
	 * Resets all static aspects and generates a new distribution with the specified seed
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

		List<AspectEntry> availableAspects = new ArrayList<>(REGISTERED_ASPECTS.size());
		availableAspects.addAll(REGISTERED_ASPECTS);

		List<AspectEntry> possibleAspects = new ArrayList<>();

		for(Entry<AspectItem, List<AspectItemEntry>> item : REGISTERED_ITEMS.entrySet()) {
			AspectItem itemStack = item.getKey();

			if(this.matchedAspects.containsKey(itemStack)) {
				continue;
			}

			//AspectItemEntry's must be grouped by group ID so that each group's
			//aspect requirements are fulfilled individually
			Map<Integer, List<AspectItemEntry>> itemEntriesByGroup = new LinkedHashMap<>();

			for(AspectItemEntry itemEntry : item.getValue()) {
				List<AspectItemEntry> groupItemEntries = itemEntriesByGroup.computeIfAbsent(itemEntry.group, k -> new ArrayList<>());

				groupItemEntries.add(itemEntry);
			}

			for(List<AspectItemEntry> itemEntries : itemEntriesByGroup.values()) {
				List<Aspect> itemAspects = new ArrayList<>(itemEntries.size());
				if(!this.fillItemAspects(itemAspects, itemEntries.size(), itemEntries, possibleAspects, availableAspects, rnd)) {
					this.fillItemAspects(itemAspects, itemEntries.size(), itemEntries, possibleAspects, REGISTERED_ASPECTS, rnd);
				}

				for(Aspect itemAspect : itemAspects) {
					this.removeAvailableAspect(itemAspect, availableAspects);
				}

				List<Aspect> mergedAspects = this.matchedAspects.get(itemStack);
				if(mergedAspects == null) {
					mergedAspects = new ArrayList<>(itemAspects.size());
				}

				for(Aspect aspect : itemAspects) {
					Aspect mergedAspect = null;
					for(Aspect ma : mergedAspects) {
						if(ma.type() == aspect.type()) {
							mergedAspect = ma;
							break;
						}
					}
					if(mergedAspect == null) {
						mergedAspects.add(aspect);
					} else {
						mergedAspects.remove(mergedAspect);
						mergedAspects.add(new Aspect(mergedAspect.type(), mergedAspect.amount() + aspect.amount()));
					}
				}

				this.updateMatchedAspects(itemStack, mergedAspects);
			}
		}
	}

	private void removeAvailableAspect(Aspect itemAspect, List<AspectEntry> availableAspects) {
		Iterator<AspectEntry> it = availableAspects.iterator();
		AspectEntry availableAspect;
		while(it.hasNext() && (availableAspect = it.next()) != null) {
			if(availableAspect.aspect.equals(itemAspect.type())) {
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
			if(possibleAspects.isEmpty()) {
				this.fillPossibleAspects(itemEntries, possibleAspects, availableAspects, itemAspectsSize < possibleAspectCount ? itemAspects : null);
			}
			AspectEntry randomAspect = possibleAspects.get(rnd.nextInt(possibleAspects.size()));
			possibleAspects.remove(randomAspect);
			AspectItemEntry matchingItemEntry = null;
			Collections.shuffle(itemEntries, rnd);
			for(AspectItemEntry itemEntry : itemEntries) {
				if(itemEntry.matchEntry(randomAspect)) {
					matchingItemEntry = itemEntry;
					break;
				}
			}
			float baseAmount = randomAspect.baseAmount * matchingItemEntry.amountMultiplier;
			itemAspects.add(new Aspect(randomAspect.aspect, (int)(baseAmount + baseAmount * matchingItemEntry.amountVaration * (rnd.nextFloat() * 2.0F - 1.0F))));
			foundMatches = true;
		}
		return foundMatches;
	}

	private int fillPossibleAspects(List<AspectItemEntry> itemEntries, List<AspectEntry> possibleAspects, List<AspectEntry> availableAspects, List<Aspect> takenAspects) {
		possibleAspects.clear();
		for(AspectItemEntry itemEntry : itemEntries) {
			for(AspectEntry availableAspect : availableAspects) {
				if(itemEntry.matchEntry(availableAspect) && !possibleAspects.contains(availableAspect)) {
					if(takenAspects == null) {
						possibleAspects.add(availableAspect);
					} else {
						boolean isTaken = false;
						for(Aspect takenAspect : takenAspects) {
							if(takenAspect.type() == availableAspect.aspect) {
								isTaken = true;
								break;
							}
						}
						if(!isTaken) {
							possibleAspects.add(availableAspect);
						}
					}
				}
			}
		}
		return possibleAspects.size();
	}

	/**
	 * Returns a list of all static aspects on an item
	 * @return
	 */
	@Nonnull
	public List<Aspect> getStaticAspects(ItemStack stack) {
		AspectItem item = getAspectItem(stack);
		if(item != null)
			return this.getStaticAspects(item);
		return new ArrayList<>();
	}

	/**
	 * Returns a list of all static aspects on the specified item
	 * @param item
	 * @return
	 */
	@Nonnull
	public List<Aspect> getStaticAspects(AspectItem item) {
		List<Aspect> aspects = this.matchedAspects.get(item);
		if(aspects == null)
			aspects = new ArrayList<>();
		return aspects;
	}

	/**
	 * Returns the matching aspect item for the specified stack
	 * @param stack
	 * @return
	 */
	@Nullable
	public static AspectItem getAspectItem(ItemStack stack) {
		List<AspectItem> potentialMatches = ITEM_TO_ASPECT_ITEMS.get(stack.getItem());
		if(potentialMatches != null) {
			for(AspectItem aspectItem : potentialMatches) {
				if(aspectItem.matches(stack))
					return aspectItem;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all discovered aspects on an item. If you specify a discovery container
	 * this will only return the discovered aspects in the discovery container.
	 * If the discovery container is null this will return all static aspects on an item.
	 * @param item
	 * @return
	 */
	public List<Aspect> getDiscoveredStaticAspects(AspectItem item, @Nullable DiscoveryContainer<?> discoveryContainer) {
		List<Aspect> aspects = new ArrayList<>();
		if(discoveryContainer == null) {
			aspects.addAll(this.getStaticAspects(item));
		} else {
			aspects.addAll(discoveryContainer.getDiscoveredStaticAspects(this, item));
		}
		return aspects;
	}

	/**
	 * Returns a list of all discovered aspect types on an item. If you specify a discovery container
	 * this will only return the discovered aspect types in the discovery container.
	 * If the discovery container is null this will return all static aspect types on an item.
	 * @param item
	 * @return
	 */
	public List<Holder<AspectType>> getDiscoveredAspectTypes(AspectItem item, DiscoveryContainer<?> discoveryContainer) {
		List<Holder<AspectType>> aspects = new ArrayList<>();
		for(Aspect aspect : this.getDiscoveredStaticAspects(item, discoveryContainer)) {
			aspects.add(aspect.type());
		}
		return aspects;
	}
}
