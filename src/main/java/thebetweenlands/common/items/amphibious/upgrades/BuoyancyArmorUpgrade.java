package thebetweenlands.common.items.amphibious.upgrades;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.api.item.amphibious.TickingAmphibiousArmorUpgrade;

import java.util.function.Predicate;

public class BuoyancyArmorUpgrade extends SimpleAmphibiousArmorUpgrade implements TickingAmphibiousArmorUpgrade {
	public BuoyancyArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, EquipmentSlot... armorTypes) {
		super(maxDamage, damageEvent, matcher, armorTypes);
	}

	@Override
	public void onArmorTick(Level level, Player player, ItemStack stack, int upgradeCount, int armorCount) {
		if (player.isEyeInFluid(FluidTags.WATER)) {
			if (upgradeCount > 0) {
				double speedMod = upgradeCount * 0.01D;

				if (player.zza != 0) {
					speedMod *= 0.5D;
				}

				if (player.isShiftKeyDown()) {
					player.setDeltaMovement(player.getDeltaMovement().subtract(0.0D, speedMod, 0.0D));
				} else if (player.jumping) {
					player.setDeltaMovement(player.getDeltaMovement().add(0.0D, speedMod + 0.002D, 0.0D));
				}
			}
		}
	}
}
