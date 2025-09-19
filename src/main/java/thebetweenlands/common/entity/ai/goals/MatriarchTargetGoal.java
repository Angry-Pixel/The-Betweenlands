package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawMatriarch;
import thebetweenlands.common.registries.SoundRegistry;

public class MatriarchTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

	protected double baseRange;
	protected double revengeRange;

	public MatriarchTargetGoal(ChiromawMatriarch creature, Class<T> classTarget, boolean checkSight, double range) {
		super(creature, classTarget, checkSight);
		this.baseRange = this.revengeRange = range;
	}

	@Override
	public boolean canUse() {
		if (super.canUse()) {
			if (this.target != null) {
				double distance = this.mob.distanceTo(this.target);
				if (distance <= 256.0D)
					this.mob.playSound(SoundRegistry.CHIROMAW_MATRIARCH_ROAR.get(), 2F, 1.5F);
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
		if (this.mob.getTarget() != null) {
			this.revengeRange = this.mob.distanceTo(this.mob.getTarget());
		}
		return Math.max(this.baseRange, this.revengeRange);
	}
}
