package thebetweenlands.tileentities;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import thebetweenlands.blocks.container.BlockBLDualFurnace;
import thebetweenlands.items.ItemGeneric;
import thebetweenlands.items.ItemGeneric.EnumItemGeneric;

public class TileEntityBLDualFurnace extends TileEntity implements ISidedInventory
{
	private static final int[] slotsTop = new int[] {0, 3};
	private static final int[] slotsBottom = new int[] {2, 1, 5, 4};
	private static final int[] slotsSides = new int[] {1, 4};
	private ItemStack[] furnaceItemStacks = new ItemStack[8];
	public int furnaceBurnTime;
	public int currentItemBurnTime;
	public int furnaceCookTime;
	public int furnaceBurnTime2;
	public int currentItemBurnTime2;
	public int furnaceCookTime2;
	private String customName;

	@Override
	public int getSizeInventory() {
		return furnaceItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return furnaceItemStacks[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (furnaceItemStacks[slot] != null) {
			ItemStack itemstack;

			if (furnaceItemStacks[slot].stackSize <= amount) {
				itemstack = furnaceItemStacks[slot];
				furnaceItemStacks[slot] = null;
				return itemstack;
			}
			else {
				itemstack = furnaceItemStacks[slot].splitStack(amount);
				if (furnaceItemStacks[slot].stackSize == 0)
					furnaceItemStacks[slot] = null;
				return itemstack;
			}
		}
		else

			return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (furnaceItemStacks[slot] != null) {
			ItemStack itemstack = furnaceItemStacks[slot];
			furnaceItemStacks[slot] = null;
			return itemstack;
		}
		else
			return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		furnaceItemStacks[slot] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
			itemstack.stackSize = getInventoryStackLimit();
	}

	@Override
	public String getInventoryName() {
		return hasCustomInventoryName() ? customName : "container.dualFurnaceBL";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return customName != null && customName.length() > 0;
	}

	public void getStackDisplayName(String name) {
		customName = name;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		furnaceItemStacks = new ItemStack[getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte slot = nbttagcompound1.getByte("Slot");
			if (slot >= 0 && slot < furnaceItemStacks.length)
				furnaceItemStacks[slot] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
		}
		furnaceBurnTime = nbt.getShort("BurnTime");
		furnaceCookTime = nbt.getShort("CookTime");
		currentItemBurnTime = getItemBurnTime(furnaceItemStacks[1]);

		furnaceBurnTime2 = nbt.getShort("BurnTime2");
		furnaceCookTime2 = nbt.getShort("CookTime2");
		currentItemBurnTime2 = getItemBurnTime(furnaceItemStacks[4]);

		if (nbt.hasKey("CustomName", 8))
			customName = nbt.getString("CustomName");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setShort("BurnTime", (short)furnaceBurnTime);
		nbt.setShort("CookTime", (short)furnaceCookTime);

		nbt.setShort("BurnTime2", (short)furnaceBurnTime2);
		nbt.setShort("CookTime2", (short)furnaceCookTime2);

		NBTTagList nbttaglist = new NBTTagList();
		for (int slot = 0; slot < furnaceItemStacks.length; ++slot) {
			if (furnaceItemStacks[slot] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)slot);
				furnaceItemStacks[slot].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbt.setTag("Items", nbttaglist);
		if (hasCustomInventoryName())
			nbt.setString("CustomName", customName);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int count) {
		return furnaceCookTime * count / 200;
	}

	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int remainingTime) {
		if (currentItemBurnTime == 0)
			currentItemBurnTime = 200;

		return furnaceBurnTime * remainingTime / currentItemBurnTime;
	}

	public boolean isBurning() {
		return furnaceBurnTime > 0;
	}

	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled2(int count) {
		return furnaceCookTime2 * count / 200;
	}

	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled2(int remainingTime) {
		if (currentItemBurnTime2 == 0)
			currentItemBurnTime2 = 200;

		return furnaceBurnTime2 * remainingTime / currentItemBurnTime2;
	}

