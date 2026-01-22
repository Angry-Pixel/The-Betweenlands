package thebetweenlands.common.entity.monster;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.EntityAIFlyRandomly;
import thebetweenlands.common.entity.ai.goals.EntityAIMoveToDirect;
import thebetweenlands.common.entity.ai.goals.WightBuffSwampHagGoal;
import thebetweenlands.common.entity.ai.goals.WightSeekBonePileGoal;
import thebetweenlands.common.entity.movement.BLFlightMoveControl;
import thebetweenlands.common.network.clientbound.WightVolatileParticlesPacket;
import thebetweenlands.common.registries.AttributeRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.FluidTypeRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

//TODO fix flight
public class Wight extends Monster implements BLEntity {

    protected static final EntityDataAccessor<Boolean> HIDING_STATE_DW = SynchedEntityData.defineId(Wight.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> VOLATILE_STATE_DW = SynchedEntityData.defineId(Wight.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> GROW_TIMER = SynchedEntityData.defineId(Wight.class, EntityDataSerializers.INT);
    private static final EntityDimensions VOLATILE_DIMENSIONS = EntityDimensions.scalable(0.7F, 0.7F).withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, new Vec3(0.0F, 0.5F, 0.0F)));
	
    public static final EntityDataAccessor<Optional<BlockPos>> TARGET_BLOCK = SynchedEntityData.defineId(Wight.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    protected final MoveControl flightMoveControl;
    protected final MoveControl groundMoveControl;
    private int hidingAnimationTicks = 0;
    private int lastHidingAnimationTicks = 0;
    private int volatileCooldownTicks = 10 / 2 + 20;
    private int volatileTicks = 0;
    private float volatileReceivedDamage = 0.0F;
    private boolean canTurnVolatile = true;
    private boolean canTurnVolatileOnTarget = false;
    private boolean didTurnVolatileOnPlayer = false;
    private int growCount, prevGrowCount = 40;
	public boolean canTransformInToShaman = true;

    public Wight(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 10;
        this.setPathfindingMalus(PathType.WATER, 0.2F);
        this.flightMoveControl = new BLFlightMoveControl(this);
        this.groundMoveControl = this.moveControl = new MoveControl(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HIDING_STATE_DW, false);
        builder.define(VOLATILE_STATE_DW, false);
        builder.define(GROW_TIMER, this.growCount);
        builder.define(TARGET_BLOCK, Optional.empty()); //temp to stop null crash (should probably make it optional)
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WightAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WightSeekBonePileGoal(this));
        this.goalSelector.addGoal(3, new WightBuffSwampHagGoal(this));
        this.goalSelector.addGoal(4, new EntityAIMoveToDirect<>(this, this.getAttributeValue(Attributes.FLYING_SPEED)) {
        	@Override
        	public boolean canUse() {
        		return this.entity.getTarget() != null;
        	}
        	
            @Nullable
            @Override
			protected Vec3 getTarget() {
				if (this.entity.volatileTicks >= 20) {
					LivingEntity target = this.entity.getTarget();
					if (target != null)
						return new Vec3(target.getX(), target.getEyeY(), target.getZ());
				}
				return null;
			}
        });
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.4D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.3D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new EntityAIFlyRandomly<>(this) {
            @Override
            public boolean canUse() {
                return this.entity.isVolatile() && this.entity.volatileTicks >= 20 && this.entity.getTarget() == null && super.canUse();
            }

            @Override
            protected double getFlightSpeed() {
                return 0.1D;
            }
        });

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true, player -> !player.isShiftKeyDown()));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 76.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.33D)
				.add(Attributes.FLYING_SPEED, 0.32D)
				.add(Attributes.ATTACK_DAMAGE, 6.0D)
				.add(Attributes.FOLLOW_RANGE, 64.0D)
				.add(AttributeRegistry.VOLATILE_COOLDOWN)
				.add(AttributeRegistry.VOLATILE_HEALTH_START)
				.add(AttributeRegistry.VOLATILE_MAX_DAMAGE)
				.add(AttributeRegistry.VOLATILE_LENGTH);
	}

    @Override
    public void aiStep() {
        if (this.level().isClientSide()) {
            this.prevGrowCount = this.growCount;
            this.growCount = this.getGrowTimer();
            if (this.getGrowTimer() > 0 && this.getGrowTimer() < 40)
                if (this.tickCount % 4 == 0)
                    this.spawnVolatileParticles(true);
        } else {
            if (this.isInTar()) {
                if (this.getGrowTimer() > 0)
                    this.setGrowTimer(Math.max(0, this.getGrowTimer() - 1));
                if (this.getGrowTimer() <= 0) {
                    this.convertTo(EntityRegistry.TAR_BEAST.get(), false);
                }
            }

            if (!this.isInTar() && this.getGrowTimer() < 40)
                this.setGrowTimer(Math.min(40, this.getGrowTimer() + 1));

            if (this.getTarget() == null) {
                this.setHiding(true);

                this.canTurnVolatileOnTarget = false;
            } else {
                this.setHiding(false);

                if (this.canTurnVolatile && !this.isVolatile() && !this.isPassenger() && this.canPossess(this.getTarget()) && this.canTurnVolatileOnTarget) {
                    if (this.volatileCooldownTicks > 0) {
                        this.volatileCooldownTicks--;
                    }

                    if (this.getHealth() <= this.getMaxHealth() * this.getAttribute(AttributeRegistry.VOLATILE_HEALTH_START).getValue() && this.volatileCooldownTicks <= 0) {
                        this.setVolatile(true);
                        this.didTurnVolatileOnPlayer = true;
                        this.volatileReceivedDamage = 0.0F;
                        this.volatileCooldownTicks = this.getMaxVolatileCooldown() + this.level().getRandom().nextInt(this.getMaxVolatileCooldown()) + 20;
                        this.volatileTicks = 0;

                        PacketDistributor.sendToPlayersTrackingEntity(this, new WightVolatileParticlesPacket(this.getId()));
                        this.level().playSound(null, this.blockPosition(), SoundRegistry.WIGHT_ATTACK.get(), SoundSource.HOSTILE, 1.6F, 1.0F);
                    }
                } else if (this.didTurnVolatileOnPlayer && this.isVolatile() && !this.canPossess(this.getTarget())) {
                    this.setVolatile(false);
                    this.didTurnVolatileOnPlayer = false;
                }
            }
        }

        if (this.isVolatile()) {
            if (!this.level().isClientSide()) {
                if (this.volatileTicks < this.getAttribute(AttributeRegistry.VOLATILE_LENGTH).getValue()) {
                    this.volatileTicks++;

					if (this.volatileTicks >= 20) {
						this.noPhysics = true;
					}

                } else {
                    if (!this.level().isClientSide()) {
                        this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, 0.075D, 0.0D));

                        this.fallDistance = 0;

                        if (this.didTurnVolatileOnPlayer && this.onGround()) {
                            this.setVolatile(false);
                            this.didTurnVolatileOnPlayer = false;
                        }
                    }
                    this.noPhysics = false;
                }

                if (this.volatileTicks < 20) {
                    this.moveControl.setWantedPosition(this.getX(), this.getY() + 1.0D, this.getZ(), 0.15D);
                }

                if (this.getTarget() != null) {
                    LivingEntity attackTarget = this.getTarget();

                    if (this.getVehicle() == null && this.distanceTo(attackTarget) < 1.75D && this.canPossess(attackTarget)) {
                        this.startRiding(attackTarget, true);
                        this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(attackTarget));
                    }

                    if (this.getVehicle() == null) {
                        double dx = attackTarget.getX() - this.getX();
                        double dz = attackTarget.getZ() - this.getZ();
                        double dy = attackTarget.getEyeY() - (this.getEyeY());
                        double dist = Mth.sqrt((float) (dx * dx + dz * dz));
                        float yaw = (float) (Math.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
                        float pitch = (float) (-(Math.atan2(dy, dist) * Mth.RAD_TO_DEG));
                        this.absRotateTo(yaw, pitch);
                        this.setYHeadRot(yaw);
                    } else {
                        this.absRotateTo(0, 0);
                        this.setYHeadRot(0);

						if (this.tickCount % 5 == 0 && this.hasLineOfSight(this.getTarget()) && !this.isWearingSkullMask(this.getTarget())) {
						/*	List<VolatileSoul> existingSouls = this.level().getEntitiesOfClass(VolatileSoul.class, this.getBoundingBox().inflate(16.0D));
							if (existingSouls.size() < 16) {
								VolatileSoul soul = new VolatileSoul(this.level(), this);
								float mx = this.level().getRandom().nextFloat() - 0.5F;
								float my = this.level().getRandom().nextFloat() / 2.0F;
								float mz = this.level().getRandom().nextFloat() - 0.5F;
								Vec3 dir = new Vec3(mx, my, mz).normalize();
								soul.moveTo(this.getX() + dir.x * 0.5D, this.getY() + dir.y * 1.5D, this.getZ() + dir.z * 0.5D, 0, 0);
								soul.shoot(mx * 2.0D, my * 2.0D, mz * 2.0D, 1.0F, 1.0F);
								this.level().addFreshEntity(soul);
						
							}*/ //This is fucking annoying and too random imo
						}
                    }
                }

                this.moveControl = this.flightMoveControl;
            }

            if (this.level().isClientSide() && (this.getVehicle() == null || this.tickCount % 4 == 0)) {
                this.spawnVolatileParticles(false);
            }

            this.refreshDimensions();
        } else {
            if (!this.level().isClientSide()) {
                this.noPhysics = false;
                this.moveControl = this.groundMoveControl;
            }

            this.refreshDimensions();
        }

        this.lastHidingAnimationTicks = this.hidingAnimationTicks;
        if (this.isHiding()) {
            if (this.hidingAnimationTicks > 0)
                this.hidingAnimationTicks--;
        } else {
            if (this.hidingAnimationTicks < 12)
                this.hidingAnimationTicks++;
        }
        super.aiStep();
    }

	@Override
    public void travel(Vec3 travelVector) {
        if (this.isVolatile()) {
            //[VanillaCopy] of FlyingMob.travel (for easier porting)
            if (this.isControlledByLocalInstance()) {
                if (this.isInWater()) {
                    this.moveRelative(0.02F, travelVector);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
                } else if (this.isInLava()) {
                    this.moveRelative(0.02F, travelVector);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5F));
                } else {
                    BlockPos ground = getBlockPosBelowThatAffectsMyMovement();
                    float f = 0.91F;
                    if (this.onGround()) {
                        f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;
                    }

                    float f1 = 0.16277137F / (f * f * f);
                    f = 0.91F;
                    if (this.onGround()) {
                        f = this.level().getBlockState(ground).getFriction(this.level(), ground, this) * 0.91F;
                    }

                    this.moveRelative(this.onGround() ? 0.1F * f1 : 0.02F, travelVector);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    this.setDeltaMovement(this.getDeltaMovement().scale(f));
                }
            }
            this.calculateEntityAnimation(false);
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public boolean onClimbable() {
        if (this.isVolatile()) return false;
        return super.onClimbable();
    }

    public boolean isInTar() {
        return this.isEyeInFluidType(FluidTypeRegistry.TAR.get());
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isInTar() || this.getGrowTimer() < 40;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!this.isVolatile()) {
            super.checkFallDamage(y, onGround, state, pos);
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        if (!this.isVolatile()) {
            return super.causeFallDamage(fallDistance, multiplier, source);
        }
        return false;
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return this.isVolatile() ? VOLATILE_DIMENSIONS : super.getDefaultDimensions(pose);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        boolean isCreative = source.getEntity() instanceof Player player && player.isCreative();
        return (this.isHiding() && !isCreative) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPushable() {
        return !this.isHiding() && super.isPushable();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isVolatile() && source.is(DamageTypes.IN_WALL)) {
            return false;
        }
        float prevHealth = this.getHealth();
        boolean ret = super.hurt(source, amount);
        float dealtDamage = prevHealth - this.getHealth();
        if (this.didTurnVolatileOnPlayer && this.isVolatile() && this.getVehicle() != null) {
            this.volatileReceivedDamage += dealtDamage;
            if (this.volatileReceivedDamage >= this.getAttribute(AttributeRegistry.VOLATILE_MAX_DAMAGE).getValue()) {
                this.setVolatile(false);
                this.didTurnVolatileOnPlayer = false;
            }
        }
        if (this.getTarget() != null && source.getEntity() == this.getTarget()) {
            this.canTurnVolatileOnTarget = true;
        }
        return ret;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (this.isVolatile()) {
            return false;
        }
        if (super.doHurtTarget(entity)) {
            if (entity == this.getTarget()) {
                this.canTurnVolatileOnTarget = true;
            }
            return true;
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("volatile", this.isVolatile());
        compound.putInt("volatile_cooldown", this.volatileCooldownTicks);
        compound.putInt("volatile_ticks", this.volatileTicks);
        compound.putFloat("volatile_damage", this.volatileReceivedDamage);
        compound.putBoolean("can_turn_volatile_on_target", this.canTurnVolatileOnTarget);
        compound.putBoolean("can_turn_volatile", this.canTurnVolatile);
        compound.putBoolean("turned_volatile_on_player", this.didTurnVolatileOnPlayer);
        compound.putInt("grow_timer", this.getGrowTimer());
        compound.putBoolean("can_transform_in_to_shaman", this.canTransformInToShaman);	
        getTargetBlock().ifPresent(blockpos -> {
        	compound.put("targetBlock", NbtUtils.writeBlockPos(blockpos));
        });
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVolatile(compound.getBoolean("volatile"));
        this.volatileCooldownTicks = compound.getInt("volatile_cooldown");
        this.volatileTicks = compound.getInt("volatile_ticks");
        this.volatileReceivedDamage = compound.getFloat("volatile_damage");
        this.canTurnVolatileOnTarget = compound.getBoolean("can_turn_volatile_on_target");
        this.canTurnVolatile = compound.getBoolean("can_turn_volatile");
        this.didTurnVolatileOnPlayer = compound.getBoolean("turned_volatile_on_player");
        this.setGrowTimer(compound.getInt("grow_timer"));
        this.canTransformInToShaman = compound.getBoolean("can_transform_in_to_shaman");
        if (compound.contains("targetBlock", 99))
        	this.setTargetBlock(NbtUtils.readBlockPos(compound, "targetBlock").get());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.WIGHT_MOAN.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundRegistry.WIGHT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.WIGHT_DEATH.get();
    }

    public boolean isHiding() {
        return this.getEntityData().get(HIDING_STATE_DW);
    }

    public void setHiding(boolean hiding) {
        this.getEntityData().set(HIDING_STATE_DW, hiding);
    }

    public int getGrowTimer() {
        return this.getEntityData().get(GROW_TIMER);
    }

    public void setGrowTimer(int timer) {
        this.getEntityData().set(GROW_TIMER, timer);
    }

    public float getHidingAnimation(float partialTicks) {
        return Mth.lerp(partialTicks, this.lastHidingAnimationTicks, this.hidingAnimationTicks) / 12.0F;
    }

    public float getGrowthFactor(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevGrowCount, this.growCount);
    }

    public boolean isVolatile() {
        return this.getEntityData().get(VOLATILE_STATE_DW);
    }

    public void setVolatile(boolean isVolatile) {
        this.getEntityData().set(VOLATILE_STATE_DW, isVolatile);

        if (!isVolatile) {
            Entity ridingEntity = this.getVehicle();
            if (ridingEntity != null) {
                this.stopRiding();
                if (!this.level().isClientSide()) {
                    this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(ridingEntity));
                }
            }
        }
    }

    public int getMaxVolatileCooldown() {
        return (int) this.getAttribute(AttributeRegistry.VOLATILE_COOLDOWN).getValue();
    }

    public Optional<BlockPos> getTargetBlock() {
    	return this.getEntityData().get(TARGET_BLOCK);
	}

    public void setTargetBlock(BlockPos pos) {
    	this.getEntityData().set(TARGET_BLOCK, Optional.of(pos));
	}

    public void clearTargetBlock() {
        this.entityData.set(TARGET_BLOCK, Optional.empty());
    }

	public boolean canPossess(LivingEntity entity) {
		if (entity instanceof SwampHag)
			return true;

		if (entity instanceof BonePuppetRanged)
			return true;

		if (entity instanceof Player)
			return !this.isWearingSkullMask(entity);

		return false;
	}

    public boolean isWearingSkullMask(LivingEntity entity) {
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        return !helmet.isEmpty() && helmet.is(ItemRegistry.SKULL_MASK);
    }

    public void setCanTurnVolatile(boolean canTurnVolatile) {
        this.canTurnVolatile = canTurnVolatile;
    }

    private void spawnVolatileParticles(boolean tarred) {
        final double radius = 0.3F;

        final double cx = this.getX();
        final double cy = this.getY() + 0.35D;
        final double cz = this.getZ();

        for (int i = 0; i < 8; i++) {
            double px = this.level().getRandom().nextFloat() * 0.7F;
            double py = this.level().getRandom().nextFloat() * 0.7F;
            double pz = this.level().getRandom().nextFloat() * 0.7F;
            Vec3 vec = new Vec3(px, py, pz).subtract(new Vec3(0.35F, 0.35F, 0.35F)).normalize();
            px = cx + vec.x * radius;
            py = cy + vec.y * radius;
            pz = cz + vec.z * radius;
//			if (tarred) {
//				float tintChange = 1F / 40F * growCount;
//				BLParticles.STEAM_PURIFIER.spawn(this.level(), px, py + 0.25D, pz).setRBGColorF(tintChange, tintChange,
//					tintChange);
//			} else
//				BLParticles.STEAM_PURIFIER.spawn(this.level(), px, py, pz);
        }
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public static class WightAttackGoal extends MeleeAttackGoal {

        private final Wight wight;

        public WightAttackGoal(Wight wight, double speedModifier, boolean mustSee) {
            super(wight, speedModifier, mustSee);
            this.wight = wight;
        }

        @Override
        public boolean canUse() {
            return !this.wight.isVolatile() && !this.wight.isHiding() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.wight.isVolatile() && !this.wight.isHiding() && super.canContinueToUse();
        }
    }
}
