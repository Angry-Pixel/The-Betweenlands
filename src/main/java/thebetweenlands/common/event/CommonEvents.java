package thebetweenlands.common.event;

import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.common.component.SimpleAttachmentHandler;
import thebetweenlands.common.handler.*;

public class CommonEvents {

	public static void init() {
		ArmorHandler.init();
		AttackDamageHandler.init();
		CorrosionHandler.init();
		ElixirCommonHandler.init();
		FoodSicknessHandler.init();
		PlayerDecayHandler.init();
		ShieldHandler.init();
		SimulacrumHandler.init();

		NeoForge.EVENT_BUS.addListener(SimpleAttachmentHandler::onPlayerJoinWorld);
	}
}
