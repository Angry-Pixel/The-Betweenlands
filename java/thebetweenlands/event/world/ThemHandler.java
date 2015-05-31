package thebetweenlands.event.world;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thebetweenlands.TheBetweenlands;

public class ThemHandler {
	public static final ThemHandler INSTANCE = new ThemHandler();
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTick(PlayerTickEvent event) {
		if(event.side == Side.SERVER) return;
		TheBetweenlands.proxy.spawnThem();
	}
}