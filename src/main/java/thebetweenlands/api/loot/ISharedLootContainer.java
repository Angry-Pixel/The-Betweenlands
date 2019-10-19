package thebetweenlands.api.loot;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ILootContainer;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;

public interface ISharedLootContainer extends ILootContainer, IInventory {
	@Nullable
	public StorageID getSharedLootPoolStorageID();

	public boolean isSharedLootTable();

	public void removeLootTable();

	public boolean fillInventoryWithLoot(@Nullable EntityPlayer player);
	
	/**
	 * Sets the shared loot table of this loot container and registers/links it to the specified
	 * shared loot pool storage ({@link SharedLootPoolStorage#registerSharedLootInventory(net.minecraft.util.math.BlockPos, ResourceLocation)}.
	 * @param storage
	 * @param lootTable
	 * @param lootTableSeed
	 */
	public void setSharedLootTable(SharedLootPoolStorage storage, ResourceLocation lootTable, long lootTableSeed);
}
