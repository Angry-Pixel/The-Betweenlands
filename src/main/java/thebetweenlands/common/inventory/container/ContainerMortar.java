package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.slot.SlotPestle;
import thebetweenlands.common.inventory.slot.SlotRestrictionNoMeta;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityMortar;

public class ContainerMortar  extends Container {

    private final int numRows = 2;
    private TileEntityMortar pestleAndMortar;

    public ContainerMortar(InventoryPlayer playerInventory, TileEntityMortar tile) {
        super();
        int i = (numRows - 4) * 18;
        pestleAndMortar = tile;

        addSlotToContainer(new Slot(tile, 0, 35, 36));
        addSlotToContainer(new SlotPestle(tile, 1, 79, 36));
        addSlotToContainer(new Slot(tile, 2, 123, 36));
        addSlotToContainer(new SlotRestrictionNoMeta(tile, 3, 79, 8, new ItemStack(ItemRegistry.LIFE_CRYSTAL), 1));

        for (int j = 0; j < 3; j++)
            for (int k = 0; k < 9; k++)
                addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 7 + k * 18, 119 + j * 18 + i));
        for (int j = 0; j < 9; j++)
            addSlotToContainer(new Slot(playerInventory, j, 7 + j * 18, 177 + i));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack stack = null;
        Slot slot = (Slot) inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            if (slotIndex == 1) {
                if (stack1.getItem() == ItemRegistry.PESTLE) {
                    if(stack1.getTagCompound().getBoolean("active"))
                        stack1.getTagCompound().setBoolean("active", false);
                }
            }
            if (slotIndex > 3) {
                if (stack1.getItem() == ItemRegistry.PESTLE)
                    if (!mergeItemStack(stack1, 1, 2, true))
                        return null;
                if (stack1.getItem() != ItemRegistry.PESTLE && stack1.getItem() != ItemRegistry.LIFE_CRYSTAL)
                    if (!mergeItemStack(stack1, 0, 1, true))
                        return null;
                if (stack1.getItem() == ItemRegistry.LIFE_CRYSTAL)
                    if (!mergeItemStack(stack1, 3, 4, true))
                        return null;
            } else if (!mergeItemStack(stack1, 4, inventorySlots.size(), false))
                return null;
            if (stack1.getCount() == 0)
                slot.putStack(null);
            else

                slot.onSlotChanged();
            if (stack1.getCount() != stack.getCount())
                slot.onTake(player, stack1);
            else
                return null;
        }
        return stack;
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        pestleAndMortar.sendGUIData(this, listener);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : listeners)
            pestleAndMortar.sendGUIData(this, (IContainerListener) crafter);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        super.updateProgressBar(id, value);
        pestleAndMortar.getGUIData(id, value);
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }
}
