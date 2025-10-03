package thebetweenlands.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.projectile.SnailPoisonJet;
import thebetweenlands.common.registries.SoundRegistry;

public class BloodSnail extends Monster implements RangedAttackMob, BLEntity {

	public BloodSnail(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, 50, 3.0F));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 5.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.2D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D)
			.add(Attributes.FOLLOW_RANGE, 16.0D);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 3;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SNAIL_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.SNAIL_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SNAIL_DEATH.get();
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if (super.doHurtTarget(entity)) {
			if (entity instanceof LivingEntity living) {
				byte duration = 0;
				if (this.level().getDifficulty() == Difficulty.NORMAL)
					duration = 7;
				else if (this.level().getDifficulty() == Difficulty.HARD)
					duration = 15;

				if (duration > 0) {
					living.addEffect(new MobEffectInstance(MobEffects.POISON, duration * 20));
					living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration * 20));
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void performRangedAttack(LivingEntity entity, float velocity) {
		if (this.hasLineOfSight(entity)) {
			SnailPoisonJet missile = new SnailPoisonJet(this.level(), this);
			missile.setXRot(-20.0F);
			double targetX = entity.getX() - this.getX();
			double targetY = entity.getEyeY() - 1.1D - missile.getY();
			double targetZ = entity.getZ() - this.getZ();
			float target = Mth.sqrt((float) (targetX * targetX + targetZ * targetZ));
			missile.shoot(targetX, targetY + target * 0.1F, targetZ, 0.75F, 8.0F);
			this.level().addFreshEntity(missile);
		}
	}
}
