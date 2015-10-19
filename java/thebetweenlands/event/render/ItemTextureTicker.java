package thebetweenlands.event.render;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import thebetweenlands.items.BLItemRegistry;

public class ItemTextureTicker {
	public static final ItemTextureTicker INSTANCE = new ItemTextureTicker();
	
	private final Random rnd = new Random();

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.START && event.side == Side.CLIENT) {
			BLItemRegistry.shimmerStone.tickTexture(rnd);
		}
	}
}
