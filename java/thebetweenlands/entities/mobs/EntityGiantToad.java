package thebetweenlands.entities.mobs;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.model.ControlledAnimation;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.IEquippable;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityGiantToad extends EntityCreature implements IEntityBL {
	private int temper = 0;
	private int ticksOnGround = 0;
	private int strokeTicks = 0;
	private boolean prevOnGround;
	private ControlledAnimation leapingAnim = new ControlledAnimation(4);
	private ControlledAnimation swimmingAnim = new ControlledAnimation(8);
	private ControlledAnimation waterStanceAnim = new ControlledAnimation(4);
	private List<EntityLivingBase> trackedTargets = new ArrayList<EntityLivingBase>();

	public static final int DW_SWIM_STROKE = 20;
	public static final int DW_TAMED = 21;

	public EntityGiantToad(World worldObj) {
		super(worldObj);
		this.getNavigator().setCanSwim(true);
		this.getNavigator().setAvoidsWater(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.1D));
		this.tasks.addTask(2, new EntityAIWander(this, 0));
		this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(4, new EntityAILookIdle(this));
		this.setSize(1.6F, 1.5F);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60.0D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(DW_SWIM_STROKE, (byte) 0);
		dataWatcher.addObject(DW_TAMED, (byte) 0);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("Tamed", this.isTamed());
		nbt.setInteger("Temper", this.temper);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setTamed(nbt.getBoolean("Tamed"));
		this.temper = nbt.getInteger("Temper");
	}

	public boolean isTamed() {
		return this.dataWatcher.getWatchableObjectByte(DW_TAMED) == 1;
	}

	public void setTamed(boolean tamed) {
		this.dataWatcher.updateObject(DW_TAMED, tamed ? (byte)1 : (byte)0);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onUpdate() {
		prevOnGround = onGround;

		//Extend AABB so that the player doesn't suffocate in blocks
		if(this.riddenByEntity != null) {
			this.boundingBox.maxY = this.boundingBox.minY + this.height + this.riddenByEntity.height - 1F;
		}
		super.onUpdate();
		this.boundingBox.maxY = this.boundingBox.minY + this.height;

		if(this.onGround) {
			this.ticksOnGround++;
		} else {
			this.ticksOnGround = 0;
		}
		if(!this.worldObj.isRemote) {
			if(this.strokeTicks > 0) {
				this.strokeTicks--;
				this.dataWatcher.updateObject(DW_SWIM_STROKE, (byte) 1);
			} else {
				this.dataWatcher.updateObject(DW_SWIM_STROKE, (byte) 0);
			}
		}
		if (!worldObj.isRemote) {
			if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
				this.heal(1.0F);
			}
			this.setAir(20);
			PathEntity path = getNavigator().getPath();
			if (path != null && !path.isFinished() && !this.isMovementBlocked()) {
				if(this.inWater) {
					int index = path.getCurrentPathIndex();
					if (index < path.getCurrentPathLength()) {
						PathPoint nextHopSpot = path.getPathPointFromIndex(index);
						float x = (float) (nextHopSpot.xCoord - posX);
						float z = (float) (nextHopSpot.zCoord - posZ);
						float angle = (float) (Math.atan2(z, x));
						float distance = (float) Math.sqrt(x * x + z * z);
						if (distance > 1) {
							if(this.strokeTicks == 0) {
								double speedMultiplier = (Math.min(distance, 4.0F) / 4.0F * 0.8F + 0.2F);
								motionX += speedMultiplier * 0.8F * MathHelper.cos(angle);
								motionZ += speedMultiplier * 0.8F * MathHelper.sin(angle);
								this.worldObj.setEntityState(this, (byte)8);
								this.strokeTicks = 40;
							} else if(this.isCollidedHorizontally) {
								motionX += 0.01 * MathHelper.cos(angle);
								motionZ += 0.01 * MathHelper.sin(angle);
							}
						}
					}
				} else if(onGround) {
					int index = path.getCurrentPathIndex();
					if (index < path.getCurrentPathLength()) {
						PathPoint nextHopSpot = path.getPathPointFromIndex(index);
						float x = (float) (nextHopSpot.xCoord - posX);
						float z = (float) (nextHopSpot.zCoord - posZ);
						float angle = (float) (Math.atan2(z, x));
						float distance = (float) Math.sqrt(x * x + z * z);
						if (distance > 1) {
							if (this.ticksOnGround > 20) {
								double speedMultiplier = (Math.min(distance, 2.0F) / 2.0F * 0.8F + 0.2F);
								motionY += speedMultiplier * 0.6;
								motionX += speedMultiplier * 0.5 * MathHelper.cos(angle);
								motionZ += speedMultiplier * 0.5 * MathHelper.sin(angle);
							} else if(this.isCollidedHorizontally) {
								motionX += 0.01 * MathHelper.cos(angle);
								motionZ += 0.01 * MathHelper.sin(angle);
							}
						}
					}
				}
			}
			if(this.riddenByEntity != null) {
				List<EntityLivingBase> targets = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox.expand(0.6D, 0.6D, 0.6D));
				EntityLivingBase closestTarget = null;
				float lastAngDiff = 0.0F;
				for(EntityLivingBase target : targets) {
					if(target.getAITarget() == this.riddenByEntity || (this.riddenByEntity instanceof EntityLivingBase && ((EntityLivingBase)this.riddenByEntity).getAITarget() == target)) {
						float x = (float) (target.posX - posX);
						float z = (float) (target.posZ - posZ);
						float angle = (float) (Math.atan2(z, x));
						float angDiff = (float)Math.abs(this.rotationYaw % 360.0F - Math.toDegrees(angle) % 360.0F + 90) % 360.0F;
						float angDiffWrapped = Math.min(angDiff, Math.abs(360.0F - angDiff));
						//Only attack mobs in front of the toad (+-50 deg.)
						if(angDiffWrapped <= 50 && (angDiffWrapped < lastAngDiff || closestTarget == null)) {
							closestTarget = target;
							lastAngDiff = angDiffWrapped;
						}
					}
				}
				if(closestTarget != null) {
					DamageSource damageSource = new EntityDamageSourceIndirect("mob", this, this.riddenByEntity);
					float attackDamage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
					if(closestTarget.attackEntityFrom(damageSource, attackDamage)) {
						boolean doesJump = true;
						//Random chance for the target to attack back
						if(this.rand.nextInt(35) == 0) {
							if(closestTarget.attackEntityAsMob(this))
								doesJump = false;
						}
						float x = (float) (closestTarget.posX - posX);
						float z = (float) (closestTarget.posZ - posZ);
						if(doesJump && ((this.onGround && this.ticksOnGround >= 5) || (this.inWater && this.strokeTicks == 0))) {
							float angle = (float) (Math.atan2(z, x));
							if(!this.inWater)
								motionY += 0.4;
							motionX += 0.5 * MathHelper.cos(angle);
							motionZ += 0.5 * MathHelper.sin(angle);
							if(this.inWater) {
								this.strokeTicks = 20;
								this.worldObj.setEntityState(this, (byte)8);
							}
							this.onGround = false;
						}
						closestTarget.knockBack(this, attackDamage, -x, -z);
					}
				}
			}
		}

		if(this.worldObj.isRemote) {
			waterStanceAnim.updateTimer();
			if(this.inWater) {
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
			if(this.dataWatcher.getWatchableObjectByte(DW_SWIM_STROKE) == 1) {
				if(this.strokeTicks < 20)
					this.strokeTicks++;
			} else {
				this.strokeTicks = 0;
			}

			if(this.inWater && this.dataWatcher.getWatchableObjectByte(DW_SWIM_STROKE) == 1 && this.strokeTicks < 12) {
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
		return Math.min((1F - (float)Math.pow(1-this.swimmingAnim.getAnimationFraction(partialRenderTicks), 2)) * 2.5F * this.swimmingAnim.getAnimationFraction(partialRenderTicks), 1.0F);
	}

	public float getWaterStanceProgress(float partialRenderTicks) {
		return waterStanceAnim.getAnimationProgressSinSqrt(partialRenderTicks);
	}

	@Override
	public String pageName() {
		return "giantToad";
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:giantToadLiving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:giantToadHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:giantToadDeath";
	}

	@Override
	public boolean interact(EntityPlayer player) {
		if(!this.worldObj.isRemote) {
			boolean holdsEquipment = player.getHeldItem() != null && (player.getHeldItem().getItem() instanceof IEquippable || player.getHeldItem().getItem() == BLItemRegistry.amuletSlot);
			if(holdsEquipment)
				return false;
			boolean holdsWings = player.getHeldItem() != null && player.getHeldItem().getItem() == BLItemRegistry.itemsGeneric && player.getHeldItem().getItemDamage() == EnumItemGeneric.DRAGONFLY_WING.id;
			if(this.riddenByEntity == null && this.isTamed() && (!holdsWings || this.getHealth() >= this.getMaxHealth())) {
				player.mountEntity(this);
			} else if(holdsWings) {
				if(!this.isTamed()) {
					this.temper += this.rand.nextInt(4) + 1;
					if(this.temper >= 30) {
						this.worldObj.setEntityState(this, (byte)7);
						this.setTamed(true);
						this.temper = 0;
					} else {
						this.worldObj.setEntityState(this, (byte)6);
					}
					if(!player.capabilities.isCreativeMode) {
						player.getHeldItem().stackSize--;
						if(player.getHeldItem().stackSize <= 0)
							player.setCurrentItemOrArmor(0, null);
					}
				}
				if(this.getHealth() < this.getMaxHealth()){
					this.worldObj.setEntityState(this, (byte)6);
					this.heal(4.0F);
					player.getHeldItem().stackSize--;
					if(player.getHeldItem().stackSize <= 0)
						player.setCurrentItemOrArmor(0, null);
				}
			}
		}
		return false;
	}

	@Override
	public boolean canBePushed() {
		return this.riddenByEntity == null;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		Entity entity = source.getEntity();
		return this.riddenByEntity != null && this.riddenByEntity.equals(entity) ? false : super.attackEntityFrom(source, damage);
	}

	@Override
	protected boolean isMovementBlocked() {
		return this.riddenByEntity != null ? true : super.isMovementBlocked();
	}

	@Override
	public void moveEntityWithHeading(float strafing, float forward) {
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
			this.stepHeight = 1F;
			this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
			this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
			strafing = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
			forward = ((EntityLivingBase)this.riddenByEntity).moveForward;

			if (forward <= 0.0F) {
				forward *= 0.25F;
			}

			boolean onWaterSurface = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 1), MathHelper.floor_double(this.posZ)).getMaterial().isLiquid();

			if(!this.inWater || !onWaterSurface) {
				if (this.onGround && forward != 0.0F) {
					if(!this.worldObj.isRemote) {
						if(this.ticksOnGround > 4) {
							motionY += 0.5;
							motionX += forward / 1.5F * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
							motionZ += forward / 1.5F * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
							motionX += strafing / 2.0F * MathHelper.cos((float) Math.toRadians(this.rotationYaw));
							motionZ += strafing / 2.0F * MathHelper.sin((float) Math.toRadians(this.rotationYaw));
						}
					}
				}
			} else {
				if(this.motionY < 0.0F) {
					if(this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.9D), MathHelper.floor_double(this.posZ)).getMaterial().isLiquid()) {
						this.motionY *= 0.05F;
					}
				}
				if(this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 1.1D), MathHelper.floor_double(this.posZ)).getMaterial().isLiquid()) {
					this.motionY += 0.02F;
				}

				if(!this.worldObj.isRemote) {
					if(this.isCollidedHorizontally) {
						this.motionY += 0.2D;
						this.strokeTicks = 0;
					}
				}

				if(!this.worldObj.isRemote && forward > 0.0F) {
					if(forward != 0.0F && this.strokeTicks == 0) {
						motionX += forward / 1.25F * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
						motionZ += forward / 1.25F * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
						motionX += strafing / 1.25F * MathHelper.cos((float) Math.toRadians(this.rotationYaw));
						motionZ += strafing / 1.25F * MathHelper.sin((float) Math.toRadians(this.rotationYaw));
						this.worldObj.setEntityState(this, (byte)8);
						this.strokeTicks = 20;
					}
				}
			}
			if(!this.worldObj.isRemote && forward > 0.0F) {
				motionX += 0.05D * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
				motionZ += 0.05D * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
			}

			super.moveEntityWithHeading(0, 0);
		} else {
			this.stepHeight = 0.5F;
			this.jumpMovementFactor = 0.02F;
			super.moveEntityWithHeading(strafing, forward);
		}
	}

	@Override
	public double getMountedYOffset() {
		return super.getMountedYOffset();
	}

	@SideOnly(Side.CLIENT)
	protected void spawnToadParticles(boolean isHeart) {
		String s = isHeart ? "heart" : "smoke";

		for (int i = 0; i < 7; ++i) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			this.worldObj.spawnParticle(s, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleHealthUpdate(byte type) {
		if (type == 8) {
			this.strokeTicks = 0;
		} if (type == 7) {
			this.spawnToadParticles(true);
		} else if (type == 6) {
			this.spawnToadParticles(false);
			this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
		} else {
			super.handleHealthUpdate(type);
		}
	}
}
