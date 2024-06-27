package thebetweenlands.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.ai.goals.EntityAIFlyRandomly;
import thebetweenlands.common.entities.ai.goals.EntityAIMoveToDirect;
import thebetweenlands.common.registries.AttributeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

// TODO: whight keeps losing its target when it goes volitile & seems to not move towards target while volitile ether
// ill have to do more tests to find out why

public class EntityWight extends BetweenlandsEntity {

	protected static final EntityDataAccessor<Boolean> HIDING_STATE_DW = SynchedEntityData.defineId(EntityWight.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Boolean> VOLATILE_STATE_DW = SynchedEntityData.defineId(EntityWight.class, EntityDataSerializers.BOOLEAN);
	protected final MoveControl flightMoveHelper;
	protected final MoveControl groundMoveHelper;
	private int hidingAnimationTicks = 0;
	private int lastHidingAnimationTicks = 0;
	private int volatileCooldownTicks = (int) AttributeRegistry.VOLATILE_COOLDOWN_ATTRIB.value().getDefaultValue() / 2 + 20;
	private int volatileTicks = 0;
	private float volatileReceivedDamage = 0.0F;
	private boolean canTurnVolatile = true;
	private boolean canTurnVolatileOnTarget = true;
	private boolean didTurnVolatileOnPlayer = false;
	private static final EntityDataAccessor<Integer> GROW_TIMER = SynchedEntityData.defineId(EntityWight.class, EntityDataSerializers.INT);
	public int growCount, prevGrowCount;

	public EntityDimensions dimensions;

	public EntityWight(EntityType<? extends Monster> p_33002_, Level p_33003_) {
		super(p_33002_, p_33003_);
		this.ambientSoundTime = 80;
		/*this.flightMoveHelper = new FlightMoveHelper(this) {
            @Override
            protected double getFlightSpeed() {
                return this.mob.getAttributes().getInstance(VOLATILE_FLIGHT_SPEED_ATTRIB).getValue();
            }
        };*/
		this.moveControl = this.flightMoveHelper = this.groundMoveHelper = new MoveControl(this);
		this.dimensions = EntityDimensions.scalable(0.7F, 2.2F);    // defalt size
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(HIDING_STATE_DW, false);
		builder.define(VOLATILE_STATE_DW, false);
		builder.define(GROW_TIMER, 40);

	}

	@Override
	public AttributeMap getAttributes() {
		return new AttributeMap(EntityWight.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 76.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.33D)
			.add(Attributes.ATTACK_DAMAGE, 6.0D)
			.add(Attributes.FOLLOW_RANGE, 80.0D)
			.add(AttributeRegistry.VOLATILE_HEALTH_START_ATTRIB)
			.add(AttributeRegistry.VOLATILE_COOLDOWN_ATTRIB)
			.add(AttributeRegistry.VOLATILE_FLIGHT_SPEED_ATTRIB)
			.add(AttributeRegistry.VOLATILE_LENGTH_ATTRIB)
			.add(AttributeRegistry.VOLATILE_MAX_DAMAGE_ATTRIB).build());
	}

