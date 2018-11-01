package thebetweenlands.api.loot;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.storage.loot.ILootContainer;

public interface ISharedLootContainer extends ILootContainer, IInventory {
	public boolean isSharedLootTable();

	public void removeLootTable();

	public boolean fillInventoryWithLoot(@Nullable EntityPlayer player);
}
