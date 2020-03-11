package thebetweenlands.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public abstract class TileEntityAbstractBLFurnace extends TileEntityBasicInventory implements ISidedInventory, ITickable {
    private static final String NBT_BURN_TIME = "BurnTime";
    private static final String NBT_COOK_TIME = "CookTime";
    private static final String NBT_CUSTOM_NAME = "CustomName";

    private String customName;
    private ArrayList<FurnaceData> furnaceData = new ArrayList<>();

    private final int[] sideSlots;
    private final int[] bottomSlots;
    private final int[] inputSlots;
    private final int[] outputSlots;
    private final int[] fuelSlots;
    private final int[] fluxSlots;
    private int furnaceAmount;

    /**
     * @param name The name, used for translating
     * @param furnaceAmount The amount of furnaces, each one have (input, output, fuel & flux)
     */
    public TileEntityAbstractBLFurnace(String name, int furnaceAmount) {
        super(furnaceAmount * 4, name);
        this.furnaceAmount = furnaceAmount;
        IntStream.range(0, furnaceAmount).forEach(i -> furnaceData.add(new FurnaceData(i)));

        inputSlots = furnaceData.stream().flatMapToInt(data -> IntStream.of(data.getInputSlot())).toArray();
        outputSlots = furnaceData.stream().flatMapToInt(data -> IntStream.of(data.getOutputSlot())).toArray();
        fuelSlots = furnaceData.stream().flatMapToInt(data -> IntStream.of(data.getFuelSlot())).toArray();
        fluxSlots = furnaceData.stream().flatMapToInt(data -> IntStream.of(data.getFluxSlot())).toArray();
        bottomSlots = ArrayUtils.addAll(outputSlots, fuelSlots);
        sideSlots = ArrayUtils.addAll(fuelSlots, fluxSlots);
    }

    public static boolean isItemFlux(ItemStack itemstack) {
        return itemstack.getItem() == ItemRegistry.ITEMS_MISC && itemstack.getItemDamage() == EnumItemMisc.LIMESTONE_FLUX.getID();
    }

    @Override
    public String getName() {
        return hasCustomName() ? customName : super.getName();
    }

    @Override
    public boolean hasCustomName() {
        return customName != null && customName.length() > 0;
    }

    public void setStackDisplayName(String name) {
        customName = name;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        for (FurnaceData data: furnaceData) {
            if (nbt.hasKey(NBT_BURN_TIME, Constants.NBT.TAG_SHORT) && nbt.hasKey(NBT_COOK_TIME, Constants.NBT.TAG_SHORT)) {
                data.furnaceBurnTime = nbt.getShort(NBT_BURN_TIME);
                data.furnaceCookTime = nbt.getShort(NBT_COOK_TIME);
            } else {
                data.furnaceBurnTime = nbt.getShort(NBT_BURN_TIME + data.index);
                data.furnaceCookTime = nbt.getShort(NBT_COOK_TIME + data.index);
            }
            data.currentItemBurnTime = TileEntityFurnace.getItemBurnTime(getStackInSlot(data.getFuelSlot()));
        }
        if (nbt.hasKey(NBT_CUSTOM_NAME, 8))
            customName = nbt.getString(NBT_CUSTOM_NAME);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        for (FurnaceData data: furnaceData) {
            nbt.setShort(NBT_BURN_TIME + data.index, (short) data.furnaceBurnTime);
            nbt.setShort(NBT_COOK_TIME + data.index, (short) data.furnaceCookTime);
        }
        if (hasCustomName())
            nbt.setString(NBT_CUSTOM_NAME, customName);
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

    @SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int index, int count) {
        return furnaceData.get(index).furnaceCookTime * count / 200;
    }

    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int index, int remainingTime) {
        FurnaceData data = furnaceData.get(index);
        if (data.currentItemBurnTime == 0)
            data.currentItemBurnTime = 200;

        return data.furnaceBurnTime * remainingTime / data.currentItemBurnTime;
    }

    public boolean isBurning(int index) {
        return furnaceData.get(index).furnaceBurnTime > 0;
    }

    public FurnaceData getFurnaceData(int index) {
        return furnaceData.get(index);
    }

    @Override
    public void update() {
        boolean isDirty = false;

        boolean wasBurning = false;
        
        for (FurnaceData data : furnaceData) {
            wasBurning |= isBurning(data.index);
        }
        
        boolean isBurning = false;
        
        for (FurnaceData data : furnaceData) {
            if (data.furnaceBurnTime > 0)
                data.furnaceBurnTime = Math.max(0, data.furnaceBurnTime - 1);
            else if (data.furnaceBurnTime < 0)
                data.furnaceBurnTime = 0;

            if (!world.isRemote) {
                ItemStack fuelStack = getStackInSlot(data.getFuelSlot());
                if (data.furnaceBurnTime != 0 || !fuelStack.isEmpty() && !getStackInSlot(data.getInputSlot()).isEmpty()) {
                    if (data.furnaceBurnTime == 0 && canSmelt(data)) {
                        data.currentItemBurnTime = data.furnaceBurnTime = TileEntityFurnace.getItemBurnTime(fuelStack);

                        if (data.furnaceBurnTime > 0) {
                            isDirty = true;

                            if (!fuelStack.isEmpty()) {
                                ItemStack containerItem = fuelStack.getItem().getContainerItem(fuelStack);
                                fuelStack.shrink(1);

                                if (fuelStack.getCount() == 0) {
                                    setInventorySlotContents(data.getFuelSlot(), containerItem);
                                }
                            }
                        }
                    }

                    if (isBurning(data.index) && canSmelt(data)) {
                        ++data.furnaceCookTime;

                        if (data.furnaceCookTime == 200) {
                            data.furnaceCookTime = 0;
                            smeltItem(data);
                            isDirty = true;
                        }
                    } else {
                        data.furnaceCookTime = 0;
                    }
                }

                if(data.furnaceBurnTime > 0) {
                	isBurning = true;
                }
            }
        }

        if(wasBurning != isBurning) {
        	updateState(isBurning);
        	isDirty = true;
        }
        
        if (isDirty) {
            markDirty();
        }
    }

    private boolean canSmelt(FurnaceData data) {
        ItemStack inputStack = getStackInSlot(data.getInputSlot());
        if (inputStack.isEmpty())
            return false;
        else {
            ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult(inputStack);
            if (smeltingResult.isEmpty()) return false;

            ItemStack outputStack = getStackInSlot(data.getOutputSlot());
            if (outputStack.isEmpty()) return true;
            if (!outputStack.isItemEqual(smeltingResult)) return false;
            int result = outputStack.getCount() + smeltingResult.getCount();
            return result <= getInventoryStackLimit() && result <= outputStack.getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
        }
    }

    private void smeltItem(FurnaceData data) {
        if (canSmelt(data)) {
            ItemStack inputStack = getStackInSlot(data.getInputSlot());
            ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult(inputStack);

            ItemStack outputStack = getStackInSlot(data.getOutputSlot());
            if (outputStack.isEmpty()) {
                setInventorySlotContents(data.getOutputSlot(), smeltingResult.copy());
                outputStack = getStackInSlot(data.getOutputSlot());
            } else if (outputStack.getItem() == smeltingResult.getItem())
                outputStack.grow(smeltingResult.getCount()); // Forge BugFix: Results may have multiple items

            if (ItemRegistry.isIngotFromOre(inputStack, outputStack)) {
                ItemStack fluxStack = getStackInSlot(data.getFluxSlot());
                if (!fluxStack.isEmpty()) {
                    boolean useFlux = this.world.rand.nextInt(3) == 0;
                    if (useFlux && outputStack.getCount() + 1 <= getInventoryStackLimit() && outputStack.getCount() + 1 <= outputStack.getMaxStackSize()) {
                        outputStack.grow(1);
                    }
                    fluxStack.shrink(1);
                    if (fluxStack.getCount() <= 0)
                        setInventorySlotContents(data.getFluxSlot(), ItemStack.EMPTY);
                }
            }

            inputStack.shrink(1);
            if (inputStack.getCount() <= 0)
                setInventorySlotContents(data.getInputSlot(), ItemStack.EMPTY);
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        return getOutputSlots().noneMatch(slotMatch(slot)) &&
                (getFuelSlots().anyMatch(slotMatch(slot)) ? TileEntityFurnace.isItemFuel(itemstack) :
                        getFluxSlots().noneMatch(slotMatch(slot)) || isItemFlux(itemstack));
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? bottomSlots : (side == EnumFacing.UP ? inputSlots : sideSlots);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {
        if (direction == EnumFacing.DOWN && getFuelSlots().anyMatch(slotMatch(slot))) {
            return stack.getItem() == Items.BUCKET;
        }
        return true;
    }

    private IntPredicate slotMatch(int slot) {
        return x -> x == slot;
    }

    private IntStream getOutputSlots() {
        return IntStream.of(outputSlots);
    }

    private IntStream getFuelSlots() {
        return IntStream.of(fuelSlots);
    }

    private IntStream getFluxSlots() {
        return IntStream.of(fluxSlots);
    }

    /**
     * Called when the furnace is turned on or off
     * @param active The status of the furnace
     */
    protected abstract void updateState(boolean active);

    public int getFurnaceAmount() {
        return furnaceAmount;
    }

    public static class FurnaceData implements Cloneable {
        private int furnaceBurnTime;
        private int currentItemBurnTime;
        private int furnaceCookTime;
        private int index;

        public FurnaceData(int index) {
            this.index = index;
        }

        public int getFurnaceBurnTime() {
            return furnaceBurnTime;
        }

        public void setFurnaceBurnTime(int furnaceBurnTime) {
            this.furnaceBurnTime = furnaceBurnTime;
        }

        public int getCurrentItemBurnTime() {
            return currentItemBurnTime;
        }

        public void setCurrentItemBurnTime(int currentItemBurnTime) {
            this.currentItemBurnTime = currentItemBurnTime;
        }

        public int getFurnaceCookTime() {
            return furnaceCookTime;
        }

        public void setFurnaceCookTime(int furnaceCookTime) {
            this.furnaceCookTime = furnaceCookTime;
        }

        public final int getInputSlot() {
            return index * 4;
        }

        public final int getOutputSlot() {
            return getInputSlot() + 2;
        }

        public final int getFuelSlot() {
            return getInputSlot() + 1;
        }

        public final int getFluxSlot() {
            return getInputSlot() + 3;
        }

        @Override
        public FurnaceData clone() {
            try {
                return (FurnaceData) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
