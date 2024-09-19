package thebetweenlands.common.items.amphibious.upgrades;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.TickingAmphibiousArmorUpgrade;
import thebetweenlands.api.item.amphibious.ToggleableAmphibiousArmorUpgrade;
import thebetweenlands.common.items.amphibious.AmphibiousArmorItem;

import java.util.function.Predicate;

public class GlideArmorUpgrade extends SimpleAmphibiousArmorUpgrade implements TickingAmphibiousArmorUpgrade, ToggleableAmphibiousArmorUpgrade {
	public GlideArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, EquipmentSlot... armorTypes) {
		super(maxDamage, damageEvent, matcher, armorTypes);
	}

	@Override
	public void onArmorTick(Level level, Player player, ItemStack stack, int upgradeCount, int armorCount) {
		if (player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).getBoolean("glide")) {
			if (player.isSprinting() && !player.onGround() && !player.jumping && !player.getAbilities().flying) {
				player.fallDistance = 0.0F;
				if (level.isClientSide())
					player.setDeltaMovement(player.getDeltaMovement().multiply(1.05D, 0.6D - upgradeCount * 0.1D, 1.05D));

				if (player.tickCount % 20 == 0) {
					AmphibiousArmorItem.damageUpgrade(stack, player, this, 1, AmphibiousArmorUpgrade.DamageEvent.ON_USE, false);
				}
			}
		}
	}

	@Override
	public boolean onToggle(Level level, Player player, ItemStack heldStack) {
		boolean existingValue = player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).getBoolean("glide");
		player.getPersistentData().getCompound(AmphibiousArmorItem.AUTO_TOGGLES_KEY).putBoolean("glide", !existingValue);
		return !existingValue;
	}
}
