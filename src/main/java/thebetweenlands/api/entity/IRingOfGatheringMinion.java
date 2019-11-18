package thebetweenlands.api.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public interface IRingOfGatheringMinion {
	/**
	 * Called when the entity is being returned from the ring.
	 * This entity is not yet spawned in the world. If the entity needs to be
	 * spawned in the world then it needs to be done in this method.
	 * @param user Entity using the ring to return this entity
	 * @param nbt NBT returned by {@link #returnToRing(UUID)}
	 * @return
	 */
	public boolean returnFromRing(Entity user, NBTTagCompound nbt);

	/**
	 * Called when the entity is returned to the ring.
	 * No additional data of the entity is saved, only the NBT that is returned
	 * by this method.
	 * @param userId
	 * @return
	 */
	public default NBTTagCompound returnToRing(UUID userId) {
		return new NBTTagCompound();
	}

	@Nullable
	public UUID getRingOwnerId();

	public default boolean shouldReturnOnDeath(boolean isOwnerLoggedIn) {
		return true;
	}

	public default boolean shouldReturnOnUnload(boolean isOwnerLoggedIn) {
		//Don't kill if player has logged out, causing the chunks to unload
		return isOwnerLoggedIn;
	}

	/**
	 * Whether this entity can only be returned by an animator.
	 * Default is true if entity is dead.
	 * @return
	 */
	public default boolean isRespawnedByAnimator() {
		return !((Entity) this).isEntityAlive();
	}

	public default int getAnimatorLifeCrystalCost() {
		return 24;
	}

	public default int getAnimatorSulfurCost() {
		return 16;
	}
}
