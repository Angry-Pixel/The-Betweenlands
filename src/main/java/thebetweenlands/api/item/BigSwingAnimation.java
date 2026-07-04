package thebetweenlands.api.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface BigSwingAnimation {

	float getSwingSpeedMultiplier(LivingEntity entity, ItemStack stack);

	default double getAoEReach(LivingEntity entity, ItemStack stack) {
		return 2.8D;
	}

	default double getAoEHalfAngle(LivingEntity entity, ItemStack stack) {
		return 45.0D;
	}

	default boolean preventsShieldUse(LivingEntity entity, ItemStack stack) {
		return true;
	}

	default void onLeftClick(Player player, ItemStack stack) {
	}
}
