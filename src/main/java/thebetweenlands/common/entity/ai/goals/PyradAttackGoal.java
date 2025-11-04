package thebetweenlands.common.entity.ai.goals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.monster.Pyrad;
import thebetweenlands.common.entity.projectile.PyradFlame;
import thebetweenlands.common.registries.SoundRegistry;

public class PyradAttackGoal extends Goal {
	private final Pyrad pyrad;
	private int attackStep;
	private int attackTime;

	public PyradAttackGoal(Pyrad pyrad) {
		this.pyrad = pyrad;
	}

	@Override
	public boolean canUse() {
		LivingEntity target = this.pyrad.getTarget();
		return target != null && target.isAlive();
	}

	@Override
	public void start() {
		this.attackStep = 0;
	}

	@Override
	public void stop() {
		this.pyrad.setCharging(false);
	}

	@Override
	public void tick() {
		--this.attackTime;
		LivingEntity target = this.pyrad.getTarget();

		if (target != null) {
			double distSq = this.pyrad.distanceToSqr(target);

			if (distSq < 4.0D) {
				if (this.attackTime <= 0) {
					this.attackTime = 20;
					this.pyrad.doHurtTarget(target);
				}

				this.pyrad.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
			} else if (distSq < 256.0D) {
				double dx = target.getX() - this.pyrad.getX();
				double dy = target.getBoundingBox().minY + (double) (target.getBbHeight() / 2.0F) - (this.pyrad.getY() + (double) (this.pyrad.getBbHeight() / 2.0F));
				double dz = target.getZ() - this.pyrad.getZ();

				if (this.attackTime <= 0) {
					++this.attackStep;

					if (this.attackStep == 1) {
						this.attackTime = 20 + this.pyrad.getRandom().nextInt(40);
						this.pyrad.setCharging(true);
					} else if (this.attackStep <= 4) {
						this.attackTime = 6;
					} else {
						this.attackTime = 60 + this.pyrad.getRandom().nextInt(40);
						this.attackStep = 0;
						this.pyrad.setCharging(false);
					}

					if (this.attackStep > 1) {
						float f = Mth.sqrt(Mth.sqrt((float) distSq)) * 0.8F;
						this.pyrad.playSound(SoundRegistry.PYRAD_SHOOT.get(), 1F, 0.8F + this.pyrad.getRandom().nextFloat() * 0.4F);
						int numberFlames = 6; //TODO unhardcode again

						for (int i = 0; i < (numberFlames > 1 ? this.pyrad.getRandom().nextInt(numberFlames) : 0) + 1; ++i) {
							PyradFlame flame = new PyradFlame(this.pyrad.level(), this.pyrad, new Vec3(dx + this.pyrad.getRandom().nextGaussian() * (double) f, dy, dz + this.pyrad.getRandom().nextGaussian() * (double) f));
							flame.setY(this.pyrad.getY() + (double) (this.pyrad.getBbHeight() / 2.0F) + 0.5D);
							this.pyrad.level().addFreshEntity(flame);
						}
					}
				}

				this.pyrad.getLookControl().setLookAt(target, 10.0F, 10.0F);
			}
		}

		super.tick();
	}
}
