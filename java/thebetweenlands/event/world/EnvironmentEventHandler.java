package thebetweenlands.event.world;

import net.minecraftforge.event.world.WorldEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.network.message.MessageSyncEnvironmentEvent;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.EnvironmentEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public class EnvironmentEventHandler {
	public static final EnvironmentEventHandler INSTANCE = new EnvironmentEventHandler();

	private static int lastSync = 0;

	//Load world data to update the events
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if(event.world.provider instanceof WorldProviderBetweenlands && !event.world.isRemote) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)event.world.provider;
			provider.getWorldData();
		}
	}
	
	@SubscribeEvent
	public void onTick(WorldTickEvent event) {
		if(event.world.provider instanceof WorldProviderBetweenlands && !event.world.isRemote) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)event.world.provider;
			
			//Always save the world data
			provider.getWorldData().markDirty();
			
			for(EnvironmentEvent eevent : provider.getWorldData().getEnvironmentEventRegistry().getEvents().values()) {
				if(!eevent.isLoaded()) continue;
				eevent.update(event.world);
				if(eevent.isDirty()) {
					eevent.setDirty(false);
					TheBetweenlands.networkWrapper.sendToAll(new MessageSyncEnvironmentEvent(eevent));
				}
			}
			lastSync++;
			if(lastSync >= 80) {
				lastSync = 0;
				for(EnvironmentEvent eevent : provider.getWorldData().getEnvironmentEventRegistry().getEvents().values()) {
					TheBetweenlands.networkWrapper.sendToAll(new MessageSyncEnvironmentEvent(eevent));
				}
			}
		}
	}
}
