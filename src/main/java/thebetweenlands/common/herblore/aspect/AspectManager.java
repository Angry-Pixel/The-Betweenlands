package thebetweenlands.common.herblore.aspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.*;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class AspectManager {

	private static final Map<Item, List<AspectItem>> ITEM_TO_ASPECT_ITEMS = new HashMap<>();
	private final Map<AspectItem, List<Aspect>> matchedAspects = new LinkedHashMap<>();

	/**
	 * Returns a list of all generated and matched aspects
	 *
	 * @return
	 */
	public Map<AspectItem, List<Aspect>> getMatchedAspects() {
		return Collections.unmodifiableMap(this.matchedAspects);
	}

	/**
	 * Returns the aspect seed based on the world seed
	 *
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
	 *
	 * @param level
	 * @return
	 */
	@Nullable
	public static AspectManager get(Level level) {
		BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.get(level);
		if (storage != null) {
			return storage.getAspectManager();
		}
		return null;
	}

	/**
	 * Loads all static aspects from an NBT and complements any missing data
	 *
	 * @param nbt
	 * @param aspectSeed
	 */
	public void loadAndPopulateStaticAspects(CompoundTag nbt, HolderLookup.Provider provider, long aspectSeed) {
		if (nbt != null && nbt.contains("entries")) {
			this.loadStaticAspects(nbt, provider);
			//System.out.println("Loaded aspects: ");
			//int loaded = this.matchedAspects.size();
			//for(Entry<AspectItem, List<Aspect>> entry : this.matchedAspects.entrySet()) {
			//	System.out.println(entry.getKey().original.getTranslationKey() + " " + entry.getValue());
			//}
			this.updateAspects(provider, aspectSeed);
			//System.out.println("Updated aspects: ");
			//for(Entry<AspectItem, List<Aspect>> entry : this.matchedAspects.entrySet()) {
			//	System.out.println(entry.getKey().original.getTranslationKey() + " " + entry.getValue());
			//}
			//System.out.println("Total inserted: " + (this.matchedAspects.size() - loaded));
		} else {
			this.generateStaticAspects(provider, aspectSeed);
		}
	}

	private void updateMatchedAspects(AspectItem item, List<Aspect> aspects) {
		Collections.sort(aspects);
		this.matchedAspects.put(item, aspects);
	}

	/**
	 * Loads all static aspects from an NBT
	 *
	 * @param nbt
	 */
	public void loadStaticAspects(CompoundTag nbt, HolderLookup.Provider provider) {
		this.matchedAspects.clear();
		ListTag entryList = (ListTag) nbt.get("entries");
		entryIT:
		for (int i = 0; i < entryList.size(); i++) {
			CompoundTag entryCompound = entryList.getCompound(i);
			//System.out.println("Getting aspect item: " + entryCompound);
			AspectItem itemEntry = readAspectItemFromNBT(entryCompound, provider);
			if (itemEntry == null) {
				//System.out.println("Failed getting aspect item");
				continue;
			}
			//System.out.println("Getting aspect list for item: " + itemEntry);
			ListTag aspectList = (ListTag) entryCompound.get("aspects");
			List<Aspect> itemAspects = new ArrayList<>();
			for (int c = 0; c < aspectList.size(); c++) {
				CompoundTag aspectCompound = aspectList.getCompound(c);
				Aspect aspect = Aspect.readFromNBT(aspectCompound, provider);
				if (aspect == null) {
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
	 *
	 * @param nbt
	 */
	public void saveStaticAspects(CompoundTag nbt, HolderLookup.Provider provider) {
		ListTag entryList = new ListTag();
		for (Entry<AspectItem, List<Aspect>> entry : this.matchedAspects.entrySet()) {
			AspectItem itemEntry = entry.getKey();
			List<Aspect> itemAspects = entry.getValue();
			CompoundTag entryCompound = new CompoundTag();
			writeAspectItemToNbt(itemEntry, entryCompound, provider);
			ListTag aspectList = new ListTag();
			for (Aspect aspect : itemAspects) {
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
	 *
	 * @param tag
	 * @return
	 */
	public static CompoundTag writeAspectItemToNbt(AspectItem aspectItem, CompoundTag tag, HolderLookup.Provider provider) {
		tag.put("item", aspectItem.item().getDefaultInstance().save(provider));
		return tag;
	}

	/**
	 * Reads an aspect item from the specified NBT
	 *
	 * @param tag
	 * @return
	 */
	@Nullable
	public static AspectItem readAspectItemFromNBT(CompoundTag tag, HolderLookup.Provider provider) {
		ItemStack item = tag.contains("item") ? ItemStack.parseOptional(provider, tag.getCompound("item")) : null;
		if (item == null)
			return null;
		return AspectManager.getAspectItem(item);
	}

	/**
	 * Resets all static aspects and generates a new distribution with the specified seed
	 *
	 * @param aspectSeed
	 */
	public void resetStaticAspects(HolderLookup.Provider provider, long aspectSeed) {
		this.generateStaticAspects(provider, aspectSeed);
	}

	private void generateStaticAspects(HolderLookup.Provider provider, long seed) {
		this.matchedAspects.clear();
		this.updateAspects(provider, seed);
	}

	private void updateAspects(HolderLookup.Provider provider, long seed) {
		LegacyRandomSource random = (LegacyRandomSource) RandomSource.create(seed);

		for (AspectItem entry : provider.lookupOrThrow(BLRegistries.Keys.ASPECT_ITEMS).listElements().map(Holder::value).toList()) {
			this.matchedAspects.computeIfAbsent(entry, aspectItem -> aspectItem.calculator().getAspects(provider, random));
		}
	}

	/**
	 * Returns a list of all static aspects on an item
	 *
	 * @return
	 */
	@Nonnull
	public List<Aspect> getStaticAspects(ItemStack stack) {
		AspectItem item = getAspectItem(stack);
		if (item != null)
			return this.getStaticAspects(item);
		return new ArrayList<>();
	}

	/**
	 * Returns a list of all static aspects on the specified item
	 *
	 * @param item
	 * @return
	 */
	@Nonnull
	public List<Aspect> getStaticAspects(AspectItem item) {
		List<Aspect> aspects = this.matchedAspects.get(item);
		if (aspects == null)
			aspects = new ArrayList<>();
		return aspects;
	}

	/**
	 * Returns the matching aspect item for the specified stack
	 *
	 * @param stack
	 * @return
	 */
	@Nullable
	public static AspectItem getAspectItem(ItemStack stack) {
		List<AspectItem> potentialMatches = ITEM_TO_ASPECT_ITEMS.get(stack.getItem());
		if (potentialMatches != null) {
			for (AspectItem aspectItem : potentialMatches) {
				if (stack.is(aspectItem.item()))
					return aspectItem;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all discovered aspects on an item. If you specify a discovery container
	 * this will only return the discovered aspects in the discovery container.
	 * If the discovery container is null this will return all static aspects on an item.
	 *
	 * @param item
	 * @return
	 */
	public List<Aspect> getDiscoveredStaticAspects(AspectItem item, @Nullable DiscoveryContainer<?> discoveryContainer) {
		List<Aspect> aspects = new ArrayList<>();
		if (discoveryContainer == null) {
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
	 *
	 * @param item
	 * @return
	 */
	public List<Holder<AspectType>> getDiscoveredAspectTypes(AspectItem item, DiscoveryContainer<?> discoveryContainer) {
		List<Holder<AspectType>> aspects = new ArrayList<>();
		for (Aspect aspect : this.getDiscoveredStaticAspects(item, discoveryContainer)) {
			aspects.add(aspect.type());
		}
		return aspects;
	}
}
