package thebetweenlands.common.capability.base;

import com.google.common.base.Preconditions;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Internal representation and wrapper of entity capabilities.
 * <p>Use their respective {@link Capability} to interface with the data.
 * <p><b>Note:</b> This class <b>must</b> be an implementation of the capability it provides!
 *
 * @param <F> The default implementation of the capability
 * @param <T> The capability
 * @param <E> The entity type
 */
public abstract class EntityCapability<F extends EntityCapability<F, T, E>, T, E extends Entity> extends AbstractCapability<F, T, E> {
	private E entity;
	private boolean dirty = false;

	protected EntityCapability() {
		//Make sure the entity capability is the implementation of the capability
		Preconditions.checkState(this.getCapabilityClass().isAssignableFrom(this.getClass()), "Entity capability %s must implement %s", this.getClass().getName(), this.getCapabilityClass().getName());
	}

	@SuppressWarnings("unchecked")
	void setEntity(Entity entity) {
		this.entity = (E) entity;
	}

	/**
	 * Initializes the default values
	 * <p><b>Note:</b> This is called before the capability is attached to the entity
	 */
	protected void init() {

	}

	/**
	 * Returns the entity
	 * @return
	 */
	public final E getEntity() {
		return this.entity;
	}


	/**
	 * Returns the entity capability that is specific to the specified entity
	 * @param entity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final EntityCapability<?, ?, E> getEntityCapability(E entity) {
		if(entity.hasCapability(this.getCapability(), null))
			return (EntityCapability<?, ?, E>) entity.getCapability(this.getCapability(), null);
		return null;
	}

	/**
	 * Writes the tracked data to the nbt
	 * @param nbt
	 */
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {

	}

	/**
	 * Reads the tracked data from the nbt
	 * @param nbt
	 */
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {

	}

	/**
	 * Marks the data as dirty
	 */
	public final void markDirty() {
		this.dirty = true;
	}

	/**
	 * Sets whether the data is dirty
	 * @param dirty
	 */
	public final void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Returns whether the data is dirty
	 * @return
	 */
	public final boolean isDirty() {
		return this.dirty;
	}

	/**
	 * Tracking time, return a negative number for no tracking
	 * @return
	 */
	public int getTrackingTime() {
		return -1;
	}

	/**
	 * Returns whether this capability is persistent for players
	 * @return
	 */
	public boolean isPersistent() {
		return false;
	}
	
	@Override
	public boolean isApplicable(Entity obj) {
		return false;
	}
}
