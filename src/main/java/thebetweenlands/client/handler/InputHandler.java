package thebetweenlands.client.handler;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.BetweenlandsKeybinds;
import thebetweenlands.common.network.serverbound.OpenPouchPacket;
import thebetweenlands.common.network.serverbound.UpdateRingStatePacket;

public class InputHandler {

	private static boolean lastRingState = false;
	private static boolean lastSecondaryRingState = false;

	public static void handleKeybindInputs(InputEvent.Key event) {
		Player player = BetweenlandsClient.getClientPlayer();

		if (player != null && event.getAction() == InputConstants.PRESS) {
			if (BetweenlandsKeybinds.OPEN_POUCH.matches(event.getKey(), event.getScanCode()) && BetweenlandsKeybinds.OPEN_POUCH.consumeClick()) {
				PacketDistributor.sendToServer(OpenPouchPacket.INSTANCE);
			}

			if (BetweenlandsKeybinds.USE_RING.matches(event.getKey(), event.getScanCode()) && BetweenlandsKeybinds.USE_RING.consumeClick()) {
				lastRingState = !lastRingState;
				PacketDistributor.sendToServer(new UpdateRingStatePacket(0, lastRingState));
			}

			if (BetweenlandsKeybinds.USE_SECONDARY_RING.matches(event.getKey(), event.getScanCode()) && BetweenlandsKeybinds.USE_SECONDARY_RING.consumeClick()) {
				lastSecondaryRingState = !lastSecondaryRingState;
				PacketDistributor.sendToServer(new UpdateRingStatePacket(1, lastSecondaryRingState));
			}
		}
	}
}
