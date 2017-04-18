package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.serverbound.MessageUpdatePuppeteerState;
import thebetweenlands.common.network.serverbound.MessageUpdateSummoningState;
import thebetweenlands.common.registries.KeyBindRegistry;

public class InputHandler {
	private InputHandler() { }

	private static boolean wasUseButtonPressed = false;
	private static boolean wasRingUseButtonPressed = false;

	@SubscribeEvent
	public static void onInput(InputEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		if(player != null) {
			updateUseButtonState();
			updateRingUseButtonState();
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
			TheBetweenlands.networkWrapper.sendToServer(new MessageUpdateSummoningState(true));
		} else if(wasRingUseButtonPressed && !KeyBindRegistry.USE_RING.isKeyDown()) {
			wasRingUseButtonPressed = false;
			TheBetweenlands.networkWrapper.sendToServer(new MessageUpdateSummoningState(false));
		}
	}
}