	@Override
	public void aiStep() {
		super.aiStep();

		if (this.level().isClientSide) {
			prevGrowCount = growCount;
			growCount = getGrowTimer();
			if (getGrowTimer() > 0 && getGrowTimer() < 40)
				if (this.tickCount % 4 == 0)
					this.spawnVolatileParticles(true);
		}

		if (!this.level().isClientSide) {
			if (isInTar()) {
				if (getGrowTimer() > 0)
					setGrowTimer(Math.max(0, getGrowTimer() - 1));
				if (getGrowTimer() <= 0) {
					//EntityTarBeast tar_beast = new EntityTarBeast(getEntityWorld());
					//tar_beast.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
					//tar_beast.setGrowTimer(0);
					//getEntityWorld().spawnEntity(tar_beast);
					kill();
				}
			}

			if (!isInTar() && getGrowTimer() < 40)
				setGrowTimer(Math.min(40, getGrowTimer() + 1));

			if (this.getTarget() == null) {
				this.setHiding(true);

				this.canTurnVolatileOnTarget = false;
			} else {
				this.setHiding(false);

				if (this.canTurnVolatile && !this.isVolatile() && !this.isRiding() && this.canPossess(this.getTarget()) && this.canTurnVolatileOnTarget) {
					if (this.volatileCooldownTicks > 0) {
						this.volatileCooldownTicks--;
					}

					if (this.getHealth() <= this.getMaxHealth() * this.getAttribute(AttributeRegistry.VOLATILE_HEALTH_START_ATTRIB).getValue() && this.volatileCooldownTicks <= 0) {
						this.setVolatile(true);
						this.didTurnVolatileOnPlayer = true;
						this.volatileReceivedDamage = 0.0F;
						this.volatileCooldownTicks = this.getMaxVolatileCooldown() + this.level().random.nextInt(this.getMaxVolatileCooldown()) + 20;
						this.volatileTicks = 0;

						//TheBetweenlands.networkWrapper.sendToAllAround(new MessageWightVolatileParticles(this), new TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 32));
						this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.WIGHT_ATTACK.get(), SoundSource.HOSTILE, 1.6F, 1.0F);
					}
				} else if (this.didTurnVolatileOnPlayer && this.isVolatile() && !this.canPossess(this.getTarget())) {
					this.setVolatile(false);
					this.didTurnVolatileOnPlayer = false;
				}
			}
		}

		if (this.isVolatile()) {
			if (!this.level().isClientSide) {
				if (this.volatileTicks < this.getAttribute(AttributeRegistry.VOLATILE_LENGTH_ATTRIB).getValue()) {
					this.volatileTicks++;

					if (this.volatileTicks >= 20) {
						this.noPhysics = true;
						this.setNoGravity(true);
					}
				} else {
					if (!this.level().isClientSide) {
						this.yya -= 0.075F;

						this.fallDistance = 0;

						if (this.didTurnVolatileOnPlayer && this.onGround()) {
							this.setVolatile(false);
							this.didTurnVolatileOnPlayer = false;
						}
					}

					this.noPhysics = false;
					this.setNoGravity(false);
				}

				if (this.volatileTicks < 20) {
					this.moveControl.setWantedPosition(this.xo, this.yo + 1.0D, this.zo, 0.15D);
				}

				if (this.getTarget() != null) {
					LivingEntity attackTarget = this.getTarget();

					if (this.getRidingEntity() == null && this.distanceTo(attackTarget) < 1.75D && this.canPossess(attackTarget)) {
						this.startRiding(attackTarget, true);
						this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(attackTarget));
					}

					if (this.getRidingEntity() == null) {
						double dx = attackTarget.getX() - this.getX();
						double dz = attackTarget.getZ() - this.getZ();
						double dy;
						if (attackTarget instanceof Mob) {
							dy = attackTarget.getY() + (double) attackTarget.getEyeHeight() - (this.getY() + (double) this.getEyeHeight());
						} else {
							dy = (attackTarget.getBoundingBox().minY + attackTarget.getBoundingBox().maxY) / 2.0D - (this.getY() + (double) this.getEyeHeight());
						}
						double dist = Math.sqrt(dx * dx + dz * dz);
						float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
						float pitch = (float) (-(Math.atan2(dy, dist) * 180.0D / Math.PI));
						this.setRot(yaw, pitch);
						this.setYHeadRot(yaw);
					} else {
						this.setRot(0, 0);
						this.setYHeadRot(0);

						// shoot souls
	                    /* disabled untill i implement them
	                    if (this.tickCount % 5 == 0 && this.canEntityBeSeen(this.getTarget()) && !this.isWearingSkullMask(this.getTarget())) {
	                        List<EntityVolatileSoul> existingSouls = this.level.getEntitiesWithinAABB(EntityVolatileSoul.class, this.getBoundingBox().grow(16.0D, 16.0D, 16.0D));
	                        if (existingSouls.size() < 16) {
	                            EntityVolatileSoul soul = new EntityVolatileSoul(this.level);
	                            float mx = this.level.random.nextFloat() - 0.5F;
	                            float my = this.level.random.nextFloat() / 2.0F;
	                            float mz = this.level.random.nextFloat() - 0.5F;
	                            Vector3d dir = new Vector3d(mx, my, mz);
	                            soul.setOwner(this.getUniqueID());
	                            soul.setLocationAndAngles(this.getX() + dir.x * 0.5D, this.getY() + dir.y * 1.5D, this.getZ() + dir.z * 0.5D, 0, 0);
	                            soul.shoot(mx * 2.0D, my * 2.0D, mz * 2.0D, 1.0F, 1.0F);
	                            this.level.spawnEntity(soul);
	                        }
	                    }
	                    */
					}
				}

				this.moveControl = this.flightMoveHelper;
			}

