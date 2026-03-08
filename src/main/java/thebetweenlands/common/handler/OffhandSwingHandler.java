package thebetweenlands.common.handler;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import thebetweenlands.api.item.BigSwingAnimation;

public class OffhandSwingHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(OffhandSwingHandler::preventShieldUseStart);
		NeoForge.EVENT_BUS.addListener(OffhandSwingHandler::preventShieldUseClick);
		NeoForge.EVENT_BUS.addListener(OffhandSwingHandler::preventShieldUseTick);
	}

	private static void preventShieldUseStart(LivingEntityUseItemEvent.Start event) {
		if (handleItemUse(event.getEntity(), event.getItem())) {
			event.setCanceled(true);
		}
	}

	private static void preventShieldUseClick(PlayerInteractEvent.RightClickItem event) {
		if (handleItemUse(event.getEntity(), event.getItemStack())) {
			event.setCanceled(true);
		}
	}

	private static void preventShieldUseTick(LivingEntityUseItemEvent.Tick event) {
		if (handleItemUse(event.getEntity(), event.getItem())) {
			event.setCanceled(true);
		}
	}

	private static boolean handleItemUse(LivingEntity entity, ItemStack useStack) {
		if (!useStack.isEmpty() && useStack.canPerformAction(ItemAbilities.SHIELD_BLOCK)) {
			for (InteractionHand hand : InteractionHand.values()) {
				ItemStack stack = entity.getItemInHand(hand);

				if (!stack.isEmpty() && stack.getItem() instanceof BigSwingAnimation animation && animation.preventsShieldUse(entity, stack)) {
					return true;
				}
			}
		}

		return false;
	}
}
