package thebetweenlands.api.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;

import javax.annotation.Nullable;
import java.util.UUID;

public interface RingOfGatheringMinion {

	/**
	 * Called when the entity is being returned from the ring.
	 * This entity is not yet spawned in the world. If the entity needs to be
	 * spawned in the world then it needs to be done in this method.
	 * @param user Entity using the ring to return this entity
	 * @param tag CompoundTag returned by {@link #returnToRing(UUID)}
	 * @return
	 */
	public boolean returnFromRing(Entity user, CompoundTag tag);

	/**
	 * Called when the entity should be teleported back to the user
	 * @param user
	 * @return
	 */
	public default void returnToCall(Entity user) {
		((Entity) this).setPos(user.position());
	}

	/**
	 * Called when the entity is returned to the ring.
	 * No additional data of the entity is saved, only the NBT that is returned
	 * by this method.
	 * @param userId
	 * @return
	 */
	default CompoundTag returnToRing(UUID userId) {
		return new CompoundTag();
	}

	@Nullable
	UUID getRingOwnerUUID();

	default boolean shouldReturnOnDeath(boolean isOwnerLoggedIn) {
		return true;
	}

	default boolean shouldReturnOnUnload(boolean isOwnerLoggedIn) {
		//Don't kill if player has logged out, causing the chunks to unload
		return isOwnerLoggedIn;
	}

	default boolean shouldReturnOnCall() {
		return !(this instanceof TamableAnimal animal) || !animal.isInSittingPose();
	}

	/**
	 * Whether this entity can only be returned by an animator.
	 * Default is true if entity is dead.
	 * @return
	 */
	default boolean isRespawnedByAnimator() {
		return !((Entity) this).isAlive();
	}

	default int getAnimatorLifeCrystalCost() {
		return 24;
	}

	default int getAnimatorSulfurCost() {
		return 16;
	}
}
