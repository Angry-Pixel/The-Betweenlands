package thebetweenlands.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public interface UnbreakableItem {

	static boolean isStackBroken(ItemStack stack) {
		return stack.getDamageValue() + 1 >= stack.getMaxDamage();
	}

	static void hurtButDontBreak(ItemStack stack, int amount, LivingEntity entity) {
		if (stack.isDamageableItem() && entity.level() instanceof ServerLevel serverLevel) {
			amount = stack.getItem().damageItem(stack, amount, entity, item -> {});
			if (!entity.hasInfiniteMaterials()) {
				if (amount > 0) {
					amount = EnchantmentHelper.processDurabilityChange(serverLevel, stack, amount);
					if (amount <= 0) {
						return;
					}
				}

				if (entity instanceof ServerPlayer sp && amount != 0) {
					CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(sp, stack, stack.getDamageValue() + amount);
				}

				int i = stack.getDamageValue() + amount;
				stack.setDamageValue(i);
			}
		}
	}
}
