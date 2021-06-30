package thebetweenlands.common.inventory.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IAmphibianArmorUpgrade;
import thebetweenlands.common.inventory.InventoryAmphibianArmor;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.slot.SlotInvRestriction;
import thebetweenlands.common.item.armor.amphibian.AmphibianArmorUpgrades;
import thebetweenlands.common.item.armor.amphibian.ItemAmphibianArmor;

public class ContainerAmphibiousArmour extends Container {
	public static class SlotUpgrade extends SlotInvRestriction {

		private final InventoryItem inventoryItem;

		public SlotUpgrade(InventoryItem inventory, int slotIndex, int x, int y) {
			super(inventory, slotIndex, x, y);
			this.inventoryItem = inventory;
		}

		@Override
		public ItemStack onTake(EntityPlayer player, ItemStack stack) {
			ItemStack ret = super.onTake(player, stack);

			InventoryAmphibianArmor inv = this.inventory instanceof InventoryAmphibianArmor ? (InventoryAmphibianArmor) this.inventory : null;

			try {
				if(inv != null) {
					inv.setPauseUpgradeDamageUpdates(true);
				}

				ItemStack invItem = this.inventoryItem.getInventoryItemStack();

				if(invItem.getItem() instanceof ItemAmphibianArmor) {
					IAmphibianArmorUpgrade upgrade = AmphibianArmorUpgrades.getUpgrade(((ItemAmphibianArmor) invItem.getItem()).armorType, stack);

					if(upgrade != null) {
						int damage = ((ItemAmphibianArmor) invItem.getItem()).getUpgradeDamage(invItem, this.getSlotIndex());

						if(damage > 0) {

							int invCount = this.inventory.getStackInSlot(this.getSlotIndex()).getCount();

							if(invCount == 0 && stack.getCount() > 1) {
								//Don't allow taking last item when picking up multiple items
								this.inventory.setInventorySlotContents(this.getSlotIndex(), ret.splitStack(1));
							} else if(invCount == 0 && stack.getCount() == 1) {
								//Transfer upgrade damage of armor to stored damage on stack
								ItemAmphibianArmor.setUpgradeItemStoredDamage(ret, damage, upgrade.getMaxDamage());
								((ItemAmphibianArmor) invItem.getItem()).setUpgradeDamage(invItem, this.getSlotIndex(), 0, 0);
							}

						}
					}
				}
			} finally {
				if(inv != null) {
					inv.setPauseUpgradeDamageUpdates(false);
				}
			}

			return ret;
		}

	}

	@Nullable
	private final InventoryItem inventory;

	private final int numSlots = 3;

	public ContainerAmphibiousArmour(EntityPlayer player, InventoryItem inventoryItem) {
		InventoryPlayer playerInventory = player.inventory;
		this.inventory = inventoryItem;

		addSlotToContainer(new SlotUpgrade(inventoryItem, 0, 43, 43));
		addSlotToContainer(new SlotUpgrade(inventoryItem, 1, 79, 43));
		addSlotToContainer(new SlotUpgrade(inventoryItem, 2, 115, 43));

		for (int l = 0; l < 3; ++l)
			for (int j1 = 0; j1 < 9; ++j1)
				this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 7 + j1 * 18, 101 + l * 18));

		for (int i1 = 0; i1 < 9; ++i1)
			this.addSlotToContainer(new Slot(playerInventory, i1, 7 + i1 * 18, 159));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotIndex);

		if(slot != null && slot.getHasStack()) {
			ItemStack merge = slot.getStack();
			stack = merge.copy();

			if(slotIndex >= this.numSlots) {
				if(!this.mergeItemStack(merge, 0, this.numSlots, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				//Try to merge all but last item
				if(merge.getCount() > 1) {
					ItemStack lastItem = merge.splitStack(1);
					stack = merge.copy();

					this.mergeItemStack(merge, this.numSlots, this.inventorySlots.size(), false);

					lastItem.setCount(1 + merge.getCount());
					merge = lastItem;
					stack = merge.copy();

					InventoryAmphibianArmor inv = this.inventory instanceof InventoryAmphibianArmor ? (InventoryAmphibianArmor) this.inventory : null;

					try {
						if(inv != null) {
							inv.setPauseUpgradeDamageUpdates(true);
						}

						slot.putStack(merge);
						slot.onSlotChanged();
					} finally {
						if(inv != null) {
							inv.setPauseUpgradeDamageUpdates(false);
						}
					}
				}

				//Try to merge last item with upgrade damage applied
				if(merge.getCount() == 1) {
					ItemStack invItem = this.inventory.getInventoryItemStack();

					if(invItem.getItem() instanceof ItemAmphibianArmor) {
						IAmphibianArmorUpgrade upgrade = AmphibianArmorUpgrades.getUpgrade(((ItemAmphibianArmor) invItem.getItem()).armorType, stack);

						if(upgrade != null) {
							int damage = ((ItemAmphibianArmor) invItem.getItem()).getUpgradeDamage(invItem, slotIndex);

							if(damage > 0) {
								//Store upgrade damage in item before merging
								ItemAmphibianArmor.setUpgradeItemStoredDamage(merge, damage, upgrade.getMaxDamage());
							}
						}
					}

					if(!this.mergeItemStack(merge, this.numSlots, this.inventorySlots.size(), false)) {
						//Undo stored damage because merge failed
						ItemAmphibianArmor.setUpgradeItemStoredDamage(merge, 0, 0);
						return ItemStack.EMPTY;
					} else if(invItem.getItem() instanceof ItemAmphibianArmor) {
						//Remove upgrade damage since it is now stored in item
						((ItemAmphibianArmor) invItem.getItem()).setUpgradeDamage(invItem, slotIndex, 0, 0);
					}
				}
			}

			if(merge.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if(merge.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}
		}

		return stack;
	}

	public InventoryItem getItemInventory() {
		return this.inventory;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		if(this.getItemInventory().getInventoryItemStack().isEmpty()) {
			return false;
		}

		//Check if armor is in main inventory
		for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
			if(player.inventory.getStackInSlot(i) == this.inventory.getInventoryItemStack()) {
				return true;
			}
		}

		return false;
	}
}