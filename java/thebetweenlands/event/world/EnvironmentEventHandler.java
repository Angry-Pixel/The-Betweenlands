package thebetweenlands.event.world;

import net.minecraftforge.event.world.WorldEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.network.message.MessageSyncEnvironmentEvent;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.EnvironmentEvent;
import thebetweenlands.world.events.EnvironmentEventRegistry;
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
			for(EnvironmentEvent eevent : EnvironmentEventRegistry.getEvents().values()) {
				eevent.update(event.world.rand);
				if(eevent.isDirty()) {
					provider.getWorldData().markDirty();
					TheBetweenlands.networkWrapper.sendToAll(new MessageSyncEnvironmentEvent(eevent));
				}
			}
			lastSync++;
			if(lastSync >= 60) {
				lastSync = 0;
				for(EnvironmentEvent eevent : EnvironmentEventRegistry.getEvents().values()) {
					TheBetweenlands.networkWrapper.sendToAll(new MessageSyncEnvironmentEvent(eevent));
				}
			}
		}
	}
}
