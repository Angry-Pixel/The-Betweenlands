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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.loot.ISharedLootContainer;
import thebetweenlands.api.loot.ISharedLootPool;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.SharedLootPoolStorage;

public abstract class TileEntityLootInventory extends TileEntityBasicInventory implements ISharedLootContainer {
	protected StorageID storageId;
	protected ResourceLocation lootTable;
	protected long lootTableSeed;
	protected long sharedLootTableSeed;
	protected boolean isSharedLootTable;

	public TileEntityLootInventory(int invtSize, String name) {
		super(invtSize, name);
	}

	@Override
	public boolean fillInventoryWithLoot(@Nullable EntityPlayer player) {
		return fillInventoryWithLoot(this.world, this, player, this.lootTableSeed);
	}

	public static boolean fillInventoryWithLoot(World world, ISharedLootContainer inventory, @Nullable EntityPlayer player, long seed) {
		ResourceLocation lootTableLocation = inventory.getLootTable();

		if(lootTableLocation != null && world instanceof WorldServer) {
			LootTable lootTable = null;

			if(inventory.isSharedLootTable()) {
				StorageID storageId = inventory.getSharedLootPoolStorageID();

				if(storageId != null) {
					ILocalStorageHandler handler = BetweenlandsWorldStorage.forWorld(world).getLocalStorageHandler();

					ILocalStorage storage = handler.getLocalStorage(storageId);

					boolean foundLootTable = false;

					if(storage instanceof SharedLootPoolStorage) {
						SharedLootPoolStorage sharedStorage = (SharedLootPoolStorage) storage;

						ISharedLootPool sharedLootPool = sharedStorage.getOrCreateSharedLootPool(lootTableLocation);

						if(sharedLootPool != null) {
							lootTable = sharedLootPool.getLootTableView();
							foundLootTable = true;
						}
					}

					if(!foundLootTable) {
						TheBetweenlands.logger.info("Shared loot inventory could not find shared loot pool storage. Storage ID: " + storageId + "." + (inventory instanceof TileEntity ? " " + ((TileEntity) inventory).getPos() : ""));
					}
				} else {
					TheBetweenlands.logger.info("Shared loot inventory has null storage ID.");
				}
			} else {
				lootTable = world.getLootTableManager().getLootTableFromLocation(lootTableLocation);
			}

			if(lootTable != null) {
				inventory.removeLootTable();

				Random random;

				if(seed == 0L) {
					random = new Random();
				} else {
					random = new Random(seed);
				}

				LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) world);

				if(player != null) {
					lootBuilder.withLuck(player.getLuck());
				}

				List<ItemStack> loot = lootTable.generateLootForPools(random, lootBuilder.build());

				fillInventoryRandomly(random, loot, inventory);

				return true;
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

	public void setLootTable(@Nullable ResourceLocation lootTable, long lootTableSeed) {
		this.lootTable = lootTable;
		this.lootTableSeed = lootTableSeed;
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
	public void removeLootTable() {
		if(this.lootTable != null) {
			this.lootTable = null;
			this.markDirty();
		}
	}

	@Override
	public ResourceLocation getLootTable() {
		return this.lootTable;
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

			if(compound.hasKey("StorageID", Constants.NBT.TAG_COMPOUND)) {
				this.storageId = StorageID.readFromNBT(compound.getCompoundTag("StorageID"));
			}

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

			if(this.storageId != null) {
				compound.setTag("StorageID", this.storageId.writeToNBT(new NBTTagCompound()));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void accessSlot(int slot) {
		if(fillInventoryWithLoot(this.world, this, null, this.lootTableSeed)) {
			this.markDirty();
		}
	}

	@Override
	public void clear() {
		if(fillInventoryWithLoot(this.world, this, null, this.lootTableSeed)) {
			this.markDirty();
		}
		super.clear();
	}

	@Override
	public boolean isSharedLootTable() {
		return this.isSharedLootTable;
	}

	@Override
	public StorageID getSharedLootPoolStorageID() {
		return this.storageId;
	}
}
