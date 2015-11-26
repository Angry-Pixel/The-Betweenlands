package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraft.client.Minecraft;
import thebetweenlands.client.BLPlayerControllerMP;

public class ItemCorrosionHandler {
	public static final ItemCorrosionHandler INSTANCE = new ItemCorrosionHandler();

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		//FIXME: Needs a better way of doing this without breaking stuff
		/*if(Minecraft.getMinecraft().playerController != null && Minecraft.getMinecraft().playerController instanceof BLPlayerControllerMP == false) {
			Minecraft.getMinecraft().playerController = new BLPlayerControllerMP(Minecraft.getMinecraft(), Minecraft.getMinecraft().getNetHandler(), Minecraft.getMinecraft().playerController);
		}*/
	}
}
