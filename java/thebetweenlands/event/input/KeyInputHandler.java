package thebetweenlands.event.input;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.input.KeyBindingsBL;
import thebetweenlands.items.equipment.ItemLurkerSkinPouch;
import thebetweenlands.network.message.MessageOpenPouch;

public class KeyInputHandler {
	public static final KeyInputHandler INSTANCE = new KeyInputHandler();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onInput(InputEvent.KeyInputEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if(player != null) {
			if(KeyBindingsBL.OPEN_POUCH.isPressed()) {
				if(ItemLurkerSkinPouch.getFirstPouch(player) != null) {
					TheBetweenlands.networkWrapper.sendToServer(new MessageOpenPouch());
				}
			}
		}
	}
}
