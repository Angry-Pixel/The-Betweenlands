package thebetweenlands.common.tile;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public class TileEntityLootInventory extends TileEntityBasicInventory implements ILootContainer {
	public ResourceLocation lootTable;
	public long lootTableSeed;

	public TileEntityLootInventory(int invtSize, String name) {
		super(invtSize, name);
	}

	public void fillWithLoot(@Nullable EntityPlayer player) {
		if (this.lootTable != null) {
			LootTable loottable = this.worldObj.getLootTableManager().getLootTableFromLocation(this.lootTable);
			this.lootTable = null;
			Random random;

			if (this.lootTableSeed == 0L) {
				random = new Random();
			} else {
				random = new Random(this.lootTableSeed);
			}

			LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) this.worldObj);

			if (player != null) {
				lootBuilder.withLuck(player.getLuck());
			}

			loottable.fillInventory(this, random, lootBuilder.build());
		}
	}

	public void setLootTable(ResourceLocation lootTable, long lootTableSeed) {
		this.lootTable = lootTable;
		this.lootTableSeed = lootTableSeed;
	}

	@Override
	public ResourceLocation getLootTable() {
		return lootTable;
	}

	@Override
	protected void writeInventoryNBT(NBTTagCompound nbt) {
		if(!this.checkLootAndWrite(nbt)) {
			super.writeInventoryNBT(nbt);
		}
	}

	@Override
	protected void readInventoryNBT(NBTTagCompound nbt) {
		if(!this.checkLootAndRead(nbt)) {
			super.readInventoryNBT(nbt);
		}
	}

	/**
	 * Tries to read a loot table from the NBT. Returns true if found loot table != null
	 * @param compound
	 * @return
	 */
	protected boolean checkLootAndRead(NBTTagCompound compound) {
		if (compound.hasKey("LootTable", 8)) {
			this.lootTable = new ResourceLocation(compound.getString("LootTable"));
			this.lootTableSeed = compound.getLong("LootTableSeed");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Writes the loot table to NBT. Returns true if loot table != null
	 * @param compound
	 * @return
	 */
	protected boolean checkLootAndWrite(NBTTagCompound compound) {
		if (this.lootTable != null) {
			compound.setString("LootTable", this.lootTable.toString());

			if (this.lootTableSeed != 0L) {
				compound.setLong("LootTableSeed", this.lootTableSeed);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void accessSlot(int slot) {
		this.fillWithLoot(null);
	}

	@Override
	public void clear() {
		this.fillWithLoot(null);
		super.clear();
	}
}
