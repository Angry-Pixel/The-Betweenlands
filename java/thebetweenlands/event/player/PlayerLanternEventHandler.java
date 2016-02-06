package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import thebetweenlands.entities.properties.list.EntityPropertiesLantern;

public class PlayerLanternEventHandler {
	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			return;
		}
		EntityPropertiesLantern.update();
	}
}
