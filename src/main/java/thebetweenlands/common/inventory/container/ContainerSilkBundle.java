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
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.util.InventoryUtils;

public class ContainerSilkBundle extends Container {
	@Nullable
	private final InventoryItem inventory;
	public List<ItemStack> acceptedItems = new ArrayList<>();
	private int numRows = 3;

	public ContainerSilkBundle(EntityPlayer player, InventoryPlayer playerInventory, @Nullable InventoryItem itemInventory) {
		this.inventory = itemInventory;
		
		acceptedItems.add(EnumItemCrushed.GROUND_BLUE_EYED_GRASS.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_BLOOD_SNAIL_SHELL.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_PALE_GRASS.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_MILKWEED.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_GOLDEN_CLUB.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_MARSH_MARIGOLD.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_DEEP_WATER_CORAL.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_MARSH_HIBISCUS.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_BUTTON_BUSH.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_SWAMP_KELP.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_POISON_IVY.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_PICKEREL_WEED.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_COPPER_IRIS.create(1));
		acceptedItems.add(EnumItemCrushed.GROUND_ANGLER_TOOTH.create(1));
		acceptedItems.add(EnumItemMisc.CREMAINS.create(1));
		acceptedItems.add(EnumItemMisc.TAR_DRIP.create(1)); //temp

		if(this.inventory == null || this.inventory.isEmpty()) {
			return;
		}

		this.numRows = this.inventory.getSizeInventory() / 9;
		int yOffset = (this.numRows - 4) * 18;

		//new slot restriction list with meta
		this.addSlotToContainer(new SlotRestrictionListWithMeta(itemInventory, 0, 98, 4, getItemList(), 1, this));
		this.addSlotToContainer(new SlotRestrictionListWithMeta(itemInventory, 1, 116, 4, getItemList(), 1, this));
		this.addSlotToContainer(new SlotRestrictionListWithMeta(itemInventory, 2, 134, 4, getItemList(), 1, this));
		this.addSlotToContainer(new SlotRestrictionListWithMeta(itemInventory, 3, 152, 4, getItemList(), 1, this));
				

		for (int row = 0; row < 3; ++row) {
			for (int column = 0; column < 9; ++column) {
				this.addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 103 + row * 18 + yOffset));
			}
		}

		for (int column = 0; column < 9; ++column) {
			this.addSlotToContainer(new Slot(playerInventory, column, 8 + column * 18, 161 + yOffset));
		}
	}

	public List<ItemStack> getItemList() {
		return acceptedItems;
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