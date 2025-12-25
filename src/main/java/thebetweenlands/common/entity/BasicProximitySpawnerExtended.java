package thebetweenlands.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class BasicProximitySpawnerExtended extends BasicProximitySpawner {

	protected BasicProximitySpawnerExtended(EntityType<? extends Mob> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 5.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.0D);
	}

	/**
	 * Which entity should be spawned on activation
	 *
	 * @return an Entity or null.
	 */
	@Nullable
	public abstract
	Entity getEntitySpawned();

	/**
	 * How Many Entities should be spawned
	 *
	 * @return an int amount.
	 */
	public abstract int getEntitySpawnCount();

	/**
	 * How many spawns this does (NYI)
	 *
	 * @return amount of uses.
	 */
	public abstract int maxUseCount();

}
