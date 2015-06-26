package thebetweenlands.entities.entityAI;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.entities.mobs.EntityDarkDruid;

public class EntityAIDruidTeleport extends EntityAIBase {
	private EntityAINearestAttackableTarget.Sorter nearestEntitySorter;
	private IEntitySelector farEntitySelector;

	private EntityDarkDruid druid;

	private Entity entityToTeleportTo;

	public EntityAIDruidTeleport(EntityDarkDruid druid) {
		this.druid = druid;
		setMutexBits(1);
        nearestEntitySorter = new EntityAINearestAttackableTarget.Sorter(druid);
        farEntitySelector = new FarEntitySelector(druid, 6);
	}

	@Override
	public boolean shouldExecute() {
		if (druid.canTeleport() && druid.getRNG().nextFloat() < 0.8F) {
			List<EntityPlayer> nearPlayers = druid.worldObj.getEntitiesWithinAABB(EntityPlayer.class, druid.boundingBox.expand(32, 10, 32));
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
	public boolean continueExecuting() {
		return false;
	}

	public class FarEntitySelector implements IEntitySelector {
		private Entity entity;
		private double minDistanceSquared;

		public FarEntitySelector(Entity entity, double minDistance) {
			this.entity = entity;
			this.minDistanceSquared = minDistance * minDistance;
		}

		@Override
		public boolean isEntityApplicable(Entity entity) {
			return this.entity.getDistanceSqToEntity(entity) > minDistanceSquared;
		}
	}
}
