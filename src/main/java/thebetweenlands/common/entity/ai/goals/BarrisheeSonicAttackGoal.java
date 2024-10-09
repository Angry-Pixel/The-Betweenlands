package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.entity.boss.Barrishee;

import java.util.EnumSet;
import java.util.List;

public class BarrisheeSonicAttackGoal extends Goal {

	private final Barrishee barrishee;
	private int cooldown;
	private final int minCooldown;
	private final int maxCooldown;
	private int missileCount;
	private int shootCount;

	public BarrisheeSonicAttackGoal(Barrishee barrishee, int minCooldown, int maxCooldown) {
		this.barrishee = barrishee;
		this.minCooldown = minCooldown;
		this.maxCooldown = maxCooldown;
		this.cooldown = minCooldown + barrishee.getRandom().nextInt(maxCooldown - minCooldown);
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		LivingEntity target = this.barrishee.getTarget();

		if(target != null) {
			double distance = this.barrishee.distanceToSqr(target);

			if (distance >= 9.0D && distance <= 144.0D && this.barrishee.onGround() && this.barrishee.isReadyForSpecialAttack() && this.barrishee.isLookingAtAttackTarget(target)) {
				return this.cooldown-- < 0;
			}
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return this.barrishee.getTarget() != null && this.shootCount != -1 && this.missileCount != -1;
	}

	@Override
	public void start() {
		this.missileCount = 0;
		this.shootCount = 0;
		this.barrishee.playSound(this.barrishee.getScreamSound(), 0.75F, 0.5F);
		this.barrishee.setScreamTimer(0);
	}

	@Override
	public void stop() {
		this.cooldown = minCooldown + this.barrishee.getRandom().nextInt(maxCooldown - minCooldown);
		this.shootCount = -1;
		this.missileCount = -1;
		if (this.barrishee.isScreamingBeam()) {
			this.barrishee.setIsScreamingBeam(false);
		}
	}

	@Override
	public void tick() {
		LivingEntity target = this.barrishee.getTarget();

		if(target != null) {
			int distance = Mth.floor(this.barrishee.distanceTo(target));

			if (this.barrishee.getScreamTimer() >= 25) {
				if (!this.barrishee.isScreamingBeam()) {
					this.barrishee.setIsScreamingBeam(true);
				}

				float f = (float) Mth.atan2(target.getZ() - this.barrishee.getZ(), target.getX() - this.barrishee.getX());
				this.missileCount++;

				if (this.missileCount % 2 == 0) {
					this.shootCount++;
					double d2 = 2D + this.shootCount;
					this.checkIfBeamHitsAnyone(barrishee.level(), BlockPos.containing(this.barrishee.getX() + (double) Mth.cos(f) * d2, this.barrishee.getY() + this.barrishee.getEyeHeight(), this.barrishee.getZ() + (double) Mth.sin(f) * d2));
				}
			}

			if (this.shootCount >= distance || this.shootCount >= 12 || !target.isAlive()) {
				this.stop();
			}
		}
	}

	public void checkIfBeamHitsAnyone(Level level, BlockPos pos) {
		AABB hitBox = new AABB(pos).inflate(0D, 0.25D, 0D);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, hitBox);

		for (LivingEntity entity : list) {
			if(this.barrishee.doHurtTarget(entity)) {
				entity.knockback(0.75F, Mth.sin(this.barrishee.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(this.barrishee.getYRot() * Mth.DEG_TO_RAD));
			}
		}
	}
}
