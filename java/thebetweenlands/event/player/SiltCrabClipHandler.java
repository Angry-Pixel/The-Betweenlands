package thebetweenlands.event.player;

import thebetweenlands.entities.mobs.EntitySiltCrab;
import thebetweenlands.forgeevent.entity.player.PlayerEventGetHurtSound;
import thebetweenlands.lib.ModInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SiltCrabClipHandler {
	@SubscribeEvent
	public void onPlayerEventGetHurtSound(PlayerEventGetHurtSound event) {
		if (event.entityPlayer.getAITarget() instanceof EntitySiltCrab) {
			event.hurtSound = ModInfo.ID + ":crabSnip";
		}
	}
}
