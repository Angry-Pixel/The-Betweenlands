package thebetweenlands.common.items.shield;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;

import java.util.List;

public class SwatShieldItem extends BaseShieldItem {
	public SwatShieldItem(Tier tier, Properties properties) {
		super(tier, properties);
	}

	/**
	 * Sets whether the specified user is preparing for a charge attack
	 *
	 * @param stack
	 * @param user
	 * @param charging
	 */
	public void setPreparingCharge(ItemStack stack, LivingEntity user, boolean charging) {
		user.getPersistentData().putBoolean("thebetweenlands.shield.charging", charging);
	}

	/**
	 * Returns whether the specified user is preparing for a charge attack
	 *
	 * @param stack
	 * @param user
	 * @return
	 */
	public static boolean isPreparingCharge(ItemStack stack, LivingEntity user) {
		return user.getPersistentData().getBoolean("thebetweenlands.shield.charging");
	}

	/**
	 * Sets how long the specified user has been preparing for a charge attack
	 *
	 * @param stack
	 * @param user
	 * @param ticks
	 */
	public void setPreparingChargeTicks(ItemStack stack, LivingEntity user, int ticks) {
		user.getPersistentData().putInt("thebetweenlands.shield.chargingTicks", ticks);
	}

	/**
	 * Returns how long the specified user has been preparing for a charge attack
	 *
	 * @param stack
	 * @param user
	 * @return
	 */
	public int getPreparingChargeTicks(ItemStack stack, LivingEntity user) {
		return user.getPersistentData().getInt("thebetweenlands.shield.chargingTicks");
	}

	/**
	 * Sets for how much longer the specified user can charge
	 *
	 * @param stack
	 * @param user
	 * @param ticks
	 */
	public void setRemainingChargeTicks(ItemStack stack, LivingEntity user, int ticks) {
		user.getPersistentData().putInt("thebetweenlands.shield.remainingRunningTicks", ticks);
	}

	/**
	 * Returns for how much longer the specified user can charge
	 *
	 * @param stack
	 * @param user
	 * @return
	 */
	public static int getRemainingChargeTicks(ItemStack stack, LivingEntity user) {
		return user.getPersistentData().getInt("thebetweenlands.shield.remainingRunningTicks");
	}

	/**
	 * Returns for how many ticks the user can charge for the specified preparation ticks
	 *
	 * @param stack
	 * @param user
	 * @param preparingTicks
	 * @return
	 */
	public int getChargeTime(ItemStack stack, LivingEntity user, int preparingTicks) {
		float strength = Mth.clamp(this.getPreparingChargeTicks(stack, user) / 20.0F - 0.2F, 0, 1);
		return (int) (strength * strength * this.getMaxChargeTime(stack, user));
	}

	/**
	 * Returns the maximum charge ticks
	 *
	 * @param stack
	 * @param user
	 * @return
	 */
	public int getMaxChargeTime(ItemStack stack, LivingEntity user) {
		return 80;
	}

	/**
	 * Called when an enemy is rammed
	 *
	 * @param stack
	 * @param user
	 * @param enemy
	 * @param rammingDir
	 */
	public void onEnemyRammed(ItemStack stack, LivingEntity user, LivingEntity enemy, Vec3 rammingDir) {
		boolean attacked;

		if (user instanceof Player player) {
			attacked = enemy.hurt(user.damageSources().playerAttack(player), 10.0F);

			if (user instanceof ServerPlayer sp)
				AdvancementCriteriaRegistry.SWAT_SHIELD.get().trigger(sp, enemy);
		} else {
			attacked = enemy.hurt(user.damageSources().mobAttack(user), 10.0F);
		}

		if (attacked) {
			enemy.knockback(2.0F, -rammingDir.x, -rammingDir.z);
		}
	}

