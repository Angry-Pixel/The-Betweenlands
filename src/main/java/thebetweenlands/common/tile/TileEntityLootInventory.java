package thebetweenlands.common.tile;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.common.TheBetweenlands;

public class TileEntityLootInventory extends TileEntityBasicInventory implements ILootContainer {
	protected ResourceLocation lootTable;
	protected long lootTableSeed;

	public TileEntityLootInventory(int invtSize, String name) {
		super(invtSize, name);
	}

	public void fillWithLoot(@Nullable EntityPlayer player) {
		if (this.lootTable != null) {
			LootTable lootTable = this.world.getLootTableManager().getLootTableFromLocation(this.lootTable);
			this.lootTable = null;
			Random random;

			if (this.lootTableSeed == 0L) {
				random = new Random();
			} else {
				random = new Random(this.lootTableSeed);
			}

			LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) this.world);

			if (player != null) {
				lootBuilder.withLuck(player.getLuck());
			}

			List<ItemStack> loot = lootTable.generateLootForPools(random, lootBuilder.build());

			///// Nothing to see here, does the same as lootTable.fillInventory but fixes a vanilla bug /////

			//Get empty slots
			List<Integer> emptySlots = Lists.<Integer>newArrayList();
			for (int i = 0; i < this.inventoryHandler.getSlots(); ++i) {
				if (this.inventoryHandler.getStackInSlot(i).isEmpty()) {
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
					return;
				}

				if (itemstack.isEmpty()) {
					this.inventoryHandler.setStackInSlot(((Integer)emptySlots.remove(emptySlots.size() - 1)).intValue(), ItemStack.EMPTY);
				} else {
					this.inventoryHandler.setStackInSlot(((Integer)emptySlots.remove(emptySlots.size() - 1)).intValue(), itemstack);
				}
			}
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
