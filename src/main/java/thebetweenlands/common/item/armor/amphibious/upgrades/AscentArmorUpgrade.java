package thebetweenlands.common.item.armor.amphibious.upgrades;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.TickingAmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.ToggleableAmphibiousArmorUpgrade;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorItem;

import java.util.function.Predicate;

public class AscentArmorUpgrade extends SimpleAmphibiousArmorUpgrade implements TickingAmphibiousArmorUpgrade, ToggleableAmphibiousArmorUpgrade {
	public AscentArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, EquipmentSlot... armorTypes) {
		super(maxDamage, damageEvent, matcher, armorTypes);
	}

	@Override
	public void onArmorTick(Level level, Player player, ItemStack stack, int upgradeCount, int armorCount) {
		int ascentBoostTicks = player.getPersistentData().getInt(AmphibiousArmorItem.NBT_ASCENT_BOOST_TICKS);
		if (player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).getBoolean("ascent")) {
			if (player.isShiftKeyDown() && player.onGround() && !player.getPersistentData().getBoolean(AmphibiousArmorItem.NBT_ASCENT_BOOST)) {
				player.getPersistentData().putInt(AmphibiousArmorItem.NBT_ASCENT_BOOST_TICKS, ++ascentBoostTicks);
			} else if (!player.isShiftKeyDown() && player.isInWater()) {
				if (ascentBoostTicks > 10 && !player.getPersistentData().getBoolean(AmphibiousArmorItem.NBT_ASCENT_BOOST)) {
					ascentBoostTicks = 30;
					player.getPersistentData().putInt(AmphibiousArmorItem.NBT_ASCENT_BOOST_TICKS, ascentBoostTicks);

					player.getPersistentData().putBoolean(AmphibiousArmorItem.NBT_ASCENT_BOOST, true);

					player.setDeltaMovement(player.getDeltaMovement().add(0.0D, 0.75D, 0.0D));

					Vec3 lookVec = player.getLookAngle().normalize();
					double speed = 1.2D;
					player.setDeltaMovement(player.getDeltaMovement().add(lookVec.x * speed, lookVec.y * 0.5D, lookVec.z * speed));
					player.hurtMarked = true; //let the client know movement needs to be synced
					AmphibiousArmorItem.damageUpgrade(stack, player, this, 1, AmphibiousArmorUpgrade.DamageEvent.ON_USE, false);
				}

				ascentBoostTicks = Math.max(0, ascentBoostTicks - 1);
				player.getPersistentData().putInt(AmphibiousArmorItem.NBT_ASCENT_BOOST_TICKS, ascentBoostTicks);
			}
		}
	}

	@Override
	public boolean onToggle(Level level, Player player, ItemStack heldStack) {
		boolean existingValue = player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).getBoolean("ascent");
		player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).putBoolean("ascent", !existingValue);
		return !existingValue;
	}
}
