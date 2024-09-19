package thebetweenlands.common.handler;

import net.minecraft.core.NonNullList;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thebetweenlands.common.component.item.AmphibiousUpgrades;
import thebetweenlands.common.inventory.slot.AmphibiousArmorSlot;
import thebetweenlands.common.items.LurkerSkinArmorItem;
import thebetweenlands.common.items.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.ArmorMaterialRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

public class ArmorHandler {

	static void reduceFireDamageWithSyrmorite(LivingDamageEvent.Pre event) {
		DamageSource source = event.getSource();
		LivingEntity entity = event.getEntity();

		if (source.is(DamageTypeTags.IS_FIRE)) {
			float damageMultiplier = 1;
			Iterable<ItemStack> armorStacks = entity.getArmorAndBodyArmorSlots();
			float reductionAmount = 0.25F;
			for (ItemStack stack : armorStacks) {
				if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem armor && armor.getMaterial().is(ArmorMaterialRegistry.SYRMORITE)) {
					damageMultiplier -= reductionAmount;
				}
			}
			if (damageMultiplier < 0.001F) {
				event.setNewDamage(0.01F); //Set to tiny amount so armor still takes damage
				entity.clearFire();
			} else {
				event.setNewDamage(event.getOriginalDamage() * damageMultiplier);
			}
		}
	}

	static void modifyBreakSpeedWithLurkerArmor(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();

		if (player.isEyeInFluid(FluidTags.WATER)) {
			int pieces = 0;
			boolean fullSetAmphibious = true;
			boolean hasMiningUpgrade = false;

			for (ItemStack stack : player.getInventory().armor) {
				if (stack.getItem() instanceof LurkerSkinArmorItem) {
					fullSetAmphibious = false;
					pieces++;
				} else if (!(stack.getItem() instanceof AmphibiousArmorItem)) {
					fullSetAmphibious = false;
				} else if (stack.getOrDefault(DataComponentRegistry.AMPHIBIOUS_UPGRADES, AmphibiousUpgrades.EMPTY).getAllUniqueUpgradesWithCounts().containsKey(AmphibiousArmorUpgradeRegistry.MINING_SPEED)) {
					hasMiningUpgrade = true;
				}
			}

			// only give full mining speed if we have full amphibious set + the mining upgrade
			if (pieces == 0 && fullSetAmphibious && hasMiningUpgrade) {
				pieces = 4;
			}

			if (pieces != 0) {
				event.setNewSpeed(event.getNewSpeed() * (5.0F * (player.onGround() ? 1.0F : 5.0F) / 4.0F * pieces));
			}
		}
	}

	static void ignoreDamageWhenStackingAmphibiousUpgrades(ItemStackedOnOtherEvent event) {
		if (event.getSlot() instanceof AmphibiousArmorSlot slot && !event.getStackedOnItem().isEmpty() && !event.getCarriedItem().isEmpty()) {
			if (event.getCarriedItem().has(DataComponentRegistry.UPGRADE_DAMAGE) && !event.getStackedOnItem().has(DataComponentRegistry.UPGRADE_DAMAGE)) {
				event.setCanceled(true);
				var damage = event.getCarriedItem().remove(DataComponentRegistry.UPGRADE_DAMAGE);
				int k3 = event.getClickAction() == ClickAction.PRIMARY ? event.getStackedOnItem().getCount() : 1;
				event.getCarriedSlotAccess().set(slot.safeInsert(event.getStackedOnItem(), k3));
				event.getCarriedItem().set(DataComponentRegistry.UPGRADE_DAMAGE, damage);
			}
		}
	}
}
