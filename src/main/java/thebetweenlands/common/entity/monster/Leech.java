package thebetweenlands.common.entity.monster;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.datagen.tags.BLEntityTagProvider;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class Leech extends Monster implements BLEntity {

	private static final int MAX_BLOOD_LEVEL = 5;
	private static final int TIME_TO_FLEE = 600;

	public final int attackCountDown = 20;
	public int hungerCoolDown;
	private int drainage;
	public float moveProgress;
	public final boolean firstTickCheck;
	public int fleeingTick;

	final AnimationMathHelper mathSucking = new AnimationMathHelper();

	private NearestAttackableTargetGoal<LivingEntity> targetGoal;
	private WaterAvoidingRandomStrollGoal wanderGoal;
	private MeleeAttackGoal meleeGoal;
	private LeechAvoidEntityGoal<? extends LivingEntity> avoidGoal;

	private static final EntityDataAccessor<Byte> BLOOD_CONSUMED = SynchedEntityData.defineId(Leech.class, EntityDataSerializers.BYTE);

	public Leech(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.moveProgress = 0;
		this.firstTickCheck = false;
		this.drainage = 0;
	}

	@Override
	protected void registerGoals() {
		this.targetGoal = new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, true);
		this.wanderGoal = new WaterAvoidingRandomStrollGoal(this, 0.8D);
		this.meleeGoal = new MeleeAttackGoal(this, 1.0D, false);
		this.avoidGoal = new LeechAvoidEntityGoal<>(this, LivingEntity.class, 6, 0.5D, 0.6D);

		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, this.meleeGoal);
		this.goalSelector.addGoal(5, this.wanderGoal);
		this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(0, this.targetGoal);
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(BLOOD_CONSUMED, (byte)0);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 20.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.25D)
			.add(Attributes.ATTACK_DAMAGE, 3.0D)
			.add(Attributes.FOLLOW_RANGE, 16.0D);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.LEECH_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.LEECH_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.LEECH_DEATH.get();
	}

	public void onCollideWithEntity(LivingEntity entity) {
		if (!this.level().isClientSide()) {
			if (!entity.isVehicle() && this.getBloodConsumed() <= 0) {
				this.startRiding(entity);
				this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(entity));
				this.targetSelector.removeGoal(this.targetGoal);
				this.goalSelector.removeGoal(this.meleeGoal);
			}
		}
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide()) {
			if (this.fleeingTick == 0 && this.getTarget() != null && this.distanceTo(this.getTarget()) < 2) {
				this.onCollideWithEntity(this.getTarget());
			}
			if (this.getVehicle() != null) {
				this.setRot(this.getVehicle().getYRot(), this.getVehicle().getXRot());
				if (this.getVehicle().isOnFire() && this.getVehicle() instanceof LivingEntity living) {
					living.addEffect(new MobEffectInstance(MobEffects.POISON, 120 + this.getBloodConsumed() * 200 / MAX_BLOOD_LEVEL, 0));
					this.setBloodConsumed(0);
					this.flee();
					this.stopRiding();
				}
			}
			if (this.getBloodConsumed() == MAX_BLOOD_LEVEL && this.getVehicle() != null) {
				this.stopRiding();
			}
			if (this.fleeingTick > 0) {
				this.fleeingTick--;
				if (this.fleeingTick == 0) {
					this.stopFleeing();
				}
			}
		}

		if (this.firstTick) {
			this.stopRiding();
		}
		if (--this.hungerCoolDown == 0) {
			if (this.getBloodConsumed() > 0) {
				this.setBloodConsumed(this.getBloodConsumed() - 1);
			}
		}
		if (this.getVehicle() != null) {
			this.moveProgress = 1 + this.mathSucking.swing(1, 0.15F, false);
			if (this.getRandom().nextInt(10) == 0) {
				for (int i = 0; i < 8; i++) {
					this.level().addParticle(DustParticleOptions.REDSTONE,
						this.getX() + (this.getRandom().nextFloat() - this.getRandom().nextFloat()),
						this.getY() + this.getRandom().nextFloat(),
						this.getZ() + (this.getRandom().nextFloat() - this.getRandom().nextFloat()),
						0, 0, 0);
				}
			}
		} else if (!this.level().isClientSide()) {
			this.moveProgress = this.mathSucking.swing(1, 0.15F, false);
		}

		if (this.getVehicle() instanceof LivingEntity && getBloodConsumed() < MAX_BLOOD_LEVEL) {
			this.drainage++;
			if (this.drainage >= attackCountDown && this.deathTime == 0) {
				this.getVehicle().hurt(this.damageSources().mobAttack(this), (int)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
				this.drainage = 0;
				this.setBloodConsumed(this.getBloodConsumed() + 1);
			}
		}
		super.tick();
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getVehicle();
		super.stopRiding();
		if (entity != null && !this.level().isClientSide()) {
			this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(entity));
		}
	}

	private void flee() {
		this.fleeingTick = TIME_TO_FLEE;
		if(!this.level().isClientSide() && this.getVehicle() instanceof LivingEntity entity) {
			this.avoidGoal.setAvoidClass(entity.getClass());
			this.goalSelector.addGoal(0, this.avoidGoal);
			this.targetSelector.removeGoal(this.targetGoal);
			this.goalSelector.removeGoal(this.meleeGoal);
		}
	}

	private void stopFleeing() {
		if(!this.level().isClientSide()) {
			this.goalSelector.removeGoal(this.avoidGoal);
			this.targetSelector.addGoal(0, this.targetGoal);
			this.goalSelector.addGoal(1, this.meleeGoal);
		}
	}

	@Override
	public Vec3 getVehicleAttachmentPoint(Entity entity) {
		return new Vec3(0.0D, -this.getYOffset(), 0.0D);
	}

	public double getYOffset() {
		if (this.getVehicle() instanceof Player)
			return this.getVehicle().getBbHeight() -2.5F;
		else if (this.getVehicle() != null)
			return this.getVehicle().getBbHeight() * 0.5D - 1.25D;
		else
			return 0.0D;
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@Override
	public boolean canAttackType(EntityType<?> type) {
		return type.is(BLEntityTagProvider.LEECH_ATTACKS);
	}

	public int getBloodConsumed() {
		return this.getEntityData().get(BLOOD_CONSUMED) & 0xFF;
	}

	public void setBloodConsumed(int amount) {
		this.hungerCoolDown = 500;
		this.getEntityData().set(BLOOD_CONSUMED, (byte) amount);
		if (amount == 0 && this.getVehicle() == null && !this.level().isClientSide()) {
			this.targetSelector.addGoal(0, this.targetGoal);
			this.goalSelector.addGoal(0, this.meleeGoal);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("blood", this.getBloodConsumed());
		tag.putInt("flee_tick", this.fleeingTick);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setBloodConsumed(tag.getInt("blood"));
		this.fleeingTick = tag.getInt("flee_tick");
	}

	private static class LeechAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

		public LeechAvoidEntityGoal(PathfinderMob mob, Class<T> entityClassToAvoid, float maxDistance, double walkSpeedModifier, double sprintSpeedModifier) {
			super(mob, entityClassToAvoid, maxDistance, walkSpeedModifier, sprintSpeedModifier);
		}

		public void setAvoidClass(Class<? extends LivingEntity> avoidClass) {
			this.avoidClass = (Class<T>) avoidClass;
		}
	}
}
