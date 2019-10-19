package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.loot.ISharedLootContainer;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;

public class TileEntityChestBetweenlands extends TileEntityChest implements ISharedLootContainer {
	protected StorageID storageId;
	protected boolean isSharedLootTable;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		//Use vanilla behaviour to prevent inventory from resetting when creating double chest
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(2, 2, 2));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}

	@Override
	protected boolean checkLootAndRead(NBTTagCompound compound) {
		this.isSharedLootTable = false;

		if(super.checkLootAndRead(compound)) {
			this.isSharedLootTable = compound.getBoolean("SharedLootTable");

			if(compound.hasKey("StorageID", Constants.NBT.TAG_COMPOUND)) {
				this.storageId = StorageID.readFromNBT(compound.getCompoundTag("StorageID"));
			}

			return true;
		}

		return false;
	}

	@Override
	protected boolean checkLootAndWrite(NBTTagCompound compound) {
		if(super.checkLootAndWrite(compound)) {
			compound.setBoolean("SharedLootTable", this.isSharedLootTable);

			if(this.storageId != null) {
				compound.setTag("StorageID", this.storageId.writeToNBT(new NBTTagCompound()));
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean isSharedLootTable() {
		return this.isSharedLootTable;
	}

	@Override
	public void fillWithLoot(@Nullable EntityPlayer player) {
		this.fillInventoryWithLoot(player);
	}

	@Override
	public boolean fillInventoryWithLoot(@Nullable EntityPlayer player) {
		return TileEntityLootInventory.fillInventoryWithLoot(this.world, this, player, this.lootTableSeed);
	}

	@Override
	public void removeLootTable() {
		if(this.lootTable != null) {
			this.lootTable = null;
			this.markDirty();
		}
	}

	@Override
	public void setLootTable(ResourceLocation lootTable, long lootTableSeed) {
		super.setLootTable(lootTable, lootTableSeed);
		this.isSharedLootTable = false;
		this.markDirty();
	}

	@Override
	public void setSharedLootTable(SharedLootPoolStorage storage, ResourceLocation lootTable, long lootTableSeed) {
		if(!lootTable.equals(this.lootTable)) {
			storage.registerSharedLootInventory(this.pos, lootTable);
		}
		this.storageId = storage.getID();
		this.lootTable = lootTable;
		this.lootTableSeed = lootTableSeed;
		this.isSharedLootTable = true;
		this.markDirty();
	}

	@Override
	public StorageID getSharedLootPoolStorageID() {
		return this.storageId;
	}
}
