package thebetweenlands.event.world;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.network.message.MessageSyncEnvironmentEvent;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.EnvironmentEvent;
import thebetweenlands.world.events.EnvironmentEventRegistry;

public class EnvironmentEventHandler {
	public static final EnvironmentEventHandler INSTANCE = new EnvironmentEventHandler();

	private int lastSync = 0;

	//Load world data to update the events
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if(event.world.provider instanceof WorldProviderBetweenlands && !event.world.isRemote) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)event.world.provider;
			provider.getWorldData();
		}
	}

	//Update events on the server side
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if(event.world.provider instanceof WorldProviderBetweenlands && !event.world.isRemote) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)event.world.provider;

			//Always save the world data
			provider.getWorldData().markDirty();

			EnvironmentEventRegistry reg = provider.getWorldData().getEnvironmentEventRegistry();
			for(EnvironmentEvent eevent : reg.getEvents().values()) {
				if(!eevent.isLoaded()) continue;
				if (reg.isEnabled()) {
					eevent.update(event.world);
				}
				if(eevent.isDirty()) {
					eevent.setDirty(false);
					TheBetweenlands.networkWrapper.sendToAll(new MessageSyncEnvironmentEvent(eevent));
				}
			}

			this.lastSync++;
			if(this.lastSync >= 80) {
				this.lastSync = 0;
				for(EnvironmentEvent eevent : provider.getWorldData().getEnvironmentEventRegistry().getEvents().values()) {
					TheBetweenlands.networkWrapper.sendToAll(new MessageSyncEnvironmentEvent(eevent));
				}
			}
		}
	}
	
	//Update events on the client side
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		World world = TheBetweenlands.proxy.getClientWorld();
		if(world != null && world.isRemote && world.provider instanceof WorldProviderBetweenlands) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
			EnvironmentEventRegistry reg = provider.getWorldData().getEnvironmentEventRegistry();
			for(EnvironmentEvent eevent : reg.getEvents().values()) {
				if(!eevent.isLoaded()) continue;
				eevent.update(world);
			}
		}
	}
}
