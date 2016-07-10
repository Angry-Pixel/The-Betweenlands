package thebetweenlands.entities.properties;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * If you want to keep certain values in sync with clients, use a tracker time of >= 0.
 * Use 0 if you want changes to be sent every tick if the data has changed. Using a tracker time > 0
 * limits how often the packet is sent. The tracker automatically detects any changes and keeps
 * the values in sync.
 */
public abstract class EntityProperties<E extends Entity> implements IExtendedEntityProperties {
	private World world;
	private Entity entity;
	private final Set<PropertiesTracker> trackers = Collections.newSetFromMap(new WeakHashMap<PropertiesTracker, Boolean>());;

	@Override
	public final void init(Entity entity, World world) {
		this.entity = entity;
		this.world = world;
		this.initProperties();
	}

	/**
	 * Returns a list of all currently active trackers
	 * @return
	 */
	public Set<PropertiesTracker> getTrackers() {
		return Collections.unmodifiableSet(this.trackers);
	}

	/**
	 * Forces all active trackers to sync the next tick
	 */
	public void sync() {
		for(PropertiesTracker tracker : this.trackers) {
			tracker.setReady();
		}
	}

	/**
	 * Returns the world
	 * @return
	 */
	public final World getWorld() {
		return this.world;
	}

	/**
	 * Returns the entity
	 * @return
	 */
	public final E getEntity() {
		return (E) this.entity;
	}

	/**
	 * Initializes the properties
	 */
	protected void initProperties() { }

	/**
	 * ID of this property
	 * @return
	 */
	public abstract String getID();

	/**
	 * Entity class or superclass this property should be applied to
	 * @return
	 */
	public abstract Class<E> getEntityClass();

	/**
	 * Tracking time, return a negative number for no tracking
	 * @return
	 */
	public int getTrackingTime() {
		return -1;
	}

	/**
	 * Determines how often the tracking sensitive data is compared
	 * @return
	 */
	public int getTrackingUpdateTime() {
		return 0;
	}

	/**
	 * Write any tracking sensitive data to this NBT. The tracker will fire if
	 * the NBT isn't equal and the tracking timer is ready.
	 * Return true to force the tracker to sync.
	 * @return
	 */
	protected boolean saveTrackingSensitiveData(NBTTagCompound nbt) {
		return false;
	}

	/**
	 * Client reads tracking sensitive data from this method
	 * @param nbt
	 */
	protected void loadTrackingSensitiveData(NBTTagCompound nbt) { }

	/**
	 * Called when the data is syncing
	 */
	protected void onSync() { }

	public static final class PropertiesTracker {
		private int trackingTimer = 0;
		private int trackingUpdateTimer = 0;
		private boolean trackerReady = false;
		private boolean trackerDataChanged = false;

		private NBTTagCompound prevTrackerData = new NBTTagCompound();

		private final Entity entity;
		private final EntityProperties props;

		private PropertiesTracker(Entity entity, EntityProperties props) {
			this.entity = entity;
			this.props = props;
		}

		/**
		 * Updates the tracker
		 */
		public void updateTracker() {
			int trackingFrequency = this.props.getTrackingTime();
			if(trackingFrequency >= 0 && !this.trackerReady) {
				this.trackingTimer++;
				if(this.trackingTimer >= trackingFrequency) {
					this.trackerReady = true;
				}
			}
			int trackingUpdateFrequency = this.props.getTrackingUpdateTime();
			if(this.trackingUpdateTimer < trackingUpdateFrequency) this.trackingUpdateTimer++;
			if(this.trackingUpdateTimer >= trackingUpdateFrequency) {
				if(!this.trackerDataChanged) {
					this.trackingUpdateTimer = 0;
					NBTTagCompound currentTrackingData = new NBTTagCompound();
					if(this.props.saveTrackingSensitiveData(currentTrackingData)) {
						this.trackerDataChanged = true;
					} else {
						if(!currentTrackingData.equals(this.prevTrackerData)) {
							this.trackerDataChanged = true;
						}
					}
					this.prevTrackerData = currentTrackingData;
				}
			}
		}

		/**
		 * Forces the tracker to sync the next tick
		 */
		void setReady() {
			this.trackerReady = true;
			this.trackerDataChanged = true;
		}

		/**
		 * Returns true if the data has changed and the tracking timer is ready and resets the tracking timer
		 * @return
		 */
		boolean isTrackerReady() {
			boolean ready = this.props.getTrackingTime() >= 0 && this.trackerReady && this.trackerDataChanged;
			if(ready) {
				this.trackingTimer = 0;
				this.trackerReady = false;
				this.trackerDataChanged = false;
				return true;
			}
			return false;
		}

		/**
		 * Called when the data is syncing
		 */
		void onSync() {
			this.props.onSync();
		}

		/**
		 * Returns the properties
		 * @return
		 */
		public EntityProperties getProperties() {
			return this.props;
		}

		/**
		 * Returns the tracked entity
		 * @return
		 */
		public Entity getEntity() {
			return this.entity;
		}

		/**
		 * Removes this tracker from the property
		 */
		public void removeTracker() {
			this.props.trackers.remove(this);
		}
	}

	/**
	 * Creates a new tracker for this property
	 * @param entity
	 * @return
	 */
	public final PropertiesTracker createTracker(Entity entity) {
		PropertiesTracker tracker = new PropertiesTracker(entity, this);
		this.trackers.add(tracker);
		return tracker;
	}
	
	/**
	 * Returns whether this property is persistent (only for players)
	 * @return
	 */
	protected boolean isPersistent() {
		return false;
	}
}
