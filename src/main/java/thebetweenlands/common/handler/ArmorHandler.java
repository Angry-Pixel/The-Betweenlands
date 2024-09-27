package thebetweenlands.common.handler;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import thebetweenlands.common.component.item.AmphibiousUpgrades;
import thebetweenlands.common.inventory.slot.AmphibiousArmorSlot;
import thebetweenlands.common.item.armor.LurkerSkinArmorItem;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.ArmorMaterialRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ArmorHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ArmorHandler::ignoreDamageWhenStackingAmphibiousUpgrades);
		NeoForge.EVENT_BUS.addListener(ArmorHandler::modifyBreakSpeedWithLurkerArmor);
		NeoForge.EVENT_BUS.addListener(ArmorHandler::protectFromMagicDamage);
		NeoForge.EVENT_BUS.addListener(ArmorHandler::reduceFireDamageWithSyrmorite);
	}

	private static void reduceFireDamageWithSyrmorite(LivingDamageEvent.Pre event) {
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

	private static void modifyBreakSpeedWithLurkerArmor(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();

		if (player.isEyeInFluid(Tags.Fluids.WATER)) {
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

	private static void ignoreDamageWhenStackingAmphibiousUpgrades(ItemStackedOnOtherEvent event) {
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

	private static void protectFromMagicDamage(LivingIncomingDamageEvent event) {
		if(event.getSource().is(Tags.DamageTypes.IS_MAGIC)) {
			float damageMultiplier = 1.0F;

			LivingEntity entityHit = event.getEntity();

			ItemStack boots = entityHit.getItemBySlot(EquipmentSlot.FEET);
			ItemStack legs = entityHit.getItemBySlot(EquipmentSlot.LEGS);
			ItemStack chest = entityHit.getItemBySlot(EquipmentSlot.CHEST);
			ItemStack helm = entityHit.getItemBySlot(EquipmentSlot.HEAD);

			if (!boots.isEmpty() && boots.is(ItemRegistry.ANCIENT_BOOTS) && boots.getDamageValue() < boots.getMaxDamage())
				damageMultiplier -= 0.125F;
			if (!legs.isEmpty()  && legs.is(ItemRegistry.ANCIENT_LEGGINGS) && legs.getDamageValue() < legs.getMaxDamage())
				damageMultiplier -= 0.125F;
			if (!chest.isEmpty() && chest.is(ItemRegistry.ANCIENT_CHESTPLATE) && chest.getDamageValue() < chest.getMaxDamage())
				damageMultiplier -= 0.125F;
			if (!helm.isEmpty() && helm.is(ItemRegistry.ANCIENT_HELMET) && helm.getDamageValue() < helm.getMaxDamage())
				damageMultiplier -= 0.125F;

			event.setAmount(event.getAmount() * damageMultiplier);
		}
	}
}
