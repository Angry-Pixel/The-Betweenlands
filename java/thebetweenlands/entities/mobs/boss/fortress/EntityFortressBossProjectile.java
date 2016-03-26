package thebetweenlands.entities.mobs.boss.fortress;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;

public class EntityFortressBossProjectile extends Entity implements IProjectile {
	public static final int OWNER_DW = 18;
	public static final int DEFLECTION_STATE_DW = 19;

	private String throwerUUID = "";
	private int ticksInAir = 0;
	private boolean canDismount = false;

	public EntityFortressBossProjectile(World world) {
		super(world);
		this.setSize(0.65F, 0.65F);
		this.noClip = true;
	}

	public EntityFortressBossProjectile(World world, Entity source) {
		this(world);
		if(source != null) {
			this.setOwner(source.getUniqueID().toString());
			this.setThrower(source.getUniqueID().toString());
		}
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(OWNER_DW, "");
		this.dataWatcher.addObject(DEFLECTION_STATE_DW, (byte)0);
	}

	public void setDeflectable(boolean deflectable) {
		this.dataWatcher.updateObject(DEFLECTION_STATE_DW, (byte)(deflectable ? 1 : 0));
	}

	public boolean isDeflectable() {
		return this.dataWatcher.getWatchableObjectByte(DEFLECTION_STATE_DW) == (byte) 1;
	}

	public void setOwner(String ownerUUID) {
		this.dataWatcher.updateObject(OWNER_DW, ownerUUID);
	}

	public String getOwnerUUID() {
		return this.dataWatcher.getWatchableObjectString(OWNER_DW);
	}

