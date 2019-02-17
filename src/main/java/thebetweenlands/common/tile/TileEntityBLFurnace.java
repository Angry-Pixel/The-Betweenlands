package thebetweenlands.common.tile;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.block.container.BlockBLFurnace;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityBLFurnace extends TileEntityBasicInventory implements ISidedInventory, ITickable {
    private static final int[] slotsTop = new int[]{0};
    private static final int[] slotsBottom = new int[]{2, 1};
    private static final int[] slotsSides = new int[]{1, 3};
    //private NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    public int furnaceBurnTime;
    public int currentItemBurnTime;
    public int furnaceCookTime;
    private String customName;

    public TileEntityBLFurnace() {
        super(4, "sulfur_furnace");
    }

    public static boolean isItemFlux(ItemStack itemstack) {
        return itemstack.getItem() == ItemRegistry.ITEMS_MISC && itemstack.getItemDamage() == EnumItemMisc.LIMESTONE_FLUX.getID();
    }

    @Override
	public int getSizeInventory() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.size() <= 0;
    }

    @Override
	public ItemStack getStackInSlot(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (!inventory.get(slot).isEmpty()) {
            ItemStack itemstack;

            if (inventory.get(slot).getCount() <= amount) {
                itemstack = inventory.get(slot);
                inventory.set(slot, ItemStack.EMPTY);
                return itemstack;
            } else {
                itemstack = inventory.get(slot).splitStack(amount);
                if (inventory.get(slot).getCount() == 0)
                    inventory.set(slot, ItemStack.EMPTY);
                return itemstack;
            }
        } else

            return  ItemStack.EMPTY;
    }

    //@Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (!inventory.get(slot).isEmpty()) {
            ItemStack itemstack = inventory.get(slot);
            inventory.set(slot, ItemStack.EMPTY);
            return itemstack;
        } else
            return  ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
        inventory.set(slot, itemstack);
        if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit())
            itemstack.setCount(getInventoryStackLimit());
    }

    @Override
    public String getName() {
        return hasCustomName() ? customName : "container.furnace_bl";
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
        furnaceBurnTime = nbt.getShort("BurnTime");
        furnaceCookTime = nbt.getShort("CookTime");
        currentItemBurnTime = TileEntityFurnace.getItemBurnTime(inventory.get(1));
        if (nbt.hasKey("CustomName", 8))
            customName = nbt.getString("CustomName");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("BurnTime", (short) furnaceBurnTime);
        nbt.setShort("CookTime", (short) furnaceCookTime);
        if (hasCustomName())
            nbt.setString("CustomName", customName);
        return nbt;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @OnlyIn(Dist.CLIENT)
    public int getCookProgressScaled(int count) {
        return furnaceCookTime * count / 200;
    }

    @OnlyIn(Dist.CLIENT)
    public int getBurnTimeRemainingScaled(int remainingTime) {
        if (currentItemBurnTime == 0)
            currentItemBurnTime = 200;

        return furnaceBurnTime * remainingTime / currentItemBurnTime;
    }

    public boolean isBurning() {
        return furnaceBurnTime > 0;
    }

    @Override
    public void update() {
        boolean isBurning = furnaceBurnTime > 0;
        boolean isDirty = false;

        if (furnaceBurnTime > 0)
            furnaceBurnTime = Math.max(0, furnaceBurnTime - 1);
        else if (furnaceBurnTime < 0)
            furnaceBurnTime = 0;

        if (!world.isRemote) {
            if (furnaceBurnTime != 0 || !inventory.get(1).isEmpty()&& !inventory.get(0).isEmpty()) {
                if (furnaceBurnTime == 0 && canSmelt()) {
                    currentItemBurnTime = furnaceBurnTime = TileEntityFurnace.getItemBurnTime(inventory.get(1));
                    
                    if (furnaceBurnTime > 0) {
                        isDirty = true;

                        if (!inventory.get(1).isEmpty()) {
                            inventory.get(1).shrink(1);

                            if (inventory.get(1).getCount() == 0) {
                                inventory.set(1, inventory.get(1).getItem().getContainerItem(inventory.get(1)));
                            }
                        }
                    }
                }

                if (isBurning() && canSmelt()) {
                    ++furnaceCookTime;

                    if (furnaceCookTime == 200) {
                        furnaceCookTime = 0;
                        smeltItem();
                        isDirty = true;
                    }
                } else {
                    furnaceCookTime = 0;
                }
            }

            if (isBurning != furnaceBurnTime > 0) {
                isDirty = true;
                BlockBLFurnace.setState(furnaceBurnTime > 0, world, pos);
            }
        }

        if (isDirty) {
            markDirty();
        }
    }

    private boolean canSmelt() {
        if (inventory.get(0).isEmpty())
            return false;
        else {
            ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(inventory.get(0));
            if (itemstack.isEmpty()) return false;
            if (inventory.get(2).isEmpty()) return true;
            if (!inventory.get(2).isItemEqual(itemstack)) return false;
            int result = inventory.get(2).getCount() + itemstack.getCount();
            return result <= getInventoryStackLimit() && result <= inventory.get(2).getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
        }
    }

    public void smeltItem() {
        if (canSmelt()) {
            ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(inventory.get(0));

            if (inventory.get(2).isEmpty())
                inventory.set(2,itemstack.copy());

            else if (inventory.get(2).getItem() == itemstack.getItem())
                inventory.get(2).grow(itemstack.getCount()); // Forge BugFix: Results may have multiple items

            if (ItemRegistry.isIngotFromOre(inventory.get(0), inventory.get(2))) {
                if (!inventory.get(3).isEmpty()) {
                    boolean useFlux = this.world.rand.nextInt(3) == 0;
                    if (useFlux && inventory.get(2).getCount() + 1 <= getInventoryStackLimit() && inventory.get(2).getCount() + 1 <= inventory.get(2).getMaxStackSize()) {
                        inventory.get(2).grow(1);
                    }
                    inventory.get(3).shrink(1);
                    if (inventory.get(3).getCount() <= 0)
                        inventory.set(3, ItemStack.EMPTY);
                }
            }

            inventory.get(0).shrink(1);
            if (inventory.get(0).getCount() <= 0)
                inventory.set(0, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        return slot != 2 && (slot == 1 ? TileEntityFurnace.isItemFuel(itemstack) : slot != 3 || isItemFlux(itemstack));
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
        if (direction == EnumFacing.DOWN && slot == 1) {
            Item item = stack.getItem();

            if (item != Items.WATER_BUCKET && item != Items.BUCKET)
                return false;
        }
        return true;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return  ItemStack.EMPTY;
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

}
