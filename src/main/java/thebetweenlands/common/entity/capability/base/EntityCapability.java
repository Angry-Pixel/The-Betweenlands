package thebetweenlands.common.entity.capability.base;

import java.util.concurrent.Callable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * Internal representation and wrapper of entity capabilities.
 * <p>Use their respective {@link Capability} to interface with the data.
 * <p><b>Note:</b> This class <b>must</b> be an implementation of the capability it provides!
 *
 * @param <F> The default implementation of the capability
 * @param <T> The capability
 * @param <E> The entity type
 */
public abstract class EntityCapability<F, T, E extends Entity> implements IStorage<T>, Callable<T> {
	private E entity;
	private boolean dirty = false;

	@Override
	public final NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public final void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
		if(nbt instanceof NBTTagCompound) {
			this.readFromNBT((NBTTagCompound)nbt);
		}
	}

	@Override
	public final T call() throws Exception {
		return (T) this.getDefaultCapabilityImplementation();
	}

	@SuppressWarnings("unchecked")
	void setEntity(Entity entity) {
		this.entity = (E) entity;
	}

	/**
	 * Returns the entity
	 * @return
	 */
	public final E getEntity() {
		return this.entity;
	}

	/**
	 * Returns the capability ID
	 * @return
	 */
	public abstract ResourceLocation getID();

	/**
	 * Returns a <b>new</b> instance of the capability with the default state
	 * @return
	 */
	protected abstract T getDefaultCapabilityImplementation();

	/**
	 * Returns the internal capability instance.
	 * <p>Use the {@link net.minecraftforge.common.capabilities.CapabilityInject} annotation to retrieve the capability
	 * @return
	 */
	protected abstract Capability<T> getCapability();

	/**
	 * Returns the internal capability class
	 * @return
	 */
	protected abstract Class<T> getCapabilityClass();

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
	 * Writes the data to the nbt
	 * @param nbt
	 */
	public void writeToNBT(NBTTagCompound nbt) {

	}

	/**
	 * Reads the data from the nbt
	 * @param nbt
	 */
	public void readFromNBT(NBTTagCompound nbt) {

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
	 * Returns whether this capability is applicable to the specified entity
	 * @param entity
	 * @return
	 */
	public abstract boolean isApplicable(Entity entity);

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
}
