package thebetweenlands.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.inventory.slot.SlotRestriction;
import thebetweenlands.inventory.slot.SlotRestrictionNoMeta;
import thebetweenlands.inventory.slot.SlotSizeRestriction;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.tileentities.TileEntityAnimator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerAnimator extends Container {

	private final int numRows = 2;
	private TileEntityAnimator animator;

	public ContainerAnimator(InventoryPlayer playerInventory, TileEntityAnimator tile) {
		super();
		int i = (numRows - 4) * 18;
		animator = tile;

		addSlotToContainer(new SlotSizeRestriction(tile, 0, 79, 23, 1));
		addSlotToContainer(new SlotRestrictionNoMeta(tile, 1, 34, 57, new ItemStack(BLItemRegistry.lifeCrystal), 1));
		addSlotToContainer(new SlotRestriction(tile, 2, 124, 57, new ItemStack(BLItemRegistry.itemsGeneric, 1, EnumItemGeneric.SULFUR.ordinal()), 64));

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
			if (slotIndex > 2) {
				if (stack1.getItem() == BLItemRegistry.itemsGeneric && stack1.getItemDamage() == EnumItemGeneric.SULFUR.ordinal())
					if (!mergeItemStack(stack1, 2, 3, true))
						return null;
				if (stack1.getItem() == BLItemRegistry.lifeCrystal)
					if (!mergeItemStack(stack1, 1, 2, true))
						return null;
				if (stack1.stackSize == 1 && stack1 != new ItemStack(BLItemRegistry.itemsGeneric, 1, EnumItemGeneric.SULFUR.ordinal()) && stack1.getItem() != BLItemRegistry.lifeCrystal)
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
		animator.sendGUIData(this, crafter);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (Object crafter : crafters)
			animator.sendGUIData(this, (ICrafting) crafter);
	}
	
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) 
    {
		super.updateProgressBar(id, value);
		animator.getGUIData(id, value);   	
    }

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}
}
