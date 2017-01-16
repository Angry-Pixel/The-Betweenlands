package thebetweenlands.client.event.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.network.serverbound.MessageOpenPouch;
import thebetweenlands.common.registries.KeyBindRegistry;

public class KeyInputHandler {
	private KeyInputHandler() { }

	@SubscribeEvent
	public static void onInput(InputEvent.KeyInputEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(player != null) {
			if(KeyBindRegistry.OPEN_POUCH.isPressed()) {
				if(ItemLurkerSkinPouch.getFirstPouch(player) != null) {
					TheBetweenlands.networkWrapper.sendToServer(new MessageOpenPouch());
				}
			}
		}
	}
}
