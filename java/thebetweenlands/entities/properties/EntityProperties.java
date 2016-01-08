package thebetweenlands.entities.properties;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * If you want to keep certain values in sync with clients, use a tracker time of >= 0.
 * Use 0 if you want changes to be sent every tick if the data has changed. Using a tracker time > 0
 * limits how often the packet is sent. The tracker automatically detects any changes and keeps
 * the values in sync.
 */
public abstract class EntityProperties implements IExtendedEntityProperties {
	/**
	 * ID of this property
	 * @return
	 */
	public abstract String getID();

	/**
	 * Entity class or superclass this property should be applied to
	 * @return
	 */
	public abstract Class<? extends Entity> getEntityClass();

	/**
	 * Tracking time, return a negative number for no tracking
	 * @return
	 */
	public int getTrackingTime() {
		return -1;
	}

	/**
	 * Write any tracking sensitive data to this NBT. The tracker will fire if
	 * the NBT isn't equal and the tracking timer is ready.
	 * Return true to force the tracker to sync.
	 * @return
	 */
	public boolean saveTrackingSensitiveData(NBTTagCompound nbt) {
		return false;
	}

	/**
	 * Client reads tracking sensitive data from this method
	 * @param nbt
	 */
	public void loadTrackingSensitiveData(NBTTagCompound nbt) { }

	public static final class PropertiesTracker {
		private int trackingTimer = 0;
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
			if(!this.trackerDataChanged) {
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
	}

	/**
	 * Creates a new tracker
	 * @param entity
	 * @return
	 */
	public final PropertiesTracker createTracker(Entity entity) {
		return new PropertiesTracker(entity, this);
	}
}
