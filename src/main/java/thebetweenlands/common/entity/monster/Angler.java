package thebetweenlands.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntityWithSpawnRules;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class Angler extends Monster implements BLEntityWithSpawnRules <Angler> {
	private static final EntityDataAccessor<Boolean> IS_LEAPING = SynchedEntityData.defineId(Angler.class, EntityDataSerializers.BOOLEAN);

	public Angler(EntityType<? extends Angler> type, Level level) {
		super(type, level);
        moveControl = new Angler.AnglerMoveHelper(this);
    }

	@Override
	protected void registerGoals() {
        goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.7D, true) {
    		@Override
    		protected void checkAndPerformAttack(LivingEntity enemy) {
    			double attackReachSqr = (0.75D + enemy.getBbWidth()) * (0.75D + enemy.getBbWidth());
    			if (this.mob.distanceToSqr(enemy.getX(), enemy.getY(), enemy.getZ()) <= attackReachSqr) {
    				this.resetAttackCooldown();
    				this.mob.doHurtTarget(enemy);
    			} else {
    				this.resetAttackCooldown();
    			}
    		}
        });
        goalSelector.addGoal(1, new MoveTowardsRestrictionGoal(this, 0.4D));
        goalSelector.addGoal(2, new RandomStrollGoal(this, 0.5D, 20));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    	targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, 0, true, true, null));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_LEAPING, false);
    }

	public boolean isLeaping() {
		return entityData.get(IS_LEAPING);
	}

	private void setIsLeaping(boolean leaping) {
		entityData.set(IS_LEAPING, leaping);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20D)
				.add(Attributes.FOLLOW_RANGE, 12D)
				.add(Attributes.MOVEMENT_SPEED, 0.6D)
				.add(Attributes.ATTACK_DAMAGE, 2D);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.FISH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.FISH_DEATH.get();
    }

	@Override
	 protected SoundEvent getAmbientSound() {
		return isInWater() ? SoundEvents.FISH_SWIM : SoundRegistry.FISH_FLOP.get();
	}

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }
    
	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(isInWater() ? SoundEvents.FISH_SWIM : SoundRegistry.FISH_FLOP.get(), 0.5F, 2F);
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }
/*
    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.ANGLER;
    }
*/

	@Override
	public int getMaxSpawnClusterSize() {
		return 3;
	}

	public boolean isGrounded() {
		return !isInWater() && level().isEmptyBlock(blockPosition().above()) && level().getBlockState(blockPosition().below()).isRedstoneConductor(level(), blockPosition().below());
	}

	@Override
    protected PathNavigation createNavigation(Level world){
        return new WaterBoundPathNavigation(this, world);
    }

	@Override
	public float getWalkTargetValue(BlockPos pos, LevelReader level) {
		return level.getFluidState(pos).is(FluidTags.WATER) ? 10.0F + level.getMaxLocalRawBrightness(pos) - 0.5F : super.getWalkTargetValue(pos, level);
	}

	@Override
	public void aiStep() {
		if (level().isClientSide()) {
			if(level().getGameTime()%5 == 0)
				if (isInWater()) {
					for (int i = 0; i < 2; ++i) {
						double a = Math.toRadians(this.getYRot());
						double offSetX = -Math.sin(a) * this.getBbWidth() * 0.5D;
						double offSetZ = Math.cos(a) * this.getBbWidth() * 0.5D;
						this.level().addParticle(ParticleTypes.BUBBLE, this.getX() + offSetX, this.getY() + this.getBbHeight() * 0.5D + this.getRandom().nextDouble() * 0.5D, this.getZ() + offSetZ, 0.0D, 0.4D, 0.0D);
					}
				}
		}

		if (isInWater()) {
			setAirSupply(300);
		} else if (onGround()) {
			setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F), 0.5D, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F)));
			setYRot(random.nextFloat() * 360.0F);
			if(isLeaping())
				setIsLeaping(false);
			setOnGround(false);
			hasImpulse = true;
			if(level().getGameTime()%5==0) {
				level().playSound(null, getX(), getY(), getZ(), SoundRegistry.FISH_FLOP.get(), SoundSource.HOSTILE, 1F, 1F);
				this.hurt(this.damageSources().drown(), 0.5F);
			}
		}

		super.aiStep();
	}

	@Override
	public void tick() {
		if (!level().isClientSide()) {
			if (getTarget() != null && !level().containsAnyLiquid(getTarget().getBoundingBox())) {
				double distance = distanceTo(getTarget());
				if (distance > 1.0F && distance < 6.0F)
					if (isInWater() && level().isEmptyBlock(new BlockPos((int) getX(), (int) getY() + 1, (int) getZ()))) {
						if (!isLeaping()) {
							setIsLeaping(true);
							level().playSound(null, getX(), getY(), getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1F, 2F);
						}
						double distanceX = getTarget().getX() - getX();
						double distanceY = getTarget().getY() + (double) (getTarget().getBbHeight() * 0.5F) - (this.getY() + (double) this.getEyeHeight());
						double distanceZ = getTarget().getZ() - getZ();
						float distanceSqrRoot = Mth.sqrt((float) (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ));
						double motionX = distanceX / distanceSqrRoot * 0.5D * 0.900000011920929D + getDeltaMovement().x * 0.70000000298023224D;
						double motionY = 0.125D;
						double motionZ = distanceZ / distanceSqrRoot * 0.5D * 0.900000011920929D + getDeltaMovement().z * 0.70000000298023224D;
						setDeltaMovement(getDeltaMovement().add(motionX * 0.25D, motionY, motionZ * 0.25D));
					}
			}
		}
		super.tick();
	}

	@Override
    public void travel(Vec3 travel_vector) {
		if (isEffectiveAi()) {
			if (isInWater()) {
				moveRelative(0.1F, travel_vector);
				move(MoverType.SELF, getDeltaMovement());
				setDeltaMovement(getDeltaMovement().scale(0.8999999761581421D));
				if (getTarget() == null) {
					setDeltaMovement(getDeltaMovement().add(0.0D, -0.005D, 0.0D));
				}
			} else {
				super.travel(travel_vector);
			}
		} else {
			super.travel(travel_vector);
		}
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if (hasLineOfSight(entity)) {
			if (super.doHurtTarget(entity)) {
				playSound(SoundRegistry.ANGLER_ATTACK.get(), 1, 1);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean checkSpawnObstruction(LevelReader level) {
		return level.isUnobstructed(this);
	}
/*
    @Override
    public boolean isPushedByWater() {
        return false;
    }
*/
    //AIs

    static class AnglerMoveHelper extends MoveControl {
        private final Angler angler;

        public AnglerMoveHelper(Angler angler) {
            super(angler);
            this.angler = angler;
        }

		public void tick() {
	         if (this.operation == MoveControl.Operation.MOVE_TO && !this.angler.getNavigation().isDone()) {
	             Vec3 vec3d = new Vec3(this.wantedX - this.angler.getX(), this.wantedY - this.angler.getY(), this.wantedZ - this.angler.getZ());
	             double d0 = vec3d.length();
	             double d1 = vec3d.x / d0;
	             double d2 = vec3d.y / d0;
	             double d3 = vec3d.z / d0;
	             float f = (float)(Mth.atan2(vec3d.z, vec3d.x) * (double)(180F / (float)Math.PI)) - 90.0F;
	             this.angler.setYRot(this.rotlerp(this.angler.getYRot(), f, 90.0F));
	             this.angler.yBodyRot = this.angler.getYRot();
	             float f1 = (float)(this.speedModifier * this.angler.getAttributeValue(Attributes.MOVEMENT_SPEED));
	             float f2 = Mth.lerp(0.125F, this.angler.getSpeed(), f1);
	             this.angler.setSpeed(f2);
	             double d4 = Math.sin((double)(this.angler.tickCount + this.angler.getId()) * 0.5D) * 0.05D;
	             double d5 = Math.cos((double)(this.angler.getYRot() * ((float)Math.PI / 180F)));
	             double d6 = Math.sin((double)(this.angler.getYRot() * ((float)Math.PI / 180F)));
	             double d7 = Math.sin((double)(this.angler.tickCount + this.angler.getId()) * 0.75D) * 0.05D;
	             this.angler.setDeltaMovement(this.angler.getDeltaMovement().add(d4 * d5, d7 * (d6 + d5) * 0.25D + (double)f2 * d2 * 0.1D, d4 * d6));
	             LookControl lookcontroller = this.angler.getLookControl();
	             double d8 = this.angler.getX() + d1 * 2.0D;
	             double d9 = this.angler.getEyeY() + d2 / d0;
	             double d10 = this.angler.getZ() + d3 * 2.0D;
	             double d11 = lookcontroller.getWantedX();
	             double d12 = lookcontroller.getWantedY();
	             double d13 = lookcontroller.getWantedZ();
	             if (!lookcontroller.isLookingAtTarget()) {
	                d11 = d8;
	                d12 = d9;
	                d13 = d10;
	             }
	             this.angler.getLookControl().setLookAt(Mth.lerp(0.125D, d11, d8), Mth.lerp(0.125D, d12, d9), Mth.lerp(0.125D, d13, d10), 10.0F, 40.0F);
	          } else {
	             this.angler.setSpeed(0.0F);
	          }
	       }
    }

	@Override
	public boolean canSpawnHere(EntityType<Angler> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) { 
		return level.getDifficulty() != Difficulty.PEACEFUL && level.getBlockState(pos).is(BlockRegistry.SWAMP_WATER) && pos.getY() <= TheBetweenlands.LAYER_HEIGHT +3;
	}
}
