package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.slot.SlotRestriction;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.tile.TileEntityCenser;

public class ContainerCenser extends Container {
	public static final int SLOT_FUEL = 0;
	public static final int SLOT_INPUT = 1;
	public static final int SLOT_INTERNAL = 2;

	protected TileEntityCenser censer;

	public ContainerCenser(InventoryPlayer inventory, TileEntityCenser tileentity) {
		censer = tileentity;

		int yOffset = 91;

		addSlotToContainer(new SlotRestriction(tileentity, 0, 80, 48 + yOffset, EnumItemMisc.SULFUR.create(1), 64, this));
		addSlotToContainer(new Slot(tileentity, 1, 44, 12 + yOffset));
		addSlotToContainer(new Slot(tileentity, 2, 80, 12 + yOffset) {
			@Override
			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {
				return false;
			}

			@Override
			public boolean canTakeStack(EntityPlayer playerIn) {
				return false;
			}

			@Override
			public ItemStack decrStackSize(int amount) {
				return ItemStack.EMPTY;
			}

			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}

			@Override
			public void putStack(ItemStack stack) {
			}
		});

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + yOffset));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142 + yOffset));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		if(slotIndex == SLOT_INTERNAL) {
			return ItemStack.EMPTY;
		}
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			newStack = slotStack.copy();
			if (slotIndex > 2) {
				if (EnumItemMisc.SULFUR.isItemOf(slotStack)) {
					if (!mergeItemStack(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!mergeItemStack(slotStack, 1, 2, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(slotStack, 3, inventorySlots.size(), false))
				return ItemStack.EMPTY;
			if (slotStack.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
			if (slotStack.getCount() != newStack.getCount())
				slot.onTake(player, slotStack);
			else
				return ItemStack.EMPTY;
		}
		return newStack;
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		censer.sendGUIData(this, listener);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener listener : listeners) {
			censer.sendGUIData(this, listener);
		}
	}

	@Override
	public void updateProgressBar(int id, int value) {
		censer.receiveGUIData(id, value);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.censer.isUsableByPlayer(player);
	}
}
