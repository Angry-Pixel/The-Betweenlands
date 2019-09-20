package thebetweenlands.common.loot.shared;

import java.util.Random;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.loot.ISharedLootPool;
import thebetweenlands.api.loot.LootTableView;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class SharedLootPool implements ISharedLootPool {
	protected ResourceLocation lootTableLocation;

	protected LocationStorage location;

	protected LootTable view;

	protected final Object2IntMap<String> removedItems = new Object2IntOpenHashMap<>();
	protected final Object2LongMap<String> poolSeeds = new Object2LongOpenHashMap<>();
	protected final Object2LongMap<String> entrySeeds = new Object2LongOpenHashMap<>();

	protected long sharedLootSeed;

	protected int guaranteeCounter;

	private SharedLootPool(ResourceLocation lootTableLocation, long seed) {
		this.lootTableLocation = lootTableLocation;
		this.sharedLootSeed = seed;
	}

	public SharedLootPool(ResourceLocation lootTableLocation, long seed, @Nullable LocationStorage location) {
		this(lootTableLocation, seed);
		this.location = location;
	}

	public SharedLootPool(NBTTagCompound nbt, @Nullable LocationStorage location) {
		this(null, 0);
		this.location = location;
		this.readFromNBT(nbt);
	}

	@Override
	public ResourceLocation getLootTable() {
		return this.lootTableLocation;
	}

	@Override
	public LootTableView getLootTableView() {
		return new SharedLootTableView(this);
	}

	@Override
	public void regenerate() {
		this.removedItems.clear();
		this.entrySeeds.clear();
		this.poolSeeds.clear();
		this.guaranteeCounter = 0;

		this.setLocationDirty();
	}

	@Override
	public void refill() {
		this.removedItems.clear();
		this.guaranteeCounter = 0;

		this.setLocationDirty();
	}

	@Override
	public int getRemovedItems(String pool, int poolRoll, String entry) {
		return this.removedItems.getInt(String.format("%s#%d#%s", pool, poolRoll, entry));
	}

	@Override
	public void setRemovedItems(String pool, int poolRoll, String entry, int count) {
		this.removedItems.put(String.format("%s#%d#%s", pool, poolRoll, entry), count);

		this.setLocationDirty();
	}

	@Override
	public long getLootPoolSeed(Random rand, String pool, int poolRoll) {
		String key = String.format("%s#%d", pool, poolRoll);
		long seed;
		if(this.poolSeeds.containsKey(key)) {
			seed = this.poolSeeds.get(key);
		} else {
			this.poolSeeds.put(key, seed = rand.nextLong());

			this.setLocationDirty();
		}
		return seed;
	}

	@Override
	public long getLootEntrySeed(Random rand, String pool, int poolRoll, String entry) {
		String key = String.format("%s#%d#%s", pool, poolRoll, entry);
		long seed;
		if(this.entrySeeds.containsKey(key)) {
			seed = this.entrySeeds.get(key);
		} else {
			this.entrySeeds.put(key, seed = rand.nextLong());

			this.setLocationDirty();
		}
		return seed;
	}

	@Override
	public int getGuaranteeCounter() {
		return this.guaranteeCounter;
	}

	void incrementGuaranteeCounter() {
		this.guaranteeCounter++;
		this.setLocationDirty();
	}

	@Override
	public float getGuaranteePercentage() {
		int lootInventories = this.location != null ? this.location.getLootInventories(this.lootTableLocation) : 0;
		return lootInventories <= 0 ? 1.0F : Math.min((float)this.getGuaranteeCounter() / (float)lootInventories, 1.0F);
	}

	protected void setLocationDirty() {
		if(this.location != null) {
			this.location.markDirty();
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if(!this.removedItems.isEmpty()) {
			NBTTagCompound removedItemsNbt = new NBTTagCompound();
			for(Object2IntMap.Entry<String> entry : this.removedItems.object2IntEntrySet()) {
				removedItemsNbt.setInteger(entry.getKey(), entry.getIntValue());
			}
			nbt.setTag("removedItems", removedItemsNbt);
		}

		if(!this.poolSeeds.isEmpty()) {
			NBTTagCompound poolSeedsNbt = new NBTTagCompound();
			for(Object2LongMap.Entry<String> entry : this.poolSeeds.object2LongEntrySet()) {
				poolSeedsNbt.setLong(entry.getKey(), entry.getLongValue());
			}
			nbt.setTag("poolSeeds", poolSeedsNbt);
		}

		if(!this.entrySeeds.isEmpty()) {
			NBTTagCompound entrySeedsNbt = new NBTTagCompound();
			for(Object2LongMap.Entry<String> entry : this.entrySeeds.object2LongEntrySet()) {
				entrySeedsNbt.setLong(entry.getKey(), entry.getLongValue());
			}
			nbt.setTag("entrySeeds", entrySeedsNbt);
		}

		if(this.lootTableLocation != null) {
			nbt.setString("lootTable", this.lootTableLocation.toString());
		}

		nbt.setInteger("generatedLootTables", this.guaranteeCounter);

		nbt.setLong("sharedLootSeed", this.sharedLootSeed);

		return nbt;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		this.removedItems.clear();
		NBTTagCompound removedItemsNbt = nbt.getCompoundTag("removedItems");
		for(String key : removedItemsNbt.getKeySet()) {
			this.removedItems.put(key, removedItemsNbt.getInteger(key));
		}

		this.poolSeeds.clear();
		NBTTagCompound poolSeedsNbt = nbt.getCompoundTag("poolSeeds");
		for(String key : poolSeedsNbt.getKeySet()) {
			this.poolSeeds.put(key, poolSeedsNbt.getLong(key));
		}

		this.entrySeeds.clear();
		NBTTagCompound entrySeedsNbt = nbt.getCompoundTag("entrySeeds");
		for(String key : entrySeedsNbt.getKeySet()) {
			this.entrySeeds.put(key, entrySeedsNbt.getLong(key));
		}

		this.lootTableLocation = null;
		if(nbt.hasKey("lootTable", Constants.NBT.TAG_STRING)) {
			this.lootTableLocation = new ResourceLocation(nbt.getString("lootTable"));
		}

		this.guaranteeCounter = nbt.getInteger("generatedLootTables");

		this.sharedLootSeed = nbt.getLong("sharedLootSeed");
	}

	@Override
	public long getLootTableSeed() {
		return this.sharedLootSeed;
	}
}
