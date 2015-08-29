package thebetweenlands.entities.entityAI;

import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.Vec3;

public class EntityAIBLAvoidEntity extends EntityAIBase {
	public final IEntitySelector field_98218_a = new IEntitySelector() {
		@Override
		public boolean isEntityApplicable(Entity p_82704_1_) {
			return p_82704_1_.isEntityAlive() && entity.getEntitySenses().canSee(p_82704_1_);
		}
	};

	private EntityCreature entity;

	private double farSpeed;

	private double nearSpeed;

	private Entity closestLivingEntity;

	private float targetDistanceFromEntity;

	private PathEntity entityPathEntity;

	private PathNavigate entityPathNavigate;

	private Class targetEntityClass;

	public EntityAIBLAvoidEntity(EntityCreature entity, Class<?> clazz, float targetDistanceFromEntity, double farSpeed, double nearSpeed) {
		this.entity = entity;
		targetEntityClass = clazz;
		this.targetDistanceFromEntity = targetDistanceFromEntity;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		entityPathNavigate = entity.getNavigator();
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (targetEntityClass == EntityPlayer.class) {
			if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed()) {
				return false;
			}
			closestLivingEntity = entity.worldObj.getClosestPlayerToEntity(entity, targetDistanceFromEntity);
			if (closestLivingEntity == null) {
				return false;
			}
		} else {
			List list = entity.worldObj.selectEntitiesWithinAABB(targetEntityClass, entity.boundingBox.expand(targetDistanceFromEntity, 3, targetDistanceFromEntity), field_98218_a);
			if (list.isEmpty()) {
				return false;
			}
			closestLivingEntity = (Entity) list.get(0);
		}
		Vec3 pos = RandomPositionGenerator.findRandomTargetBlockAwayFrom(entity, 16, 7, Vec3.createVectorHelper(closestLivingEntity.posX, closestLivingEntity.posY, closestLivingEntity.posZ));
		if (pos == null) {
			return false;
		} else if (closestLivingEntity.getDistanceSq(pos.xCoord, pos.yCoord, pos.zCoord) < closestLivingEntity.getDistanceSqToEntity(entity)) {
			return false;
		} else {
			entityPathEntity = entityPathNavigate.getPathToXYZ(pos.xCoord, pos.yCoord, pos.zCoord);
			return entityPathEntity == null ? false : entityPathEntity.isDestinationSame(pos);
		}
	}

	@Override
	public boolean continueExecuting() {
		return !entityPathNavigate.noPath();
	}

	@Override
	public void startExecuting() {
		entityPathNavigate.setPath(entityPathEntity, farSpeed);
	}

	@Override
	public void resetTask() {
		closestLivingEntity = null;
	}

	@Override
	public void updateTask() {
		if (entity.getDistanceSqToEntity(closestLivingEntity) < 49) {
			entity.getNavigator().setSpeed(nearSpeed);
		} else {
			entity.getNavigator().setSpeed(farSpeed);
		}
	}

	public void setTargetEntityClass(Class<?> targetEntityClass) {
		this.targetEntityClass = targetEntityClass;
	}
}
