package thebetweenlands.common.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageSyncEnvironmentEvent;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class EnvironmentEventHandler {
	private EnvironmentEventHandler() { }

	//Update events on the server side
	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if(event.phase == Phase.END && !event.world.isRemote) {
			BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(event.world);

			if(storage != null) {
				BLEnvironmentEventRegistry reg = storage.getEnvironmentEventRegistry();

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
						TheBetweenlands.networkWrapper.sendToDimension(new MessageSyncEnvironmentEvent(eevent), event.world.provider.getDimension());
					}
				}

				storage.setEnvironmentEventSyncTicks(storage.getEnvironmentEventSyncTicks() + 1);
				if(storage.getEnvironmentEventSyncTicks() >= 80) {
					storage.setEnvironmentEventSyncTicks(0);
					for(EnvironmentEvent eevent : storage.getEnvironmentEventRegistry().getEvents().values()) {
						TheBetweenlands.networkWrapper.sendToDimension(new MessageSyncEnvironmentEvent(eevent), event.world.provider.getDimension());
					}
				}
			}
		}
	}

	//Update events on the client side
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.END && !Minecraft.getMinecraft().isGamePaused()) {
			World world = Minecraft.getMinecraft().world;
			if(world != null && world.isRemote) {
				BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(world);
				if(storage != null) {
					BLEnvironmentEventRegistry reg = storage.getEnvironmentEventRegistry();
					for(EnvironmentEvent eevent : reg.getEvents().values()) {
						if(!eevent.isLoaded()) 
							continue;
						eevent.update(world);
					}
				}
			}
		}
	}

	//Send packet to sync events on joining
	@SubscribeEvent
	public static void joinWorld(EntityJoinWorldEvent event) {
		if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
			BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorld(event.getWorld());
			if(storage != null) {
				for(EnvironmentEvent eevent : storage.getEnvironmentEventRegistry().getEvents().values()) {
					TheBetweenlands.networkWrapper.sendTo(new MessageSyncEnvironmentEvent(eevent), (EntityPlayerMP)event.getEntity());
				}
			}
		}
	}
}
