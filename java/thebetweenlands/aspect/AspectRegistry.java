package thebetweenlands.aspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.aspect.aspects.IAspect;

public class AspectRegistry {
	public static enum AspectTier {
		COMMON, UNCOMMON, RARE
	}

	public static enum AspectType {
		HERB
	}

	public static class AspectEntry {
		public final IAspect aspect;
		public final AspectTier tier;
		public final AspectType type;
		public float baseAmount;

		public AspectEntry(IAspect aspect, AspectTier tier, AspectType type, float baseAmount) {
			this.aspect = aspect;
			this.tier = tier;
			this.type = type;
			this.baseAmount = baseAmount;
		}

		public boolean matchEntry(ItemEntryAspects itemEntry) {
			return itemEntry.tier == this.tier && itemEntry.type == this.type;
		}
	}

	public static final class ItemEntryAspects {
		public final ItemEntry item;
		public final AspectTier tier;
		public final AspectType type;
		public final float amountMultiplier, amountVaration;

		public ItemEntryAspects(ItemEntry item, AspectTier tier, AspectType type, float amountMultiplier, float amountVariation) {
			this.item = item;
			this.tier = tier;
			this.type = type;
			this.amountMultiplier = amountMultiplier;
			this.amountVaration = amountVariation;
		}
	}

	public static final class ItemEntry {
		public final Item item;
		public final int damage;

		public ItemEntry(Item item, int damage) {
			this.item = item;
			this.damage = damage;
		}

		public ItemEntry(Item item) {
			this(item, -1);
		}

		public ItemEntry(ItemStack itemStack) {
			this(itemStack.getItem(), itemStack.getItemDamage());
		}

		public boolean matchItemStack(ItemStack itemStack) {
			return itemStack.getItem().equals(this.item) && (this.damage == -1 || itemStack.getItemDamage() == this.damage);
		}

		public boolean matchItem(Item item) {
			return this.item.equals(item);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof ItemEntry) {
				ItemEntry itemEntry = (ItemEntry)obj;
				return itemEntry.item.equals(this.item) && itemEntry.damage == this.damage;
			}
			return super.equals(obj);
		}
	}

	private final List<AspectEntry> registeredAspects = new ArrayList<AspectEntry>();
	private final Map<ItemEntry, List<ItemEntryAspects>> registeredItems = new LinkedHashMap<ItemEntry, List<ItemEntryAspects>>();
	private final Map<ItemEntry, List<ItemAspect>> matchedAspects = new LinkedHashMap<ItemEntry, List<ItemAspect>>();

	public Map<ItemEntry, List<ItemAspect>> getMatchedAspects() {
		return Collections.unmodifiableMap(this.matchedAspects);
	}

	public void registerAspect(AspectEntry entry) {
		this.registeredAspects.add(entry);
	}

	public void addAspectsToItem(ItemEntryAspects entry) {
		ItemEntry itemEntry = entry.item;
		for(Entry<ItemEntry, List<ItemEntryAspects>> e : this.registeredItems.entrySet()) {
			if(e.getKey().equals(itemEntry)) {
				itemEntry = e.getKey();
				break;
			}
		}
		List<ItemEntryAspects> entryList = this.registeredItems.get(itemEntry);
		if(entryList == null) {
			entryList = new ArrayList<ItemEntryAspects>();
			this.registeredItems.put(entry.item, entryList);
		}
		entryList.add(entry);
	}

	public void loadAspects(long seed) {
		//TODO: Currently depends on the order of the registered item and aspect entries

		Random rnd = new Random();
		rnd.setSeed(seed);

		this.matchedAspects.clear();

		List<AspectEntry> availableAspects = new ArrayList<AspectEntry>(this.registeredAspects.size());
		availableAspects.addAll(this.registeredAspects);

		List<AspectEntry> possibleAspects = new ArrayList<AspectEntry>();

		for(Entry<ItemEntry, List<ItemEntryAspects>> item : this.registeredItems.entrySet()) {
			ItemEntry itemStack = item.getKey();
			List<ItemEntryAspects> itemEntries = item.getValue();
			List<ItemAspect> itemAspects = new ArrayList<ItemAspect>(itemEntries.size());
			if(!this.fillItemAspects(itemAspects, itemEntries.size(), itemEntries, possibleAspects, availableAspects, rnd)) {
				this.fillItemAspects(itemAspects, itemEntries.size(), itemEntries, possibleAspects, this.registeredAspects, rnd);
			}
			for(ItemAspect itemAspect : itemAspects) {
				this.removeAvailableAspect(itemAspect, availableAspects);
			}
			this.matchedAspects.put(itemStack, itemAspects);
		}
	}

	private void removeAvailableAspect(ItemAspect itemAspect, List<AspectEntry> availableAspects) {
		Iterator<AspectEntry> it = availableAspects.iterator();
		AspectEntry availableAspect = null;
		while(it.hasNext() && (availableAspect = it.next()) != null) {
			if(availableAspect.aspect.equals(itemAspect.aspect)) {
				it.remove();
			}
		}
	}

	private boolean fillItemAspects(List<ItemAspect> itemAspects, int itemAspectsSize, List<ItemEntryAspects> itemEntries, List<AspectEntry> possibleAspects,
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
			ItemEntryAspects matchingItemEntry = null;
			Collections.shuffle(itemEntries, rnd);
			for(ItemEntryAspects itemEntry : itemEntries) {
				if(randomAspect.matchEntry(itemEntry)) {
					matchingItemEntry = itemEntry;
					break;
				}
			}
			float baseAmount = randomAspect.baseAmount * matchingItemEntry.amountMultiplier;
			itemAspects.add(new ItemAspect(randomAspect.aspect, baseAmount + baseAmount * matchingItemEntry.amountVaration * (rnd.nextFloat() * 2.0F - 1.0F)));
			foundMatches = true;
		}
		//TODO: Merge down same aspects into one aspect with combined amount
		return foundMatches;
	}

	private int fillPossibleAspects(List<ItemEntryAspects> itemEntries, List<AspectEntry> possibleAspects, List<AspectEntry> availableAspects, List<ItemAspect> takenAspects) {
		possibleAspects.clear();
		for(ItemEntryAspects itemEntry : itemEntries) {
			for(AspectEntry availableAspect : availableAspects) {
				if(availableAspect.matchEntry(itemEntry) && !possibleAspects.contains(availableAspect) && (takenAspects == null || !takenAspects.contains(availableAspect))) {
					possibleAspects.add(availableAspect);
				}
			}
		}
		return possibleAspects.size();
	}

	public List<ItemAspect> getAspects(ItemEntry item) {
		for(Entry<ItemEntry, List<ItemAspect>> e : this.matchedAspects.entrySet()) {
			if(e.getKey().equals(item)) {
				return e.getValue();
			}
		}
		return null;
	}
}