	/**
	 * Called every tick when charging
	 *
	 * @param stack
	 * @param user
	 */
	public void onChargingUpdate(ItemStack stack, LivingEntity user) {
		if (user.onGround() && !user.isShiftKeyDown()) {
			Vec3 dir = user.getLookAngle();
			dir = new Vec3(dir.x, 0, dir.z).normalize();

			double speed = user instanceof Player ? 0.35D : 0.2D;

			user.setDeltaMovement(user.getDeltaMovement().add(dir.x * speed, 0.0D, dir.z * speed));

			if (user instanceof Player player) {
				player.getFoodData().addExhaustion(0.15F);
			}
		}

		if (Math.sqrt(user.getDeltaMovement().x() * user.getDeltaMovement().x() + user.getDeltaMovement().z() * user.getDeltaMovement().z()) > 0.2D) {
			Vec3 moveDir = user.getDeltaMovement().normalize();

			List<LivingEntity> targets = user.level().getEntitiesOfClass(LivingEntity.class, user.getBoundingBox().inflate(1), e -> e != user);

			for (LivingEntity target : targets) {
				Vec3 dir = target.position().subtract(user.position()).normalize();

				//45� angle range
				if (target.canBeCollidedWith() && Math.toDegrees(Math.acos(moveDir.dot(dir))) < 45) {
					this.onEnemyRammed(stack, user, target, moveDir);
				}
			}
		}
	}

	@Override
	public void onUseTick(Level level, LivingEntity user, ItemStack stack, int count) {
		boolean preparing = this.isPreparingCharge(stack, user);

		int runningTicks = this.getRemainingChargeTicks(stack, user);

		if (preparing && runningTicks <= 0) {
			this.setPreparingChargeTicks(stack, user, this.getPreparingChargeTicks(stack, user) + 1);
		}

		if (preparing && !user.isShiftKeyDown() && runningTicks <= 0) {
			this.setRemainingChargeTicks(stack, user, this.getChargeTime(stack, user, this.getPreparingChargeTicks(stack, user)));
			this.setPreparingChargeTicks(stack, user, 0);
			this.setPreparingCharge(stack, user, false);

			this.onStartedCharging(stack, user);
		} else if (!preparing && user.isShiftKeyDown()) {
			this.setRemainingChargeTicks(stack, user, 0);
			this.setPreparingChargeTicks(stack, user, 0);
			this.setPreparingCharge(stack, user, true);
		}

		if (runningTicks > 0) {
			this.onChargingUpdate(stack, user);
			this.setRemainingChargeTicks(stack, user, --runningTicks);
			if (runningTicks <= 0) {
				user.stopUsingItem();
				this.onStoppedCharging(stack, user);
			}
		}

		super.onUseTick(level, user, stack, count);
	}

	protected void onStartedCharging(ItemStack stack, LivingEntity user) {

	}

	protected void onStoppedCharging(ItemStack stack, LivingEntity user) {
		if (!user.level().isClientSide() && user instanceof Player player) {
			player.getCooldowns().addCooldown(this, 8 * 20);
		}
	}

	@Override
	public void onStopUsing(ItemStack stack, LivingEntity entity, int timeLeft) {
		super.onStopUsing(stack, entity, timeLeft);

		if (this.getRemainingChargeTicks(stack, entity) > 0) {
			this.onStoppedCharging(stack, entity);
		}

		this.setPreparingChargeTicks(stack, entity, 0);
		this.setRemainingChargeTicks(stack, entity, 0);
		this.setPreparingCharge(stack, entity, false);
	}

	@Override
	public boolean canBlockDamageSource(ItemStack stack, LivingEntity attacked, InteractionHand hand, DamageSource source) {
		if (this.getRemainingChargeTicks(stack, attacked) > 0 && source.getDirectEntity() != null) {
			return true;
		}
		return super.canBlockDamageSource(stack, attacked, hand, source);
	}

	@Override
	public float getBlockedDamage(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		if (this.getRemainingChargeTicks(stack, attacked) > 0) {
			return 0;
		}
		return super.getBlockedDamage(stack, attacked, damage, source);
	}

	@Override
	public float getDefenderKnockbackMultiplier(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		if (this.getRemainingChargeTicks(stack, attacked) > 0) {
			return 0;
		}
		return super.getDefenderKnockbackMultiplier(stack, attacked, damage, source);
	}
}
