package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.monster.SwampHag;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.EnumSet;

public class ThrowWormGoal extends Goal {

	private final SwampHag hag;
	@Nullable
	private LivingEntity target;

	public ThrowWormGoal(SwampHag hag) {
		this.hag = hag;
		this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		this.target = this.hag.getTarget();

		if (!this.hag.isRidingMummy())
			return false;
		if (this.hag.isRidingMummy() /*&& !this.hag.getMummyMount().isSpawningFinished()*/)
			return false;
		if (this.target == null)
			return false;
		else {
			double distance = this.hag.distanceToSqr(this.target);
			return distance >= 16.0D && distance <= 1024.0D && this.hag.getThrowTimer() >= 90 && this.hag.hasLineOfSight(this.target);
		}
	}

	@Override
	public boolean canContinueToUse() {
		return this.target != null && this.hag.hurtTime <= 40 && this.hag.getThrowTimer() >= 90 && this.hag.isRidingMummy() && this.hag.hasLineOfSight(this.target);
	}

	@Override
	public void tick() {
		if(!this.hag.getIsThrowing())
			this.hag.setIsThrowing(true);
		if(this.target != null) {
			this.hag.lookAt(this.target, 30F, 30F);
			this.hag.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
			if (hag.getThrowTimer() == 90) {
				double targetX = this.target.getX() - this.hag.getX();
				double targetY = this.target.getBoundingBox().minY + (double) (this.target.getBbHeight() / 2.0F) - (this.hag.getY() + (double) (this.hag.getBbHeight() / 2.0F));
				double targetZ = this.target.getZ() - this.hag.getZ();
//				double targetDistance = Math.sqrt(targetX * targetX + targetZ * targetZ);
//				TinySludgeWorm worm = new TinySludgeWorm(this.hag.level());
//				worm.setPos(this.hag.getX(), this.hag.getY() + (double) this.hag.getEyeHeight() - 0.10000000149011612D, this.hag.getZ());
//				this.throwWorm(worm, targetX, targetY + targetDistance * 0.2D, targetZ, 1.6F, 0F);
//				this.hag.level().addFreshEntity(worm);
				this.hag.playSound(SoundRegistry.WORM_THROW.get(), 1F, 1F + (this.hag.level().getRandom().nextFloat() - this.hag.level().getRandom().nextFloat()) * 0.8F);
				this.hag.playPullSound = true;
			}
		}
		if (this.hag.getThrowTimer() == 102) {
			if (this.hag.getIsThrowing()) {
				this.hag.setIsThrowing(false);
				this.hag.setThrowTimer(0);
				this.hag.playPullSound = true;
			}
			this.stop();
		}
	}

	@Override
	public void stop() {
		this.target = null;
	}

//	public void throwWorm(TinySludgeWorm entity, double x, double y, double z, float velocity, float inaccuracy) {
//		double f = Math.sqrt(x * x + y * y + z * z);
//		x /= f;
//		y /= f;
//		z /= f;
//		x = x + entity.getRandom().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
//		y = y + entity.getRandom().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
//		z = z + entity.getRandom().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
//		x = x * (double) velocity;
//		y = y * (double) velocity;
//		z = z * (double) velocity;
//		entity.setDeltaMovement(x, y, z);
//		double f1 = Math.sqrt(x * x + z * z);
//		entity.setYRot((float) (Mth.atan2(x, z) * Mth.RAD_TO_DEG));
//		entity.setXRot((Mth.atan2(y, f1) * Mth.DEG_TO_RAD));
//		entity.yRot0 = entity.getYRot();
//		entity.xRot0 = entity.getXRot();
//	}
}
