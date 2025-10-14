package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.entity.DraetonPuller;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.function.Predicate;

public class ChiromawRiderTargetPullerGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

	protected final double minHeight;

	public ChiromawRiderTargetPullerGoal(PathfinderMob creature, Class<T> targetType, boolean checkSight, double minHeight) {
		super(creature, targetType, 10, checkSight, false, entity -> entity instanceof DraetonPuller);
		this.minHeight = minHeight;
	}

	public ChiromawRiderTargetPullerGoal(PathfinderMob creature, Class<T> targetType, int chance, boolean checkSight, boolean onlyNearby, Predicate<LivingEntity> targetSelector, double minHeight) {
		super(creature, targetType, chance, checkSight, onlyNearby, targetSelector.and(entity -> entity instanceof DraetonPuller));
		this.minHeight = minHeight;
	}

	@Override
	public boolean canUse() {
		if (super.canUse()) {
			if (this.target != null) {
				if (this.minHeight > 0) {
					Entity checkEntity = this.target;

					//TODO once draeton is implemented
//					if (checkEntity instanceof DraetonPuller puller) {
//						Draeton carriage = puller.getCarraige();
//						if (carriage != null) {
//							checkEntity = carriage;
//						}
//					}

					BlockPos surface = this.mob.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, checkEntity.blockPosition());
					if (checkEntity.getY() - surface.getY() < this.minHeight) {
						this.target = null;
						return false;
					}
				}

				double distance = this.mob.distanceToSqr(this.target);

				if (distance <= 576.0D) {
					this.mob.playSound(SoundRegistry.GREEBLING_HEY.get(), 0.5F, 1F);
				}
			}

			return true;
		}
		return false;
	}

	@Override
	protected AABB getTargetSearchArea(double targetDistance) {
		return this.mob.getBoundingBox().inflate(targetDistance);
	}

	@Override
	protected double getFollowDistance() {
		return 90; //because softcoding is for wimps :P
	}
}
