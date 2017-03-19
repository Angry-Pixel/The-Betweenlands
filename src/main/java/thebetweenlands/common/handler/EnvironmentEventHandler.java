package thebetweenlands.common.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageSyncEnvironmentEvent;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.EnvironmentEvent;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;
import thebetweenlands.util.config.ConfigHandler;

public class EnvironmentEventHandler {
	private EnvironmentEventHandler() { }

	private static int lastSync = 0;

	//Load world data to update the events
	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		if(event.getWorld().provider instanceof WorldProviderBetweenlands && !event.getWorld().isRemote) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)event.getWorld().provider;
			provider.getWorldData();
		}
	}

	//Update events on the server side
	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if(event.phase == Phase.END && event.world.provider instanceof WorldProviderBetweenlands && !event.world.isRemote) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)event.world.provider;

			//Always save the world data
			provider.getWorldData().markDirty();

			EnvironmentEventRegistry reg = provider.getWorldData().getEnvironmentEventRegistry();
			for(EnvironmentEvent eevent : reg.getEvents().values()) {
				if(!eevent.isLoaded()) continue;
				if (reg.isDisabled()) {
					eevent.setActive(false, eevent.isActive());
					eevent.setDefaults();
				} else {
					eevent.update(event.world);
				}
				if(eevent.isDirty()) {
					eevent.setDirty(false);
					TheBetweenlands.networkWrapper.sendToDimension(new MessageSyncEnvironmentEvent(eevent), ConfigHandler.dimensionId);
				}
			}

			lastSync++;
			if(lastSync >= 80) {
				lastSync = 0;
				for(EnvironmentEvent eevent : provider.getWorldData().getEnvironmentEventRegistry().getEvents().values()) {
					TheBetweenlands.networkWrapper.sendToDimension(new MessageSyncEnvironmentEvent(eevent), ConfigHandler.dimensionId);
				}
			}
		}
	}

	//Update events on the client side
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.END && !Minecraft.getMinecraft().isGamePaused()) {
			World world = Minecraft.getMinecraft().theWorld;
			if(world != null && world.isRemote && world.provider instanceof WorldProviderBetweenlands) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
				EnvironmentEventRegistry reg = provider.getWorldData().getEnvironmentEventRegistry();
				for(EnvironmentEvent eevent : reg.getEvents().values()) {
					if(!eevent.isLoaded()) 
						continue;
					eevent.update(world);
				}
			}
		}
	}

	//Send packet to sync events on joining
	@SubscribeEvent
	public static void joinWorld(EntityJoinWorldEvent event) {
		if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP && event.getWorld().provider instanceof WorldProviderBetweenlands) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)event.getWorld().provider;
			for(EnvironmentEvent eevent : provider.getWorldData().getEnvironmentEventRegistry().getEvents().values()) {
				TheBetweenlands.networkWrapper.sendTo(new MessageSyncEnvironmentEvent(eevent), (EntityPlayerMP)event.getEntity());
			}
		}
	}
}
