package thebetweenlands.common.entity.ai;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;

public class EntityAIDruidTeleport extends EntityAIBase {
	private EntityAINearestAttackableTarget.Sorter nearestEntitySorter;
	private Predicate<Entity> farSelector;

	private EntityDarkDruid druid;

	private Entity entityToTeleportTo;

	public EntityAIDruidTeleport(EntityDarkDruid druid) {
		this.druid = druid;
		setMutexBits(1);
        nearestEntitySorter = new EntityAINearestAttackableTarget.Sorter(druid);
        farSelector = new FarEntitySelector(druid, 6);
	}

	@Override
	public boolean shouldExecute() {
		if (druid.canTeleport() && druid.getRNG().nextFloat() < 0.4F) {
			List<EntityPlayer> nearPlayers = druid.world.getEntitiesWithinAABB(EntityPlayer.class, druid.getEntityBoundingBox().grow(24, 10, 24));
			Collections.sort(nearPlayers, nearestEntitySorter);
			for (EntityPlayer player : nearPlayers) {
				if (player.onGround && !player.capabilities.disableDamage) {
					entityToTeleportTo = player;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void startExecuting() {
		druid.teleportNearEntity(entityToTeleportTo);
	}

	@Override
	public void resetTask() {
		entityToTeleportTo = null;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return false;
	}


	public class FarEntitySelector implements Predicate<Entity> {
		private Entity entity;
		private double minDistanceSquared;

		public FarEntitySelector(Entity entity, double minDistance) {
			this.entity = entity;
			this.minDistanceSquared = minDistance * minDistance;
		}

		@Override
		public boolean test(Entity entity) {
			return this.entity.getDistanceSq(entity) > minDistanceSquared;
		}
	}
}
