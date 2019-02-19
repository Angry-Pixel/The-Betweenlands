package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.EntityRegistry;

public class EntityVolatileSoul extends Entity implements IProjectile, IEntityBL {
	private Entity target = null;
	private int strikes = 0;
	private int ticksInAir;

	protected static final DataParameter<Optional<UUID>> OWNER_UUID_DW = EntityDataManager.createKey(EntityVolatileSoul.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	protected Deque<Vec3d> trail = new LinkedList<>();
	
	public EntityVolatileSoul(World world) {
		super(EntityRegistry.VOLATILE_SOUL, world);
		this.setSize(0.3F, 0.3F);
		this.noClip = true;
	}

	@Override
	protected void registerData() {
		this.getDataManager().register(OWNER_UUID_DW, Optional.empty());
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
		if (target.entity != null && target.entity instanceof EntityLivingBase && target.entity instanceof EntityWight == false && target.entity instanceof EntitySwampHag == false) {
			if(!this.world.isRemote()) {
				if(target.entity instanceof EntityPlayer && ((EntityPlayer)target.entity).isActiveItemStackBlocking() && ((EntityPlayer)target.entity).getItemInUseCount() <= 15) {
					this.motionX *= -6;
					this.motionY *= -6;
					this.motionZ *= -6;
					this.strikes++;
					return;
				}
				target.entity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.getOwner()), 3);
				if(this.isAlive() && target.entity instanceof EntityPlayer && (!target.entity.isAlive() || ((EntityLivingBase)target.entity).getHealth() <= 0.0F)) {
					target.entity.remove();
					/*EntityWight wight = new EntityWight(this.world);
					wight.setLocationAndAngles(target.entity.posX, target.entity.posY + 0.05D, target.entity.posZ, target.entity.rotationYaw, target.entity.rotationPitch);
					if(this.world.getCollidingBoundingBoxes(wight, wight.boundingBox).isEmpty()) {
						this.world.spawnEntityInWorld(wight);
					}*/
				}
				this.remove();
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
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.strikes++;
			if(this.strikes >= 3) {
				this.remove();
				return true;
			}
			this.markVelocityChanged();
			if (source.getTrueSource() != null) {
				if(!this.world.isRemote()) {
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
	public void tick() {
		if(!this.world.isRemote() && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || this.getOwner() == null || !this.getOwner().isAlive())) {
			this.remove();
			return;
		}

		if(!this.world.isRemote()) {
			if((this.getOwner() == null || !this.getOwner().isAlive() || this.getOwner() instanceof EntityWight == false || !((EntityWight)this.getOwner()).isVolatile()) /*|| this.target instanceof EntityFortressBoss*/)
				this.remove();
		}

		if(this.isAlive()) {
			this.ticksInAir++;
			if(this.world.isRemote()) {
				this.trail.push(this.getPositionVector());
				while(this.trail.size() > 4) {
					this.trail.removeLast();
				}
			}
			if(this.target == null || !this.target.isAlive()) {
				List<Entity> targetList = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getBoundingBox().grow(16.0D, 16.0D, 16.0D));
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
				double dx = this.target.getBoundingBox().minX + (this.target.getBoundingBox().maxX - this.target.getBoundingBox().minX) / 2.0D - this.posX;
				double dy = this.target.getBoundingBox().minY + (this.target.getBoundingBox().maxY - this.target.getBoundingBox().minY) / 2.0D - this.posY;
				double dz = this.target.getBoundingBox().minZ + (this.target.getBoundingBox().maxZ - this.target.getBoundingBox().minZ) / 2.0D - this.posZ;
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
			List<Entity> hitEntities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().grow(this.motionX, this.motionY, this.motionZ).grow(2.0D, 2.0D, 2.0D));
			double minDist = 0.0D;
			for (int i = 0; i < hitEntities.size(); ++i) {
				Entity entity1 = (Entity)hitEntities.get(i);
				if (entity1.canBeCollidedWith() && (this.ticksInAir >= 10)) {
					float f = 0.3F;
					AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)f, (double)f, (double)f);
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

		super.tick();
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
	protected void readAdditional(NBTTagCompound nbt) {
		if(nbt.hasKey("ownerUUID")) {
			this.setOwner(nbt.getUniqueId("ownerUUID"));
		}
		if(nbt.contains("strikes", Constants.NBT.TAG_INT)) {
			this.strikes = nbt.getInt("strikes");
		}
	}

	@Override
	protected void writeAdditional(NBTTagCompound nbt) {
		nbt.setUniqueId("ownerUUID", this.getOwnerUUID());
		nbt.setInt("strikes", this.strikes);
	}
	
	@OnlyIn(Dist.CLIENT)
	public Collection<Vec3d> getTrail() {
		return this.trail;
	}
}