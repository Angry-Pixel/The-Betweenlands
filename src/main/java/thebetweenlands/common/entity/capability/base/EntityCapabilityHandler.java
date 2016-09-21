package thebetweenlands.common.entity.capability.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.event.entity.player.PlayerEvent.StopTracking;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class EntityCapabilityHandler {
	private static final List<EntityCapability<?, ?>> REGISTERED_CAPABILITIES = new ArrayList<EntityCapability<?, ?>>();
	private static final Map<ResourceLocation, EntityCapability<?, ?>> ID_CAPABILITY_MAP = new HashMap<ResourceLocation, EntityCapability<?, ?>>();

	private static final Map<EntityPlayerMP, List<EntityCapabilityTracker>> TRACKER_MAP = new HashMap<EntityPlayerMP, List<EntityCapabilityTracker>>();

	private int updateTimer = 0;

	/**
	 * Registers an entity capability
	 * @param entityCapability
	 */
	public static <T, F extends T> void registerEntityCapability(EntityCapability<F, T> entityCapability) {
		REGISTERED_CAPABILITIES.add(entityCapability);
	}

	/**
	 * Registers all capabilities to the {@link CapabilityManager}. Must be called during pre init.
	 */
	public static void registerCapabilities() {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.PREINITIALIZATION));
		for(EntityCapability<?, ?> capability : REGISTERED_CAPABILITIES) {
			registerCapability(capability);
		}
	}

	/**
	 * Returns the capability with the specified ID
	 * @param id
	 */
	public static EntityCapability<?, ?> getCapability(ResourceLocation id) {
		return ID_CAPABILITY_MAP.get(id);
	}

	@SuppressWarnings("unchecked")
	private static <T> void registerCapability(EntityCapability<?, T> capability) {
		CapabilityManager.INSTANCE.register((Class<T>) capability.getClass(), capability, capability);
		ID_CAPABILITY_MAP.put(capability.getID(), capability);
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent.Entity event) {
		for(EntityCapability<?, ?> entityCapability : REGISTERED_CAPABILITIES) {
			if(entityCapability.isApplicable(event.getEntity())) {
				final Capability<?> capabilityInstance = entityCapability.getCapability();

				event.addCapability(entityCapability.getID(), new ICapabilitySerializable<NBTTagCompound>() {
					private Object entityCapability = this.getNewInstance();

					private EntityCapability<?, ?> getNewInstance() {
						EntityCapability<?, ?> entityCapability = (EntityCapability<?, ?>)capabilityInstance.getDefaultInstance();
						entityCapability.setEntity(event.getEntity());
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
	public void onServerTickEvent(ServerTickEvent event) {
		if(event.phase == Phase.END) {
			updateTimer++;
			if(updateTimer > 20) {
				updateTimer = 0;
				Iterator<Entry<EntityPlayerMP, List<EntityCapabilityTracker>>> it = TRACKER_MAP.entrySet().iterator();
				while(it.hasNext()) {
					Entry<EntityPlayerMP, List<EntityCapabilityTracker>> entry = it.next();
					EntityPlayerMP player = entry.getKey();
					if(!player.getEntityWorld().playerEntities.contains(player))
						it.remove();
				}
			}
		}
	}

	/**
	 * Returns a list of all found registered capabilities on an entity
	 * @param entity
	 * @return
	 */
	private static List<EntityCapability<?, ?>> getEntityCapabilities(Entity entity) {
		List<EntityCapability<?, ?>> capabilities = new ArrayList<EntityCapability<?, ?>>();

		for(EntityCapability<?, ?> capability : REGISTERED_CAPABILITIES) {
			if(entity.hasCapability(capability.getCapability(), null))
				capabilities.add((EntityCapability<?, ?>) entity.getCapability(capability.getCapability(), null));
		}

		return capabilities;
	}

	/**
	 * Adds all necessary trackers for an entity
	 * @param watcher
	 * @param target
	 */
	private static void addTrackers(EntityPlayerMP watcher, Entity target) {
		List<EntityCapability<?, ?>> entityCapabilities = getEntityCapabilities(target);
		for(EntityCapability<?, ?> capability : entityCapabilities) {
			if(capability.getTrackingTime() >= 0) {
				List<EntityCapabilityTracker> trackers = TRACKER_MAP.get(watcher);
				if(trackers == null)
					TRACKER_MAP.put(watcher, trackers = new ArrayList<EntityCapabilityTracker>());
				trackers.add(new EntityCapabilityTracker(capability, watcher));
			}
		}
	}

	/**
	 * Removes all necessary trackers for an entity
	 * @param watcher
	 * @param target
	 */
	private static void removeTrackers(EntityPlayerMP watcher, Entity target) {
		List<EntityCapability<?, ?>> entityCapabilities = getEntityCapabilities(target);
		for(EntityCapability<?, ?> capability : entityCapabilities) {
			if(capability.getTrackingTime() >= 0) {
				List<EntityCapabilityTracker> trackers = TRACKER_MAP.get(watcher);
				if(trackers != null) {
					Iterator<EntityCapabilityTracker> it = trackers.iterator();
					while(it.hasNext()) {
						EntityCapabilityTracker tracker = it.next();
						if(tracker.getWatcher() == watcher)
							it.remove();
					}

					if(trackers.isEmpty()) {
						TRACKER_MAP.remove(watcher);
					}
				}
			}
		}
	}
}
