package thebetweenlands.common.entity.projectiles;

import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.entity.mobs.EntityTarminion;

public class EntityThrownTarminion extends EntityThrowable {
	private UUID ownerUUID = null;

	public EntityThrownTarminion(World world) {
		super(world);
	}

	public EntityThrownTarminion(World world, EntityLivingBase entity) {
		super(world, entity);
		this.ownerUUID = entity.getUniqueID();
	}

	public EntityThrownTarminion(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setUniqueId("owner", this.ownerUUID);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.ownerUUID = nbt.getUniqueId("owner");
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(!this.onGround) {
			if (this.world.isRemote) {
				BLParticles.TAR_BEAST_DRIP.spawn(this.world, this.posX, this.posY, this.posZ);
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.entityHit != null && result.entityHit instanceof EntityLivingBase) {
			if(!(result.entityHit instanceof EntityTarminion)) {
				if(!this.world.isRemote) {
					result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 2);

					if (this.isBurning() && !(result.entityHit instanceof EntityEnderman)) {
						result.entityHit.setFire(5);
					}
				}

				if(this.world.isRemote) {
					for (int i = 0; i < 8; i++) {
						BLParticles.SPLASH_TAR.spawn(this.world, this.posX, this.posY, this.posZ);
					}
				}
			}

			if (!this.world.isRemote) {
				EntityTarminion tarminion = spawnTarminion();
				if(result.entityHit instanceof EntityMob) {
					tarminion.setAttackTarget((EntityLivingBase) result.entityHit);
				}
			}
		} else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			if (!this.world.isRemote) {
				spawnTarminion();
			}
		}
	}

	private EntityTarminion spawnTarminion() {
		if (!this.world.isRemote) {
			this.setDead();
			EntityTarminion tarminion = new EntityTarminion(this.world);
			tarminion.setTamed(true);
			if(this.ownerUUID != null) {
				tarminion.setOwnerId(this.ownerUUID);
			}
			Vec3d motionVec = new Vec3d(this.motionX, this.motionY, this.motionZ);
			motionVec = motionVec.normalize();
			double speed = 0.25D;
			tarminion.motionX = motionVec.x * speed;
			tarminion.motionY = motionVec.y * speed;
			tarminion.motionZ = motionVec.z * speed;
			tarminion.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
			this.world.spawnEntity(tarminion);
			return tarminion;
		}
		return null;
	}
}