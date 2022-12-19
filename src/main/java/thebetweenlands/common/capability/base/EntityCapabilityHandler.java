package thebetweenlands.common.capability.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerEvent.StopTracking;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.api.capability.ISerializableCapability;

public class EntityCapabilityHandler {
	private static final List<EntityCapability<?, ?, ? extends Entity>> REGISTERED_CAPABILITIES = new ArrayList<EntityCapability<?, ?, ? extends Entity>>();
	private static final Map<ResourceLocation, EntityCapability<?, ?, ? extends Entity>> ID_CAPABILITY_MAP = new HashMap<ResourceLocation, EntityCapability<?, ?, ? extends Entity>>();

	private static final Map<EntityPlayerMP, List<EntityCapabilityTracker>> TRACKER_MAP = new HashMap<EntityPlayerMP, List<EntityCapabilityTracker>>();

	private static int updateTimer = 0;

	/**
	 * Registers an entity capability
	 * @param entityCapability
	 */
	public static <T, F extends EntityCapability<F, T, E>, E extends Entity> void registerEntityCapability(EntityCapability<F, T, E> entityCapability) {
		//Make sure the entity capability is the implementation of the capability
		Preconditions.checkState(entityCapability.getCapabilityClass().isAssignableFrom(entityCapability.getClass()), "Entity capability %s must implement %s", entityCapability.getClass().getName(), entityCapability.getCapabilityClass().getName());
		REGISTERED_CAPABILITIES.add(entityCapability);
	}