			if (this.level().isClientSide && (this.getRidingEntity() == null || this.tickCount % 4 == 0)) {
				this.spawnVolatileParticles(false);
			}

			this.setSize(0.7F, 0.7F);
		} else {
			if (!this.level().isClientSide) {
				this.noPhysics = false;
				this.setNoGravity(false);
				this.moveControl = this.groundMoveHelper;
			}

			this.setSize(0.7F, 2.2F);
		}

		this.lastHidingAnimationTicks = this.hidingAnimationTicks;
		if (this.isHiding()) {
			if (this.hidingAnimationTicks < 12)
				this.hidingAnimationTicks++;
		} else {
			if (this.hidingAnimationTicks > 0)
				this.hidingAnimationTicks--;
		}

	}

	public boolean isInTar() {
		//System.out.println("IN TAR!");
		return false;//this.world.isMaterialInBB(this.getEntityBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), BLMaterialRegistry.TAR);
	}


	//@Override
	//protected boolean isMovementBlocked() {
	//	return super.isMovementBlocked() || isInTar() || getGrowTimer() < 40;
	//}


	@Override
	public int calculateFallDamage(float distance, float damageMultiplier) {
		if (!this.isVolatile()) {
			super.calculateFallDamage(distance, damageMultiplier);
		}
		return 0;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		if (!this.isVolatile()) {
			super.checkFallDamage(y, onGroundIn, state, pos);
		}
	}


	//TODO: this curently pushes volitile wrights into the positive z direction... i think i may have miss interperated a function or two-hundred...
	@Override
	public void travel(Vec3 in) {
		if (this.isVolatile()) {
			//Use flight movement

			if (this.isInWater()) {
				this.moveRelative(0.02F, in);
				this.move(MoverType.SELF, new Vec3(this.xxa, this.yya, this.zza));
				this.xxa *= 0.800000011920929F;
				this.yya *= 0.800000011920929F;
				this.zza *= 0.800000011920929F;
			} else if (this.isInLava()) {
				this.moveRelative(0.02F, in);
				this.move(MoverType.SELF, new Vec3(this.xxa, this.yya, this.zza));
				this.xxa *= 0.5F;
				this.yya *= 0.5F;
				this.zza *= 0.5F;
			} else {
				float f = 0.91F;

				if (this.onGround()) {
					f = (1f - this.level().getBlockState(BlockPos.containing(Math.floor(this.getX()), Math.floor(this.getBoundingBox().minY) - 1, Math.floor(this.getZ()))).getBlock().getFriction()) * 0.91F;
				}

				float f1 = 0.16277136F / (f * f * f);
				this.moveRelative(this.onGround() ? 0.1F * f1 : 0.02F, in);
				f = 0.91F;

				if (this.onGround()) {
					f = (1f - this.level().getBlockState(BlockPos.containing(Math.floor(this.getX()), Math.floor(this.getBoundingBox().minY) - 1, Math.floor(this.getZ()))).getBlock().getFriction()) * 0.91F;
				}

				this.move(MoverType.SELF, new Vec3(this.xxa, this.yya, this.zza));
				this.xxa *= f;
				this.yya *= f;
				this.zza *= f;
			}

			double d1 = this.xo - this.xOld;
			double d0 = this.zo - this.zOld;
			float f2 = (float) Math.sqrt(d1 * d1 + d0 * d0) * 4.0F;

			if (f2 > 1.0F) {
				f2 = 1.0F;
			}
			this.walkAnimation.update(f2, 0.4F);
		} else {
			//Use normal movement

			super.travel(in);
		}
	}


	@Override
	public boolean canAttack(LivingEntity entity) {
		if (this.isVolatile()) {
			return false;
		}
		if (super.canAttack(entity)) {
			if (entity == this.getTarget()) {
				this.canTurnVolatileOnTarget = true;
			}
			return true;
		}
		return false;
	}

	private void spawnVolatileParticles(boolean tarred) {
		final double radius = 0.3F;

		//final double cx = this.posX;
		//final double cy = this.posY + 0.35D;
		//final double cz = this.posZ;

		for (int i = 0; i < 8; i++) {
			double px = this.level().random.nextFloat() * 0.7F;
			double py = this.level().random.nextFloat() * 0.7F;
			double pz = this.level().random.nextFloat() * 0.7F;
			//Vector3d vec = new Vector3d(px, py, pz).subtract(new Vector3d(0.35F, 0.35F, 0.35F)).normalize();
			//px = cx + vec.x * radius;
			//py = cy + vec.y * radius;
			//pz = cz + vec.z * radius;
			if (tarred) {
				float tintChange = 1F / 40F * growCount;
				//BLParticles.STEAM_PURIFIER.spawn(this.world, px, py + 0.25D, pz).setRBGColorF(tintChange, tintChange,
				//		tintChange);
			} else {
			}
			//BLParticles.STEAM_PURIFIER.spawn(this.world, px, py, pz);
		}
	}

	public boolean isHiding() {
		return this.entityData.get(HIDING_STATE_DW);
	}

	public void setHiding(boolean hiding) {
		this.entityData.set(HIDING_STATE_DW, hiding);
	}

	public int getGrowTimer() {
		return entityData.get(GROW_TIMER);
	}

	public void setGrowTimer(int timer) {
		entityData.set(GROW_TIMER, timer);
	}

	public float getHidingAnimation(float partialTicks) {
		return (this.lastHidingAnimationTicks + (this.hidingAnimationTicks - this.lastHidingAnimationTicks) * partialTicks) / 12.0F;
	}

	public float getGrowthFactor(float partialTicks) {
		return prevGrowCount + (growCount - prevGrowCount) * partialTicks;
	}

	public boolean isVolatile() {
		return this.entityData.get(VOLATILE_STATE_DW);
	}

	public void setVolatile(boolean isVolatile) {
		this.entityData.set(VOLATILE_STATE_DW, isVolatile);

		if (!isVolatile) {
			Entity ridingEntity = this.getRidingEntity();
			if (ridingEntity != null) {
				this.dismountRidingEntity();
				if (!this.level().isClientSide) {
					//this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(ridingEntity));
				}
			}
		}
	}

	public int getMaxVolatileCooldown() {
		return (int) this.getAttribute(AttributeRegistry.VOLATILE_COOLDOWN_ATTRIB).getValue();
	}

	public boolean canPossess(LivingEntity entity) {
		if (entity instanceof EntitySwampHag) {
			return true;
		}
		if (entity instanceof Player) {
			return true;
			//return !this.isWearingSkullMask(entity);
		}
		return false;
	}

	public boolean isWearingSkullMask(LivingEntity entity) {
		if (entity instanceof Player player) {
			ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
			if (!helmet.isEmpty() && helmet.getItem() == Items.CARVED_PUMPKIN) {
				return true;
			}
		}
		return false;
	}

	public void setCanTurnVolatile(boolean canTurnVolatile) {
		this.canTurnVolatile = canTurnVolatile;
	}

	@Override
	public EntityDimensions getDefaultDimensions(Pose pose) {
		return dimensions;
	}

	// bandaid solution
	public void setSize(float x, float y) {
		this.dimensions.scale(x, y);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.WIGHT_DEATH.get();
	}

	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(4, new SwampHagAttackGoal(this));
		//this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));
		//this.goalSelector.addGoal(2, new TemptGoal(this, 0.5D, ItemRegistry.SAP_SPIT, true));
		//this.goalSelector.addGoal(3, new EntityWight.GeckoAvoidGoal<>(this, Player.class, PLAYER_MIN_DISTANCE, 0.65, 1));
		// rain this.goalSelector.addGoal(4, new Goal);
		this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.6));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.targetSelector.addGoal(2, new SwampHagTargetGoal<>(this, Player.class));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.goalSelector.addGoal(3, new EntityAIMoveToDirect<EntityWight>(this, this.getAttributes().getInstance(AttributeRegistry.VOLATILE_FLIGHT_SPEED_ATTRIB).getValue()) {
			@Override
			protected Vector3d getTarget() {
				if (this.entity.volatileTicks >= 20) {
					LivingEntity target = this.entity.getTarget();
					if (target != null) {
						return new Vector3d(target.xo, target.yo + target.getEyeHeight() / 2.0D, target.zo);
					} else {
						TheBetweenlands.LOGGER.info("lost target");
					}
				}
				return null;
			}
		});

		this.goalSelector.addGoal(9, new EntityAIFlyRandomly<EntityWight>(this) {
			@Override
			public boolean canUse() {
				return this.entity.isVolatile() && this.entity.volatileTicks >= 20 && this.entity.getTarget() == null && super.canUse();
			}

			@Override
			protected double getFlightSpeed() {
				return 0.1D;
			}
		});
	      /*
	       this.tasks.addTask(3, new EntityAIMoveToDirect<EntityWight>(this, this.getAttributeMap().getAttributeInstance(VOLATILE_FLIGHT_SPEED_ATTRIB).getAttributeValue()) {
            @Override
            protected Vec3d getTarget() {
                if (this.entity.volatileTicks >= 20) {
                    LivingEntity target = this.entity.getAttackTarget();
                    if (target != null) {
                        return new Vec3d(target.posX, target.posY + target.getEyeHeight() / 2.0D, target.posZ);
                    }
                }
                return null;
            }
        });
	       */
	}

	static class SwampHagAttackGoal extends MeleeAttackGoal {
		public SwampHagAttackGoal(EntityWight p_33822_) {
			super(p_33822_, 1.0D, false);
		}
	}

	static class SwampHagTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
		public SwampHagTargetGoal(EntityWight p_33832_, Class<T> p_33833_) {
			super(p_33832_, p_33833_, true);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_33034_) {
		return SoundRegistry.WIGHT_HURT.get();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.WIGHT_MOAN.get();
	}

	static class GeckoAvoidGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
		public GeckoAvoidGoal(EntityWight gecko, Class<T> p_29276_, float p_29277_, double p_29278_, double p_29279_) {
			super(gecko, p_29276_, p_29277_, p_29278_, p_29279_, EntitySelector.NO_SPECTATORS::test);
		}
	}


	// TODO: remove the folowing bandaid solutions

	public boolean isRiding() {
		return this.isPassenger();
	}

	public Entity getRidingEntity() {
		return this.getVehicle();
	}

	public void dismountRidingEntity() {
		Entity joe = this.getRidingEntity();
		this.stopRiding();
		this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(joe));
	}
}
