package thebetweenlands.entities.mobs.boss.fortress;

import java.util.List;
import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.mobs.IEntityBL;

public class EntityFortressBossTurret extends EntityMob implements IEntityBL {
	public static final int TARGET_DW = 17;
	public static final int OWNER_DW = 18;

	private boolean particlesSpawned = false;
	private double anchorX, anchorY, anchorZ;
	private int attackTicks = 0;
	private int attackDelay = 40;

	public EntityFortressBossTurret(World world) {
		super(world);
		float width = 0.4F;
		float height = 0.4F;
		this.setSize(width, height);
	}

	public EntityFortressBossTurret(World world, Entity source) {
		super(world);
		if(source != null)
			this.setOwner(source.getUniqueID().toString());
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(TARGET_DW, "");
		this.dataWatcher.addObject(OWNER_DW, "");
	}

	protected String getTargetUUID() {
		return this.dataWatcher.getWatchableObjectString(TARGET_DW);
	}

	protected void setTargetUUID(String uuid) {
		this.dataWatcher.updateObject(TARGET_DW, uuid != null ? uuid : "");
	}

	public void setTarget(EntityPlayer target) {
		this.setTargetUUID(target.getUniqueID().toString());
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

	private Entity getEntityByUUID(UUID p_152378_1_) {
		for (int i = 0; i < this.worldObj.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.worldObj.loadedEntityList.get(i);
			if (p_152378_1_.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	public EntityPlayer getTarget() {
		if(this.getTargetUUID() != null && this.getTargetUUID().length() > 0) {
			try {
				return this.worldObj.func_152378_a(UUID.fromString(this.getTargetUUID())); 
			} catch (IllegalArgumentException ex) {
				this.setTargetUUID("");
				return null;
			}
		}
		return null;
	}

	@Override
	public String pageName() {
		return "thisShouldntHaveAPageButTheManualImplementationForcesMeToGiveItAPageName";
	}

	public int getAttackDelay() {
		return this.attackDelay;
	}

	public void setAttackDelay(int delay) {
		this.attackDelay = delay;
	}

	public void setAnchor(double x, double y, double z) {
		this.anchorX = x;
		this.anchorY = y;
		this.anchorZ = z;
	}

	public double getAnchorX() {
		return this.anchorX;
	}

	public double getAnchorY() {
		return this.anchorY;
	}

	public double getAnchorZ() {
		return this.anchorZ;
	}
	
	@Override
	protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0D);
    }

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setDouble("anchorX", this.anchorX);
		nbt.setDouble("anchorY", this.anchorY);
		nbt.setDouble("anchorZ", this.anchorZ);
		nbt.setString("targetUUID", this.getTargetUUID());
		nbt.setInteger("attackDelay", this.attackDelay);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.anchorX = nbt.getDouble("anchorX");
		this.anchorY = nbt.getDouble("anchorY");
		this.anchorZ = nbt.getDouble("anchorZ");
		this.setTargetUUID(nbt.getString("targetUUID"));
		this.attackDelay = nbt.getInteger("attackDelay");
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		super.onSpawnWithEgg(data);
		this.anchorX = this.posX;
		this.anchorY = this.posY;
		this.anchorZ = this.posZ;
		return data;
	}

	@Override
	public void onUpdate() {
		if(!this.worldObj.isRemote && (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.setDead();
			return;
		}
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		super.onUpdate();

		if(this.worldObj.isRemote) {
			if(!this.particlesSpawned) {
				this.particlesSpawned = true;
				for(int i = 0; i < 6; i++)
					this.spawnVolatileParticles();
			}
			if(this.worldObj.rand.nextInt(6) == 0)
				BLParticle.GREEN_FLAME.spawn(this.worldObj, this.posX, this.posY + 0.2F, this.posZ, (this.worldObj.rand.nextFloat() - 0.5F) / 5.0F, (this.worldObj.rand.nextFloat() - 0.5F) / 5.0F, (this.worldObj.rand.nextFloat() - 0.5F) / 5.0F, 1.0F);
		}

		if(this.getTarget() == null) {
			AxisAlignedBB searchBB = this.boundingBox.expand(16, 16, 16);
			List<EntityPlayer> eligiblePlayers = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, searchBB);
			EntityPlayer closest = null;
			for(EntityPlayer player : eligiblePlayers) {
				if(closest == null || closest.getDistanceToEntity(this) > player.getDistanceToEntity(this))
					closest = player;
			}
			if(closest != null) 
				this.setTarget(closest);
		}

		if(this.getTarget() != null) {
			this.faceEntity(this.getTarget(), 360.0F, 360.0F);
			this.attackTicks++;

			if(this.attackTicks > this.attackDelay) {
				if(!this.worldObj.isRemote) {
					if(!this.isObstructedByBoss()) {
						Vec3 diff = Vec3.createVectorHelper(this.posX, this.posY, this.posZ)
								.subtract(Vec3.createVectorHelper(this.getTarget().boundingBox.minX + (this.getTarget().boundingBox.maxX - this.getTarget().boundingBox.minX) / 2.0D,
										this.getTarget().boundingBox.minY + (this.getTarget().boundingBox.maxY - this.getTarget().boundingBox.minY) / 2.0D,
										this.getTarget().boundingBox.minZ + (this.getTarget().boundingBox.maxZ - this.getTarget().boundingBox.minZ) / 2.0D)).normalize();
						EntityFortressBossProjectile bullet = new EntityFortressBossProjectile(this.worldObj, this.getOwner());
						bullet.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
						float speed = 0.5F;
						bullet.setThrowableHeading(diff.xCoord, diff.yCoord, diff.zCoord, speed, 0.0F);
						this.worldObj.spawnEntityInWorld(bullet);
					}
					this.setDead();
				} else {
					for(int i = 0; i < 6; i++)
						this.spawnVolatileParticles();
					this.setDead();
				}
			}
		} else {
			this.attackTicks = 0;
		}
	}

	public boolean isObstructedByBoss() {
		Vec3 ray = this.getLookVec().normalize();
		Vec3 currentPos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
		Vec3 nextPos = currentPos.addVector(ray.xCoord * 64.0D, ray.yCoord * 64.0D, ray.zCoord * 64.0D);
		Entity hitEntity = null;
		List hitEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(64, 64, 64));
		double minDist = 0.0D;
		for (int i = 0; i < hitEntities.size(); ++i) {
			Entity entity = (Entity)hitEntities.get(i);
			if (entity.canBeCollidedWith()) {
				float f = 0.3F;
				AxisAlignedBB entityBB = entity.boundingBox.expand((double)f, (double)f, (double)f);
				MovingObjectPosition result = entityBB.calculateIntercept(currentPos, nextPos);
				if (result != null) {
					double dst = currentPos.distanceTo(result.hitVec);
					if (dst < minDist || minDist == 0.0D) {
						hitEntity = entity;
						minDist = dst;
					}
				}
			}
		}
		if(hitEntity == null || (hitEntity instanceof EntityFortressBoss == false && hitEntity != this.getOwner())) {
			return false;
		}
		return true;
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
	public void moveEntityWithHeading(float strafe, float forward) {
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
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return false;
	}
}
