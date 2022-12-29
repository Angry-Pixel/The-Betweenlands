package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IRingOfGatheringMinion;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.client.render.model.ControlledAnimation;
import thebetweenlands.common.entity.EntityTameableBL;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityGiantToad extends EntityTameableBL implements IEntityBL, IRingOfGatheringMinion, net.minecraft.entity.passive.IAnimals {
	private static final DataParameter<Byte> DW_SWIM_STROKE = EntityDataManager.createKey(EntityGiantToad.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> DW_TAMED = EntityDataManager.createKey(EntityGiantToad.class, DataSerializers.BOOLEAN);

	private int temper = 0;
	private int ticksOnGround = 0;
	private int strokeTicks = 0;
	private boolean prevOnGround;
	private ControlledAnimation leapingAnim = new ControlledAnimation(4);
	private ControlledAnimation swimmingAnim = new ControlledAnimation(8);
	private ControlledAnimation waterStanceAnim = new ControlledAnimation(4);

	public EntityGiantToad(World world) {
		super(world);
		this.setPathPriority(PathNodeType.WATER, 0.0F);
		this.setSize(1.6F, 1.5F);
		this.stepHeight = 1.0F;
		this.experienceValue = 8;
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 1.0D));
		this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.05D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(DW_SWIM_STROKE, (byte) 0);
		dataManager.register(DW_TAMED, false);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Temper", this.temper);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.temper = nbt.getInteger("Temper");
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public void onUpdate() {
		prevOnGround = onGround;

		//Extend AABB so that the player doesn't suffocate in blocks
		if (this.isBeingRidden()) {
			this.setEntityBoundingBox(this.getEntityBoundingBox().setMaxY(this.getEntityBoundingBox().minY + this.height + this.getControllingPassenger().height));
		}
		super.onUpdate();
		this.setEntityBoundingBox(this.getEntityBoundingBox().setMaxY(this.getEntityBoundingBox().minY + this.height));

		if (this.onGround) {
			this.ticksOnGround++;
		} else {
			this.ticksOnGround = 0;
		}
		if (!this.world.isRemote) {
			if (this.strokeTicks > 0) {
				this.strokeTicks--;
				this.dataManager.set(DW_SWIM_STROKE, (byte) 1);
			} else {
				this.dataManager.set(DW_SWIM_STROKE, (byte) 0);
			}
		}
		if (!world.isRemote) {
			if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
				this.heal(1.0F);
			}
			this.setAir(20);
			Path path = getNavigator().getPath();
			if (path != null && !path.isFinished() && !this.isMovementBlocked()) {
				if (this.inWater) {
					int index = path.getCurrentPathIndex();
					if (index < path.getCurrentPathLength()) {
						PathPoint nextHopSpot = path.getPathPointFromIndex(index);
						float x = (float) (nextHopSpot.x - posX);
						float z = (float) (nextHopSpot.z - posZ);
						float angle = (float) (Math.atan2(z, x));
						float distance = (float) Math.sqrt(x * x + z * z);
						if (distance > 1) {
							if (this.strokeTicks == 0) {
								double speedMultiplier = (Math.min(distance, 4.0F) / 4.0F * 0.8F + 0.2F);
								motionX += speedMultiplier * 0.8F * MathHelper.cos(angle);
								motionZ += speedMultiplier * 0.8F * MathHelper.sin(angle);
								this.world.setEntityState(this, (byte) 8);
								this.strokeTicks = 40;
							} else if (this.collidedHorizontally) {
								motionX += 0.01 * MathHelper.cos(angle);
								motionZ += 0.01 * MathHelper.sin(angle);
							}
						} else {
							path.incrementPathIndex();
						}
					}
				} else if (onGround) {
					int index = path.getCurrentPathIndex();
					if (index < path.getCurrentPathLength()) {
						PathPoint nextHopSpot = path.getPathPointFromIndex(index);
						float x = (float) (nextHopSpot.x - posX);
						float z = (float) (nextHopSpot.z - posZ);
						float angle = (float) (Math.atan2(z, x));
						float distance = (float) Math.sqrt(x * x + z * z);
						if (distance > 1) {
							if (this.ticksOnGround > 20) {
								double speedMultiplier = (Math.min(distance, 2.0F) / 2.0F * 0.8F + 0.2F);
								motionY += speedMultiplier * 0.6 + MathHelper.clamp((nextHopSpot.y - this.posY) / 6.0D, 0.0D, 0.3D);
								motionX += speedMultiplier * 0.5 * MathHelper.cos(angle);
								motionZ += speedMultiplier * 0.5 * MathHelper.sin(angle);
								ForgeHooks.onLivingJump(this);
							} else if (this.collidedHorizontally) {
								motionX += 0.01 * MathHelper.cos(angle);
								motionZ += 0.01 * MathHelper.sin(angle);
							}
						} else {
							path.incrementPathIndex();
						}
					}
				}
			}
			if (this.isBeingRidden()) {
				List<EntityLivingBase> targets = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(0.6D, 0.6D, 0.6D));
				EntityLivingBase closestTarget = null;
				float lastAngDiff = 0.0F;
				Entity controllingPassenger = this.getControllingPassenger();
				for (EntityLivingBase target : targets) {
					if (target.getRevengeTarget() == controllingPassenger || (controllingPassenger instanceof EntityLivingBase && ((EntityLivingBase) controllingPassenger).getRevengeTarget() == target)) {
						float x = (float) (target.posX - posX);
						float z = (float) (target.posZ - posZ);
						float angle = (float) (Math.atan2(z, x));
						float angDiff = (float) Math.abs(this.rotationYaw % 360.0F - Math.toDegrees(angle) % 360.0F + 90) % 360.0F;
						float angDiffWrapped = Math.min(angDiff, Math.abs(360.0F - angDiff));
						//Only attack mobs in front of the toad (+-50 deg.)
						if (angDiffWrapped <= 50 && (angDiffWrapped < lastAngDiff || closestTarget == null)) {
							closestTarget = target;
							lastAngDiff = angDiffWrapped;
						}
					}
				}
				if (closestTarget != null) {
					DamageSource damageSource = new EntityDamageSourceIndirect("mob", this, controllingPassenger);
					float attackDamage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
					if (closestTarget.attackEntityFrom(damageSource, attackDamage)) {
						boolean doesJump = true;
						//Random chance for the target to attack back
						if (this.rand.nextInt(35) == 0) {
							if (closestTarget.attackEntityAsMob(this))
								doesJump = false;
						}
						float x = (float) (closestTarget.posX - posX);
						float z = (float) (closestTarget.posZ - posZ);
						if (doesJump && ((this.onGround && this.ticksOnGround >= 5) || (this.inWater && this.strokeTicks == 0))) {
							float angle = (float) (Math.atan2(z, x));
							if (!this.inWater)
								motionY += 0.4;
							motionX += 0.5 * MathHelper.cos(angle);
							motionZ += 0.5 * MathHelper.sin(angle);
							if (this.inWater) {
								this.strokeTicks = 20;
								this.world.setEntityState(this, (byte) 8);
							} else
								ForgeHooks.onLivingJump(this);
							this.onGround = false;
						}
						closestTarget.knockBack(this, attackDamage / 2.5F, -x, -z);
					}
				}
			}
		}

		if (this.world.isRemote) {
			waterStanceAnim.updateTimer();
			if (this.inWater) {
				waterStanceAnim.increaseTimer();
			} else {
				waterStanceAnim.decreaseTimer();
			}

			leapingAnim.updateTimer();
			if (this.inWater || onGround || prevOnGround) {
				leapingAnim.decreaseTimer();
			} else {
				leapingAnim.increaseTimer();
			}

			this.swimmingAnim.updateTimer();
			if (this.dataManager.get(DW_SWIM_STROKE) == 1) {
				if (this.strokeTicks < 20)
					this.strokeTicks++;
			} else {
				this.strokeTicks = 0;
			}

			if (this.inWater && this.dataManager.get(DW_SWIM_STROKE) == 1 && this.strokeTicks < 12) {
				this.swimmingAnim.increaseTimer();
			} else {
				this.swimmingAnim.decreaseTimer();
			}
		}
	}

	public float getLeapProgress(float partialRenderTicks) {
		return leapingAnim.getAnimationProgressSinSqrt(partialRenderTicks);
	}

	public float getSwimProgress(float partialRenderTicks) {
		return Math.min((1F - (float) Math.pow(1 - this.swimmingAnim.getAnimationFraction(partialRenderTicks), 2)) * 2.5F * this.swimmingAnim.getAnimationFraction(partialRenderTicks), 1.0F);
	}

	public float getWaterStanceProgress(float partialRenderTicks) {
		return waterStanceAnim.getAnimationProgressSinSqrt(partialRenderTicks);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.GIANT_TOAD_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.GIANT_TOAD_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.GIANT_TOAD_DEATH;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		boolean holdsEquipment = hand == EnumHand.MAIN_HAND && !player.getHeldItem(hand).isEmpty() && (player.getHeldItem(hand).getItem() instanceof IEquippable || player.getHeldItem(hand).getItem() == ItemRegistry.AMULET_SLOT);
		if (holdsEquipment)
			return true;
		boolean holdsWings = EnumItemMisc.DRAGONFLY_WING.isItemOf(player.getHeldItem(hand));
		if (!this.isBeingRidden() && this.isTamed() && (!holdsWings || this.getHealth() >= this.getMaxHealth()) && !player.isSneaking() && !this.world.isRemote) {
			player.startRiding(this);
			return true;
		} else if (holdsWings) {
			if (!this.isTamed()) {
				if (!this.world.isRemote) {
					this.temper += this.rand.nextInt(4) + 1;
					if (this.temper >= 30 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
						this.world.setEntityState(this, (byte) 7);
						this.setTamedBy(player);
						this.temper = 0;
					} else {
						this.world.setEntityState(this, (byte) 6);
					}
					if (!player.capabilities.isCreativeMode) {
						player.getHeldItem(hand).shrink(1);
						if (player.getHeldItem(hand).getCount() <= 0)
							player.setHeldItem(hand, ItemStack.EMPTY);
					}
				}
				return true;
			}
			if (this.getHealth() < this.getMaxHealth()) {
				if (!this.world.isRemote) {
					this.world.setEntityState(this, (byte) 6);
					this.heal(4.0F);
					player.getHeldItem(hand).shrink(1);
					if (player.getHeldItem(hand).getCount() <= 0)
						player.setHeldItem(hand, ItemStack.EMPTY);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canBePushed() {
		return !this.isBeingRidden();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		Entity entity = source.getTrueSource();
		return !(this.isBeingRidden() && this.getControllingPassenger() != null && this.getControllingPassenger().equals(entity)) && super.attackEntityFrom(source, damage);
	}

	@Override
	protected boolean isMovementBlocked() {
		return this.isBeingRidden() || super.isMovementBlocked();
	}

	@Override
	public void travel(float strafing,float up,  float forward) {
		Entity controllingPassenger = this.getControllingPassenger();
		if (this.isBeingRidden() && controllingPassenger != null && controllingPassenger instanceof EntityLivingBase) {
			this.prevRotationYaw = this.rotationYaw = controllingPassenger.rotationYaw;
			this.rotationPitch = controllingPassenger.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
			strafing = ((EntityLivingBase) controllingPassenger).moveStrafing * 0.5F;
			forward = ((EntityLivingBase) controllingPassenger).moveForward;

			if (forward <= 0.0F) {
				forward *= 0.25F;
			}

			boolean onWaterSurface = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY + 1), MathHelper.floor(this.posZ))).getMaterial().isLiquid();

			if (!this.inWater || !onWaterSurface) {
				if (this.onGround && forward != 0.0F) {
					if (!this.world.isRemote) {
						if (this.ticksOnGround > 4) {
							motionY += 0.5;
							motionX += forward / 1.5F * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
							motionZ += forward / 1.5F * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
							motionX += strafing / 2.0F * MathHelper.cos((float) Math.toRadians(this.rotationYaw));
							motionZ += strafing / 2.0F * MathHelper.sin((float) Math.toRadians(this.rotationYaw));
							ForgeHooks.onLivingJump(this);
						}
					}
				}
			} else {
				if (this.motionY < 0.0F) {
					if (this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY + 0.9D), MathHelper.floor(this.posZ))).getMaterial().isLiquid()) {
						this.motionY *= 0.05F;
					}
				}
				if (this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY + 1.1D), MathHelper.floor(this.posZ))).getMaterial().isLiquid()) {
					this.motionY += 0.02F;
				}

				if (!this.world.isRemote) {
					if (this.collidedHorizontally) {
						this.motionY += 0.2D;
						this.strokeTicks = 0;
					}
				}

				if (!this.world.isRemote && forward > 0.0F) {
					if (forward != 0.0F && this.strokeTicks == 0) {
						motionX += forward / 1.25F * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
						motionZ += forward / 1.25F * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
						motionX += strafing / 1.25F * MathHelper.cos((float) Math.toRadians(this.rotationYaw));
						motionZ += strafing / 1.25F * MathHelper.sin((float) Math.toRadians(this.rotationYaw));
						this.world.setEntityState(this, (byte) 8);
						this.strokeTicks = 20;
					}
				}
			}
			if (!this.world.isRemote && forward > 0.0F) {
				motionX += 0.05D * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
				motionZ += 0.05D * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
			}

			super.travel(0,  0,0);
		} else {
			this.jumpMovementFactor = 0.02F;
			super.travel(strafing, up, forward);
		}
	}

	@SideOnly(Side.CLIENT)
	protected void spawnToadParticles(boolean isHeart) {
		EnumParticleTypes enumparticletypes = isHeart ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL;
		for (int i = 0; i < 7; ++i) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		if (id == 8) {
			this.strokeTicks = 0;
		}
		if (id == 7) {
			this.spawnToadParticles(true);
		} else if (id == 6) {
			this.spawnToadParticles(false);
			this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
		} else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
	}

	@Override
	public boolean canPassengerSteer() {
		//TODO: onGround only updates properly if this return false??
		return false;
	}
	
	@Override
	public void fall(float distance, float damageMultiplier) { 
		distance = Math.max(0, distance - 6.0F);
		super.fall(distance, damageMultiplier);
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.TOAD;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}
	
	@Override
	public NBTTagCompound returnToRing(UUID userId) {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean returnFromRing(Entity user, NBTTagCompound nbt) {
		double prevX = this.posX;
		double prevY = this.posY;
		double prevZ = this.posZ;
		float prevYaw = this.rotationYaw;
		float prevPitch = this.rotationPitch;
		this.readFromNBT(nbt);
		this.setLocationAndAngles(prevX, prevY, prevZ, prevYaw, prevPitch);
		if(!this.isEntityAlive()) {
			//Revivd by animator
			this.setHealth(this.getMaxHealth());
		}
		this.world.spawnEntity(this);
		return true;
	}

	@Override
	public boolean shouldReturnOnUnload(boolean isOwnerLoggedIn) {
		return IRingOfGatheringMinion.super.shouldReturnOnUnload(isOwnerLoggedIn) && !this.isSitting();
	}

	@Override
	public UUID getRingOwnerId() {
		return this.getOwnerId();
	}
}

