package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.GenericPartEntity;
import thebetweenlands.common.entity.ai.goals.EmberlingFireBreathGoal;
import thebetweenlands.common.entity.movement.EmberlingMoveControl;
import thebetweenlands.common.registries.*;

public class Emberling extends TamableAnimal implements BLEntity, Enemy {

	public final PartEntity<?> tailPart;
	private static final EntityDataAccessor<Boolean> IS_FLAME_ATTACKING = SynchedEntityData.defineId(Emberling.class, EntityDataSerializers.BOOLEAN);
	public float animationTicks, prevAnimationTicks;

	private final MoveControl waterMoveControl;
	private final MoveControl groundMoveControl;

	private final GroundPathNavigation groundPathNavigation;
	private final WaterBoundPathNavigation waterPathNavigation;

	public Emberling(EntityType<? extends TamableAnimal> type, Level level) {
		super(type, level);
		this.tailPart = new GenericPartEntity<>(this, 0.5F, 0.5F);

		this.waterMoveControl = new EmberlingMoveControl(this);
		this.groundMoveControl = new MoveControl(this);

		this.groundPathNavigation = new GroundPathNavigation(this, level);
		this.groundPathNavigation.setCanFloat(true);

		this.waterPathNavigation = new WaterBoundPathNavigation(this, level);
		this.waterPathNavigation.setCanFloat(true);

		this.updateMovementAndPathfinding();
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return TamableAnimal.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 30.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.5D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D)
			.add(Attributes.FOLLOW_RANGE, 16.0D)
			.add(Attributes.STEP_HEIGHT, 1.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 0.25D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new EmberlingFireBreathGoal(this));
		this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 0.65D, false));
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.6D));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F) {
			@Override
			public boolean canUse() {
				return !Emberling.this.isInSittingPose() && super.canUse();
			}
		});
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				return !Emberling.this.isInSittingPose() && super.canUse();
			}
		});
		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Mob.class, 0, false, true, entity -> entity instanceof Enemy) {
			@Override
			public boolean canUse() {
				return Emberling.this.isTame() && super.canUse();
			}
		});
		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, false, true) {
			@Override
			public boolean canUse() {
				return !Emberling.this.isTame() && super.canUse();
			}
		});
		this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_FLAME_ATTACKING, false);
	}

	@Override
	public PartEntity<?>[] getParts() {
		return new PartEntity<?>[]{this.tailPart};
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	public void setShootingFlames(boolean flames) {
		this.getEntityData().set(IS_FLAME_ATTACKING, flames);
	}

	public boolean isShootingFlames() {
		return this.getEntityData().get(IS_FLAME_ATTACKING);
	}


	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(ItemRegistry.OCTINE_INGOT) || stack.is(ItemRegistry.OCTINE_NUGGET);
	}

	@Override
	public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		return null;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.EMBERLING_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.EMBERLING_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.EMBERLING_DEATH.get();
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return !this.isTame();
	}

	@Override
	public boolean canBeLeashed() {
		return this.isTame();
	}

	@Override
	public void tick() {
		super.tick();

		this.updateMovementAndPathfinding();

		if (!this.level().isClientSide() && this.isInSittingPose()) {
			if (this.getHealth() < this.getMaxHealth()) {
				if (this.tickCount % 600 == 0) {
					if (this.level().getBlockState(this.blockPosition().below()).is(BLBlockTagProvider.EMBERLING_HEALS_ON)) {
						this.heal(1); // passive heal, 1 health every 30s whilst sleeping on an octine block.
						this.spawnTamingParticles(true);
						this.level().broadcastEntityEvent(this, (byte) 7);
					}
				}
			}
		}

		if (this.level().isClientSide()) {
			if (!this.isShootingFlames()) {
				if (this.tickCount % 5 == 0) {
					if (!this.isInSittingPose()) {
						this.flameParticles(this.level(), this.tailPart.getX(), this.tailPart.getY() + 0.25D, this.tailPart.getZ(), this.getRandom());
					} else {
						this.sleepingParticles(this.level(), this.tailPart.getX(), this.tailPart.getY() + 0.25D, this.tailPart.getZ(), this.getRandom());
					}
				}
			} else {
				this.spawnFlameBreathParticles(this.level(), this.getX(), this.getEyeY(), this.getZ(), this.getRandom());
			}
		}
	}

	protected void updateMovementAndPathfinding() {
		this.moveControl = this.isInWater() ? this.waterMoveControl : this.groundMoveControl;

		if (this.isInWater() && !this.level().isEmptyBlock(BlockPos.containing(this.getX(), this.getBoundingBox().maxY + 0.25D, this.getZ()))) {
			this.navigation = this.waterPathNavigation;
		} else {
			this.navigation = this.groundPathNavigation;
		}
		double a = Math.toRadians(this.yBodyRot);
		double offSetX = -Math.sin(a) * (this.isInSittingPose() ? -0.2D : 1.85D);
		double offSetZ = Math.cos(a) * (this.isInSittingPose() ? -0.2D : 1.85D);
		this.tailPart.moveTo(this.getX() - offSetX, this.getY() + 0.2D, this.getZ() - offSetZ, 0.0F, 0.0F);
	}

	public float smoothedAngle(float partialTicks) {
		return this.prevAnimationTicks + (this.animationTicks - this.prevAnimationTicks) * partialTicks;
	}

	public void flameParticles(Level level, double x, double y, double z, RandomSource rand) {
		for (int count = 0; count < 3; ++count) {
			int motionX = rand.nextBoolean() ? 1 : -1;
			int motionZ = rand.nextBoolean() ? 1 : -1;
			double velY = rand.nextFloat() * 0.05D;
			double velZ = rand.nextFloat() * 0.025D * motionZ;
			double velX = rand.nextFloat() * 0.025D * motionX;
			if (this.tailPart.isUnderWater()) {
				level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE, x, y, z, velX, velY, velZ);
				level.addAlwaysVisibleParticle(ParticleTypes.SMOKE, x, y, z, velX, velY, velZ);
			} else {
				level.addAlwaysVisibleParticle(ParticleTypes.FLAME, x, y, z, velX, velY, velZ);
			}
		}
	}

	protected void spawnFlameBreathParticles(Level level, double x, double y, double z, RandomSource rand) {
		for (int count = 0; count < 5; ++count) {
			Vec3 look = this.getViewVector(1.0F).normalize();
			double a = Math.toRadians(this.yBodyRot);
			double offSetX = -Math.sin(a);
			double offSetZ = Math.cos(a);
			int motionX = rand.nextBoolean() ? 1 : -1;
			int motionY = rand.nextBoolean() ? 1 : -1;
			int motionZ = rand.nextBoolean() ? 1 : -1;
			double velX = rand.nextFloat() * 0.1D * motionX;
			double velY = rand.nextFloat() * 0.1D * motionY;
			double velZ = rand.nextFloat() * 0.1D * motionZ;

			float speed = 0.15F;
			level.addParticle(ParticleTypes.FLAME, x + offSetX + velX, y + velY, z + offSetZ + velZ, look.x * speed, look.y * speed, look.z * speed);
		}
	}

	public void sleepingParticles(Level level, double x, double y, double z, RandomSource rand) {
		if (this.tickCount % 60 == 5 || this.tickCount % 60 == 15 || this.tickCount % 60 == 25) {
			int motionX = rand.nextBoolean() ? 1 : -1;
			int motionZ = rand.nextBoolean() ? 1 : -1;
			double velY = rand.nextFloat() * 0.05D;
			double velZ = rand.nextFloat() * 0.025D * motionZ;
			double velX = rand.nextFloat() * 0.025D * motionX;
			level.addAlwaysVisibleParticle(ParticleRegistry.SLEEPING.get(), x, y + 0.05F, z, velX, velY, velZ);
		}
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!this.level().isClientSide()) {
			if (this.isTame()) {
				if (this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
					this.heal(stack.is(ItemRegistry.OCTINE_NUGGET) ? 10.0F : this.getMaxHealth());
					if (this.getHealth() == this.getMaxHealth()) {
						this.playSound(SoundRegistry.EMBERLING_LIVING.get(), 1.0F, 0.75F);
					}
					stack.consume(1, player);
					this.gameEvent(GameEvent.EAT);
					return InteractionResult.sidedSuccess(this.level().isClientSide());
				} else {
					InteractionResult interactionresult = super.mobInteract(player, hand);
					if (!interactionresult.consumesAction() && this.isOwnedBy(player)) {
						this.setOrderedToSit(!this.isOrderedToSit());
						this.jumping = false;
						this.navigation.stop();
						this.setTarget(null);
						return InteractionResult.SUCCESS_NO_ITEM_USED;
					} else {
						return interactionresult;
					}
				}
			} else if (stack.is(ItemRegistry.UNDYING_EMBERS)) {
				stack.consume(1, player);
				this.tame(player);
				this.navigation.stop();
				this.setTarget(null);
				this.level().broadcastEntityEvent(this, (byte) 7);
				return InteractionResult.SUCCESS;
			}
		} else {
			boolean flag = this.isOwnedBy(player) || this.isTame() || stack.is(ItemRegistry.UNDYING_EMBERS) && !this.isTame();
			return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean isInWater() {
		return this.isInFluidType(NeoForgeMod.WATER_TYPE.value()) || this.isInFluidType(FluidTypeRegistry.SWAMP_WATER.get());
	}

	@Override
	protected void applyTamingSideEffects() {
		if (this.isTame()) {
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(90.0D);
			this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0D);
			this.setHealth(90.0F);
		} else {
			this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0D);
			this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		}
	}
}
