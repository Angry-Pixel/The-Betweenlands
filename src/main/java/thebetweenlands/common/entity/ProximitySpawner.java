package thebetweenlands.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;

public interface ProximitySpawner extends BLEntity {

	/**
	 * Amount to extend proximity area in XZ axis
	 *
	 * @return amount to expand proximity box all around in the x & z axis.
	 */
	float getProximityHorizontal();

	/**
	 * Amount to extend proximity area in Y axis
	 *
	 * @return amount to expand proximity box all around in the y axis.
	 */
	float getProximityVertical();

	/**
	 * Test if entity can sneak past
	 *
	 * @return true to allow player to sneak past. false to deny it.
	 */
	boolean canSneakPast();

	/**
	 * Test if entity needs line of sight to activate
	 *
	 * @return true for yes. false for no.
	 */
	boolean checkSight();


	/**
	 * Test if spawner is just a single use
	 *
	 * @return true to set dead after spawn. false to deny it.
	 */
	boolean isSingleUse();

	/**
	 * Action to happen just before entity spawns
	 * <p>
	 * Can be used for setting Spawned Entities' position or attributes etc
	 * By default sets the spawned entity to the same pos as the proximity spawner was.
	 * Override to change.
	 */

	default void performPreSpawnaction(@Nullable Entity spawner, @Nullable Entity entitySpawned) {
		if(spawner != null && entitySpawned != null)
			entitySpawned.setPos(spawner.blockPosition().getX() + 0.5F, spawner.blockPosition().getY(), spawner.blockPosition().getZ() + 0.5F);
	}

	/**
	 * Action to happen just after entity spawns
	 * <p>
	 * Entity can be null
	 */

	default void performPostSpawnaction(@Nullable Entity spawner, @Nullable Entity entitySpawned) { }

	/**
	 * The Proximity box used
	 *
	 * @return an AxisAlignedBB for the proximity area.
	 */
	default AABB proximityBox(LivingEntity spawner) {
		return spawner.getBoundingBox().inflate(this.getProximityHorizontal(), this.getProximityVertical(), this.getProximityHorizontal());
	}

	default <T extends LivingEntity> void checkArea(LivingEntity spawner, Class<T> toDetect) {
		List<T> list = spawner.level().getEntitiesOfClass(toDetect, this.proximityBox(spawner), entity -> this.canEntityBeDetected(spawner, entity));
		for (T entity : list) {
			if (entity != null && entity != this) {
				this.performDetectionLogic(entity);
				return;
			}
		}

		this.performIdlingLogic();
	}

	default <T extends LivingEntity> boolean canEntityBeDetected(LivingEntity spawner, T entity) {
		if (spawner.level().getDifficulty() != Difficulty.PEACEFUL && spawner instanceof Enemy) return false;
		if (this.canSneakPast() && entity.isShiftKeyDown())
			return false;
		else if (this.checkSight() && !spawner.hasLineOfSight(entity))
			return false;
		return EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity);
	}

	<T extends LivingEntity> void performDetectionLogic(T detected);

	default void performIdlingLogic() {
	}
}