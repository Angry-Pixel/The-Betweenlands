package thebetweenlands.common.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.registries.ItemRegistry;

public class ContainerDraetonUpgrades extends Container {
	private static class MainUpgradeSlot extends Slot {
		private final EntityDraeton draeton;

		private MainUpgradeSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, EntityDraeton draeton) {
			super(inventoryIn, index, xPosition, yPosition);
			this.draeton = draeton;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return this.draeton.isCraftingUpgrade(stack) || this.draeton.isFurnaceUpgrade(stack) || this.draeton.isStorageUpgrade(stack);
		}
	}

	private static class AnchorUpgradeSlot extends Slot {
		private final EntityDraeton draeton;

		private AnchorUpgradeSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, EntityDraeton draeton) {
			super(inventoryIn, index, xPosition, yPosition);
			this.draeton = draeton;
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return this.draeton.isAnchorUpgrade(stack);
		}
	}

	private static class PullerSlot extends Slot {
		private final EntityDraeton draeton;

		public PullerSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, EntityDraeton draeton) {
			super(inventoryIn, index, xPosition, yPosition);
			this.draeton = draeton;
		}

		@Override
		public void onSlotChanged() {
			super.onSlotChanged();
			this.draeton.onPullerSlotChanged(this.slotNumber);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			return ItemRegistry.CRITTER.isCapturedEntity(stack, EntityDragonFly.class) || ItemRegistry.CRITTER.isCapturedEntity(stack, EntityFirefly.class) || ItemRegistry.CRITTER.isCapturedEntity(stack, EntityChiromawTame.class);
		}
	}

	private final EntityDraeton draeton;

	public ContainerDraetonUpgrades(InventoryPlayer playerInventory, EntityDraeton draeton) {
		this.draeton = draeton;

		addSlotToContainer(new PullerSlot(draeton.getPullersInventory(), 0, 8, 23, draeton));
		addSlotToContainer(new PullerSlot(draeton.getPullersInventory(), 1, 30, 16, draeton));
		addSlotToContainer(new PullerSlot(draeton.getPullersInventory(), 2, 52, 12, draeton));
		addSlotToContainer(new PullerSlot(draeton.getPullersInventory(), 3, 114, 12, draeton));
		addSlotToContainer(new PullerSlot(draeton.getPullersInventory(), 4, 136, 16, draeton));
		addSlotToContainer(new PullerSlot(draeton.getPullersInventory(), 5, 158, 23, draeton));

		addSlotToContainer(new MainUpgradeSlot(draeton.getUpgradesInventory(), 0, 52, 53, draeton));
		addSlotToContainer(new MainUpgradeSlot(draeton.getUpgradesInventory(), 1, 114, 53, draeton));
		addSlotToContainer(new MainUpgradeSlot(draeton.getUpgradesInventory(), 2, 52, 110, draeton));
		addSlotToContainer(new MainUpgradeSlot(draeton.getUpgradesInventory(), 3, 114, 110, draeton));
		addSlotToContainer(new AnchorUpgradeSlot(draeton.getUpgradesInventory(), 4, 83, 81, draeton));
		addSlotToContainer(new Slot(draeton.getUpgradesInventory(), 5, 83, 35));

		for (int y = 0; y < 3; ++y)
			for (int x = 0; x < 9; ++x)
				addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 11 + x * 18, 175 + y * 18));

		for (int y = 0; y < 9; ++y)
			addSlotToContainer(new Slot(playerInventory, y, 11 + y * 18, 233));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.draeton.getDistanceSq(player) <= 64.0D;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack is = ItemStack.EMPTY;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack is1 = slot.getStack();
			is = is1.copy();

			if (slotIndex < 12) {
				if (!mergeItemStack(is1, 12, this.inventorySlots.size(), false))
					return ItemStack.EMPTY;
			} else if (!mergeItemStack(is1, 0, 12, false))
				return ItemStack.EMPTY;

			if (is1.getCount() == 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return is;
	}
}