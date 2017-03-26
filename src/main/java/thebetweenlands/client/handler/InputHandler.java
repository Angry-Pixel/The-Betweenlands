package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.network.serverbound.*;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;

public class InputHandler {
	private InputHandler() { }

	private static boolean wasUseButtonPressed = false;
	private static boolean wasRingUseButtonPressed = false;

	@SubscribeEvent
	public static void onInput(InputEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		if(player != null) {
			if(event instanceof KeyInputEvent) {
				if(KeyBindRegistry.OPEN_POUCH.isPressed()) {
					if(ItemLurkerSkinPouch.getFirstPouch(player) != null) {
						TheBetweenlands.networkWrapper.sendToServer(new MessageOpenPouch());
					}
				}
				if(KeyBindRegistry.SEND_SCOUT.isPressed()) {
					for (ItemStack itemStack:player.getArmorInventoryList())
						if (itemStack != null && itemStack.getItem() == ItemRegistry.SCOUT)
							TheBetweenlands.networkWrapper.sendToServer(new MessageSendScout());
				}
				if(KeyBindRegistry.RETURN_SCOUT.isPressed()) {
					for (ItemStack itemStack:player.getArmorInventoryList())
						if (itemStack != null && itemStack.getItem() == ItemRegistry.SCOUT)
							TheBetweenlands.networkWrapper.sendToServer(new MessageReturnScout());
				}
			}

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
