package thebetweenlands.common.entity.ai;

import java.util.List;

import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.Vec3d;

public class EntityAIBLAvoidEntity extends EntityAIBase {

    private EntityCreature entity;

    private double farSpeed;

    private double nearSpeed;

    private Entity closestLivingEntity;

    private float targetDistanceFromEntity;

    private Path entityPathEntity;

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
            closestLivingEntity = entity.world.getClosestPlayerToEntity(entity, targetDistanceFromEntity);
            if (closestLivingEntity == null) {
                return false;
            }
        } else {
            List list = entity.world.getEntitiesWithinAABB(targetEntityClass, entity.getEntityBoundingBox().grow(targetDistanceFromEntity, 3, targetDistanceFromEntity), Predicates.and(EntitySelectors.IS_ALIVE, EntitySelectors.CAN_AI_TARGET));
            if (list.isEmpty()) {
                return false;
            }
            closestLivingEntity = (Entity) list.get(0);
        }
        Vec3d pos = RandomPositionGenerator.findRandomTargetBlockAwayFrom(entity, 16, 7, new Vec3d(closestLivingEntity.posX, closestLivingEntity.posY, closestLivingEntity.posZ));
        if (pos == null) {
            return false;
        } else if (closestLivingEntity.getDistanceSq(pos.x, pos.y, pos.z) < closestLivingEntity.getDistanceSq(entity)) {
            return false;
        } else {
            entityPathEntity = entityPathNavigate.getPathToXYZ(pos.x, pos.y, pos.z);
            return entityPathEntity != null && entityPathEntity.isSamePath(entityPathEntity);
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
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
        if (entity.getDistanceSq(closestLivingEntity) < 49) {
            entity.getNavigator().setSpeed(nearSpeed);
        } else {
            entity.getNavigator().setSpeed(farSpeed);
        }
    }

    public void setTargetEntityClass(Class<?> targetEntityClass) {
        this.targetEntityClass = targetEntityClass;
    }
}
