package thebetweenlands.common.inventory;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IAmphibianArmorUpgrade;
import thebetweenlands.common.item.armor.amphibian.AmphibianArmorUpgrades;
import thebetweenlands.common.item.armor.amphibian.ItemAmphibianArmor;

public class InventoryAmphibianArmor extends InventoryItem {

	private final EntityEquipmentSlot armorType;
	private final ItemAmphibianArmor item;

	private boolean pauseUpgradeDamageUpdates = false;

	public InventoryAmphibianArmor(ItemStack stack, String inventoryName) {
		super(stack, 3, inventoryName);
		this.item = (ItemAmphibianArmor) stack.getItem();
		this.armorType = this.item.armorType;
	}

	@Override
	public int getInventoryStackLimit() {
		return 3;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack.getItem() instanceof ItemAmphibianArmor == false &&
				AmphibianArmorUpgrades.getUpgrade(this.armorType, stack) != null &&
				(this.getStackInSlot(slot).isEmpty() || (ItemStack.areItemStacksEqual(this.getStackInSlot(slot).copy().splitStack(1), stack.copy().splitStack(1)) && ItemAmphibianArmor.getUpgradeItemStoredDamage(stack) == 0)) /*needed to preserve upgrade damage*/;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(!this.pauseUpgradeDamageUpdates && this.getStackInSlot(slot).isEmpty()) {
			if(this.item instanceof ItemAmphibianArmor) {
				this.item.setUpgradeDamage(this.getInventoryItemStack(), slot, 0, 0);

				IAmphibianArmorUpgrade upgrade = AmphibianArmorUpgrades.getUpgrade(this.armorType, stack);

				if(upgrade != null) {
					int maxDamage = upgrade.getMaxDamage();
					int damage = ItemAmphibianArmor.getUpgradeItemStoredDamage(stack);
					this.item.setUpgradeDamage(this.getInventoryItemStack(), slot, damage, maxDamage); //Transfer stored damage to armor
					ItemAmphibianArmor.setUpgradeItemStoredDamage(stack, 0, 0); //Remove stored damage to make it stackable again
				}
			}
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
}
