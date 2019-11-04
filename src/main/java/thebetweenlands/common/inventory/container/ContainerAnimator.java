package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.inventory.slot.SlotRestrictionNoMeta;
import thebetweenlands.common.inventory.slot.SlotSizeRestriction;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAnimator;
import net.minecraft.inventory.Container;


public class ContainerAnimator extends Container {

    private final int numRows = 2;
    private TileEntityAnimator animator;

    public ContainerAnimator(InventoryPlayer playerInventory, TileEntityAnimator tile) {
        super();
        int i = (numRows - 4) * 18;
        animator = tile;

        addSlotToContainer(new SlotSizeRestriction(tile, 0, 79, 23, 1));
        addSlotToContainer(new SlotRestrictionNoMeta(tile, 1, 34, 57, new ItemStack(ItemRegistry.LIFE_CRYSTAL), 1));
        addSlotToContainer(new SlotRestriction(tile, 2, 124, 57, new ItemStack(ItemRegistry.ITEMS_MISC, 1, ItemMisc.EnumItemMisc.SULFUR.getID()), 64, this));

        for (int j = 0; j < 3; j++)
            for (int k = 0; k < 9; k++)
                addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 7 + k * 18, 119 + j * 18 + i));
        for (int j = 0; j < 9; j++)
            addSlotToContainer(new Slot(playerInventory, j, 7 + j * 18, 177 + i));
    }


    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = (Slot) inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            if (slotIndex > 2) {
                if (stack1.getItem() == ItemRegistry.ITEMS_MISC && stack1.getItemDamage() == ItemMisc.EnumItemMisc.SULFUR.getID())
                    if (!mergeItemStack(stack1, 2, 3, true))
                        return ItemStack.EMPTY;
                if (stack1.getItem() == ItemRegistry.LIFE_CRYSTAL)
                    if (!mergeItemStack(stack1, 1, 2, true))
                        return ItemStack.EMPTY;
                if (stack1.getCount() == 1 && stack1 != new ItemStack(ItemRegistry.ITEMS_MISC, 1, ItemMisc.EnumItemMisc.SULFUR.getID()) && stack1.getItem() != ItemRegistry.LIFE_CRYSTAL)
                    if (!mergeItemStack(stack1, 0, 1, true))
                        return ItemStack.EMPTY;
            } else if (!mergeItemStack(stack1, 3, inventorySlots.size(), false))
                return ItemStack.EMPTY;
            if (stack1.getCount() == 0)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();
            if (stack1.getCount() != stack.getCount())
                slot.onTake(player, stack1);
            else
                return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        animator.sendGUIData(this, listener);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : listeners)
            animator.sendGUIData(this, (IContainerListener) crafter);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        super.updateProgressBar(id, value);
        animator.getGUIData(id, value);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.animator.isUsableByPlayer(player);
    }
}