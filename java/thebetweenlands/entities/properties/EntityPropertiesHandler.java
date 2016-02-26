package thebetweenlands.entities.properties;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerEvent.StopTracking;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.properties.EntityProperties.PropertiesTracker;

public class EntityPropertiesHandler {
	private Map<Class<? extends EntityProperties>, String> propertiesIDMap = new HashMap<Class<? extends EntityProperties>, String>();
	private Map<Class<? extends Entity>, List<Class<? extends EntityProperties>>> registeredProperties = new HashMap<Class<? extends Entity>, List<Class<? extends EntityProperties>>>();
	private Map<Class<? extends Entity>, List<String>> entityPropertiesCache = new HashMap<Class<? extends Entity>, List<String>>();

	/**
	 * Registers the handler to the Forge event bus
	 */
	public void registerHandler() {
		MinecraftForge.EVENT_BUS.register(BLEntityPropertiesRegistry.HANDLER);
		FMLCommonHandler.instance().bus().register(BLEntityPropertiesRegistry.HANDLER);
	}

	/**
	 * Registers the packet that keeps the properties in sync
	 * @param networkWrapper
	 * @param packetID
	 */
	public void registerPacket(SimpleNetworkWrapper networkWrapper, int packetID) {
		networkWrapper.registerMessage(MessageSyncEntityProperties.class, MessageSyncEntityProperties.class, packetID, Side.CLIENT);
	}

