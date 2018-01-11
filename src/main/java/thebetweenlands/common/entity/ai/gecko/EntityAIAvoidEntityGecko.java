package thebetweenlands.common.entity.ai.gecko;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.mobs.EntityGecko;

public class EntityAIAvoidEntityGecko extends EntityAIGeckoHide {
	public final Predicate<Entity> viableSelector = new Predicate<Entity>() {
		@Override
		public boolean apply(Entity entity) {
			return entity.isEntityAlive() && gecko.getEntitySenses().canSee(entity);
		}
	};

	private Entity closestLivingEntity;

	private float distance;

	private Class<? extends Entity> avoidingEntityClass;

	public EntityAIAvoidEntityGecko(EntityGecko gecko, Class<? extends Entity> avoidingEntityClass, float distance, double farSpeed, double nearSpeed) {
		super(gecko, nearSpeed, nearSpeed);
		this.distance = distance;
		this.avoidingEntityClass = avoidingEntityClass;
	}

	@Override
	protected boolean shouldFlee() {
		if (avoidingEntityClass == EntityPlayer.class) {
			closestLivingEntity = gecko.world.getNearestPlayerNotCreative(gecko, distance);
			if (closestLivingEntity == null) {
				return false;
			}
		} else {
			List<Entity> list = gecko.world.getEntitiesWithinAABB(avoidingEntityClass, gecko.getEntityBoundingBox().grow(distance, 3.0D, distance), viableSelector);
			if (list.isEmpty()) {
				return false;
			}
			closestLivingEntity = list.get(0);
		}

		return true;
	}

	@Override
	protected Vec3d getFleeingCausePosition() {
		return gecko != null ? gecko.getPositionVector() : null;
	}
}
