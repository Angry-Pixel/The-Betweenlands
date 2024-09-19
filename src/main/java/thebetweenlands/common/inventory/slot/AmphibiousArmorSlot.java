package thebetweenlands.common.inventory.slot;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.common.component.item.UpgradeDamage;
import thebetweenlands.common.inventory.container.AmphibiousArmorContainer;
import thebetweenlands.common.items.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.items.amphibious.ArmorEffectHelper;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

public class AmphibiousArmorSlot extends Slot {

	private final AmphibiousArmorContainer container;
	private ItemStack prevStack;

	public AmphibiousArmorSlot(AmphibiousArmorContainer container, int slotIndex, int x, int y) {
		super(container, slotIndex, x, y);
		this.container = container;
		this.prevStack = container.getItem(slotIndex).copy();
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (this.container.getContainerStack().getItem() instanceof AmphibiousArmorItem armor) {
			return ArmorEffectHelper.getUpgrade(armor.getType().getSlot(), stack) != AmphibiousArmorUpgradeRegistry.NONE;
		}
		return super.mayPlace(stack);
	}

	@Override
	public void onTake(Player player, ItemStack stack) {
		this.container.setPauseUpgradeDamageUpdates(true);
		ItemStack invItem = this.container.getContainerStack();

		if (invItem.getItem() instanceof AmphibiousArmorItem armor) {
			Holder<AmphibiousArmorUpgrade> upgrade = ArmorEffectHelper.getUpgrade(armor.getType().getSlot(), stack);

			if (upgrade != AmphibiousArmorUpgradeRegistry.NONE) {
				var damage = stack.remove(DataComponentRegistry.UPGRADE_DAMAGE);

				if (damage != null) {
					int invCount = this.container.getItem(this.getSlotIndex()).getCount();

					if (invCount == 0 && stack.getCount() > 1) {
						//Don't allow taking last item when picking up multiple items
						this.container.setItem(this.getSlotIndex(), stack.split(1));
						this.container.getItem(this.getSlotIndex()).set(DataComponentRegistry.UPGRADE_DAMAGE, new UpgradeDamage(damage.damage(), upgrade.value().getMaxDamage()));
					} else if (invCount == 0 && stack.getCount() == 1) {
						//Transfer stored damage on stack
						stack.set(DataComponentRegistry.UPGRADE_DAMAGE, new UpgradeDamage(damage.damage(), upgrade.value().getMaxDamage()));
					}
				}
			}
		}
		this.container.setPauseUpgradeDamageUpdates(false);

		super.onTake(player, stack);
	}

	@Override
	public void setChanged() {
		if (this.container.getContainerStack().getItem() instanceof AmphibiousArmorItem armor) {
			EquipmentSlot slot = armor.getType().getSlot();

			ItemStack currentStack = this.container.getItem(this.getSlotIndex());

			if (!ItemStack.isSameItemSameComponents(this.prevStack, currentStack)) {
				Holder<AmphibiousArmorUpgrade> prevUpgrade = ArmorEffectHelper.getUpgrade(slot, this.prevStack);
				if (prevUpgrade != AmphibiousArmorUpgradeRegistry.NONE) {
					prevUpgrade.value().onChanged(slot, this.container.getContainerStack(), this.prevStack);
				}

				Holder<AmphibiousArmorUpgrade> newUpgrade = ArmorEffectHelper.getUpgrade(slot, currentStack);
				if (newUpgrade != AmphibiousArmorUpgradeRegistry.NONE) {
					newUpgrade.value().onChanged(slot, this.container.getContainerStack(), currentStack);
				}
			}

			this.prevStack = currentStack.copy();
		}
	}
}
