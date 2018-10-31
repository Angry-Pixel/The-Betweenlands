package thebetweenlands.common.tile;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.api.loot.ISharedLootContainer;
import thebetweenlands.api.loot.ISharedLootPool;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.location.LocationStorage;

public abstract class TileEntityLootInventory extends TileEntityBasicInventory implements ISharedLootContainer {
	protected ResourceLocation lootTable;
	protected long lootTableSeed;
	protected boolean isSharedLootTable;

	public TileEntityLootInventory(int invtSize, String name) {
		super(invtSize, name);
	}

	@Override
	public boolean fillWithLoot(@Nullable EntityPlayer player) {
		return fillInventoryWithLoot(this, player, this.lootTableSeed);
	}

	public static boolean fillInventoryWithLoot(ISharedLootContainer inventory, @Nullable EntityPlayer player, long seed) {
		ResourceLocation lootTableLocation = inventory.getLootTable();

		if(lootTableLocation != null && inventory instanceof TileEntity) {
			TileEntity tile = (TileEntity) inventory;

			if(tile.getWorld() instanceof WorldServer) {
				LootTable lootTable = null;

				if(inventory.isSharedLootTable()) {
					List<LocationStorage> locations = LocationStorage.getLocations(tile.getWorld(), new AxisAlignedBB(tile.getPos()));

					for(LocationStorage location : locations) {
						ISharedLootPool sharedLootPool = location.getSharedLootPool(lootTableLocation);

						if(sharedLootPool != null) {
							lootTable = sharedLootPool.getLootTableView(inventory.getMaxSharedLootRolls(), inventory.getMaxSharedLootItems());
							break;
						}
					}
				} else {
					lootTable = tile.getWorld().getLootTableManager().getLootTableFromLocation(lootTableLocation);
				}

				if(lootTable != null) {
					Random random;

					if(seed == 0L) {
						random = new Random();
					} else {
						random = new Random(seed);
					}

					LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) tile.getWorld());

					if(player != null) {
						lootBuilder.withLuck(player.getLuck());
					}

					List<ItemStack> loot = lootTable.generateLootForPools(random, lootBuilder.build());

					fillInventoryRandomly(random, loot, inventory);

					return true;
				}
			}
		}

		return false;
	}

	public static boolean fillInventoryRandomly(Random random, List<ItemStack> loot, IInventory itemHandler)  {
		//Get empty slots
		List<Integer> emptySlots = Lists.<Integer>newArrayList();
		for (int i = 0; i < itemHandler.getSizeInventory(); ++i) {
			if (itemHandler.getStackInSlot(i).isEmpty()) {
				emptySlots.add(Integer.valueOf(i));
			}
		}
		Collections.shuffle(emptySlots, random);

		//Split and shuffle items
		List<ItemStack> splittableStacks = Lists.<ItemStack>newArrayList();
		Iterator<ItemStack> iterator = loot.iterator();
		while (iterator.hasNext()) {
			ItemStack itemstack = (ItemStack)iterator.next();

			if (itemstack.getCount() <= 0) {
				iterator.remove();
			} else if (itemstack.getCount() > 1) {
				splittableStacks.add(itemstack);
				iterator.remove();
			}
		}
		int emptySlotCount = emptySlots.size();

		emptySlotCount = emptySlotCount - loot.size() - splittableStacks.size();

		while (emptySlotCount > 0 && ((List<ItemStack>)splittableStacks).size() > 0) {
			ItemStack itemstack2 = (ItemStack)splittableStacks.remove(MathHelper.getInt(random, 0, splittableStacks.size() - 1));
			int i = MathHelper.getInt(random, 1, itemstack2.getCount() / 2);
			itemstack2.shrink( i);
			ItemStack itemstack1 = itemstack2.copy();
			itemstack1.setCount(i);

			emptySlotCount--;

			if (emptySlotCount > 0 && itemstack2.getCount() > 1 && random.nextBoolean()) {
				splittableStacks.add(itemstack2);
			} else {
				loot.add(itemstack2);
			}

			if (emptySlotCount > 0 && itemstack1.getCount() > 1 && random.nextBoolean()) {
				splittableStacks.add(itemstack1);
			} else {
				loot.add(itemstack1);
			}
		}
		loot.addAll(splittableStacks);
		Collections.shuffle(loot, random);

		//Fill inventory
		for (ItemStack itemstack : loot) {
			if (!itemstack.isEmpty() && emptySlots.isEmpty()) {
				TheBetweenlands.logger.info("Tried to over-fill a container");
				return false;
			}

			if (itemstack.isEmpty()) {
				itemHandler.setInventorySlotContents(((Integer)emptySlots.remove(emptySlots.size() - 1)).intValue(), ItemStack.EMPTY);
			} else {
				itemHandler.setInventorySlotContents(((Integer)emptySlots.remove(emptySlots.size() - 1)).intValue(), itemstack);
			}
		}

		return true;
	}

	public void setLootTable(ResourceLocation lootTable, long lootTableSeed) {
		this.lootTable = lootTable;
		this.lootTableSeed = lootTableSeed;
		this.isSharedLootTable = false;
		this.markDirty();
	}

	public void setSharedLootTable(ResourceLocation lootTable) {
		this.lootTable = lootTable;
		this.lootTableSeed = 0;
		this.isSharedLootTable = true;
		this.markDirty();
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
			this.isSharedLootTable = compound.getBoolean("SharedLootTable");
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

			compound.setBoolean("SharedLootTable", this.isSharedLootTable);

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void accessSlot(int slot) {
		if(fillInventoryWithLoot(this, null, this.lootTableSeed)) {
			this.lootTable = null;
			this.markDirty();
		}
	}

	@Override
	public void clear() {
		if(fillInventoryWithLoot(this, null, this.lootTableSeed)) {
			this.lootTable = null;
			this.markDirty();
		}
		super.clear();
	}

	@Override
	public boolean isSharedLootTable() {
		return this.isSharedLootTable;
	}

	@Override
	public int getMaxSharedLootRolls() {
		return this.getSizeInventory();
	}

	@Override
	public int getMaxSharedLootItems() {
		return Integer.MAX_VALUE;
	}
}
