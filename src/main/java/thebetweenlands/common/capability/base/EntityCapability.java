package thebetweenlands.common.capability.base;

import com.google.common.base.Preconditions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageSyncEntityCapabilities;

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
	protected boolean dirty = false;

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
	 * Returns whether this capability is applicable to the entity
	 * @param obj
	 * @return
	 */
	public boolean isApplicable(Entity entity) {
		return false;
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
	public void markDirty() {
		this.dirty = true;
	}

	/**
	 * Returns whether the data is dirty
	 * @return
	 */
	public boolean isDirty() {
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
	public boolean isPersistent(EntityPlayer oldPlayer, EntityPlayer newPlayer) {
		return false;
	}

	/**
	 * Sends a packet with all the tracking sensitive data
	 */
	public void sendPacket(EntityPlayerMP player) {
		this.dirty = false;
		MessageSyncEntityCapabilities message = new MessageSyncEntityCapabilities(this);
		TheBetweenlands.networkWrapper.sendTo(message, player);
	}
}
