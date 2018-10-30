package thebetweenlands.common.loot.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.api.loot.ISharedLootPool;
import thebetweenlands.api.loot.LootTableView;

public class SharedLootPool implements ISharedLootPool {
	protected final ResourceLocation lootTableLocation;

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

	public SharedLootPool(ResourceLocation lootTableLocation) {
		this(null, null, lootTableLocation);
	}

	private SharedLootPool(ISharedLootPool pool1, ISharedLootPool pool2) {
		this(pool1, pool2, null);
	}

	@Override
	public ResourceLocation getLootTable() {
		return this.lootTableLocation;
	}

	@Override
	public LootTableView getLootTableView(int maxRolls, int maxSlots, int maxItems) {
		List<LootTableView> views = new ArrayList<>();

		if(this.pool1 == null && this.pool2 == null) {
			views.add(new SharedLootTableView(this, maxRolls, maxSlots, maxItems));
		}

		if(this.pool1 != null) {
			views.add(this.pool1.getLootTableView(maxRolls, maxSlots, maxItems));
		}

		if(this.pool2 != null) {
			views.add(this.pool2.getLootTableView(maxRolls, maxSlots, maxItems));
		}

		return new SharedLootTableView(views, maxRolls, maxSlots, maxItems);
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
	}

	@Override
	public int getRemovedItems(LootPool pool, int poolRoll, LootEntry entry) {
		//TODO Is poolName#poolRoll#entryName a sensible key?
		return this.removedItems.getInt(String.format("%s#%d#%s", pool.getName(), poolRoll, entry.getEntryName()));
	}

	@Override
	public void setRemovedItems(LootPool pool, int poolRoll, LootEntry entry, int count) {
		this.removedItems.put(String.format("%s#%d#%s", pool.getName(), poolRoll, entry.getEntryName()), count);
	}

	@Override
	public long getLootPoolSeed(Random rand, LootPool pool, int poolRoll) {
		String key = String.format("%s#%d", pool.getName(), poolRoll);
		long seed;
		if(this.poolSeeds.containsKey(key)) {
			seed = this.poolSeeds.get(key);
		} else {
			this.poolSeeds.put(key, seed = rand.nextLong());
		}
		return seed;
	}

	@Override
	public long getLootEntrySeed(Random rand, LootPool pool, int poolRoll, LootEntry entry) {
		String key = String.format("%s#%d#%s", pool.getName(), poolRoll, entry.getEntryName());
		long seed;
		if(this.entrySeeds.containsKey(key)) {
			seed = this.entrySeeds.get(key);
		} else {
			this.entrySeeds.put(key, seed = rand.nextLong());
		}
		return seed;
	}

	//TODO Saving/Loading removedItems, poolSeeds, entrySeeds, seed NBT
}