	/**
	 * Registers all capabilities to the {@link CapabilityManager}. Must be called during pre init.
	 */
	public static void registerCapabilities() {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.PREINITIALIZATION));
		for(EntityCapability<?, ?, ?> capability : REGISTERED_CAPABILITIES) {
			registerCapability(capability);
		}
	}

	/**
	 * Returns the capability with the specified ID
	 * @param id
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Entity> EntityCapability<?, ?, E> getCapability(ResourceLocation id, E entity) {
		EntityCapability<?, ?, ?> entityCapability = ID_CAPABILITY_MAP.get(id);
		if(entityCapability != null) {
			return (EntityCapability<?, ?, E>) entity.getCapability(entityCapability.getCapability(), null);
		}
		return null;
	}

	private static <T, E extends Entity> void registerCapability(EntityCapability<?, T, E> capability) {
		CapabilityManager.INSTANCE.register(capability.getCapabilityClass(), new IStorage<T>() {
			@Override
			public final NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
				if(instance instanceof ISerializableCapability) {
					NBTTagCompound nbt = new NBTTagCompound();
					((ISerializableCapability)instance).writeToNBT(nbt);
					return nbt;
				}
				return new NBTTagCompound(); //This is broken, the doc says returning null if not required, but causes a NPE
			}

			@Override
			public final void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
				if(instance instanceof ISerializableCapability && nbt instanceof NBTTagCompound) {
					((ISerializableCapability)instance).readFromNBT((NBTTagCompound)nbt);
				}
			}
		}, new Callable<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public final T call() throws Exception {
				return (T) capability.getDefaultCapabilityImplementation();
			}
		});
		ID_CAPABILITY_MAP.put(capability.getID(), capability);
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		for(EntityCapability<?, ?, ?> entityCapability : REGISTERED_CAPABILITIES) {
			if(entityCapability.isApplicable(entity)) {
				final Capability<?> capabilityInstance = entityCapability.getCapability();

				event.addCapability(entityCapability.getID(), new ICapabilitySerializable<NBTTagCompound>() {
					private Object entityCapability = this.getNewInstance();

					private EntityCapability<?, ?, ?> getNewInstance() {
						EntityCapability<?, ?, ?> entityCapability = (EntityCapability<?, ?, ?>)capabilityInstance.getDefaultInstance();
						entityCapability.setEntity(entity);
						entityCapability.init();
						return entityCapability;
					}

					@Override
					public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
						return capability == capabilityInstance;
					}

					@SuppressWarnings("unchecked")
					@Override
					public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
						return capability == capabilityInstance ? (T)this.entityCapability : null;
					}

					@Override
					public NBTTagCompound serializeNBT() {
						return this.serialize(capabilityInstance, this.entityCapability);
					}

					@SuppressWarnings("unchecked")
					private <T> NBTTagCompound serialize(Capability<T> capability, Object instance) {
						return (NBTTagCompound) capability.getStorage().writeNBT(capability, (T)instance, null);
					}

					@Override
					public void deserializeNBT(NBTTagCompound nbt) {
						this.deserialize(capabilityInstance, this.entityCapability, nbt);
					}

					@SuppressWarnings("unchecked")
					private <T> void deserialize(Capability<T> capability, Object instance, NBTTagCompound nbt) {
						capability.getStorage().readNBT(capability, (T)instance, null, nbt);
					}
				});
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerJoin(EntityJoinWorldEvent event) {
		if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
			addTrackers(player, player);
		}
	}

	@SubscribeEvent
	public static void onEntityChangeDimension(PlayerChangedDimensionEvent event) {
		if(!event.player.getEntityWorld().isRemote && event.player instanceof EntityPlayerMP)  {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			List<EntityCapabilityTracker> trackers = TRACKER_MAP.get(player);
			if(trackers != null) {
				for(EntityCapabilityTracker tracker : trackers) {
					tracker.getEntityCapability().sendPacket(tracker.getWatcher());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityStartTracking(StartTracking event) {
		if(event.getEntityPlayer() instanceof EntityPlayerMP) {
			addTrackers((EntityPlayerMP)event.getEntityPlayer(), event.getTarget());
		}
	}

	@SubscribeEvent
	public static void onEntityStopTracking(StopTracking event) {
		if(event.getEntityPlayer() instanceof EntityPlayerMP) {
			removeTrackers((EntityPlayerMP)event.getEntityPlayer(), event.getTarget());
		}
	}

	@SubscribeEvent
	public static void onEntityUpdate(PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.END && !event.player.getEntityWorld().isRemote && event.side == Side.SERVER)  {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			List<EntityCapabilityTracker> trackers = TRACKER_MAP.get(player);
			if(trackers != null) {
				Iterator<EntityCapabilityTracker> trackerIT = trackers.iterator();
				while(trackerIT.hasNext()) {
					EntityCapabilityTracker tracker = trackerIT.next();

					//Don't remove own tracker
					if(tracker.getEntityCapability().getEntity() != player) {
						Set<? extends EntityPlayer> vanillaTrackingPlayers = player.getServerWorld().getEntityTracker().getTrackingPlayers(tracker.getEntityCapability().getEntity());

						if(vanillaTrackingPlayers == null || vanillaTrackingPlayers.isEmpty() || !vanillaTrackingPlayers.contains(player)) {
							//Welp, seems like StopTracking isn't called sometimes...
							trackerIT.remove();
							tracker.remove();
						}
					}

					tracker.update();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onServerTickEvent(ServerTickEvent event) {
		if(event.phase == Phase.END) {
			updateTimer++;
			if(updateTimer > 20) {
				updateTimer = 0;
				Iterator<Entry<EntityPlayerMP, List<EntityCapabilityTracker>>> it = TRACKER_MAP.entrySet().iterator();
				while(it.hasNext()) {
					Entry<EntityPlayerMP, List<EntityCapabilityTracker>> entry = it.next();
					EntityPlayerMP player = entry.getKey();

					//System.out.println(entry.getValue().size() + " " + player.getServerWorld().loadedEntityList.size());

					if(player.getServerWorld().getMinecraftServer() != null && !player.getServerWorld().getMinecraftServer().getPlayerList().getPlayers().contains(player)) {
						it.remove();
						for(EntityCapabilityTracker tracker : entry.getValue()) {
							tracker.remove();
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		//Clone persistent capability properties
		EntityPlayer oldPlayer = event.getOriginal();
		EntityPlayer newPlayer = event.getEntityPlayer();
		List<EntityCapability<?, ?, EntityPlayer>> capabilities = getEntityCapabilities(oldPlayer);

		for(EntityCapability<?, ?, EntityPlayer> capability : capabilities) {
			if(capability.isPersistent(oldPlayer, newPlayer, event.isWasDeath()) && capability instanceof ISerializableCapability) {
				EntityCapability<?, ?, EntityPlayer> newCapability = capability.getEntityCapability(newPlayer);

				if(newCapability != null && newCapability instanceof ISerializableCapability) {
					capability.clonePersistentData(oldPlayer, newPlayer, event.isWasDeath(), (ISerializableCapability) newCapability);
				}
			}
		}
	}

	/**
	 * Returns a list of all found registered capabilities on an entity
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <E extends Entity> List<EntityCapability<?, ?, E>> getEntityCapabilities(E entity) {
		List<EntityCapability<?, ?, E>> capabilities = new ArrayList<EntityCapability<?, ?, E>>();

		for(EntityCapability<?, ?, ?> capability : REGISTERED_CAPABILITIES) {
			if(entity.hasCapability(capability.getCapability(), null)) {
				capabilities.add((EntityCapability<?, ?, E>) entity.getCapability(capability.getCapability(), null));
			}
		}

		return capabilities;
	}

	/**
	 * Adds all necessary trackers for an entity
	 * @param watcher
	 * @param target
	 */
	private static void addTrackers(EntityPlayerMP watcher, Entity target) {
		List<EntityCapability<?, ?, Entity>> entityCapabilities = getEntityCapabilities(target);

		for(EntityCapability<?, ?, Entity> capability : entityCapabilities) {
			if(capability.getTrackingTime() >= 0) {
				List<EntityCapabilityTracker> trackers = TRACKER_MAP.get(watcher);
				if(trackers == null) {
					TRACKER_MAP.put(watcher, trackers = new ArrayList<EntityCapabilityTracker>());
				}
				EntityCapabilityTracker tracker = new EntityCapabilityTracker(capability, watcher);
				trackers.add(tracker);
				tracker.add();

				//Send initial packet
				capability.sendPacket(watcher);
			}
		}
	}

	/**
	 * Removes all necessary trackers for an entity
	 * @param watcher
	 * @param target
	 */
	private static void removeTrackers(EntityPlayerMP watcher, Entity target) {
		List<EntityCapabilityTracker> trackers = TRACKER_MAP.get(watcher);

		if(trackers != null) {
			List<EntityCapability<?, ?, Entity>> entityCapabilities = getEntityCapabilities(target);

			for(EntityCapability<?, ?, Entity> capability : entityCapabilities) {
				if(capability.getTrackingTime() >= 0) {
					Iterator<EntityCapabilityTracker> it = trackers.iterator();
					while(it.hasNext()) {
						EntityCapabilityTracker tracker = it.next();

						if(tracker.getEntityCapability() == capability) {
							it.remove();
							tracker.remove();
						}
					}
				}
			}

			if(trackers.isEmpty()) {
				TRACKER_MAP.remove(watcher);
			}
		}
	}
}
