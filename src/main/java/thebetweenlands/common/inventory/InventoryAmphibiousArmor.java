package thebetweenlands.common.inventory;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IAmphibiousArmorUpgrade;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorUpgrades;
import thebetweenlands.common.item.armor.amphibious.ItemAmphibiousArmor;

public class InventoryAmphibiousArmor extends InventoryItem {

	private final EntityEquipmentSlot armorType;
	private final ItemAmphibiousArmor item;

	private boolean pauseUpgradeDamageUpdates = false;
	private boolean useUpgradeFilter = false;

	public InventoryAmphibiousArmor(ItemStack stack, String inventoryName) {
		super(stack, ((ItemAmphibiousArmor) stack.getItem()).getUpgradeSlotCount(stack), inventoryName);
		this.item = (ItemAmphibiousArmor) stack.getItem();
		this.armorType = this.item.armorType;
	}

	@Override
	public int getInventoryStackLimit() {
		return 3;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		IAmphibiousArmorUpgrade upgrade = AmphibiousArmorUpgrades.getUpgrade(this.armorType, stack);

		if(stack.getItem() instanceof ItemAmphibiousArmor == false && upgrade != null &&
				(this.getStackInSlot(slot).isEmpty() || (ItemStack.areItemStacksEqual(this.getStackInSlot(slot).copy().splitStack(1), stack.copy().splitStack(1)) && ItemAmphibiousArmor.getUpgradeItemStoredDamage(stack) == 0)) /*needed to preserve upgrade damage*/) {

			if(this.useUpgradeFilter) {
				ItemStack filter = this.item.getUpgradeFilter(this.getInventoryItemStack(), slot);

				if(!filter.isEmpty()) {
					ItemStack copy = stack.copy().splitStack(1);
					ItemAmphibiousArmor.setUpgradeItemStoredDamage(copy, 0, 0);
					
					if(!ItemStack.areItemStacksEqual(filter, copy)) {
						return false;
					}
				}
			}

			for(int i = 0; i < this.getSizeInventory(); i++) {
				ItemStack otherStack = this.getStackInSlot(i);

				if(otherStack != null) {
					IAmphibiousArmorUpgrade otherUpgrade = AmphibiousArmorUpgrades.getUpgrade(this.armorType, otherStack);

					if(otherUpgrade != null && (otherUpgrade.isBlacklisted(upgrade) || upgrade.isBlacklisted(otherUpgrade))) {
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(!this.pauseUpgradeDamageUpdates && this.getStackInSlot(slot).isEmpty()) {
			this.item.setUpgradeDamage(this.getInventoryItemStack(), slot, 0, 0);

			IAmphibiousArmorUpgrade upgrade = AmphibiousArmorUpgrades.getUpgrade(this.armorType, stack);

			if(upgrade != null) {
				int maxDamage = upgrade.getMaxDamage();
				int damage = ItemAmphibiousArmor.getUpgradeItemStoredDamage(stack);
				this.item.setUpgradeDamage(this.getInventoryItemStack(), slot, damage, maxDamage); //Transfer stored damage to armor
				ItemAmphibiousArmor.setUpgradeItemStoredDamage(stack, 0, 0); //Remove stored damage to make it stackable again
			}
		}

		if(!stack.isEmpty()) {
			this.item.setUpgradeFilter(this.getInventoryItemStack(), slot, stack);
		}

		super.setInventorySlotContents(slot, stack);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.item.setUpgradeCounts(this.getInventoryItemStack(), this);
	}

	public void setPauseUpgradeDamageUpdates(boolean pause) {
		this.pauseUpgradeDamageUpdates = pause;
	}

	public void setUseUpgradeFilter(boolean use) {
		this.useUpgradeFilter = use;
	}
}
