package thebetweenlands.client.handler;

import net.neoforged.neoforge.common.NeoForge;

public class ClientHandlerEvents {

	public static void init() {
		//elixirs
		NeoForge.EVENT_BUS.addListener(ElixirClientHandler::changePlayerRotation);
		NeoForge.EVENT_BUS.addListener(ElixirClientHandler::onClientTick);
		NeoForge.EVENT_BUS.addListener(ElixirClientHandler::updateFOVHack);
//		NeoForge.EVENT_BUS.addListener(ElixirClientHandler::setArmSwing);
//		NeoForge.EVENT_BUS.addListener(ElixirClientHandler::setPredictionRotation);
//		NeoForge.EVENT_BUS.addListener(ElixirClientHandler::renderArrowPrediction);

		NeoForge.EVENT_BUS.addListener(ItemTooltipHandler::addTooltips);
	}
}
