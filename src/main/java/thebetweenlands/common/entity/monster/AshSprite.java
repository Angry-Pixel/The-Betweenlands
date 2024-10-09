package thebetweenlands.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.AshSpriteChargeGoal;
import thebetweenlands.common.entity.ai.goals.AshSpriteMoveGoal;
import thebetweenlands.common.entity.movement.AshSpriteMoveControl;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class AshSprite extends Monster implements BLEntity {

	protected static final byte EVENT_ENABLE_NO_CLIP = 80;
	protected static final byte EVENT_DISABLE_NO_CLIP = 81;

	protected static final EntityDataAccessor<Byte> ASH_SPRITE_FLAGS = SynchedEntityData.defineId(AshSprite.class, EntityDataSerializers.BYTE);

	@Nullable
	private BlockPos boundOrigin;

	private boolean canNoClip = true;
	private int noClipTimeout = 0;

	public AshSprite(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.moveControl = new AshSpriteMoveControl(this);
		this.xpReward = 3;
	}

	@Override
	public void move(MoverType type, Vec3 pos) {
		super.move(type, pos);
		this.checkInsideBlocks();
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide()) {
			if (this.noClipTimeout > 0) {
				if (this.canNoClip) {
					this.canNoClip = false;
					this.level().broadcastEntityEvent(this, EVENT_DISABLE_NO_CLIP);
				}
				this.noClipTimeout--;
			} else {
				if (!this.canNoClip) {
					this.canNoClip = true;
					this.level().broadcastEntityEvent(this, EVENT_ENABLE_NO_CLIP);
				}
				this.noClipTimeout = 0;
			}
		}
		this.noPhysics = this.canNoClip || !this.checkSpawnObstruction(this.level());
		super.tick();
		this.noPhysics = false;
		this.setNoGravity(true);
		if (this.level().isClientSide())
			this.spawnParticles(this.level(), this.blockPosition(), this.getRandom());
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if (id == EVENT_ENABLE_NO_CLIP) {
			this.canNoClip = true;
		} else if (id == EVENT_DISABLE_NO_CLIP) {
			this.canNoClip = false;
		}
	}

	public void spawnParticles(Level level, BlockPos pos, RandomSource rand) {
		for (int i = 0; i < 4; i++) {
			level.addParticle(ParticleTypes.ASH, pos.getX() + 0.5D + (rand.nextBoolean() ? -0.5F : 0.5F) * Math.pow(rand.nextFloat(), 1F), pos.getY() + 0.5D + rand.nextFloat() - 0.5F, pos.getZ() + 0.5D + (rand.nextBoolean() ? -0.5F : 0.5F) * Math.pow(rand.nextFloat(), 1F), 0, 0.2D, 0);
		}
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(4, new AshSpriteChargeGoal(this));
		this.goalSelector.addGoal(8, new AshSpriteMoveGoal(this));
		this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this, AshSprite.class));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 14.0)
			.add(Attributes.ATTACK_DAMAGE, 4.0);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(ASH_SPRITE_FLAGS, (byte) 0);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.ASHSPRITE_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.ASHSPRITE_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.ASHSPRITE_DEATH.get();
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("BoundX"))
			this.boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		if (this.boundOrigin != null) {
			compound.putInt("BoundX", this.boundOrigin.getX());
			compound.putInt("BoundY", this.boundOrigin.getY());
			compound.putInt("BoundZ", this.boundOrigin.getZ());
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.IN_WALL)) {
			return false;
		}
		if (super.hurt(source, amount)) {
			this.noClipTimeout = 60;
			return true;
		}
		return false;
	}

	@Nullable
	public BlockPos getBoundOrigin() {
		return this.boundOrigin;
	}

	public void setBoundOrigin(@Nullable BlockPos origin) {
		this.boundOrigin = origin;
	}

	private boolean getAshSpriteFlag(int mask) {
		int i = this.getEntityData().get(ASH_SPRITE_FLAGS);
		return (i & mask) != 0;
	}

	private void setAshSpriteFlag(int mask, boolean value) {
		int i = this.getEntityData().get(ASH_SPRITE_FLAGS);
		if (value)
			i = i | mask;
		else
			i = i & ~mask;
		this.getEntityData().set(ASH_SPRITE_FLAGS, (byte) (i & 255));
	}

	public boolean isCharging() {
		return getAshSpriteFlag(1);
	}

	public void setCharging(boolean charging) {
		setAshSpriteFlag(1, charging);
	}
}
