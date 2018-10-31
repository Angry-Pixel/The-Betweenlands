package thebetweenlands.common.loot.shared;

import java.util.ArrayList;
import java.util.List;
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

	protected final ISharedLootPool pool1;
	protected final ISharedLootPool pool2;

	protected LootTable view;

	protected final Object2IntMap<String> removedItems = new Object2IntOpenHashMap<>();
	protected final Object2LongMap<String> poolSeeds = new Object2LongOpenHashMap<>();
	protected final Object2LongMap<String> entrySeeds = new Object2LongOpenHashMap<>();

	private SharedLootPool(ISharedLootPool pool1, ISharedLootPool pool2, ResourceLocation lootTableLocation) {
		this.lootTableLocation = lootTableLocation;
		this.pool1 = pool1;
		this.pool2 = pool2;
	}

	public SharedLootPool(ResourceLocation lootTableLocation, @Nullable LocationStorage location) {
		this(null, null, lootTableLocation);
		this.location = location;
	}

	public SharedLootPool(NBTTagCompound nbt, @Nullable LocationStorage location) {
		this(null, null, null);
		this.location = location;
		this.readFromNBT(nbt);
	}

	private SharedLootPool(ISharedLootPool pool1, ISharedLootPool pool2) {
		this(pool1, pool2, null);
	}

	@Override
	public ResourceLocation getLootTable() {
		return this.lootTableLocation;
	}

	@Override
	public LootTableView getLootTableView() {
		List<LootTableView> views = new ArrayList<>();

		if(this.pool1 == null && this.pool2 == null) {
			views.add(new SharedLootTableView(this));
		}

		if(this.pool1 != null) {
			views.add(this.pool1.getLootTableView());
		}

		if(this.pool2 != null) {
			views.add(this.pool2.getLootTableView());
		}

		return new SharedLootTableView(views);
	}

	@Override
	public ISharedLootPool combine(ISharedLootPool other) {
		return new SharedLootPool(this, other);
	}

	@Override
	public void regenerate() {
		this.removedItems.clear();
		this.entrySeeds.clear();
		this.poolSeeds.clear();

		this.setLocationDirty();
	}

	@Override
	public void refill() {
		this.removedItems.clear();

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
	}
}
