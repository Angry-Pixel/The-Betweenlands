package thebetweenlands.common.handler;

import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.common.component.entity.GunkData;

public class PlayerGunkHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(GunkData::onPlayerTick);
	}
	
}
