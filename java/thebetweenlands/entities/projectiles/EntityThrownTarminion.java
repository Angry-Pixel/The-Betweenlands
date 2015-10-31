package thebetweenlands.entities.projectiles;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.mobs.EntityTarminion;

public class EntityThrownTarminion extends EntityThrowable {
	public static float rotationticks;
	private String ownerUUID = null;

	public EntityThrownTarminion(World world) {
		super(world);
	}

	public EntityThrownTarminion(World world, EntityLivingBase entity) {
		super(world, entity);
		this.ownerUUID = entity.getUniqueID().toString();
	}

	public EntityThrownTarminion(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("owner", this.ownerUUID);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.ownerUUID = nbt.getString("owner");
	}

	@Override
	protected void onImpact(MovingObjectPosition target) {
		if (target.entityHit != null && target.entityHit instanceof EntityLivingBase) {
			if(!(target.entityHit instanceof EntityTarminion)) {
				if(!worldObj.isRemote) {
					target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 2);
					if (isBurning() && !(target.entityHit instanceof EntityEnderman)) {
						target.entityHit.setFire(5);
					}
				}
				for (int i = 0; i < 8; i++) {
					BLParticle.SPLASH_TAR_BEAST.spawn(worldObj, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);
				}
			}
			if (!worldObj.isRemote) {
				EntityTarminion tarminion = spawnTarminion();
				tarminion.setAttackTarget((EntityLivingBase) target.entityHit);
				tarminion.setTarget(target.entityHit);
			}
		} else if (target.typeOfHit == MovingObjectType.BLOCK) {
			if (!worldObj.isRemote) {
				spawnTarminion();
			}
		}
	}

	private EntityTarminion spawnTarminion() {
		if (!worldObj.isRemote) {
			setDead();
			EntityTarminion tarminion = new EntityTarminion(worldObj);
			tarminion.setOwner(this.ownerUUID);
			Vec3 motionVec = Vec3.createVectorHelper(this.motionX, this.motionY, this.motionZ);
			motionVec = motionVec.normalize();
			double speed = 0.25D;
			tarminion.motionX = motionVec.xCoord * speed;
			tarminion.motionY = motionVec.yCoord * speed;
			tarminion.motionZ = motionVec.zCoord * speed;
			tarminion.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
			worldObj.spawnEntityInWorld(tarminion);
			return tarminion;
		}
		return null;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(!onGround)
			if (worldObj.isRemote)
				BLParticle.DRIP_TAR_BEAST.spawn(worldObj, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);

		if (rotationticks < 360F) {
			rotationticks += 5F;
			if (rotationticks >= 360F)
				rotationticks = 0;
		}
	}
}