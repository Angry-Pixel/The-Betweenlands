package thebetweenlands.common.inventory.container;

import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.common.component.item.AmphibiousUpgrades;
import thebetweenlands.common.component.item.UpgradeDamage;
import thebetweenlands.common.items.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.items.amphibious.ArmorEffectHelper;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

public class AmphibiousArmorContainer implements Container {

	private final ArmorItem.Type armorType;
	private final AmphibiousArmorItem item;

	private boolean pauseUpgradeDamageUpdates = false;
	private boolean useUpgradeFilter = false;
	private boolean changed = false;

	private final ItemStack stack;
	private final int slots;
	private final NonNullList<AmphibiousUpgrades.UpgradeEntry> contents;

	public AmphibiousArmorContainer(ItemStack stack, int slots) {
		this.stack = stack;
		this.slots = slots;
		this.contents = NonNullList.withSize(this.slots, AmphibiousUpgrades.UpgradeEntry.EMPTY);
		AmphibiousUpgrades upgrades = stack.getOrDefault(DataComponentRegistry.AMPHIBIOUS_UPGRADES, AmphibiousUpgrades.EMPTY);
		for (int i = 0; i < this.slots; i++) {
			if (upgrades.getSlots() > i) {
				this.contents.set(i, upgrades.getEntryInSlot(i));
			} else {
				this.contents.set(i, AmphibiousUpgrades.UpgradeEntry.EMPTY);
			}
		}
		this.item = (AmphibiousArmorItem) stack.getItem();
		this.armorType = this.item.getType();
	}

	public ItemStack getContainerStack() {
		return this.stack;
	}

	@Override
	public int getContainerSize() {
		return this.slots;
	}

	@Override
	public boolean isEmpty() {
		for (AmphibiousUpgrades.UpgradeEntry itemstack : this.contents) {
			if (!itemstack.stack().isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.contents.get(slot).stack();
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack itemstack = slot >= 0 && slot < this.contents.size() && !this.contents.get(slot).stack().isEmpty() && amount > 0 ? this.contents.get(slot).stack().split(amount) : ItemStack.EMPTY;
		if (!itemstack.isEmpty()) {
			this.setChanged();
		} else {
			this.contents.set(slot, AmphibiousUpgrades.UpgradeEntry.EMPTY);
			this.setChanged();
		}

		return itemstack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return slot >= 0 && slot < this.contents.size() ? this.contents.set(slot, AmphibiousUpgrades.UpgradeEntry.EMPTY).stack() : ItemStack.EMPTY;
	}

	@Override
	public void setChanged() {
		this.changed = true;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		this.contents.clear();
	}

	@Override
	public void stopOpen(Player player) {
		if (this.changed) {
			this.stack.set(DataComponentRegistry.AMPHIBIOUS_UPGRADES, AmphibiousUpgrades.fromEntries(this.contents));
			ArmorEffectHelper.updateAttributes(this.stack);
		}
	}

	@Override
	public int getMaxStackSize() {
		return 3;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		Holder<AmphibiousArmorUpgrade> upgrade = ArmorEffectHelper.getUpgrade(this.armorType.getSlot(), stack);

		if (!(stack.getItem() instanceof AmphibiousArmorItem) && upgrade != AmphibiousArmorUpgradeRegistry.NONE &&
			(this.getItem(slot).isEmpty() || (ItemStack.isSameItem(this.getItem(slot).copy().split(1), stack.copy().split(1)) && stack.getOrDefault(DataComponentRegistry.UPGRADE_DAMAGE, UpgradeDamage.EMPTY).damage() == 0)) /*needed to preserve upgrade damage*/) {

			if (this.useUpgradeFilter) {
				ItemStack filter = this.getFilterItem(this.getContainerStack(), slot);

				if (!filter.isEmpty()) {
					ItemStack copy = stack.copy().split(1);
					copy.remove(DataComponentRegistry.UPGRADE_DAMAGE);

					if (!ItemStack.isSameItem(filter, copy)) {
						return false;
					}
				}
			}

			for (int i = 0; i < this.getContainerSize(); i++) {
				ItemStack otherStack = this.getItem(i);

				if (otherStack != null) {
					Holder<AmphibiousArmorUpgrade> otherUpgrade = ArmorEffectHelper.getUpgrade(this.armorType.getSlot(), otherStack);

					if (otherUpgrade != AmphibiousArmorUpgradeRegistry.NONE && (otherUpgrade.value().isBlacklisted(upgrade) || upgrade.value().isBlacklisted(otherUpgrade))) {
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		Holder<AmphibiousArmorUpgrade> upgrade = ArmorEffectHelper.getUpgrade(this.armorType.getSlot(), stack);
		UpgradeDamage damage = UpgradeDamage.EMPTY;
		if (!this.pauseUpgradeDamageUpdates && !this.getItem(slot).isEmpty()) {
			damage = this.getItem(slot).remove(DataComponentRegistry.UPGRADE_DAMAGE);
		}

		if (!stack.isEmpty()) {
			this.setFilterItem(this.getContainerStack(), slot, stack);
		}

		this.contents.set(slot, new AmphibiousUpgrades.UpgradeEntry(upgrade, stack));
		stack.limitSize(this.getMaxStackSize(stack));
		if (damage != null && damage.damage() != 0) {
			stack.set(DataComponentRegistry.UPGRADE_DAMAGE, damage);
		}
		this.setChanged();
	}

	public void setPauseUpgradeDamageUpdates(boolean pause) {
		this.pauseUpgradeDamageUpdates = pause;
	}

	public void setUseUpgradeFilter(boolean use) {
		this.useUpgradeFilter = use;
	}

	private ItemStack getFilterItem(ItemStack stack, int slot) {
		if (!stack.has(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS) || stack.get(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS).getSlots() < this.slots) {
			stack.set(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS, ItemContainerContents.fromItems(NonNullList.withSize(this.slots, ItemStack.EMPTY)));
		}
		return stack.get(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS).getStackInSlot(slot);
	}

	private void setFilterItem(ItemStack stack, int slot, ItemStack filterStack) {
		if (!stack.has(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS) || stack.get(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS).getSlots() < this.slots) {
			stack.set(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS, ItemContainerContents.fromItems(NonNullList.withSize(this.slots, ItemStack.EMPTY)));
		}
		NonNullList<ItemStack> itemsCopy = NonNullList.withSize(this.slots, ItemStack.EMPTY);
		stack.get(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS).copyInto(itemsCopy);
		itemsCopy.set(slot, filterStack);
		stack.set(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS, ItemContainerContents.fromItems(itemsCopy));
	}
}