	public boolean isBurning2() {
		return furnaceBurnTime2 > 0;
	}

	@Override
	public void updateEntity() {
		boolean isBurning1 = furnaceBurnTime > 0;
		boolean isDirty1 = false;
		boolean isBurning2 = furnaceBurnTime2 > 0;
		boolean isDirty2 = false;

		if (furnaceBurnTime > 0)
			--furnaceBurnTime;

		if (furnaceBurnTime2 > 0)
			--furnaceBurnTime2;

		if (!worldObj.isRemote) {
			if (furnaceBurnTime != 0 || furnaceItemStacks[1] != null && furnaceItemStacks[0] != null) {
				if (furnaceBurnTime == 0 && canSmelt()) {
					currentItemBurnTime = furnaceBurnTime = getItemBurnTime(furnaceItemStacks[1]);

					if (furnaceBurnTime > 0) {
						isDirty1 = true;

						if (furnaceItemStacks[1] != null) {
							--furnaceItemStacks[1].stackSize;

							if (furnaceItemStacks[1].stackSize == 0) {
								furnaceItemStacks[1] = furnaceItemStacks[1].getItem().getContainerItem(furnaceItemStacks[1]);
							}
						}
					}
				}

				if (isBurning() && canSmelt()) {
					++furnaceCookTime;

					if (furnaceCookTime == 200) {
						furnaceCookTime = 0;
						smeltItem();
						isDirty1 = true;
					}
				} else {
					furnaceCookTime = 0;
				}
			}

			if (furnaceBurnTime2 != 0 || furnaceItemStacks[4] != null && furnaceItemStacks[3] != null) {
				if (furnaceBurnTime2 == 0 && canSmelt2()) {
					currentItemBurnTime2 = furnaceBurnTime2 = getItemBurnTime(furnaceItemStacks[4]);

					if (furnaceBurnTime2 > 0) {
						isDirty2 = true;

						if (furnaceItemStacks[4] != null) {
							--furnaceItemStacks[4].stackSize;

							if (furnaceItemStacks[4].stackSize == 0) {
								furnaceItemStacks[4] = furnaceItemStacks[4].getItem().getContainerItem(furnaceItemStacks[4]);
							}
						}
					}
				}

				if (isBurning2() && canSmelt2()) {
					++furnaceCookTime2;

					if (furnaceCookTime2 == 200) {
						furnaceCookTime2 = 0;
						smeltItem2();
						isDirty2 = true;
					}
				} else {
					furnaceCookTime2 = 0;
				}
			}

		}

		if (isBurning1 != furnaceBurnTime > 0 || isBurning2 != furnaceBurnTime2 > 0) {
			isDirty1 = true;
			isDirty2 = true;
			BlockBLDualFurnace.updateFurnaceBlockState(furnaceBurnTime > 0 || furnaceBurnTime2 > 0, worldObj, xCoord, yCoord, zCoord);
		}

		if (isDirty1 || isDirty2) {
			markDirty();
		}
	}

	private boolean canSmelt() {
		if (furnaceItemStacks[0] == null)
			return false;
		else {
			ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(furnaceItemStacks[0]);
			if (itemstack == null) return false;
			if (furnaceItemStacks[2] == null) return true;
			if (!furnaceItemStacks[2].isItemEqual(itemstack)) return false;
			int result = furnaceItemStacks[2].stackSize + itemstack.stackSize;
			return result <= getInventoryStackLimit() && result <= furnaceItemStacks[2].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
		}
	}

	private boolean canSmelt2() {
		if (furnaceItemStacks[3] == null)
			return false;
		else {
			ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(furnaceItemStacks[3]);
			if (itemstack == null) return false;
			if (furnaceItemStacks[5] == null) return true;
			if (!furnaceItemStacks[5].isItemEqual(itemstack)) return false;
			int result = furnaceItemStacks[5].stackSize + itemstack.stackSize;
			return result <= getInventoryStackLimit() && result <= furnaceItemStacks[5].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
		}
	}