	public Entity getOwner() {
		try {
			UUID uuid = UUID.fromString(this.getOwnerUUID());
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	public void setThrower(String throwerUUID) {
		this.throwerUUID = throwerUUID;
	}


	public Entity getThrower() {
		try {
			UUID uuid = UUID.fromString(this.throwerUUID);
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	private Entity getEntityByUUID(UUID p_152378_1_) {
		for (int i = 0; i < this.worldObj.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.worldObj.loadedEntityList.get(i);
			if (p_152378_1_.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	protected void onImpact(MovingObjectPosition target) {
		if(target.entityHit instanceof EntityFortressBossProjectile == false) {
			if (target.entityHit != null && target.entityHit instanceof EntityLivingBase) {
				if(target.entityHit instanceof EntityFortressBoss) {
					EntityFortressBoss boss = (EntityFortressBoss) target.entityHit;
					Vec3 ray = Vec3.createVectorHelper(this.motionX, this.motionY, this.motionZ);
					ray = ray.normalize();
					ray.xCoord = ray.xCoord * 64.0D;
					ray.yCoord = ray.yCoord * 64.0D;
					ray.zCoord = ray.zCoord * 64.0D;
					int shieldHit = boss.rayTraceShield(Vec3.createVectorHelper(this.posX, this.posY, this.posZ), ray, false);
					if(shieldHit >= 0) {
						if(!this.worldObj.isRemote) {
							boss.setShieldActive(shieldHit, false);

							this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "thebetweenlands:fortressBossShieldDown", 1.0F, 1.0F);

							double angle = Math.PI * 2.0D / 18;
							for(int i = 0; i < 18; i++) {
								Vec3 dir = Vec3.createVectorHelper(Math.sin(angle * i), 0, Math.cos(angle * i));
								dir = dir.normalize();
								float speed = 0.8F;
								EntityFortressBossProjectile bullet = new EntityFortressBossProjectile(this.worldObj, this.getOwner());
								bullet.setLocationAndAngles(boss.posX, boss.posY, boss.posZ, 0, 0);
								bullet.setThrowableHeading(dir.xCoord, dir.yCoord, dir.zCoord, speed, 0.0F);
								this.worldObj.spawnEntityInWorld(bullet);
							}
						}
					} else {
						boss.attackEntityFrom(DamageSource.generic, 10);
					}
					if(!this.worldObj.isRemote)
						boss.setFloating(false);
				} else {
					target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getOwner()), 2);
				}
				if(!this.worldObj.isRemote)
					this.setDead();
			} else if(target.typeOfHit == MovingObjectType.BLOCK) {
				this.setDead();
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (this.isEntityInvulnerable()) {
			return false;
		} else {
			if(this.isDeflectable()) {
				this.setBeenAttacked();
				if (source.getEntity() instanceof EntityPlayer) {
					ItemStack heldItem = ((EntityPlayer)source.getEntity()).getHeldItem();
					if(heldItem != null && heldItem.getItem() instanceof ItemSword)
						if(!this.worldObj.isRemote && source.getEntity().riddenByEntity == null) {
							this.mountEntity(source.getEntity());
							return true;
						}
				}
			} else {
				if(!this.worldObj.isRemote)
					this.setDead();
			}
			return false;
		}
	}

	@Override
	public double getYOffset() {
		if (this.ridingEntity != null && this.ridingEntity instanceof EntityPlayer && this.ridingEntity == TheBetweenlands.proxy.getClientPlayer()) {
			return -1.75D;
		}
		return this.yOffset;
	}

	@Override
	public void onUpdate() {
		if(!this.worldObj.isRemote && (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.setDead();
			return;
		}

		if(!this.isDead) {
			if(this.ridingEntity == null) {
				this.ticksInAir++;
				if(this.ticksInAir > 200)
					this.setDead();
				Vec3 currentPos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
				Vec3 nextPos = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
				MovingObjectPosition hitObject = this.worldObj.rayTraceBlocks(currentPos, nextPos);
				currentPos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
				nextPos = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
				if (hitObject != null) {
					nextPos = Vec3.createVectorHelper(hitObject.hitVec.xCoord, hitObject.hitVec.yCoord, hitObject.hitVec.zCoord);
				}
				Entity hitEntity = null;
				List hitEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(0.1D, 0.1D, 0.1D));
				double minDist = 0.0D;
				for (int i = 0; i < hitEntities.size(); ++i) {
					Entity entity = (Entity)hitEntities.get(i);
					if (entity.canBeCollidedWith() && entity != this.getThrower() && entity != this) {
						float f = 0.1F;
						AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f, (double)f, (double)f);
						MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(currentPos, nextPos);
						if (movingobjectposition1 != null) {
							double d1 = currentPos.distanceTo(movingobjectposition1.hitVec);
							if (d1 < minDist || minDist == 0.0D) {
								hitEntity = entity;
								minDist = d1;
							}
						}
					}
				}
				if (hitEntity != null) {
					hitObject = new MovingObjectPosition(hitEntity);
				}
				if (hitObject != null && hitObject.entityHit != this.getThrower()) {
					this.onImpact(hitObject);
				}
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
			} else {
				if(this.ridingEntity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) this.ridingEntity;
					ItemStack heldItem = player.getHeldItem();
					if(!this.isDeflectable() || heldItem == null || heldItem.getItem() instanceof ItemSword == false) {
						if(!this.worldObj.isRemote)
							this.setDead();
					} else {
						player.setInWeb();
						player.motionY -= 1.5D;
						if(player.isSwingInProgress) {
							if(this.canDismount) {
								Vec3 look = this.ridingEntity.getLookVec();
								look.normalize();
								this.setThrowableHeading(look.xCoord, look.yCoord, look.zCoord, 0.5F, 0.0F);
								this.ridingEntity = null;
								this.setThrower(player.getUniqueID().toString());
							}
						} else {
							this.canDismount = true;
						}
					}
				}
			}
		}

		super.onUpdate();
	}

	@Override
	public void setThrowableHeading(double x, double y, double z, float speed, float randMotion) {
		float f2 = MathHelper.sqrt_double(x * x + y * y + z * z);
		x /= (double)f2;
		y /= (double)f2;
		z /= (double)f2;
		x += this.rand.nextGaussian() * 0.007499999832361937D * (double)randMotion;
		y += this.rand.nextGaussian() * 0.007499999832361937D * (double)randMotion;
		z += this.rand.nextGaussian() * 0.007499999832361937D * (double)randMotion;
		x *= (double)speed;
		y *= (double)speed;
		z *= (double)speed;
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		float f3 = MathHelper.sqrt_double(x * x + z * z);
		this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, (double)f3) * 180.0D / Math.PI);
		this.velocityChanged = true;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.setOwner(nbt.getString("ownerUUID"));
		this.setThrower(nbt.getString("throwerUUID"));
		this.ticksInAir = nbt.getInteger("ticksInAir");
		this.canDismount = nbt.getBoolean("canDismount");
		this.setDeflectable(nbt.getBoolean("deflectable"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setString("ownerUUID", this.getOwnerUUID());
		nbt.setString("throwerUUID", this.throwerUUID);
		nbt.setInteger("ticksInAir", this.ticksInAir);
		nbt.setBoolean("canDismount", this.canDismount);
		nbt.setBoolean("deflectable", this.isDeflectable());
	}
}