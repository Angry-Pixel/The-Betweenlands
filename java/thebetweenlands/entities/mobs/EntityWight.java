package thebetweenlands.entities.mobs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.items.BLItemRegistry;

public class EntityWight extends EntityMob implements IEntityBL {
	public static final IAttribute VOLATILE_COOLDOWN_ATTRIB = (new RangedAttribute("bl.volatileCooldown", 300.0D, 10.0D, Integer.MAX_VALUE)).setDescription("Volatile Cooldown");
	public static final IAttribute VOLATILE_FLIGHT_SPEED_ATTRIB = (new RangedAttribute("bl.volatileFlightSpeed", 0.25D, 0.0D, 5.0D)).setDescription("Volatile Flight Speed");
	public static final IAttribute VOLATILE_LENGTH_ATTRIB = (new RangedAttribute("bl.volatileLength", 200.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Volatile Length");
	public static final IAttribute VOLATILE_MAX_DAMAGE_ATTRIB = (new RangedAttribute("bl.volatileMaxDamage", 30.0D, 0.0D, Double.MAX_VALUE)).setDescription("Volatile Max Damage");

	public static final int ATTACK_STATE_DW = 20;
	public static final int ANIMATION_STATE_DW = 21;
	public static final int VOLATILE_STATE_DW = 22;

	private EntityAIAttackOnCollide meleeAttack = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.5D, false);
	private EntityPlayer previousTarget;
	private boolean updateHasBeenSeen = false;

	//Volatile stuff
	private int volatileCooldown = (int) VOLATILE_COOLDOWN_ATTRIB.getDefaultValue() + 20;
	private int volatileProgress = 0;
	private double waypointX;
	private double waypointY;
	private double waypointZ;
	private float volatileReceivedDamage = 0.0F;

	public EntityWight(World world) {
		super(world);
		setSize(0.7F, 2.2F);
		getNavigator().setCanSwim(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, meleeAttack);
		tasks.addTask(2, new EntityAIWander(this, 0.3D));
		tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1D, false));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAILeapAtTarget(this, 0.5F));
		//this.volatileCooldown = 10;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(ATTACK_STATE_DW, (byte) 0);
		dataWatcher.addObject(ANIMATION_STATE_DW, (float) 1);
		dataWatcher.addObject(VOLATILE_STATE_DW, (byte) 0);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(80.0D);

		this.getAttributeMap().registerAttribute(VOLATILE_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_LENGTH_ATTRIB);
		this.getAttributeMap().registerAttribute(VOLATILE_MAX_DAMAGE_ATTRIB);
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
		if (rand.nextBoolean())
			return "thebetweenlands:wightHurt1";
		else
			return "thebetweenlands:wightHurt2";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:wightDeath";
	}

	@Override
	public void onUpdate() {
		EntityPlayer target = worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);

		if(target != null && !target.isSneaking() && !(target.getCurrentArmor(3) != null && target.getCurrentArmor(3).getItem() == BLItemRegistry.skullMask)) {
			setTargetSpotted(target, true);
		}

		if(target != null && target != previousTarget && target.isSneaking() && !(target.getCurrentArmor(3) != null && target.getCurrentArmor(3).getItem() == BLItemRegistry.skullMask)) {
			setTargetSpotted(target, false);
		}

		if((target == null && previousTarget != null) || (target != null && target.getCurrentArmor(3) != null && target.getCurrentArmor(3).getItem() == BLItemRegistry.skullMask)) {
			setTargetSpotted(target, false);
		}

		if (!worldObj.isRemote && getAttackTarget() != null) {
			dataWatcher.updateObject(ATTACK_STATE_DW, Byte.valueOf((byte) 1));

			if(!this.isVolatile()) {
				this.volatileCooldown--;
				if(this.volatileCooldown <= 0) {
					this.setVolatile(true);
					this.volatileCooldown = this.getVolatileCooldown() + this.worldObj.rand.nextInt(this.getVolatileCooldown()) + 20;
					this.volatileProgress = 0;
				}
			}
		}

		if(this.isVolatile()) {
			if(!this.worldObj.isRemote) {
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
				if (getAttackTarget() != null && getDistanceToEntity(getAttackTarget()) < 2) {
					onCollideWithEntity(getAttackTarget());
				}
			} else {
				this.spawnVolatileParticles();
			}
			this.setSize(0.7F, 0.7F);
		} else {
			this.setSize(0.7F, 2.2F);
		}

		if (!worldObj.isRemote && getAttackTarget() == null) {
			dataWatcher.updateObject(ATTACK_STATE_DW, Byte.valueOf((byte) 0));
		}

		super.onUpdate();
	}

	public void onCollideWithEntity(EntityLivingBase entity) {
		if (!worldObj.isRemote && this.isVolatile()) {
			if (entity.riddenByEntity == null) {
				mountEntity(entity);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnVolatileParticles() {
		float xx = (float) (this.posX + this.motionX);
		float yy = (float) (this.posY + rand.nextFloat() * 0.7F + this.motionY);
		float zz = (float) (this.posZ + this.motionZ);
		float fixedOffset = 0.35F;
		float randomOffset = rand.nextFloat() * 0.7F - 0.35F;
		BLParticle.STEAM_PURIFIER.spawn(this.worldObj, (double) (xx - fixedOffset), (double) yy - fixedOffset, (double) (zz + randomOffset), 0.0D, 0.0D, 0.0D, 0);
		BLParticle.STEAM_PURIFIER.spawn(this.worldObj, (double) (xx + fixedOffset), (double) yy - fixedOffset, (double) (zz + randomOffset), 0.0D, 0.0D, 0.0D, 0);
		BLParticle.STEAM_PURIFIER.spawn(this.worldObj, (double) (xx + randomOffset), (double) yy + randomOffset, (double) (zz - fixedOffset), 0.0D, 0.0D, 0.0D, 0);
		BLParticle.STEAM_PURIFIER.spawn(this.worldObj, (double) (xx + randomOffset), (double) yy - randomOffset, (double) (zz + fixedOffset), 0.0D, 0.0D, 0.0D, 0);
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
			if (getAnimation() > 0)
				setAnimation(getAnimation() - 0.1F);

		} else {
			if (updateHasBeenSeen) {
				updateHasBeenSeen = false;
				setAttackTarget(null);
				tasks.removeTask(meleeAttack);
			}
			if (getAnimation() < 1)
				setAnimation(getAnimation() + 0.1F);
			if (getAnimation() == 0 && previousTarget != null) {
				previousTarget = null;
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
		entityDropItem(new ItemStack(BLItemRegistry.wightsHeart), 0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();

			if(this.isVolatile() && this.ridingEntity != null) {
				this.volatileReceivedDamage += damage;
				if(this.volatileReceivedDamage >= this.getEntityAttribute(VOLATILE_MAX_DAMAGE_ATTRIB).getAttributeValue()) {
					this.setVolatile(false);
				}
			}

			//TODO: Move this to item
			ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
			if (heldItem != null)
				if (heldItem.getItem() == BLItemRegistry.wightsBane) {
					return super.attackEntityFrom(source, this.getHealth());
				}
		}
		return super.attackEntityFrom(source, damage);
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
			return -2;
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
