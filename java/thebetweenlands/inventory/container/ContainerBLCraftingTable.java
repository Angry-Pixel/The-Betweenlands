package thebetweenlands.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;

public class ContainerBLCraftingTable extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public IInventory craftResult = new InventoryCraftResult();
	private World worldObj;
	private int posX;
	private int posY;
	private int posZ;

	public ContainerBLCraftingTable(InventoryPlayer playerInventory, World world, int x, int y, int z) {
		worldObj = world;
		posX = x;
		posY = y;
		posZ = z;
		addSlotToContainer(new SlotCrafting(playerInventory.player, craftMatrix, craftResult, 0, 124, 35));
		int craftSlot;
		int i1;

		for (craftSlot = 0; craftSlot < 3; ++craftSlot) {
			for (i1 = 0; i1 < 3; ++i1) {
				addSlotToContainer(new Slot(craftMatrix, i1 + craftSlot * 3, 30 + i1 * 18, 17 + craftSlot * 18));
			}
		}

		for (craftSlot = 0; craftSlot < 3; ++craftSlot) {
			for (i1 = 0; i1 < 9; ++i1) {
				addSlotToContainer(new Slot(playerInventory, i1 + craftSlot * 9 + 9, 8 + i1 * 18, 84 + craftSlot * 18));
			}
		}

		for (craftSlot = 0; craftSlot < 9; ++craftSlot) {
			addSlotToContainer(new Slot(playerInventory, craftSlot, 8 + craftSlot * 18, 142));
		}

		onCraftMatrixChanged(craftMatrix);
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventory) {
		craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, worldObj));
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);

		if (!worldObj.isRemote) {
			for (int i = 0; i < 9; ++i) {
				ItemStack itemstack = craftMatrix.getStackInSlotOnClosing(i);

				if (itemstack != null) {
					player.dropPlayerItemWithRandomChoice(itemstack, false);
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return worldObj.getBlock(posX, posY, posZ) != BLBlockRegistry.weedWoodCraftingTable ? false : player.getDistanceSq((double) posX + 0.5D, (double) posY + 0.5D, (double) posZ + 0.5D) <= 64.0D;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex == 0) {
				if (!mergeItemStack(itemstack1, 10, 46, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (slotIndex >= 10 && slotIndex < 37) {
				if (!mergeItemStack(itemstack1, 37, 46, false)) {
					return null;
				}
			} else if (slotIndex >= 37 && slotIndex < 46) {
				if (!mergeItemStack(itemstack1, 10, 37, false)) {
					return null;
				}
			} else if (!mergeItemStack(itemstack1, 10, 46, false)) {
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
	public boolean func_94530_a(ItemStack stack, Slot slotIndex) {
		return slotIndex.inventory != craftResult && super.func_94530_a(stack, slotIndex);
	}
}