package thebetweenlands.common.world.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.loot.ISharedLootPool;
import thebetweenlands.api.network.IGenericDataManagerAccess;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.loot.shared.SharedLootPool;

public class SharedLootPoolStorage extends LocalStorageImpl {
	private Map<ResourceLocation, SharedLootPool> sharedLootPools = new HashMap<>();
	private TObjectIntMap<ResourceLocation> lootInventories = new TObjectIntHashMap<>();

	private long lootSeed;

	public SharedLootPoolStorage(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region);
	}

	public SharedLootPoolStorage(IWorldStorage worldStorage, StorageID id, LocalRegion region, long lootSeed) {
		super(worldStorage, id, region);
		this.lootSeed = lootSeed;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		//No bounding box required.
		//Loot inventories are directly linked to the storage by storage ID.
		return null;
	}

	@Override
	public IGenericDataManagerAccess getDataManager() {
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);

		if(!this.sharedLootPools.isEmpty()) {
			NBTTagList sharedLootPoolsNbt = new NBTTagList();

			for(SharedLootPool sharedLootPool : this.sharedLootPools.values()) {
				sharedLootPoolsNbt.appendTag(sharedLootPool.writeToNBT(new NBTTagCompound()));
			}

			nbt.setTag("sharedLootPools", sharedLootPoolsNbt);
		}

		if(!this.lootInventories.isEmpty()) {
			NBTTagList lootInventoriesNbt = new NBTTagList();
			this.lootInventories.forEachEntry((table, count) -> {
				NBTTagCompound entryNbt = new NBTTagCompound();
				entryNbt.setString("table", table.toString());
				entryNbt.setInteger("count", count);
				lootInventoriesNbt.appendTag(entryNbt);
				return true;
			});
			nbt.setTag("lootInventories", lootInventoriesNbt);
		}

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.sharedLootPools.clear();
		NBTTagList sharedLootPoolsNbt = nbt.getTagList("sharedLootPools", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < sharedLootPoolsNbt.tagCount(); i++) {
			SharedLootPool sharedLootPool = new SharedLootPool(sharedLootPoolsNbt.getCompoundTagAt(i), this);
			ResourceLocation lootTable = sharedLootPool.getLootTable();
			if(lootTable != null) {
				this.sharedLootPools.put(lootTable, sharedLootPool);
			}
		}

		this.lootInventories.clear();
		NBTTagList lootInventoriesNbt = nbt.getTagList("lootInventories", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < lootInventoriesNbt.tagCount(); i++) {
			NBTTagCompound entryNbt = lootInventoriesNbt.getCompoundTagAt(i);
			this.lootInventories.put(new ResourceLocation(entryNbt.getString("table")), entryNbt.getInteger("count"));
		}
	}

	public int getSharedLootInventories(ResourceLocation lootTable) {
		return this.lootInventories.get(lootTable);
	}

	public void registerSharedLootInventory(BlockPos pos, ResourceLocation lootTable) {
		this.lootInventories.adjustOrPutValue(lootTable, 1, 1);

		//Make sure this storage is linked to the chunk the loot inventory is in
		this.linkChunk(this.getWorldStorage().getWorld().getChunk(pos));

		this.markDirty();
	}

	@Nullable
	public ISharedLootPool getSharedLootPool(ResourceLocation lootTable) {
		return this.sharedLootPools.get(lootTable);
	}

	@Nullable
	public ISharedLootPool getOrCreateSharedLootPool(ResourceLocation lootTable) {
		ISharedLootPool pool = this.getSharedLootPool(lootTable);

		if(pool != null) {
			return pool;
		}

		SharedLootPool newPool = new SharedLootPool(lootTable, this.lootSeed, this);
		this.sharedLootPools.put(lootTable, newPool);
		this.markDirty();

		return newPool;
	}

	@Nullable
	public ISharedLootPool removeSharedLootPool(ResourceLocation lootTable) {
		ISharedLootPool pool = this.sharedLootPools.remove(lootTable);
		if(pool != null) {
			this.markDirty();
		}

		return pool;
	}

	public Set<ResourceLocation> getSharedLootPoolKeys() {
		return this.sharedLootPools.keySet();
	}
}
