package thebetweenlands.common.entity.monster;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.creature.Dragonfly;
import thebetweenlands.common.entity.creature.MireSnail;
import thebetweenlands.common.entity.creature.frog.Frog;
import thebetweenlands.common.entity.monster.chiromaw.Chiromaw;
import thebetweenlands.common.registries.SoundRegistry;

public class Shambler extends Monster implements BLEntity {

	private static final EntityDataAccessor<Boolean> JAWS_OPEN = SynchedEntityData.defineId(Shambler.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> TONGUE_EXTEND = SynchedEntityData.defineId(Shambler.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> JAW_ANGLE = SynchedEntityData.defineId(Shambler.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TONGUE_LENGTH = SynchedEntityData.defineId(Shambler.class, EntityDataSerializers.INT);

	private int prevJawAngle;
	private int prevTongueLength;

	public final ShamblerTongueMultipart[] tongue_array; // we may want to make more tongue parts

	public final ShamblerTongueMultipart tongue_end = new ShamblerTongueMultipart(this, 0.5F, 0.5F);

	public Shambler(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.tongue_array = new ShamblerTongueMultipart[16];
		for (int i = 0; i < this.tongue_array.length - 1; i++)
			this.tongue_array[i] = new ShamblerTongueMultipart(this, 0.125F, 0.125F);
		this.tongue_array[this.tongue_array.length - 1] = this.tongue_end;
		this.setId(ENTITY_COUNTER.getAndAdd(this.tongue_array.length + 1) + 1);
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (int i = 0; i < this.tongue_array.length; i++)
			this.tongue_array[i].setId(id + i + 1);
	}

	@Override
	public PartEntity<?>[] getParts() {
		return this.tongue_array;
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.8D, true));
		this.goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.75D));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers(Shambler.class));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Frog.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Chiromaw.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, MireSnail.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, BloodSnail.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Dragonfly.class, 3, true, true, null).setUnseenMemoryTicks(120));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(JAWS_OPEN, false);
		builder.define(TONGUE_EXTEND, false);
		builder.define(JAW_ANGLE, 0);
		builder.define(TONGUE_LENGTH, 0);
	}

	public boolean jawsAreOpen() {
		return this.getEntityData().get(JAWS_OPEN);
	}

	public void setOpenJaws(boolean jawState) {
		this.getEntityData().set(JAWS_OPEN, jawState);
	}

	public boolean isExtendingTongue() {
		return this.getEntityData().get(TONGUE_EXTEND);
	}

	public void setExtendingTongue(boolean tongueState) {
		this.getEntityData().set(TONGUE_EXTEND, tongueState);
	}

	public void setJawAngle(int count) {
		this.getEntityData().set(JAW_ANGLE, count);
	}

	public void setJawAnglePrev(int count) {
		this.prevJawAngle = count;
	}

	public void setTongueLength(int count) {
		this.getEntityData().set(TONGUE_LENGTH, count);
	}

	public void setTongueLengthPrev(int count) {
		this.prevTongueLength = count;
	}

	public int getJawAngle() {
		return this.getEntityData().get(JAW_ANGLE);
	}

	public int getJawAnglePrev() {
		return this.prevJawAngle;
	}

	public int getTongueLength() {
		return this.getEntityData().get(TONGUE_LENGTH);
	}

	public int getTongueLengthPrev() {
		return this.prevTongueLength;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 20.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.3D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D)
			.add(Attributes.FOLLOW_RANGE, 20.0D);
	}

	@Override
	public float getVoicePitch() {
		return super.getVoicePitch() * 1.5F;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SHAMBLER_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.SHAMBLER_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SHAMBLER_DEATH.get();
	}

	@Override
	public void aiStep() {
		this.setJawAnglePrev(this.getJawAngle());
		this.setTongueLengthPrev(this.getTongueLength());

		if (!this.level().isClientSide()) {
			if (this.getTarget() != null && this.hasLineOfSight(this.getTarget()) && !this.isVehicle()) {
				getLookControl().setLookAt(this.getTarget(), 10.0F, 20.0F);
				double distance = this.distanceToTarget(this.getTarget().getX(), this.getTarget().getBoundingBox().minY, this.getTarget().getZ());

				if (distance > 5.0D) {
					if (this.jawsAreOpen()) {
						this.setOpenJaws(false);
						if (this.isExtendingTongue())
							this.setExtendingTongue(false);
					}
				}

				if (distance <= 5.0D && distance >= 1) {
					if (!this.jawsAreOpen()) {
						this.setOpenJaws(true);
						if (!this.isExtendingTongue()) {
							this.setExtendingTongue(true);
							this.playSound(SoundRegistry.SHAMBLER_LICK.get(), 1F, 1F + this.getRandom().nextFloat() * 0.3F);
						}
					}
				}
			}

			if (this.getTarget() == null) {
				if (this.jawsAreOpen())
					this.setOpenJaws(false);
				if (this.isExtendingTongue())
					this.setExtendingTongue(false);
			}

			if (this.getJawAngle() > 0 && !this.jawsAreOpen())
				this.setJawAngle(this.getJawAngle() - 1);

			if (this.jawsAreOpen() && this.getJawAngle() <= 10)
				this.setJawAngle(this.getJawAngle() + 1);

			if (this.getJawAngle() < 0 && !this.jawsAreOpen())
				this.setJawAngle(0);

			if (this.jawsAreOpen() && this.getJawAngle() > 10)
				this.setJawAngle(10);

			if (this.getTongueLength() > 0 && !this.isExtendingTongue())
				this.setTongueLength(this.getTongueLength() - 1);

			if (this.isExtendingTongue() && this.getTongueLength() <= 9)
				this.setTongueLength(this.getTongueLength() + 1);

			if (this.getTongueLength() < 0 && !this.isExtendingTongue())
				this.setTongueLength(0);

			if (this.isExtendingTongue() && this.getTongueLength() > 9) {
				this.setTongueLength(9);
				this.setExtendingTongue(false);
			}
		}
		super.aiStep();
	}

	public float distanceToTarget(double x, double y, double z) {
		float f = (float) (this.getX() - x);
		float f1 = (float) (this.getY() - y);
		float f2 = (float) (this.getZ() - z);
		return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
	}

	@Override
	public void tick() {
		super.tick();

		Vec3 vector = getViewVector(1F).normalize().scale(1D);
		double offSetX = vector.x * 0.125D;
		double offsetY = vector.y * 0.125D;
		double offSetZ = vector.z * 0.125D;
		double lengthIncrement = 0.5D / this.tongue_array.length;
		double tongueLength = lengthIncrement;

		for (ShamblerTongueMultipart part : this.tongue_array) {
			part.yRotO = part.getYRot();
			part.xRotO = part.getXRot();
			part.xOld = part.xo = part.getX();
			part.yOld = part.yo = part.getY();
			part.zOld = part.zo = part.getZ();

			part.setPos(getX() + offSetX + (vector.x * this.getTongueLength() * tongueLength), getY() + getEyeHeight() - 0.32 + offsetY + (vector.y * this.getTongueLength() * tongueLength), getZ() + offSetZ + (vector.z * this.getTongueLength() * tongueLength));
			part.setYRot(getYRot());
			part.setXRot(getXRot());

			tongueLength += lengthIncrement;
		}
		if (this.getTarget() != null)
			this.checkCollision();
	}

	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		return null;
	}

	@Override
	public void positionRider(Entity entity, MoveFunction moveFunction) {
		super.positionRider(entity, moveFunction);
		//PlayerUtils.resetFloating(entity); //TODO - I think this should set levitationStartTime to 0 in ServerPlayer now

		if (entity instanceof LivingEntity) {
			double a = Math.toRadians(getYRot());
			double offSetX = Math.sin(a) * getTongueLength() > 0 ? -0.125D : -0.4D;
			double offSetZ = -Math.cos(a) * getTongueLength() > 0 ? -0.125D : -0.4D;
			entity.setPos(this.tongue_end.getX() + offSetX, this.tongue_end.getY() - entity.getBbHeight() * 0.3D, this.tongue_end.getZ() + offSetZ);
			if (entity.isCrouching())
				entity.setPose(Pose.STANDING);
		}
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	public void checkCollision() {
		List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.tongue_end.getBoundingBox());
		for (Entity entity : list) {
			if (entity != null && entity == getTarget()) {
				if (entity instanceof LivingEntity)
					if (!this.isVehicle()) {
						entity.startRiding(this, true);
						if (!this.level().isClientSide())
							if (this.isExtendingTongue())
								this.setExtendingTongue(false); //eeeeeh!
					} else {
						if (!this.level().isClientSide())
							if (this.getTongueLength() <= 0 && !entity.isInvulnerable() && this.tickCount >= this.getLastHurtMobTimestamp() + 20) {
								this.getLookControl().setLookAt(this.getTarget(), 10.0F, 20.0F);
								this.doHurtTarget(entity); // screw you minecraft - I want to attack a passenger
							}
					}
			}
		}
	}

	public float smoothedAngle(float partialTicks) {
		return getJawAnglePrev() + (getJawAngle() - getJawAnglePrev()) * partialTicks;
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		return this.hasLineOfSight(entity) && super.doHurtTarget(entity);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data) {
		for (ShamblerTongueMultipart part : this.tongue_array) {
			part.setPos(this.getX(), this.getY(), this.getZ());
			part.setYRot(this.getYRot());
		}
		return data;
	}
}
