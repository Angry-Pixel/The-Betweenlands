package thebetweenlands.common.loot.shared;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import thebetweenlands.api.loot.ISharedLootPool;

public class SharedLootPool implements ISharedLootPool {
	protected final ResourceLocation lootTableLocation;

	protected final ISharedLootPool pool1;
	protected final ISharedLootPool pool2;

	protected LootTable view;

	protected final Object2IntMap<String> removedEntries = new Object2IntOpenHashMap<>();

	private SharedLootPool(ISharedLootPool pool1, ISharedLootPool pool2, ResourceLocation lootTableLocation) {
		this.lootTableLocation = lootTableLocation;
		this.pool1 = pool1;
		this.pool2 = pool2;
	}

	public SharedLootPool(ResourceLocation lootTableLocation) {
		this(null, null, lootTableLocation);
	}

	public SharedLootPool(ISharedLootPool pool1, ISharedLootPool pool2) {
		this(pool1, pool2, null);
	}

	@Override
	public ResourceLocation getLootTable() {
		return this.lootTableLocation;
	}

	@Nullable
	public LootTable getSharedLootTableInstance(LootTableManager manager) {
		if(this.lootTableLocation != null) {
			return manager.getLootTableFromLocation(this.lootTableLocation);
		}
		return null;
	}

	@Override
	public LootTable getLootTableView(int maxRolls) {
		List<LootTable> views = new ArrayList<>();

		if(this.pool1 == null && this.pool2 == null) {
			views.add(new SharedLootTableView(this, maxRolls));
		}

		if(this.pool1 != null) {
			views.add(this.pool1.getLootTableView(maxRolls));
		}

		if(this.pool2 != null) {
			views.add(this.pool2.getLootTableView(maxRolls));
		}

		return new SharedLootTableView(views, maxRolls);
	}

	@Override
	public ISharedLootPool combine(ISharedLootPool other) {
		return new SharedLootPool(this, other);
	}

	@Override
	public void regenerate() {
		this.removedEntries.clear();
	}

	int getRemovedEntries(LootPool pool, LootEntry entry) {
		//TODO Is poolName#entryName a sensible key?
		return this.removedEntries.getInt(String.format("%s#%s", pool.getName(), entry.getEntryName()));
	}

	void setRemovedEntries(LootPool pool, LootEntry entry, int number) {
		this.removedEntries.put(String.format("%s#%s", pool.getName(), entry.getEntryName()), number);
	}

	//TODO Saving/Loading NBT
}
