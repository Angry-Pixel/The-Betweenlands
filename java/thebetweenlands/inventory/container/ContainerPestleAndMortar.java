package thebetweenlands.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.inventory.slot.SlotPestle;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityPestleAndMortar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerPestleAndMortar extends Container {

	private final int numRows = 2;
	private TileEntityPestleAndMortar pestleAndMortar;

	public ContainerPestleAndMortar(InventoryPlayer playerInventory, TileEntityPestleAndMortar tile) {
		super();
		int i = (numRows - 4) * 18;
		pestleAndMortar = tile;

		addSlotToContainer(new Slot(tile, 0, 35, 23));
		addSlotToContainer(new SlotPestle(tile, 1, 79, 23));
		addSlotToContainer(new Slot(tile, 2, 123, 23));

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
				if (stack1.getItem() == BLItemRegistry.pestle) {
					if(stack1.getTagCompound().getBoolean("active"))
						stack1.getTagCompound().setBoolean("active", false);
				}
			}
			if (slotIndex > 2) {
				if (stack1.getItem() == BLItemRegistry.pestle)
					if (!mergeItemStack(stack1, 1, 2, true))
						return null;
				if (stack1.getItem() != BLItemRegistry.pestle)
					if (!mergeItemStack(stack1, 0, 1, true))
						return null;
			} else if (!mergeItemStack(stack1, 3, inventorySlots.size(), false)) 
				return null;
			if (stack1.stackSize == 0)
				slot.putStack(null);
			else
				
				slot.onSlotChanged();
			if (stack1.stackSize != stack.stackSize)
				slot.onPickupFromSlot(player, stack1);
			else
				return null;
		}
		return stack;
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafter) {
		super.addCraftingToCrafters(crafter);
		pestleAndMortar.sendGUIData(this, crafter);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (Object crafter : crafters)
			pestleAndMortar.sendGUIData(this, (ICrafting) crafter);
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