	public void smeltItem() {
		if (canSmelt()) {
			ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(furnaceItemStacks[0]);

			if (furnaceItemStacks[2] == null)
				furnaceItemStacks[2] = itemstack.copy();

			else if (furnaceItemStacks[2].getItem() == itemstack.getItem())
				furnaceItemStacks[2].stackSize += itemstack.stackSize; // Forge BugFix: Results may have multiple items

			if(ItemGeneric.isIngotFromOre(furnaceItemStacks[0], furnaceItemStacks[2])) {
				if(furnaceItemStacks[6] != null) {
					boolean useFlux = this.worldObj.rand.nextInt(4) == 0;
					if(useFlux && furnaceItemStacks[2].stackSize + 1 <= getInventoryStackLimit() && furnaceItemStacks[2].stackSize + 1 <= furnaceItemStacks[2].getMaxStackSize()) {
						furnaceItemStacks[2].stackSize++;
					}
					--furnaceItemStacks[6].stackSize;
					if (furnaceItemStacks[6].stackSize <= 0)
						furnaceItemStacks[6] = null;
				}
			}

			--furnaceItemStacks[0].stackSize;

			if (furnaceItemStacks[0].stackSize <= 0)
				furnaceItemStacks[0] = null;
		}
	}

	public void smeltItem2() {
		if (canSmelt2()) {
			ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(furnaceItemStacks[3]);

			if (furnaceItemStacks[5] == null)
				furnaceItemStacks[5] = itemstack.copy();

			else if (furnaceItemStacks[5].getItem() == itemstack.getItem())
				furnaceItemStacks[5].stackSize += itemstack.stackSize; // Forge BugFix: Results may have multiple items

			if(ItemGeneric.isIngotFromOre(furnaceItemStacks[3], furnaceItemStacks[5])) {
				if(furnaceItemStacks[7] != null) {
					boolean useFlux = this.worldObj.rand.nextInt(4) == 0;
					if(useFlux && furnaceItemStacks[5].stackSize + 1 <= getInventoryStackLimit() && furnaceItemStacks[5].stackSize + 1 <= furnaceItemStacks[5].getMaxStackSize()) {
						furnaceItemStacks[5].stackSize++;
					}
					--furnaceItemStacks[7].stackSize;
					if (furnaceItemStacks[7].stackSize <= 0)
						furnaceItemStacks[7] = null;
				}
			}

			--furnaceItemStacks[3].stackSize;

			if (furnaceItemStacks[3].stackSize <= 0)
				furnaceItemStacks[3] = null;
		}
	}

	public static int getItemBurnTime(ItemStack itemstack) {
		if (itemstack == null)
			return 0;
		else {
			Item item = itemstack.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.wooden_slab)
					return 150;

				if (block.getMaterial() == Material.wood)
					return 300;

				if (block == Blocks.coal_block)
					return 16000;
			}

			if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")) return 200;
			if (item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")) return 200;
			if (item instanceof ItemHoe && ((ItemHoe)item).getToolMaterialName().equals("WOOD")) return 200;
			if (item == Items.stick) return 100;
			if (item == Items.coal) return 1600;
			if (item instanceof ItemGeneric && itemstack.getItemDamage() == EnumItemGeneric.SULFUR.ordinal()) return 1600;
			if (item == Items.lava_bucket) return 20000;
			if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
			if (item == Items.blaze_rod) return 2400;
			return GameRegistry.getFuelValue(itemstack);
		}
	}

	public static boolean isItemFuel(ItemStack itemstack) {
		return getItemBurnTime(itemstack) > 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : player.getDistanceSq((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return slot == 2 || slot == 5 ? false : (slot == 1 || slot == 4 ? isItemFuel(itemstack) : true);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int slot) {
		return slot == 0 || slot == 3 ? slotsBottom : (slot == 1 || slot == 4 ? slotsTop : slotsSides);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
		return isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
		return side != 0 || slot != 1 || side != 3 || slot != 4 || itemstack.getItem() == Items.bucket;
	}
}
