package thebetweenlands.common.entity.ai;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.Vec3d;

public class EntityAvoidEntityFlatPath<T extends Entity> extends EntityAIBase {
	private final Predicate<Entity> canBeSeenSelector;
	protected EntityCreature entity;
	private final double farSpeed;
	private final double nearSpeed;
	protected T closestLivingEntity;
	private final float avoidDistance;
	private Path path;
	private final PathNavigate navigation;
	private final Class<T> classToAvoid;
	private final Predicate<? super T> avoidTargetSelector;

	public EntityAvoidEntityFlatPath(EntityCreature entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
		this(entityIn, classToAvoidIn, Predicates.alwaysTrue(), avoidDistanceIn, farSpeedIn, nearSpeedIn);
	}

	public EntityAvoidEntityFlatPath(EntityCreature entityIn, Class<T> classToAvoidIn,
			Predicate<? super T> avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
		this.canBeSeenSelector = new Predicate<Entity>() {
			public boolean apply(@Nullable Entity entity) {
				return entity.isEntityAlive() && EntityAvoidEntityFlatPath.this.entity.getEntitySenses().canSee(entity) && !EntityAvoidEntityFlatPath.this.entity.isOnSameTeam(entity);
			}
		};
		entity = entityIn;
		classToAvoid = classToAvoidIn;
		avoidTargetSelector = avoidTargetSelectorIn;
		avoidDistance = avoidDistanceIn;
		farSpeed = farSpeedIn;
		nearSpeed = nearSpeedIn;
		navigation = entityIn.getNavigator();
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		List<T> list = entity.world.<T>getEntitiesWithinAABB(classToAvoid, entity.getEntityBoundingBox().grow((double) avoidDistance, 3.0D, (double) avoidDistance), Predicates.and(EntitySelectors.CAN_AI_TARGET, canBeSeenSelector, avoidTargetSelector));
		if (list.isEmpty()) {
			return false;
		} else {
			closestLivingEntity = list.get(0);
			Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(entity, 16, 1, new Vec3d(closestLivingEntity.posX, closestLivingEntity.posY, closestLivingEntity.posZ));
			if (vec3d == null) {
				return false;
			} else if (closestLivingEntity.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < closestLivingEntity.getDistanceSq(entity)) {
				return false;
			} else {
				path = navigation.getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
				return path != null;
			}
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !navigation.noPath();
	}

	@Override
	public void startExecuting() {
		navigation.setPath(path, farSpeed);
	}

	@Override
	public void resetTask() {
		closestLivingEntity = null;
	}

	@Override
	public void updateTask() {
		if (entity.getDistanceSq(closestLivingEntity) < 64D)
			entity.getNavigator().setSpeed(nearSpeed);
		else
			entity.getNavigator().setSpeed(farSpeed);
	}
}