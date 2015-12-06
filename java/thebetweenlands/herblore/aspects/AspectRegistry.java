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
import thebetweenlands.herblore.aspects.list.AspectArmaniis;
import thebetweenlands.herblore.aspects.list.AspectAzuwynn;
import thebetweenlands.herblore.aspects.list.AspectByariis;
import thebetweenlands.herblore.aspects.list.AspectByrginaz;
import thebetweenlands.herblore.aspects.list.AspectCelawynn;
import thebetweenlands.herblore.aspects.list.AspectDayuniis;
import thebetweenlands.herblore.aspects.list.AspectFergalaz;
import thebetweenlands.herblore.aspects.list.AspectFirnalaz;
import thebetweenlands.herblore.aspects.list.AspectFreiwynn;
import thebetweenlands.herblore.aspects.list.AspectGeoliirgaz;
import thebetweenlands.herblore.aspects.list.AspectOrdaniis;
import thebetweenlands.herblore.aspects.list.AspectYeowynn;
import thebetweenlands.herblore.aspects.list.AspectYihinren;
import thebetweenlands.herblore.aspects.list.AspectYunugaz;
import thebetweenlands.utils.EnumNbtTypes;
import thebetweenlands.world.storage.BetweenlandsWorldData;

public class AspectRegistry {
	public static final IAspect AZUWYNN = new AspectAzuwynn();
	public static final IAspect ARMANIIS = new AspectArmaniis();
	public static final IAspect BYARIIS = new AspectByariis();
	public static final IAspect BYRGINAZ = new AspectByrginaz();
	public static final IAspect CELAWYNN = new AspectCelawynn();
	public static final IAspect DAYUNIIS = new AspectDayuniis();
	public static final IAspect FERGALAZ = new AspectFergalaz();
	public static final IAspect FIRNALAZ = new AspectFirnalaz();
	public static final IAspect FREIWYNN = new AspectFreiwynn();
	public static final IAspect GEOLIIRGAZ = new AspectGeoliirgaz();
	public static final IAspect ORDANIIS = new AspectOrdaniis();
	public static final IAspect YEOWYNN = new AspectYeowynn();
	public static final IAspect YUNUGAZ = new AspectYunugaz();
	public static final IAspect YIHINREN = new AspectYihinren();

	public static enum AspectTier {
		COMMON, UNCOMMON, RARE
	}

	public static enum AspectType {
		HERB, GEM_BYRGINAZ, GEM_FIRNALAZ, GEM_FERGALAZ
	}

	public static class AspectEntry {
		public final IAspect aspect;
		public final AspectTier tier;
		public final AspectType type;
		public float baseAmount;
		public String aspectName;

		public AspectEntry(IAspect aspect, AspectTier tier, AspectType type, float baseAmount) {
			this.aspect = aspect;
			this.tier = tier;
			this.type = type;
			this.baseAmount = baseAmount;
			this.aspectName = this.aspect.getName();
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
		public final String itemName;

		public ItemEntryAspects(ItemEntry item, AspectTier tier, AspectType type, float amountMultiplier, float amountVariation) {
			this.item = item;
			this.tier = tier;
			this.type = type;
			this.amountMultiplier = amountMultiplier;
			this.amountVaration = amountVariation;
			this.itemName = this.item.item.getUnlocalizedName();
		}
	}

	public static final class ItemEntry {
		public final Item item;
		public final int damage;
		private List<ItemEntryAspects> itemAspects;

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

