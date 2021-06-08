package thebetweenlands.common.inventory;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.armor.amphibian.AmphibianArmorUpgrades;
import thebetweenlands.common.item.armor.amphibian.ItemAmphibianArmor;

public class InventoryAmphibianArmor extends InventoryItem {

	private final EntityEquipmentSlot armorType;
	private final ItemAmphibianArmor item;

	public InventoryAmphibianArmor(ItemStack stack, String inventoryName) {
		super(stack, 3, inventoryName);
		this.item = (ItemAmphibianArmor) stack.getItem();
		this.armorType = this.item.armorType;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack.getItem() instanceof ItemAmphibianArmor == false && AmphibianArmorUpgrades.getUpgrade(this.armorType, stack) != null;
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		this.item.setUpgradeCounts(this.getInventoryItemStack(), this);
	}

}
