package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawMatriarch;
import thebetweenlands.common.entity.projectile.arrow.ChiromawBarb;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.EnumSet;

//[VanillaCopy] of MeleeAttackGoal, edits noted
public class MatriarchGrabAndReleaseTargetGoal extends Goal {

	private final ChiromawMatriarch matriarch;
	private final double speedModifier;
	private final boolean mustSeeTarget;
	@Nullable
	private Path path;
	private double pathedTargetX;
	private double pathedTargetY;
	private double pathedTargetZ;
	private int ticksUntilNextPathRecalculation;
	private int ticksUntilNextAttack;
	private long lastCanUseCheck;
	private float rotation;

	public MatriarchGrabAndReleaseTargetGoal(ChiromawMatriarch matriarch, double speed, boolean mustSeeTarget) {
		this.matriarch = matriarch;
		this.speedModifier = speed;
		this.mustSeeTarget = mustSeeTarget;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		long i = this.matriarch.level().getGameTime();
		if (i - this.lastCanUseCheck < 20L) {
			return false;
		} else {
			this.lastCanUseCheck = i;
			LivingEntity livingentity = this.matriarch.getTarget();
			if (livingentity == null || livingentity instanceof ChiromawMatriarch) {
				return false;
			} else if (!livingentity.isAlive()) {
				return false;
			} else {
				this.path = this.matriarch.getNavigation().createPath(livingentity, 0);
				return this.path != null || this.matriarch.isWithinMeleeAttackRange(livingentity);
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity entity = this.matriarch.getTarget();
		if (entity == null || entity instanceof ChiromawMatriarch) {
			return false;
		} else if (this.matriarch.isVehicle()) {
			return false;
		} else if (!entity.isAlive()) {
			return false;
		} else if (!this.mustSeeTarget) {
			return !this.matriarch.getNavigation().isDone();
		} else {
			return this.matriarch.isWithinRestriction(entity.blockPosition()) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity);
		}
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void start() {
		this.matriarch.getNavigation().moveTo(this.path, this.speedModifier);
		this.matriarch.setAggressive(true);
		this.ticksUntilNextPathRecalculation = 0;
		this.ticksUntilNextAttack = 0;
	}

	@Override
	public void stop() {
		LivingEntity livingentity = this.matriarch.getTarget();
		if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
			this.matriarch.setTarget(null);
		}

		this.matriarch.setAggressive(false);
		this.matriarch.getNavigation().stop();
		this.rotation = 0;
		this.matriarch.setSpinning(false);
	}

	//[VanillaCopy] of MeleeAttackGoal.tick, edits noted
	@Override
	public void tick() {
		LivingEntity target = this.matriarch.getTarget();
		if (target != null) {
			//BL: only look at target if not spinning
			if (!this.matriarch.isSpinning()) this.matriarch.getLookControl().setLookAt(target, 30.0F, 30.0F);

			this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
			if (!this.matriarch.isVehicle()) {
				if ((this.mustSeeTarget || this.matriarch.getSensing().hasLineOfSight(target)) &&
					this.ticksUntilNextPathRecalculation <= 0 &&
					(this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0 ||
						target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0 ||
						this.matriarch.getRandom().nextFloat() < 0.05F)) {
					this.pathedTargetX = target.getX();
					this.pathedTargetY = target.getY();
					this.pathedTargetZ = target.getZ();
					this.ticksUntilNextPathRecalculation = 4 + this.matriarch.getRandom().nextInt(7);
					double d0 = this.matriarch.distanceToSqr(target);
					if (d0 > 1024.0) {
						this.ticksUntilNextPathRecalculation += 10;
					} else if (d0 > 256.0) {
						this.ticksUntilNextPathRecalculation += 5;
					}

					if (!this.matriarch.getNavigation().moveTo(target, this.speedModifier)) {
						this.ticksUntilNextPathRecalculation += 15;
					}

					this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
				}

				//BL: custom attack
				if (this.matriarch.getRandom().nextBoolean())
					this.checkAndPerformDropAttack(target);
				else
					this.checkAndPerformSpinAttack(target);
			}

			this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0); //BL: move attack tick outside vehicle check so it counts down regardless

			//BL: drop logic
			if (this.matriarch.isVehicle()) {
				if (!this.matriarch.level().isEmptyBlock(this.matriarch.blockPosition().below(3)) || this.matriarch.getY() < this.matriarch.pickupHeight + 16D) {
					this.matriarch.getNavigation().stop();
					Vec3 vec3d = DefaultRandomPos.getPosAway(this.matriarch, 16, 10, Vec3.atCenterOf(this.matriarch.getBoundOrigin().pos()));
					if (vec3d != null)
						this.matriarch.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 2D);
					this.matriarch.setDeltaMovement(this.matriarch.getDeltaMovement().add(0.0D, 0.05D, 0.0D));
				}

				if ((this.matriarch.getY() >= this.matriarch.pickupHeight + 16D || this.matriarch.getDroppingTimer() <= 0 || Block.canSupportCenter(this.matriarch.level(), this.matriarch.blockPosition().above(), Direction.DOWN))) {
					this.matriarch.setReturnToNest(true);
					this.matriarch.ejectPassengers();
					this.matriarch.playSound(SoundRegistry.CHIROMAW_MATRIARCH_RELEASE.get(), 0.5F, this.matriarch.getVoicePitch());
				}

				if (this.matriarch.getDroppingTimer() >= 0) {
					if (this.ticksUntilNextAttack <= 0) {
						this.matriarch.doHurtTarget(target);
						this.ticksUntilNextAttack = this.adjustedTickDelay(20);
					}
				}

				if (this.matriarch.getDroppingTimer() >= 0)
					this.matriarch.setDroppingTimer(this.matriarch.getDroppingTimer() - 1);
			}

			//BL: spin logic
			if (this.matriarch.isSpinning()) {
				this.rotation += 30;
				if (this.rotation % 30 == 0) {
					Vec3 targetVector = target.position();
					Vec3 chiromawVectorToTarget = this.matriarch.position().subtract(targetVector);
					double height = chiromawVectorToTarget.y;
					double distance = chiromawVectorToTarget.length();
					double angle = Mth.atan2(height, distance) * (180D / Math.PI);
					ChiromawBarb arrow = new ChiromawBarb(this.matriarch, this.matriarch.level(), ItemRegistry.CHIROMAW_BARB.toStack(), null);
					arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
					arrow.shootFromRotation(this.matriarch, (float) angle, rotation, 1.5F, 0.5F, 0.5F);
					this.matriarch.playSound(SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE.get(), 0.5F, this.matriarch.getVoicePitch());
					this.matriarch.level().addFreshEntity(arrow);
				}
				if (this.rotation >= 720) {
					this.rotation = 0;
					this.matriarch.setSpinning(false);
				}
			}
		}
	}

	private void checkAndPerformSpinAttack(LivingEntity target) {
		double distToEnemySqr = this.matriarch.distanceToSqr(target);
		if (distToEnemySqr >= 36.0D && distToEnemySqr <= 100.0D && this.ticksUntilNextAttack <= 0) {
			this.matriarch.playSound(SoundRegistry.CHIROMAW_MATRIARCH_ROAR.get(), 1.0F, this.matriarch.getVoicePitch());
			this.ticksUntilNextAttack = this.adjustedTickDelay(40);
			this.matriarch.setSpinning(true);
		}
	}

	protected void checkAndPerformDropAttack(LivingEntity target) {
		if (this.ticksUntilNextAttack <= 0 && this.matriarch.isWithinMeleeAttackRange(target) && this.matriarch.getSensing().hasLineOfSight(target)) {
			this.ticksUntilNextAttack = this.adjustedTickDelay(20);
			this.matriarch.jumpFromGround();
			this.matriarch.doHurtTarget(target);
			this.matriarch.playSound(SoundRegistry.CHIROMAW_MATRIARCH_GRAB.get(), 0.5F, this.matriarch.getVoicePitch());
			target.startRiding(this.matriarch, true);
			this.matriarch.pickupHeight = this.matriarch.getY();
			this.matriarch.setDroppingTimer(120);
		}
	}
}
