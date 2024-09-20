package thebetweenlands.common.item.armor.amphibious.upgrades;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.TickingAmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.ToggleableAmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.TriggerableAmphibiousArmorUpgrade;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.item.armor.amphibious.ArmorEffectHelper;

import java.util.function.Predicate;

public class UrchinSpikeArmorUpgrade extends SimpleAmphibiousArmorUpgrade implements TickingAmphibiousArmorUpgrade, ToggleableAmphibiousArmorUpgrade, TriggerableAmphibiousArmorUpgrade {

	public UrchinSpikeArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, EquipmentSlot... armorTypes) {
		super(maxDamage, damageEvent, matcher, armorTypes);
	}

	@Override
	public void onArmorTick(Level level, Player player, ItemStack stack, int upgradeCount, int armorCount) {
		// more upgrades do more damage at 2F * urchinCount ;)
		if (player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).getBoolean("urchin")) {
			long urchinAOECooldown = player.getPersistentData().getLong(AmphibiousArmorItem.NBT_URCHIN_AOE_COOLDOWN);
			if (level.getGameTime() % 10 == 0 && level.getGameTime() >= urchinAOECooldown) {
				if (!level.isClientSide() && level.getDifficulty() != Difficulty.PEACEFUL) {
					if (ArmorEffectHelper.activateUrchinSpikes(level, player, upgradeCount)) {
						AmphibiousArmorItem.damageUpgrade(stack, player, this, 1, DamageEvent.ON_USE, false);
					}
				}
			}
		}
	}

	@Override
	public boolean onToggle(Level level, Player player, ItemStack heldStack) {
		boolean existingValue = player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).getBoolean("urchin");
		player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).putBoolean("urchin", !existingValue);
		return !existingValue;
	}

	@Override
	public boolean onTrigger(Level level, Player player, ItemStack heldStack, ItemStack armorStack, int upgradeAmount) {
		long urchinAOECooldown = player.getPersistentData().getLong(AmphibiousArmorItem.NBT_URCHIN_AOE_COOLDOWN);
		if (level.getGameTime() >= urchinAOECooldown) {
			if (!level.isClientSide() && level.getDifficulty() != Difficulty.PEACEFUL) {
				ArmorEffectHelper.spawnUrchinSpikes(level, player, upgradeAmount);
				player.getPersistentData().putLong(AmphibiousArmorItem.NBT_URCHIN_AOE_COOLDOWN, level.getGameTime() + 50);
				AmphibiousArmorItem.damageUpgrade(armorStack, player, this, 1, AmphibiousArmorUpgrade.DamageEvent.ON_USE, false);
				return true;
			}
		}
		return false;
	}
}
