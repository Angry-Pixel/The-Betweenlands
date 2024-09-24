package thebetweenlands.common.item.armor.amphibious.upgrades;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.api.item.amphibious.TickingAmphibiousArmorUpgrade;

import java.util.function.Predicate;

public class VisibilityArmorUpgrade extends SimpleAmphibiousArmorUpgrade implements TickingAmphibiousArmorUpgrade {
	public VisibilityArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, EquipmentSlot... armorTypes) {
		super(maxDamage, damageEvent, matcher, armorTypes);
	}

	@Override
	public void onArmorTick(Level level, Player player, ItemStack stack, int upgradeCount, int armorCount) {
		if (player.isEyeInFluid(FluidTags.WATER) && upgradeCount >= 1) {
			player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 10, 0));
		}
	}
}
