package thebetweenlands.common.entity.ai.goals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import thebetweenlands.common.entity.monster.chiromaw.TameChiromaw;
import thebetweenlands.common.entity.projectile.arrow.ChiromawBarb;
import thebetweenlands.common.entity.projectile.arrow.ChiromawShockBarb;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class ChiromawShootBarbGoal extends Goal {

	private final TameChiromaw chiromaw;
	public int attackTimer;

	public ChiromawShootBarbGoal(TameChiromaw chiromaw) {
		this.chiromaw = chiromaw;
	}

	@Override
	public boolean canUse() {
		return this.chiromaw.getTarget() != null && !this.chiromaw.isInSittingPose();
	}

	@Override
	public void start() {
		this.attackTimer = 0;
	}

	@Override
	public void stop() {
		this.chiromaw.setAttacking(false);
	}

	@Override
	public void tick() {
		LivingEntity target = this.chiromaw.getTarget();
		if (target != null) {
			Level level = this.chiromaw.level();
			if (target.distanceToSqr(this.chiromaw) < 576 && target.distanceToSqr(this.chiromaw) > 25 && this.chiromaw.hasLineOfSight(target)) {
				++this.attackTimer;
				if (this.attackTimer == 20) {
					AbstractArrow arrow = this.chiromaw.getElectricBoogaloo() ? new ChiromawShockBarb(EntityRegistry.CHIROMAW_SHOCK_BARB.get(), level) : new ChiromawBarb(EntityRegistry.CHIROMAW_BARB.get(), level);

					double targetX = target.getX() + target.getDeltaMovement().x() - this.chiromaw.getX();
					double targetY = target.getY() + target.getEyeHeight() - this.chiromaw.getEyeY();
					double targetZ = target.getZ() + target.getDeltaMovement().z() - this.chiromaw.getZ();

					arrow.shoot(targetX, targetY, targetZ, 1.2F, 0.0F);

					double g = -0.03D;

					double vy0 = 0.35D;

					double tmax = -vy0 / g;

					double s = vy0 * tmax + 0.5D * g * tmax * tmax;

					double h = (target.getY() + 0.5D - this.chiromaw.getY());

					double fall = h - s;

					if (fall < 0) {
						double tmin = Mth.sqrt((float) (fall * 2 / g));

						double t = tmax + tmin;

						double dx = (target.getX() + (this.chiromaw.getRandom().nextFloat() - 0.5D) * 2.2D - this.chiromaw.getX()) + target.getDeltaMovement().x() * t * 0.75D;
						double dz = (target.getZ() + (this.chiromaw.getRandom().nextFloat() - 0.5D) * 2.2D - this.chiromaw.getZ()) + target.getDeltaMovement().z() * t * 0.75D;

						double len = Mth.sqrt((float) (dx * dx + dz * dz));

						dx /= len;
						dz /= len;

						double speed = len / t * 1.5F /*constant adjustment for MC physics*/;

						arrow.setDeltaMovement(dx * speed, vy0, dz * speed);
					}

					level.addFreshEntity(arrow);

					this.chiromaw.playSound(SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE.get(), 0.5F, 1F + (this.chiromaw.getRandom().nextFloat() - this.chiromaw.getRandom().nextFloat()) * 0.8F);
					this.attackTimer = -20;
				}

			} else if (this.attackTimer > 0) {
				--this.attackTimer;
			}
			this.chiromaw.setAttacking(this.attackTimer > 10);
		}
	}
}
