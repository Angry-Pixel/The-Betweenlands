package thebetweenlands.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class ProximitySpawnerEntity extends PathfinderMob implements BLEntity {

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 5.0D)
				.add(Attributes.MOVEMENT_SPEED, 01D);
	}

	public ProximitySpawnerEntity(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
	}
	/**
	 * Amount to extend proximity area in XZ axis
	 *
	 * @return amount to expand proximity box all around in the x & z axis.
	 */
	protected abstract float getProximityHorizontal();

	/**
	 * Amount to extend proximity area in Y axis
	 *
	 * @return amount to expand proximity box all around in the y axis.
	 */
	protected abstract float getProximityVertical();

	/**
	 * Test if entity can sneak past
	 *
	 * @return true to allow player to sneak past. false to deny it.
	 */
	protected abstract boolean canSneakPast();

	/**
	 * Test if entity needs line of sight to activate
	 *
	 * @return true for yes. false for no.
	 */
	protected abstract boolean checkSight();

	/**
	 * Which entity should be spawned on activation
	 *
	 * @return an Entity or null.
	 */
	@Nullable
	protected abstract Entity getEntitySpawned();

	/**
	 * How Many Entities should be spawned
	 *
	 * @return an int amount.
	 */
	protected abstract int getEntitySpawnCount();

	/**
	 * Test if spawner is just a single use
	 *
	 * @return true to set dead after spawn. false to deny it.
	 */
	protected abstract boolean isSingleUse();

	/**
	 * How many spawns this does (NYI)
	 *
	 * @return amount of uses.
	 */
	protected abstract int maxUseCount();

	/**
	 * Action to happen just before entity spawns
	 *
	 * Can be used for setting Spawned Entities' position or attributes etc
	 * By default sets the spawned entity to the same pos as the proximity spawner was.
	 * Override to change.
	 */

	protected void performPreSpawnaction(@Nullable Entity targetEntity, @Nullable Entity entitySpawned) {
		if(entitySpawned != null)
			entitySpawned.setPos(blockPosition().getX() + 0.5F, blockPosition().getY(), blockPosition().getZ() + 0.5F);
	}

	/**
	 * Action to happen just after entity spawns
	 *
	 * Entity can be null
	 */

	protected void performPostSpawnaction(@Nullable Entity targetEntity, @Nullable Entity entitySpawned) { }

	/**
	 * The Proximity box used
	 *
	 * @return an AxisAlignedBB for the proximity area.
	 */

	protected AABB proximityBox() {
		return new AABB(blockPosition()).inflate(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal());
	}

	/**
	 * Generic area checking code and spawning.
	 *
	 * @return returns a null :( - bad modder.
	 */
	@SuppressWarnings("resource")
	@Nullable
	protected Entity checkArea() {
		if (!level().isClientSide && level().getDifficulty() != Difficulty.PEACEFUL) {
			List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				Entity entity = list.get(entityCount);
				if (entity != null)
					if (entity instanceof Player && !((Player) entity).isSpectator() && !((Player) entity).isCreative()) {
						if (canSneakPast() && entity.isCrouching())
							return null;
						else if (checkSight() && !hasLineOfSight(entity))
							return null;
						else {
							for (int count = 0; count < getEntitySpawnCount(); count++) {
								Entity spawn = getEntitySpawned();
								if (spawn != null) {
									performPreSpawnaction(entity, spawn);
									if (!spawn.isRemoved()) // just in case of pre-emptive removal
										level().addFreshEntity(entity);
									performPostSpawnaction(entity, spawn);
								}
							}
							if (!isRemoved() && isSingleUse())
								discard();
						}
					}
			}
		}
		return null;
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}
}