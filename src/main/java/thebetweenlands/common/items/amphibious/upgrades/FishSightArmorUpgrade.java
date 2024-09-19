package thebetweenlands.common.items.amphibious.upgrades;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.item.amphibious.TickingAmphibiousArmorUpgrade;
import thebetweenlands.common.entities.fishing.anadia.Anadia;
import thebetweenlands.common.items.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;

import java.util.function.Predicate;

public class FishSightArmorUpgrade extends SimpleAmphibiousArmorUpgrade implements TickingAmphibiousArmorUpgrade {
	public FishSightArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, EquipmentSlot... armorTypes) {
		super(maxDamage, damageEvent, matcher, armorTypes);
	}

	@Override
	public void onArmorTick(Level level, Player player, ItemStack stack, int upgradeCount, int armorCount) {
		if (!player.isEyeInFluid(FluidTags.WATER) && player.tickCount % 40 == 0) {
			int radius = upgradeCount * 8;

			AABB aabb = new AABB(player.blockPosition()).inflate(radius);

			for(Anadia anadia : level.getEntitiesOfClass(Anadia.class, aabb, a -> a.distanceToSqr(player) <= radius * radius)) {
				if(anadia.getGlowTimer() <= 0) {
					anadia.setGlowTimer(200);
					AmphibiousArmorItem.damageUpgrade(stack, player, this, 1, DamageEvent.ON_USE, false);
				}
			}
		}
	}
}
