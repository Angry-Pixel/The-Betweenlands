package thebetweenlands.common.entity.monster.chiromaw;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.entity.DraetonPuller;
import thebetweenlands.api.entity.RingOfGatheringMinion;
import thebetweenlands.api.item.RadialMenuEquippable;
import thebetweenlands.common.entity.ai.goals.ChiromawShootBarbGoal;
import thebetweenlands.common.entity.projectile.arrow.ChiromawBarb;
import thebetweenlands.common.entity.projectile.arrow.ChiromawShockBarb;
import thebetweenlands.common.network.serverbound.ChiromawDoubleJumpPacket;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.MobEffectRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.*;

//TODO draeton pulling
public class TameChiromaw extends TamableAnimal implements RingOfGatheringMinion {

	private static final byte EVENT_DOUBLE_JUMP = 80;

	private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(TameChiromaw.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> ELECTRIC = SynchedEntityData.defineId(TameChiromaw.class, EntityDataSerializers.BOOLEAN);

	public int doubleJumpTicks;
	public int prevWingFlapTicks, wingFlapTicks;
	public int prevRaiseWingTicks, raiseWingsTicks;

	public TameChiromaw(EntityType<? extends TamableAnimal> type, Level level) {
		super(type, level);
		this.moveControl = new FlyingMoveControl(this, 20, true);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(2, new ChiromawShootBarbGoal(this));
		this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
		this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Monster.class, 4.0F, 0.75D, 0.75D));
		this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 0.5D, true));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 0.5D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, true));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 0.2D)
			.add(Attributes.FLYING_SPEED, 0.2D)
			.add(Attributes.MAX_HEALTH, 50.0D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D)
			.add(Attributes.FOLLOW_RANGE, 32.0D);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(ATTACKING, false);
		builder.define(ELECTRIC, false);
	}

	@Override
	protected Component getTypeName() {
		if (this.getElectricBoogaloo()) {
			return Component.translatable("entity.thebetweenlands.tame_lightning_chiromaw");
		}
		return super.getTypeName();
	}

	public boolean isAttacking() {
		return this.getEntityData().get(ATTACKING);
	}

	public void setAttacking(boolean attacking) {
		this.getEntityData().set(ATTACKING, attacking);
	}

	public void setElectricBoogaloo(boolean electric) {
		this.getEntityData().set(ELECTRIC, electric);
	}

	public boolean getElectricBoogaloo() {
		return this.getEntityData().get(ELECTRIC);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("electric", this.getElectricBoogaloo());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setElectricBoogaloo(tag.getBoolean("electric"));
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
	public void tick() {
		super.tick();

		Entity riding = this.getVehicle();

		if (this.doubleJumpTicks > 1) {
			this.doubleJumpTicks--;
		}

		this.prevWingFlapTicks = this.wingFlapTicks;
		if (this.wingFlapTicks > 1) {
			this.wingFlapTicks--;
		}

		if (riding == null || riding.onGround()) {
			this.doubleJumpTicks = 0;
		}

		if (riding == null) {
			if (this.jumping && this.isInWater()) {
				this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 1, this.getZ(), 1.0D);
				this.setInSittingPose(false);
			}

			if (this.isInSittingPose()) {
				this.setDeltaMovement(Vec3.ZERO);
				if (!this.level().isClientSide()) {
					if (!Block.canSupportRigidBlock(this.level(), this.blockPosition().above())) {
						this.setInSittingPose(false);
					}
				}
			} else if (this.level().getBlockState(this.blockPosition().below()).isFaceSturdy(this.level(), this.blockPosition().below(), Direction.UP)) {
				this.getMoveControl().setWantedPosition(this.getX(), this.getY() + 1, this.getZ(), 1.0D);
			}

			if (this.getDeltaMovement().y() < 0.0D && this.getTarget() == null) {
				this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.25D, 1.0D));
			}

			this.prevRaiseWingTicks = this.raiseWingsTicks;
			if (this.raiseWingsTicks > 0) {
				this.raiseWingsTicks--;
			}
		} else {
			this.prevRaiseWingTicks = this.raiseWingsTicks;

			if (!riding.onGround() && this.raiseWingsTicks < 3) {
				this.raiseWingsTicks++;
			} else if (riding.onGround() && this.raiseWingsTicks > 0) {
				this.raiseWingsTicks--;

				if (this.raiseWingsTicks == 0) {
					this.wingFlapTicks = 0;
				}
			}
		}
	}

	@Override
	public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
		if (this.isInSittingPose()) {
			return false;
		}
		if (target instanceof Creeper || target instanceof Ghast || target instanceof ArmorStand) {
			return false;
		} else if (target instanceof TameChiromaw chiromaw) {
			return chiromaw.getOwner() != owner;
		} else {
			if (target instanceof Player player && owner instanceof Player player1 && !player1.canHarmPlayer(player)) {
				return false;
			}

			return !(target instanceof AbstractHorse abstracthorse) || !abstracthorse.isTamed();
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.isPassenger() && (source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.DROWN))) {
			return false;
		}
		if (source.getEntity() == this) {
			return false;
		}
		if (source.getDirectEntity() instanceof ChiromawBarb || (this.getElectricBoogaloo() && source.getDirectEntity() instanceof ChiromawShockBarb)) {
			return false;
		}
		if (this.getVehicle() != null && source.getEntity() == this.getVehicle() && !this.canRiderInteract()) {
			return false;
		}
		if (super.hurt(source, amount)) {
			this.setInSittingPose(false);
			return true;
		}
		return false;
	}

	@Override
	public boolean isEffectiveAi() {
		return super.isEffectiveAi() && !this.hasEffect(MobEffectRegistry.SHOCKED);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
		flyingpathnavigation.setCanOpenDoors(false);
		flyingpathnavigation.setCanFloat(true);
		flyingpathnavigation.setCanPassDoors(true);
		return flyingpathnavigation;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {

	}

	@Override
	public void travel(Vec3 travelVector) {
		//Don't wander around when sitting
		if (this.isInSittingPose() && !this.level().isClientSide()) {
			return;
		}

		if (this.isControlledByLocalInstance()) {
			if (this.isInWater()) {
				this.moveRelative(0.02F, travelVector);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
			} else if (this.isInLava()) {
				this.moveRelative(0.02F, travelVector);
				this.move(MoverType.SELF, this.getDeltaMovement());
				this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
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
				this.setDeltaMovement(this.getDeltaMovement().scale((double) f));
			}
		}

		this.calculateEntityAnimation(false);
	}

	@Override
	public boolean onClimbable() {
		return false;
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		boolean holdsEquipment = hand == InteractionHand.MAIN_HAND && !stack.isEmpty() && (stack.getItem() instanceof RadialMenuEquippable || stack.is(ItemRegistry.AMULET_SLOT));
		if (holdsEquipment)
			return InteractionResult.CONSUME;
		if (!stack.isEmpty()) {
			if (this.isFood(stack)) {
				if (this.getHealth() < this.getMaxHealth()) {
					if (!this.level().isClientSide()) {
						this.heal(5.0F);
						stack.consume(1, player);
					} else {
						this.spawnTamingParticles(true);
					}

					return InteractionResult.sidedSuccess(this.level().isClientSide());
				}
			}
			if (stack.is(ItemRegistry.NET))
				return stack.getItem().interactLivingEntity(stack, player, this, InteractionHand.MAIN_HAND);
		}

		if (this.isOwnedBy(player) && hand == InteractionHand.MAIN_HAND && !(this instanceof DraetonPuller)) {
			this.setYRot(player.getYRot());


			this.setTarget(null);

			Entity riding = this.getVehicle();

			if (riding == null) {
				if (!player.isVehicle()) { // stops multiple mounting you
					if (this.isInSittingPose()) {
						this.setInSittingPose(false);
					}
					this.playSound(SoundRegistry.CHIROMAW_MATRIARCH_LAND.get(), 0.25F, 1.5F);
					this.startRiding(player, true);
				}
			} else {
				boolean canUnmount = this.level().noCollision(this, this.getBoundingBox());

				if (canUnmount) {
					this.playSound(SoundRegistry.CHIROMAW_MATRIARCH_RELEASE.get(), 0.5F, 1F);
					this.stopRiding();
				} else if (player instanceof ServerPlayer) {
					player.displayClientMessage(Component.translatable("entity.thebetweenlands.tame_chiromaw.obstructed"), true);
				}

				if (!this.isInSittingPose()) {
					if (canUnmount) {
						List<BlockPos> sitPositions = new ArrayList<>();

						for (int yo = 2; yo <= 4; yo++) {
							for (int xo = -1; xo <= 1; xo++) {
								for (int zo = -1; zo <= 1; zo++) {
									BlockPos pos = new BlockPos(riding.blockPosition().offset(xo, yo, zo));

									if (this.level().isEmptyBlock(pos)) {
										if (Block.canSupportRigidBlock(this.level(), pos.above())) {
											sitPositions.add(pos);
										}
									}
								}
							}
						}

						if (!sitPositions.isEmpty()) {
							this.setInSittingPose(true);
							this.jumping = false;

							sitPositions.sort(Comparator.comparingDouble(pos -> this.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f)));

							BlockPos sitPosition = sitPositions.getFirst();

							this.setPos(sitPosition.getX() + 0.5f, sitPosition.getY() + 1 - this.getBbHeight(), sitPosition.getZ() + 0.5f);
							this.getNavigation().stop();
							this.setDeltaMovement(Vec3.ZERO);
							this.hurtMarked = true;
							this.setXxa(0);
							this.setYya(0);
							this.setZza(0);
						}
					}
				} else {
					this.setInSittingPose(false);
				}
			}
			return InteractionResult.sidedSuccess(this.level().isClientSide());
		}

		return super.mobInteract(player, hand);
	}

	@Override
	public Vec3 getVehicleAttachmentPoint(Entity entity) {
		if (this.getVehicle() != null && this.getVehicle() == entity) {
			return new Vec3(0.0D, 0.5D - this.getBbHeight() * (entity.isShiftKeyDown() ? 0.3D : 0.5D), 0.0D);
		}
		return super.getVehicleAttachmentPoint(entity);
	}

	@Override
	public boolean canRiderInteract() {
		return this.getVehicle() != null && this.getVehicle().isShiftKeyDown();
	}

	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		return null;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(ItemRegistry.RAW_SNAIL_FLESH); //TODO tag
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if (id == EVENT_DOUBLE_JUMP) {
			this.prevWingFlapTicks = this.wingFlapTicks = this.doubleJumpTicks = 20;
		}
	}

	public void performDoubleJump(Player player) {
		if (this.doubleJumpTicks == 0 && (!this.level().isClientSide() || !player.onGround())) {
			player.jumpFromGround();
			player.fallDistance = -2;

			this.prevWingFlapTicks = this.wingFlapTicks = this.doubleJumpTicks = 20;

			if (!this.level().isClientSide()) {
				this.level().broadcastEntityEvent(this, EVENT_DOUBLE_JUMP);
				this.playSound(SoundRegistry.CHIROMAW_MATRIARCH_FLAP.get());
			} else {
				PacketDistributor.sendToServer(new ChiromawDoubleJumpPacket(this.getId()));
			}
		}
	}

	@Override
	public CompoundTag returnToRing(UUID userId) {
		return this.saveWithoutId(new CompoundTag());
	}

	@Override
	public boolean returnFromRing(Entity user, CompoundTag tag) {
		double prevX = this.getX();
		double prevY = this.getY();
		double prevZ = this.getZ();
		float prevYaw = this.getYRot();
		float prevPitch = this.getXRot();
		this.load(tag);
		this.moveTo(prevX, prevY, prevZ, prevYaw, prevPitch);
		if (!this.isAlive()) {
			//Revived by animator
			this.setHealth(this.getMaxHealth());
		}
		this.level().addFreshEntity(this);
		return true;
	}

	@Override
	public boolean shouldReturnOnUnload(boolean isOwnerLoggedIn) {
		return RingOfGatheringMinion.super.shouldReturnOnUnload(isOwnerLoggedIn) && !this.isInSittingPose();
	}

	@Override
	public UUID getRingOwnerUUID() {
		return this.getOwnerUUID();
	}
}
