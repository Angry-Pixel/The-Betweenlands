package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.api.event.ArmSwingSpeedEvent;

public class ArmSwingSpeedHandler {

	public static void fireArmSwingEvent(ClientTickEvent.Post event) {
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null) {
			for (Entity entity : level.entitiesForRendering()) {
				if (entity instanceof LivingEntity living) {
					if (living.swinging && living.swingTime != 0) {
						ArmSwingSpeedEvent armEvent = new ArmSwingSpeedEvent(living);

						NeoForge.EVENT_BUS.post(armEvent);

						if (!armEvent.isCanceled() && armEvent.getSpeed() != 1.0F) {
							float speed = armEvent.getSpeed();

							int swingAnimationEnd = living.getCurrentSwingDuration();

							if (living.oAttackAnim < living.attackAnim) {
								living.attackAnim = living.oAttackAnim;
								living.attackAnim += 1.0F / (swingAnimationEnd / speed);
							}

							if (living.swingTime < 0 || living.attackAnim < 0) {
								living.swingTime = 0;
								living.attackAnim = 0;
							}

							living.swingTime = (int) (living.attackAnim * swingAnimationEnd);
						}
					}
				}
			}
		}
	}
}
