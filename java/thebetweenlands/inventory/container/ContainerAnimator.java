package thebetweenlands.inventory.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.inventory.slot.SlotRestriction;
import thebetweenlands.inventory.slot.SlotSizeRestriction;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityAnimator;

public class ContainerAnimator extends Container {

	private final int numRows = 2;
	private int life;
	private int sulphur;
	private TileEntityAnimator animator;

	public ContainerAnimator(InventoryPlayer playerInventory, TileEntityAnimator tile) {
		super();
		int i = (numRows - 4) * 18;
		this.animator = tile;

		addSlotToContainer(new SlotSizeRestriction(tile, 0, 80, 24, 1));
		addSlotToContainer(new SlotRestriction(tile, 1, 43, 54, new ItemStack(BLItemRegistry.materialsBL, 1, 11), 1));
		addSlotToContainer(new SlotRestriction(tile, 2, 116, 54, new ItemStack(BLItemRegistry.materialsBL, 1, 24), 64));

		for (int j = 0; j < 3; j++)
			for (int k = 0; k < 9; k++)
				addSlotToContainer(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 120 + j * 18 + i));
		for (int j = 0; j < 9; j++)
			addSlotToContainer(new Slot(playerInventory, j, 8 + j * 18, 178 + i));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex < 3) {
				if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
					return null;
				}
			} else {
				if (itemstack1.getItem() == BLItemRegistry.materialsBL && itemstack1.getItemDamage() == 11 && !(((Slot) this.inventorySlots.get(1)).getHasStack() || !((Slot) this.inventorySlots.get(1)).isItemValid(itemstack1))) {
					((Slot) this.inventorySlots.get(1)).putStack(new ItemStack(itemstack1.getItem(), 1, itemstack1.getItemDamage()));
					--itemstack1.stackSize;
				} else if (itemstack1.getItem() == BLItemRegistry.materialsBL && itemstack1.getItemDamage() == 24) {
					if (!mergeItemStack(itemstack1, 2, 3, false)) {
						return null;
					}
				} else if (!(((Slot) this.inventorySlots.get(0)).getHasStack() || !((Slot) this.inventorySlots.get(0)).isItemValid(itemstack1))) {
					((Slot) this.inventorySlots.get(0)).putStack(new ItemStack(itemstack1.getItem(), 1, itemstack1.getItemDamage()));
					--itemstack1.stackSize;
				} else
					return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		animator.sendProgressPacket();
	}
	
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) 
    {
		super.updateProgressBar(id, value);
		animator.sendProgressPacket();    	
    }

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}
}