		public List<ItemEntryAspects> getItemAspectEntries() {
			return this.itemAspects;
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

	public void addStaticAspectsToItem(ItemEntryAspects entry) {
		this.addStaticAspectsToItem(entry, 1);
	}

	public void addStaticAspectsToItem(ItemEntryAspects entry, int aspectCount) {
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
		entry.item.itemAspects = entryList;
		for(int i = 0; i < aspectCount; i++) {
			entryList.add(entry);
		}
	}

	public AspectEntry getAspectEntryFromName(String name) {
		for(AspectEntry aspect : this.registeredAspects) {
			if(aspect.aspect.getName().equals(name)) {
				return aspect;
			}
		}
		return null;
	}

	public ItemEntry getItemEntryFromName(String name, int damage) {
		for(ItemEntry e : this.registeredItems.keySet()) {
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

	public void loadStaticAspects(World world) {
		long seed = getAspectsSeed(world.getSeed());
		BetweenlandsWorldData worldData = BetweenlandsWorldData.forWorld(world);
		if(worldData != null && worldData.getData() != null && worldData.getData().hasKey("aspects")) {
			NBTTagCompound aspectsCompound = worldData.getData().getCompoundTag("aspects");
			this.loadStaticAspects(aspectsCompound);
			//System.out.println("Loaded aspects: ");
			/*for(Entry<ItemEntry, List<ItemAspect>> entry : this.matchedAspects.entrySet()) {
				System.out.println(entry.getKey().item.getUnlocalizedName() + " ");
			}*/
			this.updateAspects(seed);
			//System.out.println("Updated aspects: ");
			/*for(Entry<ItemEntry, List<ItemAspect>> entry : this.matchedAspects.entrySet()) {
				System.out.println(entry.getKey().item.getUnlocalizedName() + " ");
			}*/
		} else {
			this.generateStaticAspects(seed);
		}
		NBTTagCompound savedAspects = new NBTTagCompound();
		this.saveStaticAspects(savedAspects);
		worldData.getData().setTag("aspects", savedAspects);
		worldData.markDirty();
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
				ItemEntry itemEntry = this.getItemEntryFromName(itemName, itemDamage);
				if(itemEntry == null) {
					//System.out.println("Failed getting item entry");
					continue;
				}
				NBTTagList aspectList = (NBTTagList) entryCompound.getTag("aspects");
				List<ItemAspect> itemAspects = new ArrayList<ItemAspect>();
				for(int c = 0; c < aspectList.tagCount(); c++) {
					NBTTagCompound aspectCompound = aspectList.getCompoundTagAt(c);
					ItemAspect aspect = ItemAspect.readFromNBT(aspectCompound);
					if(aspect == null) {
						//System.out.println("Failed getting aspect");
						continue entryIT;
					}
					itemAspects.add(aspect);
				}
				this.matchedAspects.put(itemEntry, itemAspects);
			}
	}

	private void saveStaticAspects(NBTTagCompound nbt) {
		NBTTagList entryList = new NBTTagList();
		for(Entry<ItemEntry, List<ItemAspect>> entry : this.matchedAspects.entrySet()) {
			ItemEntry itemEntry = entry.getKey();
			List<ItemAspect> itemAspects = entry.getValue();
			NBTTagCompound entryCompound = new NBTTagCompound();
			entryCompound.setString("item", itemEntry.item.getUnlocalizedName());
			entryCompound.setInteger("damage", itemEntry.damage);
			NBTTagList aspectList = new NBTTagList();
			for(ItemAspect aspect : itemAspects) {
				aspectList.appendTag(aspect.writeToNBT(new NBTTagCompound()));
			}
			entryCompound.setTag("aspects", aspectList);
			entryList.appendTag(entryCompound);
		}
		nbt.setTag("entries", entryList);
	}

	public void resetStaticAspects(World world) {
		long seed = getAspectsSeed(world.getSeed());
		BetweenlandsWorldData worldData = BetweenlandsWorldData.forWorld(world);
		this.generateStaticAspects(seed);
		NBTTagCompound savedAspects = new NBTTagCompound();
		this.saveStaticAspects(savedAspects);
		worldData.getData().setTag("aspects", savedAspects);
		worldData.markDirty();
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

		for(Entry<ItemEntry, List<ItemEntryAspects>> item : this.registeredItems.entrySet()) {
			ItemEntry itemStack = item.getKey();
			if(this.matchedAspects.containsKey(itemStack)) {
				continue;
			}
			List<ItemEntryAspects> itemEntries = item.getValue();
			List<ItemAspect> itemAspects = new ArrayList<ItemAspect>(itemEntries.size());
			if(!this.fillItemAspects(itemAspects, itemEntries.size(), itemEntries, possibleAspects, availableAspects, rnd)) {
				this.fillItemAspects(itemAspects, itemEntries.size(), itemEntries, possibleAspects, this.registeredAspects, rnd);
			}
			for(ItemAspect itemAspect : itemAspects) {
				this.removeAvailableAspect(itemAspect, availableAspects);
			}
			List<ItemAspect> mergedAspects = new ArrayList<ItemAspect>(itemAspects.size());
			for(ItemAspect aspect : itemAspects) {
				ItemAspect mergedAspect = null;
				for(ItemAspect ma : mergedAspects) {
					if(ma.aspect == aspect.aspect) {
						mergedAspect = ma;
						break;
					}
				}
				if(mergedAspect == null) {
					mergedAspects.add(aspect);
				} else {
					mergedAspects.remove(mergedAspect);
					mergedAspects.add(new ItemAspect(mergedAspect.aspect, mergedAspect.amount + aspect.amount));
				}
			}
			this.matchedAspects.put(itemStack, mergedAspects);
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

	private List<ItemAspect> getStaticItemAspects(ItemEntry item) {
		for(Entry<ItemEntry, List<ItemAspect>> e : this.matchedAspects.entrySet()) {
			if(e.getKey().equals(item)) {
				return e.getValue();
			}
		}
		return new ArrayList<ItemAspect>();
	}

	public List<ItemAspect> getItemAspects(ItemStack stack) {
		List<ItemAspect> aspects = new ArrayList<ItemAspect>();
		aspects.addAll(this.getStaticItemAspects(new ItemEntry(stack)));
		if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("herbloreAspects")) {
			NBTTagList lst = stack.stackTagCompound.getTagList("herbloreAspects", EnumNbtTypes.NBT_COMPOUND.ordinal());
			for(int i = 0; i < lst.tagCount(); i++) {
				NBTTagCompound aspectCompound = lst.getCompoundTagAt(i);
				ItemAspect itemAspect = ItemAspect.readFromNBT(aspectCompound);
				aspects.add(itemAspect);
			}
		}
		return aspects;
	}

	public List<IAspect> getAspects(ItemStack stack) {
		List<IAspect> aspects = new ArrayList<IAspect>();
		for(ItemAspect aspect : this.getItemAspects(stack)) {
			aspects.add(aspect.aspect);
		}
		return aspects;
	}

	public ItemStack addItemAspects(ItemStack stack, ItemAspect... aspects) {
		if(stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		}
		if(!stack.stackTagCompound.hasKey("herbloreAspects")) {
			stack.stackTagCompound.setTag("herbloreAspects", new NBTTagList());
		}
		NBTTagList lst = stack.stackTagCompound.getTagList("herbloreAspects", EnumNbtTypes.NBT_COMPOUND.ordinal());
		for(ItemAspect aspect : aspects) {
			NBTTagCompound aspectCompound = new NBTTagCompound();
			aspect.writeToNBT(aspectCompound);
			lst.appendTag(aspectCompound);
		}
		return stack;
	}
}
