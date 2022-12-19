package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;

public class EntityVolatileSoul extends Entity implements IProjectile, IEntityBL {
	private Entity target = null;
	private int strikes = 0;
	private int ticksInAir;

	protected static final DataParameter<Optional<UUID>> OWNER_UUID_DW = EntityDataManager.createKey(EntityVolatileSoul.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	protected Deque<Vec3d> trail = new LinkedList<>();
	
	public EntityVolatileSoul(World world) {
		super(world);
		this.setSize(0.3F, 0.3F);
		this.noClip = true;
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(OWNER_UUID_DW, Optional.absent());
	}

	public void setOwner(UUID uuid) {
		this.getDataManager().set(OWNER_UUID_DW, Optional.of(uuid));
	}

	public UUID getOwnerUUID() {
		Optional<UUID> optional = this.getDataManager().get(OWNER_UUID_DW);
		return optional.isPresent() ? optional.get() : null;
	}

	public Entity getOwner() {
		try {
			UUID uuid = this.getOwnerUUID();
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	private Entity getEntityByUUID(UUID p_152378_1_) {
		for (int i = 0; i < this.world.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.world.loadedEntityList.get(i);
			if (p_152378_1_.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	protected void onImpact(RayTraceResult target) {
		if (target.entityHit != null && target.entityHit instanceof EntityLivingBase && target.entityHit instanceof EntityWight == false && target.entityHit instanceof EntitySwampHag == false) {
			if(!this.world.isRemote) {
				if(target.entityHit instanceof EntityPlayer && ((EntityPlayer)target.entityHit).isActiveItemStackBlocking() && ((EntityPlayer)target.entityHit).getItemInUseCount() <= 15) {
					this.motionX *= -6;
					this.motionY *= -6;
					this.motionZ *= -6;
					this.strikes++;
					return;
				}
				target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getOwner()), 3);
				if(!this.isDead && target.entityHit instanceof EntityPlayer && (target.entityHit.isDead || ((EntityLivingBase)target.entityHit).getHealth() <= 0.0F)) {
					target.entityHit.setDead();
					/*EntityWight wight = new EntityWight(this.world);
					wight.setLocationAndAngles(target.entityHit.posX, target.entityHit.posY + 0.05D, target.entityHit.posZ, target.entityHit.rotationYaw, target.entityHit.rotationPitch);
					if(this.world.getCollidingBoundingBoxes(wight, wight.boundingBox).isEmpty()) {
						this.world.spawnEntityInWorld(wight);
					}*/
				}
				this.setDead();
				this.motionX = 0;
				this.motionY = 0;
				this.motionZ = 0;
			}
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else {
			this.strikes++;
			if(this.strikes >= 3) {
				this.setDead();
				return true;
			}
			this.markVelocityChanged();
			if (source.getTrueSource() != null) {
				if(!this.world.isRemote) {
					Vec3d vec3 = source.getTrueSource().getLookVec();
					if (vec3 != null) {
						this.motionX = vec3.x * 1.5F;
						this.motionY = vec3.y * 1.5F;
						this.motionZ = vec3.z * 1.5F;
					}
				}
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void onUpdate() {
		if(!this.world.isRemote && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || this.getOwner() == null || this.getOwner().isDead)) {
			this.setDead();
			return;
		}

		if(!this.world.isRemote) {
			if((this.getOwner() == null || !this.getOwner().isEntityAlive() || this.getOwner() instanceof EntityWight == false || !((EntityWight)this.getOwner()).isVolatile()) /*|| this.target instanceof EntityFortressBoss*/)
				this.setDead();
		}

		if(!this.isDead) {
			this.ticksInAir++;
			if(this.world.isRemote) {
				this.trail.push(this.getPositionVector());
				while(this.trail.size() > 4) {
					this.trail.removeLast();
				}
			}
			if(this.target == null || this.target.isDead) {
				List<Entity> targetList = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(16.0D, 16.0D, 16.0D));
				List<Entity> eligibleTargets = new ArrayList<Entity>();
				if(this.world.rand.nextInt(4) > 0) {
					for(Entity e : targetList) {
						if(e instanceof EntityPlayer) {
							eligibleTargets.add((EntityPlayer)e);
						}
					}
				}
				if(eligibleTargets.isEmpty()) {
					for(Entity e : targetList) {
						if(e instanceof EntityWight == false) {
							eligibleTargets.add(e);
						}
					}
				}
				if(!eligibleTargets.isEmpty()) {
					this.target = eligibleTargets.get(this.world.rand.nextInt(eligibleTargets.size()));
				}
			} 
			if(this.target != null && this.ticksInAir >= 10) {
				double dx = this.target.getEntityBoundingBox().minX + (this.target.getEntityBoundingBox().maxX - this.target.getEntityBoundingBox().minX) / 2.0D - this.posX;
				double dy = this.target.getEntityBoundingBox().minY + (this.target.getEntityBoundingBox().maxY - this.target.getEntityBoundingBox().minY) / 2.0D - this.posY;
				double dz = this.target.getEntityBoundingBox().minZ + (this.target.getEntityBoundingBox().maxZ - this.target.getEntityBoundingBox().minZ) / 2.0D - this.posZ;
				double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				double speed = 0.075D;
				double maxSpeed = 0.8D;
				this.motionX += dx / dist * speed;
				this.motionY += dy / dist * speed;
				this.motionZ += dz / dist * speed;
				Vec3d motion = new Vec3d(this.motionX, this.motionY, this.motionZ);
				if(motion.length() > maxSpeed) {
					motion = motion.normalize();
					this.motionX = motion.x * maxSpeed;
					this.motionY = motion.y * maxSpeed;
					this.motionZ = motion.z * maxSpeed;
				}
			}
			Vec3d currentPos = new Vec3d(this.posX, this.posY, this.posZ);
			Vec3d nextPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			RayTraceResult hitObject = this.world.rayTraceBlocks(currentPos, nextPos);
			currentPos = new Vec3d(this.posX, this.posY, this.posZ);
			nextPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			if (hitObject != null) {
				nextPos = new Vec3d(hitObject.hitVec.x, hitObject.hitVec.y, hitObject.hitVec.z);
			}
			Entity hitEntity = null;
			List<Entity> hitEntities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(this.motionX, this.motionY, this.motionZ).grow(2.0D, 2.0D, 2.0D));
			double minDist = 0.0D;
			for (int i = 0; i < hitEntities.size(); ++i) {
				Entity entity1 = (Entity)hitEntities.get(i);
				if (entity1.canBeCollidedWith() && (this.ticksInAir >= 10)) {
					float f = 0.3F;
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)f, (double)f, (double)f);
					RayTraceResult movingobjectposition1 = axisalignedbb.calculateIntercept(currentPos, nextPos);
					if (movingobjectposition1 != null) {
						double d1 = currentPos.distanceTo(movingobjectposition1.hitVec);
						if (d1 < minDist || minDist == 0.0D) {
							hitEntity = entity1;
							minDist = d1;
						}
					}
				}
			}
			if (hitEntity != null) {
				hitObject = new RayTraceResult(hitEntity);
			}
			if (hitObject != null) {
				this.onImpact(hitObject);
			}
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		}

		super.onUpdate();
	}

	@Override
	public void shoot(double x, double y, double z, float speed, float randMotion) {
		float f2 = MathHelper.sqrt(x * x + y * y + z * z);
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
		float f3 = MathHelper.sqrt(x * x + z * z);
		this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, (double)f3) * 180.0D / Math.PI);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("ownerUUID")) {
			this.setOwner(nbt.getUniqueId("ownerUUID"));
		}
		if(nbt.hasKey("strikes")) {
			this.strikes = nbt.getInteger("strikes");
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		UUID ownerUuid = this.getOwnerUUID();
		if(ownerUuid != null) {
			nbt.setUniqueId("ownerUUID", ownerUuid);
		}
		nbt.setInteger("strikes", this.strikes);
	}
	
	@SideOnly(Side.CLIENT)
	public Collection<Vec3d> getTrail() {
		return this.trail;
	}
}