package thebetweenlands.common.entity.monster.chiromaw;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.monster.FlyingMonster;
import thebetweenlands.common.registries.SoundRegistry;

public class Chiromaw extends FlyingMonster {

	private static final byte TAKE_OFF_EVENT = 90;
	private static final EntityDataAccessor<Boolean> IS_HANGING = SynchedEntityData.defineId(Chiromaw.class, EntityDataSerializers.BOOLEAN);

	public Chiromaw(EntityType<? extends FlyingMonster> type, Level level) {
		super(type, level);
		this.setPathfindingMalus(PathType.WATER, -8.0F);
		this.setPathfindingMalus(PathType.BLOCKED, -8.0F);
		this.setPathfindingMalus(PathType.OPEN, 8.0F);
		this.setPathfindingMalus(PathType.FENCE, -8.0F);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 0.095D)
			.add(Attributes.FLYING_SPEED, 0.095D)
			.add(Attributes.MAX_HEALTH, 10.0D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D)
			.add(Attributes.FOLLOW_RANGE, 20.0D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(2, new WaterAvoidingRandomFlyingGoal(this, 0.5D));
		this.goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(160));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_HANGING, false);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.jumping && this.isInWater()) {
			//Moving out of water
			this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 1, this.getZ(), 1.0D);
		}

		if (this.isHanging()) {
			this.setDeltaMovement(Vec3.ZERO);
			this.setPosRaw(this.getX(), (double) Mth.floor(this.getY()) + 1.0D - (double) this.getBbHeight(), this.getZ());
		}

		if (this.getDeltaMovement().y() < 0.0D && this.getTarget() == null) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.25D, 1.0D));
		}

		if(this.level().getBlockState(this.blockPosition().below()).isFaceSturdy(this.level(), this.blockPosition().below(), Direction.UP)) {
			this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 1, this.getZ(), 1.0D);
		}
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();

		if (this.isHanging()) {
			this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 0.5D, this.getZ(), 0);

			if (this.getRandom().nextInt(250) == 0 || !this.level().getBlockState(this.blockPosition().above()).isRedstoneConductor(this.level(), this.blockPosition().above())) {
				this.setHanging(false);
				this.level().broadcastEntityEvent(this, TAKE_OFF_EVENT);
			} else if (this.getTarget() != null) {
				this.setHanging(false);
				this.level().broadcastEntityEvent(this, TAKE_OFF_EVENT);
			}
		} else {
			if (this.getTarget() == null) {
				if (this.getRandom().nextInt(20) == 0 && this.level().getBlockState(this.blockPosition().above()).isRedstoneConductor(this.level(), this.blockPosition().above())) {
					this.setHanging(true);
				}
			}
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == TAKE_OFF_EVENT) {
			this.playSound(SoundRegistry.CHIROMAW_TAKE_OFF.get());
		} else {
			super.handleEntityEvent(id);
		}
	}

	public boolean isHanging() {
		return this.getEntityData().get(IS_HANGING);
	}

	public void setHanging(boolean hanging) {
		this.getEntityData().set(IS_HANGING, hanging);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.FLYING_FIEND_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.FLYING_FIEND_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FLYING_FIEND_DEATH.get();
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 3;
	}
}
