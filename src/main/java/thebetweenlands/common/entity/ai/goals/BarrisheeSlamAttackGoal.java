package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.entity.boss.Barrishee;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.EnumSet;

public class BarrisheeSlamAttackGoal extends Goal {

	private final Barrishee barrishee;
	private int cooldown;
	private final int minCooldown;
	private final int maxCooldown;
	private int missileCount;
	private int shootCount;

	public BarrisheeSlamAttackGoal(Barrishee barrishee, int minCooldown, int maxCooldown) {
		this.barrishee = barrishee;
		this.minCooldown = minCooldown;
		this.maxCooldown = maxCooldown;
		this.cooldown = minCooldown + barrishee.getRandom().nextInt(maxCooldown - minCooldown);
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		LivingEntity target = this.barrishee.getTarget();

		if(target != null) {
			double distance = this.barrishee.distanceToSqr(target);

			if (distance >= 9.0D && distance <= 81.0D && this.barrishee.onGround() && this.barrishee.isReadyForSpecialAttack()) {
				return this.cooldown-- < 0;
			}
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity target = this.barrishee.getTarget();
		return target != null && this.shootCount != -1 && this.missileCount != -1 && this.barrishee.isLookingAtAttackTarget(target);
	}

	@Override
	public void start() {
		this.missileCount = 0;
		this.shootCount = 0;
		this.barrishee.setIsSlamming(true);
	}

	@Override
	public void stop() {
		this.cooldown = this.minCooldown + this.barrishee.getRandom().nextInt(this.maxCooldown - this.minCooldown);
		this.shootCount = -1;
		this.missileCount = -1;
		if (this.barrishee.isSlamming()) {
			this.barrishee.setIsSlamming(false);
		}
	}

	@Override
	public void tick() {
		LivingEntity target = this.barrishee.getTarget();

		if(target != null) {
			int distance = Mth.floor(barrishee.distanceTo(target));

			if (barrishee.isLookingAtAttackTarget(target)) {
				float f = (float) Mth.atan2(target.getZ() - this.barrishee.getZ(), target.getX() - this.barrishee.getX());
				this.missileCount++;
				if (this.missileCount % 2 == 0) {
					this.shootCount++;

					if(this.shootCount==1) {
						this.barrishee.playSound(SoundRegistry.WALL_SLAM.get(), 1F, 1F);
					}

					double d2 = 2.5D + (double) (this.shootCount);

					BlockPos origin = BlockPos.containing(this.barrishee.getX() + Mth.cos(f) * d2,
						this.barrishee.getY() - 1.0D, this.barrishee.getZ() + Mth.sin(f) * d2);
					BlockState block = this.barrishee.level().getBlockState(origin);

//					if (block.isNormalCube() && this.barrishee.level().getBlockEntity(origin) == null
//						&& block.getDestroySpeed(this.barrishee.level(), origin) <= 5.0F
//						&& block.getDestroySpeed(this.barrishee.level(), origin) >= 0.0F
//						&& this.barrishee.level().getBlockState(origin).isOpaqueCube()) {
//
//						ShockwaveBlock shockwaveBlock = new ShockwaveBlock(this.barrishee.level());
//						shockwaveBlock.setOrigin(origin, 10, origin.getX() + 0.5D, origin.getZ() + 0.5D, this.barrishee);
//						shockwaveBlock.moveTo(origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D, 0.0F, 0.0F);
//						shockwaveBlock.setBlock(this.barrishee.level().getBlockState(origin));
//						this.barrishee.level().addFreshEntity(shockwaveBlock);
//					}
				}
			}

			if (this.shootCount >= distance || this.shootCount >= 9 || !target.isAlive()) {
				this.stop();
			}
		}
	}
}
