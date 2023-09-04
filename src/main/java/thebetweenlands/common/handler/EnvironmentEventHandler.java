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
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.network.IGenericDataManagerAccess;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageSyncEnvironmentEventData;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEvent;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class EnvironmentEventHandler {
	private EnvironmentEventHandler() { }

	//Update events on the server side
	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if(event.phase == Phase.END && !event.world.isRemote) {
			BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorldNullable(event.world);

			if(storage != null) {
				BLEnvironmentEventRegistry reg = storage.getEnvironmentEventRegistry();

				for(IEnvironmentEvent eevent : reg.getEvents().values()) {
					if(!eevent.isLoaded()) continue;
					if (reg.isDisabled()) {
						if(eevent.isActive()) {
							eevent.setActive(false);
							eevent.setDefaults();
						}
					} else {
						eevent.update(event.world);
					}
					IGenericDataManagerAccess dataManager = eevent.getDataManager();
					if(dataManager != null) {
						dataManager.update();
						if(dataManager.isDirty()) {
							TheBetweenlands.networkWrapper.sendToDimension(new MessageSyncEnvironmentEventData(eevent, false), event.world.provider.getDimension());
						}
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
					for(IEnvironmentEvent eevent : reg.getEvents().values()) {
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
				for(IEnvironmentEvent eevent : storage.getEnvironmentEventRegistry().getEvents().values()) {
					if(eevent instanceof BLEnvironmentEvent) {
						TheBetweenlands.networkWrapper.sendTo(new MessageSyncEnvironmentEventData((BLEnvironmentEvent)eevent, true), (EntityPlayerMP)event.getEntity());
					}
					if (eevent.isActive())
						AdvancementCriterionRegistry.EVENT.trigger((EntityPlayerMP) event.getEntity(), eevent.getEventName());
				}
			}
		}
	}
}