	/**
	 * Registers a properties type
	 * @param entityClass
	 * @param propClass
	 */
	public void registerProperties(Class<? extends EntityProperties> propertiesClass) {
		EntityProperties properties = null;
		try {
			Constructor<? extends EntityProperties> propCtor = propertiesClass.getConstructor();
			properties = propCtor.newInstance();
		} catch(Exception ex) {
			throw new RuntimeException("Failed to register entity properties!", ex);
		}
		if(this.propertiesIDMap.containsValue(properties.getID())) {
			throw new RuntimeException("Duplicate entity properties ID!");
		}
		this.propertiesIDMap.put(propertiesClass, properties.getID());
		Class<? extends Entity> entityClass = properties.getEntityClass();
		List lst = this.registeredProperties.get(entityClass);
		if(lst == null) {
			this.registeredProperties.put(entityClass, lst = new ArrayList<Class<? extends EntityProperties>>());
		}
		lst.add(propertiesClass);
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityEvent.EntityConstructing event) {
		boolean cached = this.entityPropertiesCache.containsKey(event.entity.getClass());
		List<String> entityPropertiesIDCache = !cached ? new ArrayList<String>() : null;
		for(Entry<Class<? extends Entity>, List<Class<? extends EntityProperties>>> propEntry : this.registeredProperties.entrySet()) {
			if(propEntry.getKey().isAssignableFrom(event.entity.getClass())) {
				for(Class<? extends EntityProperties> propClass : propEntry.getValue()) {
					try {
						Constructor<? extends EntityProperties> propCtor = propClass.getConstructor();
						EntityProperties prop = propCtor.newInstance();
						String propID = prop.getID();
						event.entity.registerExtendedProperties(propID, prop);
						if(!cached) entityPropertiesIDCache.add(propID);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		if(!cached) {
			this.entityPropertiesCache.put(event.entity.getClass(), entityPropertiesIDCache);
		}
	}

	private Map<EntityPlayerMP, List<PropertiesTracker>> trackerMap = new HashMap<EntityPlayerMP, List<PropertiesTracker>>();

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		if(event.entity.worldObj.isRemote || event.entity instanceof EntityPlayerMP == false) return;
		EntityPlayerMP player = (EntityPlayerMP) event.entity;
		List<PropertiesTracker> trackers = this.trackerMap.get(player);
		if(trackers != null && trackers.size() > 0) {
			boolean hasPlayer = false;
			for(PropertiesTracker tracker : trackers) {
				if(hasPlayer = tracker.getEntity() == player) 
					break;
			}
			if(!hasPlayer) {
				this.addTracker(player, player);
			}
			Iterator<PropertiesTracker> it = trackers.iterator();
			while(it.hasNext()) {
				PropertiesTracker tracker = it.next();
				Entity entity = tracker.getEntity();
				WorldServer entityWorld = DimensionManager.getWorld(entity.dimension);
				if(entity == null || entity.isDead || entityWorld == null || !entityWorld.loadedEntityList.contains(entity)) {
					it.remove();
					tracker.removeTracker();
					continue;
				}
				tracker.updateTracker();
				if(tracker.isTrackerReady()) {
					MessageSyncEntityProperties message = new MessageSyncEntityProperties(tracker.getProperties(), tracker.getEntity());
					TheBetweenlands.networkWrapper.sendTo(message, player);
				}
			}
		}
	}

	private void addTracker(EntityPlayerMP player, Entity entity) {
		List<String> entityProperties = this.entityPropertiesCache.get(entity.getClass());
		if(entityProperties != null) {
			List<PropertiesTracker> trackerList = this.trackerMap.get(player);
			if(trackerList == null) {
				this.trackerMap.put(player, trackerList = new ArrayList<PropertiesTracker>());
			}
			for(String propID : entityProperties) {
				IExtendedEntityProperties prop = entity.getExtendedProperties(propID);
				if(prop instanceof EntityProperties) {
					EntityProperties blProp = (EntityProperties)prop;
					if(blProp.getTrackingTime() >= 0) {
						PropertiesTracker tracker = blProp.createTracker(entity);
						tracker.setReady();
						trackerList.add(tracker);
					}
				}
			}
		}
	}

	private void removeTracker(EntityPlayerMP player, Entity entity) {
		List<PropertiesTracker> trackerList = this.trackerMap.get(player);
		if(trackerList != null && trackerList.size() > 0) {
			Iterator<PropertiesTracker> it = trackerList.iterator();
			while(it.hasNext()) {
				PropertiesTracker tracker = it.next();
				if(tracker.getEntity().equals(entity)) {
					it.remove();
					tracker.removeTracker();
				}
			}
		}
	}

	private void removePlayer(EntityPlayerMP player) {
		List<PropertiesTracker> trackers = this.trackerMap.get(player);
		if(trackers != null && trackers.size() > 0) {
			for(PropertiesTracker tracker : trackers) {
				tracker.removeTracker();
			}
		}
		this.trackerMap.remove(player);
	}

	@SubscribeEvent
	public void joinWorld(EntityJoinWorldEvent event) {
		if (!event.world.isRemote && event.entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.entity;
			this.addTracker(player, player);
		}
	}

	@SubscribeEvent
	public void onEntityStartTracking(StartTracking event) {
		if(event.entityPlayer instanceof EntityPlayerMP) {
			this.addTracker((EntityPlayerMP)event.entityPlayer, event.target);
		}
	}

	@SubscribeEvent
	public void onEntityStopTracking(StopTracking event) {
		if(event.entityPlayer instanceof EntityPlayerMP) {
			this.removeTracker((EntityPlayerMP)event.entityPlayer, event.target);
		}
	}

	private int updateTimer = 0;

	@SubscribeEvent
	public void onServerTickEvent(ServerTickEvent event) {
		if(event.phase == Phase.END) {
			this.updateTimer++;
			if(this.updateTimer > 20) {
				this.updateTimer = 0;
				Iterator<Entry<EntityPlayerMP, List<PropertiesTracker>>> trackerMapIT = this.trackerMap.entrySet().iterator();
				while(trackerMapIT.hasNext()) {
					Entry<EntityPlayerMP, List<PropertiesTracker>> trackerEntry = trackerMapIT.next();
					EntityPlayerMP player = trackerEntry.getKey();
					WorldServer playerWorld = DimensionManager.getWorld(player.dimension);
					if(player == null || player.isDead || playerWorld == null || !playerWorld.loadedEntityList.contains(player)) {
						trackerMapIT.remove();
						for(PropertiesTracker tracker : trackerEntry.getValue()) {
							tracker.removeTracker();
						}
					}
				}
			}
		}
	}

	public <T extends EntityProperties> T getProperties(Entity entity, Class<T> props) {
		if (entity != null && this.propertiesIDMap.containsKey(props)) {
			return (T) entity.getExtendedProperties(this.propertiesIDMap.get(props));
		}
		return null;
	}

	//Some unused code that lets you hook into the pipeline and intercept vanilla packets, might be useful for something else
	/*class BLOutboundHandler extends ChannelOutboundHandlerAdapter {
			private final EntityPlayerMP target;

			public BLOutboundHandler(EntityPlayerMP target) {
				this.target = target;
			}

			@Override
			public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
				if(this.target != null && this.target.worldObj != null) {
					if(msg instanceof S14PacketEntity) {
						S14PacketEntity entityPacket = (S14PacketEntity) msg;
						Entity entity = entityPacket.func_149065_a(this.target.worldObj);
					}
				}
				ctx.write(msg, promise);
			}
		}

		private static Field f_player = ReflectionHelper.findField(NetworkDispatcher.class, "player");

		@SubscribeEvent
		public void onClientConnected(ServerConnectionFromClientEvent event) {
			BLOutboundHandler outboundHandler = null;
			try {
				NetworkDispatcher dispatcher = (NetworkDispatcher)event.manager.channel().pipeline().get("fml:packet_handler");
				outboundHandler = new BLOutboundHandler((EntityPlayerMP) f_player.get(dispatcher));
			} catch(Exception ex) {
				System.err.println("Failed to add Betweenlands outbound packet hander!");
				ex.printStackTrace();
			}
			event.manager.channel().pipeline().addAfter("fml:packet_handler", "BLOutboundAdapter", outboundHandler);
		}*/
}
