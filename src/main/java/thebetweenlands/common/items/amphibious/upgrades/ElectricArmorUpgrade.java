package thebetweenlands.common.items.amphibious.upgrades;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.TickingAmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.ToggleableAmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.TriggerableAmphibiousArmorUpgrade;
import thebetweenlands.common.component.item.AmphibiousUpgrades;
import thebetweenlands.common.items.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.items.amphibious.ArmorEffectHelper;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.function.Predicate;

public class ElectricArmorUpgrade extends SimpleAmphibiousArmorUpgrade implements TickingAmphibiousArmorUpgrade, ToggleableAmphibiousArmorUpgrade, TriggerableAmphibiousArmorUpgrade {
	public ElectricArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, EquipmentSlot... armorTypes) {
		super(maxDamage, damageEvent, matcher, armorTypes);
	}

	@Override
	public void onArmorTick(Level level, Player player, ItemStack stack, int upgradeCount, int armorCount) {
		// count increases damage
		if (player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).getBoolean("electric")) {
			long electricCooldown = player.getPersistentData().getLong(AmphibiousArmorItem.NBT_ELECTRIC_COOLDOWN);
			if (player.hurtTime == player.hurtDuration && level.getGameTime() >= electricCooldown) {
				if (!level.isClientSide() && level.getDifficulty() != Difficulty.PEACEFUL) {
					if (ArmorEffectHelper.activateElectricEntity(level, player, upgradeCount)) {
						AmphibiousArmorItem.damageUpgrade(stack, player, this, 1, DamageEvent.ON_USE, false);
					}
				}
			}
		}
	}

	@Override
	public boolean onToggle(Level level, Player player, ItemStack heldStack) {
		boolean existingValue = player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).getBoolean("electric");
		player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).putBoolean("electric", !existingValue);
		return !existingValue;
	}

	@Override
	public boolean onTrigger(Level level, Player player, ItemStack heldStack, ItemStack armorStack, int upgradeAmount) {
		long electricCooldown = player.getPersistentData().getLong(AmphibiousArmorItem.NBT_ELECTRIC_COOLDOWN);
		if (level.getGameTime() >= electricCooldown) {
			if (!level.isClientSide() && level.getDifficulty() != Difficulty.PEACEFUL) {
				ArmorEffectHelper.spawnElectricEntity(level, player, player, upgradeAmount);
				player.getPersistentData().putLong(AmphibiousArmorItem.NBT_ELECTRIC_COOLDOWN, level.getGameTime() + 50);
				AmphibiousArmorItem.damageUpgrade(armorStack, player, this, 1, AmphibiousArmorUpgrade.DamageEvent.ON_USE, false);
				return true;
			}
		}
		return false;
	}
}
