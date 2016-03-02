package thebetweenlands.entities.mobs;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.projectiles.EntityVolatileSoul;
import thebetweenlands.items.BLItemRegistry;

public class EntityWight extends EntityMob implements IEntityBL {
	public static final IAttribute VOLATILE_HEALTH_START_ATTRIB = (new RangedAttribute("bl.volatileHealthStart", 1.0D, 0.0D, 1.0D)).setDescription("Volatile Health Percentage Start");
	public static final IAttribute VOLATILE_COOLDOWN_ATTRIB = (new RangedAttribute("bl.volatileCooldown", 400.0D, 10.0D, Integer.MAX_VALUE)).setDescription("Volatile Cooldown");
	public static final IAttribute VOLATILE_FLIGHT_SPEED_ATTRIB = (new RangedAttribute("bl.volatileFlightSpeed", 0.25D, 0.0D, 5.0D)).setDescription("Volatile Flight Speed");
	public static final IAttribute VOLATILE_LENGTH_ATTRIB = (new RangedAttribute("bl.volatileLength", 600.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Volatile Length");
	public static final IAttribute VOLATILE_MAX_DAMAGE_ATTRIB = (new RangedAttribute("bl.volatileMaxDamage", 30.0D, 0.0D, Double.MAX_VALUE)).setDescription("Volatile Max Damage");

	public static final int ATTACK_STATE_DW = 20;
	public static final int ANIMATION_STATE_DW = 21;
	public static final int VOLATILE_STATE_DW = 22;

	private EntityAIAttackOnCollide meleeAttack = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.5D, false);
	private EntityPlayer previousTarget;
	private boolean updateHasBeenSeen = false;

	//Volatile stuff
	private int volatileCooldown = (int) VOLATILE_COOLDOWN_ATTRIB.getDefaultValue() / 2 + 20;
	private int volatileProgress = 0;
	private float volatileReceivedDamage = 0.0F;
	private double waypointX;
	private double waypointY;
	private double waypointZ;
	private boolean prevVolatile = false;
	private boolean isLocationGuard = false;

	public EntityWight(World world) {
		super(world);
		setSize(0.7F, 2.2F);
		this.getNavigator().setCanSwim(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, this.meleeAttack);
		this.tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWander(this, 0.3D));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(5, new EntityAILookIdle(this));
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(ATTACK_STATE_DW, (byte) 0);
		this.dataWatcher.addObject(ANIMATION_STATE_DW, (float) 1);
		this.dataWatcher.addObject(VOLATILE_STATE_DW, (byte) 0);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(76.0D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(80.0D);

		this.getAttributeMap().registerAttribute(VOLATILE_HEALTH_START_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_LENGTH_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_MAX_DAMAGE_ATTRIB);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("volatileState", this.getDataWatcher().getWatchableObjectByte(VOLATILE_STATE_DW));
		nbt.setInteger("volatileCooldown", this.volatileCooldown);
		nbt.setInteger("volatileProgress", this.volatileProgress);
		nbt.setFloat("volatileReceivedDamage", this.volatileReceivedDamage);
		nbt.setBoolean("ignoreMask", this.isLocationGuard);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.getDataWatcher().updateObject(VOLATILE_STATE_DW, nbt.getByte("volatileState"));
		this.volatileCooldown = nbt.getInteger("volatileCooldown");
		this.volatileProgress = nbt.getInteger("volatileProgress");
		this.volatileReceivedDamage = nbt.getFloat("volatileReceivedDamage");
		this.isLocationGuard = nbt.getBoolean("ignoreMask");
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
	}

	@Override
	protected String getLivingSound() {
		int randomSound = worldObj.rand.nextInt(4) + 1;
		return "thebetweenlands:wightMoan" + randomSound;
	}

	@Override
	protected String getHurtSound() {
		if (this.rand.nextBoolean()) {
			return "thebetweenlands:wightHurt1";
		} else {
			return "thebetweenlands:wightHurt2";
		}
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:wightDeath";
	}

	public boolean canPossess(EntityLivingBase entity) {
		return this.isLocationGuard || !(entity instanceof EntityPlayer && ((EntityPlayer)entity).getCurrentArmor(3) != null && ((EntityPlayer)entity).getCurrentArmor(3).getItem() == BLItemRegistry.skullMask);
	}

	@Override
	public void onUpdate() {
		EntityPlayer target = this.getAttackTarget() instanceof EntityPlayer ? (EntityPlayer)this.getAttackTarget() : null;
		if(target == null || target.isDead || target.getDistanceToEntity(this) > this.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue()) {
			target = this.worldObj.getClosestVulnerablePlayerToEntity(this, 25.0D);
		}

		if(target != null && !target.isSneaking()) {
			this.setTargetSpotted(target, true);
		}

		if(target != null && target != this.previousTarget && target.isSneaking()) {
			this.setTargetSpotted(target, false);
		}

		if(target == null && this.previousTarget != null) {
			this.setTargetSpotted(target, false);
		}

		if(this.getAttackTarget() != null) {
			if (getAnimation() > 0)
				setAnimation(getAnimation() - 0.1F);
		} else {
			if (getAnimation() < 1)
				setAnimation(getAnimation() + 0.1F);
			if (getAnimation() == 0 && previousTarget != null) {
				previousTarget = null;
			}
		}

		if (!this.worldObj.isRemote && getAttackTarget() != null) {
			this.dataWatcher.updateObject(ATTACK_STATE_DW, Byte.valueOf((byte) 1));

			if(!this.isVolatile() && this.canPossess(this.getAttackTarget())) {
				if(this.volatileCooldown > 0)
					this.volatileCooldown--;
				if(this.getHealth() <= this.getMaxHealth() * this.getEntityAttribute(VOLATILE_HEALTH_START_ATTRIB).getAttributeValue() && this.volatileCooldown <= 0) {
					this.setVolatile(true);
					this.volatileCooldown = this.getVolatileCooldown() + this.worldObj.rand.nextInt(this.getVolatileCooldown()) + 20;
					this.volatileProgress = 0;
				}
			} else if(this.isVolatile() && !this.canPossess(this.getAttackTarget())) {
				this.setVolatile(false);
			}
		}

		if(this.isVolatile()) {
			if(this.getAttackTarget() != null) {
				if(this.ridingEntity == null) {
					double dx = this.getAttackTarget().posX - this.posX;
					double dz = this.getAttackTarget().posZ - this.posZ;
					double dy;
					if (this.getAttackTarget() instanceof EntityLivingBase) {
						EntityLivingBase entitylivingbase = (EntityLivingBase)this.getAttackTarget();
						dy = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
					} else {
						dy = (this.getAttackTarget().boundingBox.minY + this.getAttackTarget().boundingBox.maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
					}
					double dist = (double)MathHelper.sqrt_double(dx * dx + dz * dz);
					float yaw = (float)(Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
					float pitch = (float)(-(Math.atan2(dy, dist) * 180.0D / Math.PI));
					this.setRotation(yaw, pitch);
					if(this.worldObj.isRemote) {
						this.setRotationYawHead(yaw);
					}
				} else {
					this.setRotation(this.ridingEntity.rotationYaw, 0);
					if(this.worldObj.isRemote) {
						this.setRotationYawHead(this.ridingEntity.rotationYaw);
					}
				}
			}
			if(!this.worldObj.isRemote) {
				if(this.ridingEntity != null && this.ridingEntity.isDead) {
					this.dismountEntity(this.ridingEntity);
				}
				if(this.volatileProgress < this.getEntityAttribute(VOLATILE_LENGTH_ATTRIB).getAttributeValue()) {
					this.volatileProgress++;
				}
				if(this.volatileProgress >= 20) {
					if(this.getAttackTarget() != null) {
						this.waypointX = this.getAttackTarget().posX;
						this.waypointY = this.getAttackTarget().boundingBox.minY + (this.getAttackTarget().boundingBox.maxY - this.getAttackTarget().boundingBox.minY) / 2.0D;
						this.waypointZ = this.getAttackTarget().posZ;
					} else {
						this.waypointX = this.posX;
						this.waypointY = this.posY;
						this.waypointZ = this.posZ;
					}
				} else {
					this.waypointX = this.posX;
					this.waypointY = this.posY + 3.0D;
					this.waypointZ = this.posZ;
				}
				if (getAttackTarget() != null && getDistanceToEntity(getAttackTarget()) < 1) {
					onCollideWithEntity(getAttackTarget());
				}
				if(this.ridingEntity != null) {
					if(this.ticksExisted % 30 == 0) {
						List<EntityVolatileSoul> existingSouls = this.worldObj.getEntitiesWithinAABB(EntityVolatileSoul.class, this.boundingBox.expand(16.0D, 16.0D, 16.0D));
						if(existingSouls.size() < 16) {
							EntityVolatileSoul soul = new EntityVolatileSoul(this.worldObj);
							float mx = this.worldObj.rand.nextFloat() - 0.5F;
							float my = this.worldObj.rand.nextFloat() / 2.0F;
							float mz = this.worldObj.rand.nextFloat() - 0.5F;
							Vec3 dir = Vec3.createVectorHelper(mx, my, mz).normalize();
							soul.setOwner(this.getUniqueID().toString());
							soul.setLocationAndAngles(this.posX + dir.xCoord * 0.5D, this.posY + dir.yCoord * 1.5D, this.posZ + dir.zCoord * 0.5D, 0, 0);
							soul.setThrowableHeading(mx * 2.0D, my * 2.0D, mz * 2.0D, 1.0F, 1.0F);
							this.worldObj.spawnEntityInWorld(soul);
						}
					}
				}
			} else {
				if(this.ridingEntity == null || this.ticksExisted % 4 == 0) {
					this.spawnVolatileParticles();
				}
			}
			this.setSize(0.7F, 0.7F);
		} else {
			this.setSize(0.7F, 2.2F);
		}

		if (!this.worldObj.isRemote && getAttackTarget() == null) {
			this.dataWatcher.updateObject(ATTACK_STATE_DW, Byte.valueOf((byte) 0));
		}

		if(this.prevVolatile != this.isVolatile()) {
			if(this.worldObj.isRemote) {
				for(int i = 0; i < 80; i++) {
					double px = this.posX + this.worldObj.rand.nextFloat() * 0.7F;
					double py = this.posY + this.worldObj.rand.nextFloat() * 2.2F;
					double pz = this.posZ + this.worldObj.rand.nextFloat() * 0.7F;
					Vec3 vec = Vec3.createVectorHelper(px, py, pz).subtract(Vec3.createVectorHelper(this.posX + 0.35F, this.posY + 1.1F, this.posZ + 0.35F)).normalize();
					BLParticle.SWAMP_SMOKE.spawn(this.worldObj, px, py, pz, vec.xCoord * 0.25F, vec.yCoord * 0.25F, vec.zCoord * 0.25F, 1.0F);
				}
			}
			this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "thebetweenlands:druidTeleport", 1.6F, 1.0F);
		}
		this.prevVolatile = this.isVolatile();

		super.onUpdate();
	}

	public void onCollideWithEntity(EntityLivingBase entity) {
		if (!this.worldObj.isRemote && this.isVolatile() && entity == this.getAttackTarget()) {
			if (entity.riddenByEntity == null) {
				mountEntity(entity);
				this.volatileProgress = 20;
			}
		}
	}

	public void setLocationGuard(boolean locationGuard) {
		this.isLocationGuard = locationGuard;
	}

	public boolean isLocationGuard() {
		return this.isLocationGuard;
	}
	
	@SideOnly(Side.CLIENT)
	private void spawnVolatileParticles() {
		final double radius = 0.3F;
		final double cx = this.posX;
		final double cy = this.posY + 0.35D;
		final double cz = this.posZ;
		for(int i = 0; i < 8; i++) {
			double px = this.worldObj.rand.nextFloat() * 0.7F;
			double py = this.worldObj.rand.nextFloat() * 0.7F;
			double pz = this.worldObj.rand.nextFloat() * 0.7F;
			Vec3 vec = Vec3.createVectorHelper(px, py, pz).subtract(Vec3.createVectorHelper(0.35F, 0.35F, 0.35F)).normalize();
			px = cx + vec.xCoord * radius;
			py = cy + vec.yCoord * radius;
			pz = cz + vec.zCoord * radius;
			BLParticle.STEAM_PURIFIER.spawn(this.worldObj, px, py, pz, 0.0D, 0.0D, 0.0D, 0);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return !this.isVolatile() ? super.getShadowSize() : 0.0F;
	}

	@Override
	protected void fall(float dst) {
		if(!this.isVolatile()) {
			super.fall(dst);
		}
	}

	@Override
	protected void updateFallState(double dst, boolean onGround) {
		if(!this.isVolatile()) {
			super.updateFallState(dst, onGround);
		}
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if(this.isVolatile()) {
			if (this.isInWater()) {
				this.moveFlying(strafe, forward, 0.02F);
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.800000011920929D;
				this.motionY *= 0.800000011920929D;
				this.motionZ *= 0.800000011920929D;
			} else if (this.handleLavaMovement()) {
				this.moveFlying(strafe, forward, 0.02F);
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
			} else {
				float friction = 0.91F;

				if (this.onGround) {
					friction = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
				}

				float groundFriction = 0.16277136F / (friction * friction * friction);
				this.moveFlying(strafe, forward, this.onGround ? 0.1F * groundFriction : 0.02F);
				friction = 0.91F;

				if (this.onGround) {
					friction = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= (double)friction;
				this.motionY *= (double)friction;
				this.motionZ *= (double)friction;
			}

			this.prevLimbSwingAmount = this.limbSwingAmount;
			double dx = this.posX - this.prevPosX;
			double dz = this.posZ - this.prevPosZ;
			float distanceMoved = MathHelper.sqrt_double(dx * dx + dz * dz) * 4.0F;

			if (distanceMoved > 1.0F) {
				distanceMoved = 1.0F;
			}

			this.limbSwingAmount += (distanceMoved - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			super.moveEntityWithHeading(strafe, forward);
		}
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();

		if(this.isVolatile()) {
			this.noClip = true;

			if (this.worldObj.isRemote) {
				return;
			}

			if(this.volatileProgress >= this.getEntityAttribute(VOLATILE_LENGTH_ATTRIB).getAttributeValue()) {
				this.motionY -= 0.075D;
				if(this.onGround) {
					this.setVolatile(false);
				}
			} else {
				double dx = this.waypointX - this.posX;
				double dy = this.waypointY - this.posY;
				double dz = this.waypointZ - this.posZ;
				double dist = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
				double speed = this.getEntityAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB).getAttributeValue();
				if(dist <= speed) {
					this.waypointX = this.posX;
					this.waypointY = this.posY;
					this.waypointZ = this.posZ;
				} else {
					this.motionX = dx / dist * speed;
					this.motionY = dy / dist * speed;
					this.motionZ = dz / dist * speed;
				}
			}
		} else {
			this.noClip = false;
		}
	}

	private void setTargetSpotted(EntityPlayer target, boolean hasBeenSeen) {
		if (hasBeenSeen) {
			if (!updateHasBeenSeen) {
				updateHasBeenSeen = true;
				tasks.addTask(1, meleeAttack);
				setAttackTarget(target);
				previousTarget = target;
			}
		} else {
			if (updateHasBeenSeen) {
				updateHasBeenSeen = false;
				setAttackTarget(null);
				tasks.removeTask(meleeAttack);
			}
		}
	}

	private void setAnimation(float progress) {
		dataWatcher.updateObject(ANIMATION_STATE_DW, progress);
	}

	public float getAnimation() {
		return dataWatcher.getWatchableObjectFloat(ANIMATION_STATE_DW);
	}

	@Override
	public boolean isEntityInvulnerable() {
		return dataWatcher.getWatchableObjectByte(ATTACK_STATE_DW) == 0;
	}

	@Override
	public boolean canBePushed() {
		return dataWatcher.getWatchableObjectByte(ATTACK_STATE_DW) == 1;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		if(!this.isLocationGuard)
			entityDropItem(new ItemStack(BLItemRegistry.wightsHeart), 0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(this.isVolatile() && source == DamageSource.inWall) {
			return false;
		}
		float prevHealth = this.getHealth();
		boolean ret = super.attackEntityFrom(source, damage);
		float dealtDamage = prevHealth - this.getHealth();
		if(this.isVolatile() && this.ridingEntity != null) {
			this.volatileReceivedDamage += dealtDamage;
			if(this.volatileReceivedDamage >= this.getEntityAttribute(VOLATILE_MAX_DAMAGE_ATTRIB).getAttributeValue()) {
				this.setVolatile(false);
			}
		}
		return ret;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if(this.isVolatile()) {
			return false;
		}
		return super.attackEntityAsMob(entity);
	}

	@Override
	public String pageName() {
		return "wight";
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public double getYOffset() {
		if(this.ridingEntity != null && this.ridingEntity instanceof EntityPlayer && this.worldObj.isRemote) {
			return -1.7D;
		}
		return this.yOffset;
	}

	public int getVolatileCooldown() {
		return (int) this.getEntityAttribute(VOLATILE_COOLDOWN_ATTRIB).getAttributeValue();
	}

	public void setVolatile(boolean v) {
		this.dataWatcher.updateObject(VOLATILE_STATE_DW, (byte)(v ? 1 : 0));
		this.volatileProgress = 0;
		this.volatileReceivedDamage = 0.0F;
		if(!v && this.ridingEntity != null) {
			this.dismountEntity(this.ridingEntity);
			this.ridingEntity = null;
		}
	}

	public boolean isVolatile() {
		return this.dataWatcher.getWatchableObjectByte(VOLATILE_STATE_DW) == 1;
	}
}
