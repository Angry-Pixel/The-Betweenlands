package thebetweenlands.common.capability.base;

import net.minecraft.entity.player.EntityPlayerMP;

public class EntityCapabilityTracker {
	private final EntityCapability<?, ?, ?> entityCapability;
	private final EntityPlayerMP watcher;

	private boolean trackerReady = false;
	private int lastUpdate = 0;

	public EntityCapabilityTracker(EntityCapability<?, ?, ?> entityCapability, EntityPlayerMP watcher) {
		this.entityCapability = entityCapability;
		this.watcher = watcher;
	}

	/**
	 * Returns the watcher
	 * @return
	 */
	public EntityPlayerMP getWatcher() {
		return this.watcher;
	}

	/**
	 * Returns the entity capability
	 * @return
	 */
	public EntityCapability<?, ?, ?> getEntityCapability() {
		return this.entityCapability;
	}

	/**
	 * Updates the tracker
	 */
	public void update() {
		if(this.lastUpdate < this.entityCapability.getTrackingTime()) {
			this.lastUpdate++;
		} else {
			this.trackerReady = true;
		}

		if(this.trackerReady && this.entityCapability.isDirty()) {
			this.lastUpdate = 0;
			this.trackerReady = false;
			this.entityCapability.sendPacket(this.watcher);
		}
	}
}
