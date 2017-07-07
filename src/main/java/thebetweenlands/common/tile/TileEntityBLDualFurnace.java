package thebetweenlands.common.tile;

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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.container.BlockBLDualFurnace;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityBLDualFurnace extends TileEntity implements ISidedInventory, ITickable
{
	private static final int[] slotsTop = new int[] {0, 4};
	private static final int[] slotsBottom = new int[] {2, 1, 6, 5};
	private static final int[] slotsSides = new int[] {1, 5, 3, 7};
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

			if (furnaceItemStacks[slot].getCount() <= amount) {
				itemstack = furnaceItemStacks[slot];
				furnaceItemStacks[slot] = null;
				return itemstack;
			}
			else {
				itemstack = furnaceItemStacks[slot].splitStack(amount);
				if (furnaceItemStacks[slot].getCount() == 0)
					furnaceItemStacks[slot] = null;
				return itemstack;
			}
		}
		else

			return null;
	}

	//@Override
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
		if (itemstack != null && itemstack.getCount() > getInventoryStackLimit())
			itemstack.setCount(getInventoryStackLimit());
	}

	@Override
	public String getName() {
		return hasCustomName() ? customName : "container.dualFurnaceBL";
	}

	@Override
	public boolean hasCustomName() {
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
				furnaceItemStacks[slot] = new ItemStack(nbttagcompound1);
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
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
		if (hasCustomName())
			nbt.setString("CustomName", customName);
		return nbt;
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
	public void update() {
		boolean isBurning1 = furnaceBurnTime > 0;
		boolean isDirty1 = false;
		boolean isBurning2 = furnaceBurnTime2 > 0;
		boolean isDirty2 = false;

		if (furnaceBurnTime > 0)
			--furnaceBurnTime;

		if (furnaceBurnTime2 > 0)
			--furnaceBurnTime2;

		if (!world.isRemote) {
			if (furnaceBurnTime != 0 || furnaceItemStacks[1] != null && furnaceItemStacks[0] != null) {
				if (furnaceBurnTime == 0 && canSmelt()) {
					currentItemBurnTime = furnaceBurnTime = getItemBurnTime(furnaceItemStacks[1]);

					if (furnaceBurnTime > 0) {
						isDirty1 = true;

						if (furnaceItemStacks[1] != null) {
							furnaceItemStacks[1].shrink(1);

							if (furnaceItemStacks[1].getCount() == 0) {
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

			if (furnaceBurnTime2 != 0 || furnaceItemStacks[5] != null && furnaceItemStacks[4] != null) {
				if (furnaceBurnTime2 == 0 && canSmelt2()) {
					currentItemBurnTime2 = furnaceBurnTime2 = getItemBurnTime(furnaceItemStacks[5]);

					if (furnaceBurnTime2 > 0) {
						isDirty2 = true;

						if (furnaceItemStacks[5] != null) {
							furnaceItemStacks[5].shrink(1);

							if (furnaceItemStacks[5].getCount() == 0) {
								furnaceItemStacks[5] = furnaceItemStacks[5].getItem().getContainerItem(furnaceItemStacks[5]);
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
			BlockBLDualFurnace.setState(furnaceBurnTime > 0 || furnaceBurnTime2 > 0, world, pos);
		}

		if (isDirty1 || isDirty2) {
			markDirty();
		}
	}

	private boolean canSmelt() {
		if (furnaceItemStacks[0] == null)
			return false;
		else {
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks[0]);
			if (itemstack == null) return false;
			if (furnaceItemStacks[2] == null) return true;
			if (!furnaceItemStacks[2].isItemEqual(itemstack)) return false;
			int result = furnaceItemStacks[2].getCount() + itemstack.getCount();
			return result <= getInventoryStackLimit() && result <= furnaceItemStacks[2].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
		}
	}

	private boolean canSmelt2() {
		if (furnaceItemStacks[4] == null)
			return false;
		else {
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks[4]);
			if (itemstack == null) return false;
			if (furnaceItemStacks[6] == null) return true;
			if (!furnaceItemStacks[6].isItemEqual(itemstack)) return false;
			int result = furnaceItemStacks[6].getCount() + itemstack.getCount();
			return result <= getInventoryStackLimit() && result <= furnaceItemStacks[6].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
		}
	}

	public void smeltItem() {
		if (canSmelt()) {
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks[0]);

			if (furnaceItemStacks[2] == null)
				furnaceItemStacks[2] = itemstack.copy();

			else if (furnaceItemStacks[2].getItem() == itemstack.getItem())
				furnaceItemStacks[2].grow(itemstack.getCount()); // Forge BugFix: Results may have multiple items

			if(ItemRegistry.isIngotFromOre(furnaceItemStacks[0], furnaceItemStacks[2])) {
				if(furnaceItemStacks[3] != null) {
					boolean useFlux = this.world.rand.nextInt(3) == 0;
					if(useFlux && furnaceItemStacks[2].getCount() + 1 <= getInventoryStackLimit() && furnaceItemStacks[2].getCount() + 1 <= furnaceItemStacks[2].getMaxStackSize()) {
						furnaceItemStacks[2].grow(1);
					}
					furnaceItemStacks[3].shrink(1);
					if (furnaceItemStacks[3].getCount() <= 0)
						furnaceItemStacks[3] = null;
				}
			}

			furnaceItemStacks[0].shrink(1);

			if (furnaceItemStacks[0].getCount() <= 0)
				furnaceItemStacks[0] = null;
		}
	}

	public void smeltItem2() {
		if (canSmelt2()) {
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(furnaceItemStacks[4]);

			if (furnaceItemStacks[6] == null)
				furnaceItemStacks[6] = itemstack.copy();

			else if (furnaceItemStacks[6].getItem() == itemstack.getItem())
				furnaceItemStacks[6].grow(itemstack.getCount()); // Forge BugFix: Results may have multiple items

			if(ItemRegistry.isIngotFromOre(furnaceItemStacks[4], furnaceItemStacks[6])) {
				if(furnaceItemStacks[7] != null) {
					boolean useFlux = this.world.rand.nextInt(3) == 0;
					if(useFlux && furnaceItemStacks[6].getCount() + 1 <= getInventoryStackLimit() && furnaceItemStacks[6].getCount() + 1 <= furnaceItemStacks[6].getMaxStackSize()) {
						furnaceItemStacks[6].grow(1);
					}
					furnaceItemStacks[7].shrink(1);
					if (furnaceItemStacks[7].getCount() <= 0)
						furnaceItemStacks[7] = null;
				}
			}

			furnaceItemStacks[4].shrink(1);

			if (furnaceItemStacks[4].getCount() <= 0)
				furnaceItemStacks[4] = null;
		}
	}

	public static int getItemBurnTime(ItemStack stack) {
		if (stack == null) {
			return 0;
		} else {
			Item item = stack.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.AIR) {
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.WOODEN_SLAB)
					return 150;

				if (block.getDefaultState().getMaterial() == Material.WOOD)
					return 300;

				if (block == Blocks.COAL_BLOCK)
					return 16000;
			}

			if (item instanceof ItemTool && "WOOD".equals(((ItemTool) item).getToolMaterialName()))
				return 200;
			if (item instanceof ItemSword && "WOOD".equals(((ItemSword) item).getToolMaterialName()))
				return 200;
			if (item instanceof ItemHoe && "WOOD".equals(((ItemHoe) item).getMaterialName()))
				return 200;
			if (item == Items.STICK)
				return 100;
			if (item == Items.COAL)
				return 1600;
			if (item instanceof ItemMisc && stack.getItemDamage() == EnumItemMisc.SULFUR.getID())
				return 1600;
			if (item == Items.LAVA_BUCKET)
				return 20000;
			if (item == Item.getItemFromBlock(Blocks.SAPLING))
				return 100;
			if (item == Items.BLAZE_ROD)
				return 2400;
			return GameRegistry.getFuelValue(stack);
		}
	}

	public static boolean isItemFuel(ItemStack itemstack) {
		return getItemBurnTime(itemstack) > 0;
	}

	public static boolean isItemFlux(ItemStack itemstack) {
		return itemstack.getItem() == ItemRegistry.ITEMS_MISC && itemstack.getItemDamage() == EnumItemMisc.LIMESTONE_FLUX.getID();
	}

	@Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return slot == 2 || slot == 6 ? false : (slot == 1 || slot == 5) ? isItemFuel(itemstack) : (slot == 3 || slot == 7) ? isItemFlux(itemstack) : true; 
	}

	@Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? slotsBottom : (side == EnumFacing.UP ? slotsTop : slotsSides);
    }

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, EnumFacing direction) {
		return isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {
		if (direction == EnumFacing.DOWN && (slot == 1 || slot == 5)) {
			Item item = stack.getItem();

			if (item != Items.WATER_BUCKET && item != Items.BUCKET)
				return false;
		}
		return true;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {	
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.furnaceItemStacks) {
			if (!itemstack.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

}
