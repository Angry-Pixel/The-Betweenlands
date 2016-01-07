package thebetweenlands.herblore.aspects;

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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import thebetweenlands.utils.EnumNBTTypes;
import thebetweenlands.world.storage.BetweenlandsWorldData;

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
		private List<AspectItemEntry> itemAspects;

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

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof AspectItem) {
				AspectItem itemEntry = (AspectItem)obj;
				return itemEntry.item.equals(this.item) && itemEntry.damage == this.damage;
			}
			return super.equals(obj);
		}

		public List<AspectItemEntry> getAspectItemEntries() {
			return this.itemAspects;
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
		entry.item.itemAspects = entryList;
		for(int i = 0; i < aspectCount; i++) {
			entryList.add(entry);
		}
	}

	public static IAspectType getAspectTypeFromName(String name) {
		for(IAspectType type : AspectRegistry.ASPECT_TYPES) {
			if(type.getName().equals(name)) return type;
		}
		return null;
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

	public static AspectManager get(World world) {
		return BetweenlandsWorldData.forWorld(world).getAspectManager();
	}

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

	public void loadStaticAspects(NBTTagCompound nbt) {
		this.matchedAspects.clear();
		NBTTagList entryList = (NBTTagList) nbt.getTag("entries");
		entryIT:
			for(int i = 0; i < entryList.tagCount(); i++) {
				NBTTagCompound entryCompound = entryList.getCompoundTagAt(i);
				String itemName = entryCompound.getString("item");
				int itemDamage = entryCompound.getInteger("damage");
				//System.out.println("Getting item entry: " + itemName);
				AspectItem itemEntry = this.getItemEntryFromName(itemName, itemDamage);
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
				this.matchedAspects.put(itemEntry, itemAspects);
			}
	}

	public void saveStaticAspects(NBTTagCompound nbt) {
		NBTTagCompound aspectCompound = new NBTTagCompound();
		NBTTagList entryList = new NBTTagList();
		for(Entry<AspectItem, List<Aspect>> entry : this.matchedAspects.entrySet()) {
			AspectItem itemEntry = entry.getKey();
			List<Aspect> itemAspects = entry.getValue();
			NBTTagCompound entryCompound = new NBTTagCompound();
			entryCompound.setString("item", itemEntry.item.getUnlocalizedName());
			entryCompound.setInteger("damage", itemEntry.damage);
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
					if(ma.aspect == aspect.aspect) {
						mergedAspect = ma;
						break;
					}
				}
				if(mergedAspect == null) {
					mergedAspects.add(aspect);
				} else {
					mergedAspects.remove(mergedAspect);
					mergedAspects.add(new Aspect(mergedAspect.aspect, mergedAspect.amount + aspect.amount));
				}
			}
			this.matchedAspects.put(itemStack, mergedAspects);
		}
	}

	private void removeAvailableAspect(Aspect itemAspect, List<AspectEntry> availableAspects) {
		Iterator<AspectEntry> it = availableAspects.iterator();
		AspectEntry availableAspect = null;
		while(it.hasNext() && (availableAspect = it.next()) != null) {
			if(availableAspect.aspect.equals(itemAspect.aspect)) {
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

	private List<Aspect> getStaticAspects(AspectItem item) {
		for(Entry<AspectItem, List<Aspect>> e : this.matchedAspects.entrySet()) {
			if(e.getKey().equals(item)) {
				return e.getValue();
			}
		}
		return new ArrayList<Aspect>();
	}

	public List<Aspect> getAspects(ItemStack stack) {
		List<Aspect> aspects = new ArrayList<Aspect>();
		aspects.addAll(this.getStaticAspects(new AspectItem(stack)));
		if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("herbloreAspects")) {
			NBTTagList lst = stack.stackTagCompound.getTagList("herbloreAspects", EnumNBTTypes.NBT_COMPOUND.ordinal());
			for(int i = 0; i < lst.tagCount(); i++) {
				NBTTagCompound aspectCompound = lst.getCompoundTagAt(i);
				Aspect itemAspect = Aspect.readFromNBT(aspectCompound);
				if(itemAspect != null) {
					aspects.add(itemAspect);
				}
			}
		}
		return aspects;
	}

	public List<IAspectType> getAspectTypes(ItemStack stack) {
		List<IAspectType> aspects = new ArrayList<IAspectType>();
		for(Aspect aspect : this.getAspects(stack)) {
			aspects.add(aspect.aspect);
		}
		return aspects;
	}

	public ItemStack addAspects(ItemStack stack, Aspect... aspects) {
		if(stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		}
		if(!stack.stackTagCompound.hasKey("herbloreAspects")) {
			stack.stackTagCompound.setTag("herbloreAspects", new NBTTagList());
		}
		NBTTagList lst = stack.stackTagCompound.getTagList("herbloreAspects", EnumNBTTypes.NBT_COMPOUND.ordinal());
		for(Aspect aspect : aspects) {
			NBTTagCompound aspectCompound = new NBTTagCompound();
			aspect.writeToNBT(aspectCompound);
			lst.appendTag(aspectCompound);
		}
		return stack;
	}
}
