package thebetweenlands.common.inventory.container;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.slot.SlotRestrictionListWithMeta;
import thebetweenlands.common.item.herblore.ItemCrushed.EnumItemCrushed;
import thebetweenlands.common.item.herblore.ItemPlantDrop.EnumItemPlantDrop;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.InventoryUtils;

public class ContainerSilkBundle extends Container {
	@Nullable
	private final InventoryItem inventory;
	public static List<ItemStack> acceptedItems = new ArrayList<>();
	private int numRows = 3;

	public ContainerSilkBundle(EntityPlayer player, InventoryPlayer playerInventory, @Nullable InventoryItem itemInventory) {
		this.inventory = itemInventory;

		if(this.inventory == null || this.inventory.isEmpty()) {
			return;
		}

		this.numRows = 2;
		int yOffset = (this.numRows - 4) * 18;

		//new slot restriction list with meta
		this.addSlotToContainer(new SlotRestrictionListWithMeta(itemInventory, 0, 78, 15, acceptedItems, 1, this));
		this.addSlotToContainer(new SlotRestrictionListWithMeta(itemInventory, 1, 42, 33, acceptedItems, 1, this));
		this.addSlotToContainer(new SlotRestrictionListWithMeta(itemInventory, 2, 114, 33, acceptedItems, 1, this));
		this.addSlotToContainer(new SlotRestrictionListWithMeta(itemInventory, 3, 78, 51, acceptedItems, 1, this));

		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9, 7 + column * 18, 119 + row * 18 + yOffset));
			}
		}

		for (int column = 0; column < 9; ++column) {
			this.addSlotToContainer(new Slot(playerInventory, column, 7 + column * 18, 177 + yOffset));
		}
	}

	public InventoryItem getItemInventory() {
		return this.inventory;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (InventoryUtils.isDisallowedInInventories(slotStack))
				return ItemStack.EMPTY;

			if (slotIndex > 3) {
				if (!mergeItemStack(slotStack, 0, 4, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(slotStack, 4, inventorySlots.size(), false))
				return ItemStack.EMPTY;


			if (slotStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}
		return stack;
	}
}