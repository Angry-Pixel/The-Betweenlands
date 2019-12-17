package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.serverbound.MessageConnectCavingRope;
import thebetweenlands.common.network.serverbound.MessageOpenPouch;
import thebetweenlands.common.network.serverbound.MessageUpdatePuppeteerState;
import thebetweenlands.common.network.serverbound.MessageUpdateRingKeybindState;
import thebetweenlands.common.registries.KeyBindRegistry;

public class InputHandler {
	private InputHandler() { }

	private static boolean wasConnectRopeButtonPressed = false;
	private static boolean wasDisconnectRopeButtonPressed = false;
	private static boolean wasUseButtonPressed = false;
	private static boolean wasRingUseButtonPressed = false;
	private static boolean wasSecondaryRingUseButtonPressed = false;
	private static boolean wasPouchButtonPressed = false;

	@SubscribeEvent
	public static void onInput(InputEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;

		if(player != null) {
			updateRopeConnectButtonState();
			updateUseButtonState();
			updateRingUseButtonState();
			updatePouchButtonState();
		}
	}

	private static void updateRopeConnectButtonState() {
		if(!wasConnectRopeButtonPressed && KeyBindRegistry.CONNECT_CAVING_ROPE.isKeyDown()) {
			wasConnectRopeButtonPressed = true;
			TheBetweenlands.networkWrapper.sendToServer(new MessageConnectCavingRope());
		} else if(wasConnectRopeButtonPressed && !KeyBindRegistry.CONNECT_CAVING_ROPE.isKeyDown()) {
			wasConnectRopeButtonPressed = false;
		}
	}

	private static void updatePouchButtonState() {
		if(!wasPouchButtonPressed && KeyBindRegistry.OPEN_POUCH.isKeyDown()) {
			wasPouchButtonPressed = true;
			TheBetweenlands.networkWrapper.sendToServer(new MessageOpenPouch());
		} else if(wasPouchButtonPressed && !KeyBindRegistry.OPEN_POUCH.isKeyDown()) {
			wasPouchButtonPressed = false;
		}
	}

	private static void updateUseButtonState() {
		if(!wasUseButtonPressed && Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown()) {
			wasUseButtonPressed = true;
			TheBetweenlands.networkWrapper.sendToServer(new MessageUpdatePuppeteerState(true));
		} else if(wasUseButtonPressed && !Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown()) {
			wasUseButtonPressed = false;
			TheBetweenlands.networkWrapper.sendToServer(new MessageUpdatePuppeteerState(false));
		}
	}

	private static void updateRingUseButtonState() {
		if(!wasRingUseButtonPressed && KeyBindRegistry.USE_RING.isKeyDown()) {
			wasRingUseButtonPressed = true;
			TheBetweenlands.networkWrapper.sendToServer(new MessageUpdateRingKeybindState(true, 0));
		} else if(wasRingUseButtonPressed && !KeyBindRegistry.USE_RING.isKeyDown()) {
			wasRingUseButtonPressed = false;
			TheBetweenlands.networkWrapper.sendToServer(new MessageUpdateRingKeybindState(false, 0));
		}
		
		if(!wasSecondaryRingUseButtonPressed && KeyBindRegistry.USE_SECONDARY_RING.isKeyDown()) {
			wasSecondaryRingUseButtonPressed = true;
			TheBetweenlands.networkWrapper.sendToServer(new MessageUpdateRingKeybindState(true, 1));
		} else if(wasSecondaryRingUseButtonPressed && !KeyBindRegistry.USE_SECONDARY_RING.isKeyDown()) {
			wasSecondaryRingUseButtonPressed = false;
			TheBetweenlands.networkWrapper.sendToServer(new MessageUpdateRingKeybindState(false, 1));
		}
	}
}
