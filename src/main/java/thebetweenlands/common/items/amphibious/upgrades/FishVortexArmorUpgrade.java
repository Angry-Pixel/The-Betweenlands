package thebetweenlands.common.items.amphibious.upgrades;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.TickingAmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.ToggleableAmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.TriggerableAmphibiousArmorUpgrade;
import thebetweenlands.common.items.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.items.amphibious.ArmorEffectHelper;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;

import java.util.function.Predicate;

public class FishVortexArmorUpgrade extends SimpleAmphibiousArmorUpgrade implements TickingAmphibiousArmorUpgrade, ToggleableAmphibiousArmorUpgrade, TriggerableAmphibiousArmorUpgrade {
	public FishVortexArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, EquipmentSlot... armorTypes) {
		super(maxDamage, damageEvent, matcher, armorTypes);
	}

	@Override
	public void onArmorTick(Level level, Player player, ItemStack stack, int upgradeCount, int armorCount) {
		if (player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).getBoolean("vortex")) {
			if (level.getGameTime() % 200 == 0) { //TODO dunno about timings yet
				if (!level.isClientSide() && level.getDifficulty() != Difficulty.PEACEFUL) {
					if (ArmorEffectHelper.activateFishVortex(level, player, upgradeCount)) {
						AmphibiousArmorItem.damageUpgrade(stack, player, this, 1, AmphibiousArmorUpgrade.DamageEvent.ON_USE, false);
					}
				}
			}
		}
	}

	@Override
	public boolean onToggle(Level level, Player player, ItemStack heldStack) {
		boolean existingValue = player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).getBoolean("vortex");
		player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).putBoolean("vortex", !existingValue);
		return !existingValue;
	}

	@Override
	public boolean onTrigger(Level level, Player player, ItemStack heldStack, ItemStack armorStack, int upgradeAmount) {
		if (!level.isClientSide() && level.getDifficulty() != Difficulty.PEACEFUL) {
			ArmorEffectHelper.activateFishVortex(level, player, upgradeAmount);
			AmphibiousArmorItem.damageUpgrade(armorStack, player,this, 1, AmphibiousArmorUpgrade.DamageEvent.ON_USE, false);
			return true;
		}
		return false;
	}
}
