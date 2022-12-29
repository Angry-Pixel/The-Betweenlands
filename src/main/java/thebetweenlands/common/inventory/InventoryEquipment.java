package thebetweenlands.common.inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.api.event.EquipmentChangedEvent;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.capability.equipment.EquipmentEntityCapability;

public class InventoryEquipment implements IInventory, ITickable {
    protected final NonNullList<ItemStack> inventory;
    protected final NonNullList<ItemStack> prevTickStacks;
    protected final EquipmentEntityCapability capability;
    private int lastChangeCheck = 0;

    public InventoryEquipment(EquipmentEntityCapability capability, NonNullList<ItemStack> inventory) {
        this.capability = capability;
        this.inventory = inventory;
        this.prevTickStacks = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);

        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack stack = this.inventory.get(i);
            if (!stack.isEmpty())
                this.prevTickStacks.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
    }

    @Override
    public String getName() {
        return "container.bl.equipment";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return (ITextComponent) (this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
    }

    @Override
    @Nullable
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStack.EMPTY;
        if (index < this.getSizeInventory()) {
            stack = ItemStackHelper.getAndRemove(inventory, index);
            this.markDirty();
        }
        return stack;
    }

    @Override
    @Nullable
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = ItemStack.EMPTY;
        if (index < this.getSizeInventory()) {
            stack = ItemStackHelper.getAndSplit(inventory, index, count);
            this.markDirty();
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        if (index < this.getSizeInventory()) {
            this.inventory.set(index, stack);
            this.markDirty();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        this.capability.markDirty();
        MinecraftForge.EVENT_BUS.post(new EquipmentChangedEvent(this.capability.getEntity(), this.capability));
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }


    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index < this.getSizeInventory()) {
            return true;
        } else {
            return false;
        }
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
        for (int i = 0; i < this.inventory.size(); ++i) {
            this.inventory.set(i, ItemStack.EMPTY);
        }
        this.markDirty();
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.size() <= 0;
    }

    @Override
    @Nullable
    public ItemStack getStackInSlot(int index) {
        return index >= this.getSizeInventory() ? ItemStack.EMPTY : this.inventory.get(index);
    }

    @Override
    public void update() {
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack stack = this.inventory.get(i);

            if (!stack.isEmpty() && stack.getItem() instanceof IEquippable) {
                ((IEquippable) stack.getItem()).onEquipmentTick(stack, this.capability.getEntity(), this);
            }
        }

        if(this.lastChangeCheck++ > 10) {
        	this.detectChangesAndMarkDirty();
        	this.lastChangeCheck = 0;
        }
    }

    protected void detectChangesAndMarkDirty() {
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack stack = this.inventory.get(i);
            ItemStack prevStack = this.prevTickStacks.get(i);

            if (!ItemStack.areItemStacksEqual(prevStack, stack)) {
                this.prevTickStacks.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
                this.markDirty();
            }
        }
    }
}
