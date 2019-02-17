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
import thebetweenlands.common.entity.mobs.EntityTarMinion;
import thebetweenlands.common.registries.EntityRegistry;

public class EntityThrownTarminion extends EntityThrowable {
	private UUID ownerUUID = null;

	public EntityThrownTarminion(World world) {
		super(EntityRegistry.TAR_MINION, world);
	}

	public EntityThrownTarminion(World world, EntityLivingBase entity) {
		super(EntityRegistry.TAR_MINION, entity, world);
		this.ownerUUID = entity.getUniqueID();
	}

	public EntityThrownTarminion(World world, double x, double y, double z) {
		super(EntityRegistry.TAR_MINION, x, y, z, world);
	}

	@Override
	public void writeAdditional(NBTTagCompound nbt) {
		super.writeAdditional(nbt);
		
		nbt.setUniqueId("owner", this.ownerUUID);
	}

	@Override
	public void readAdditional(NBTTagCompound nbt) {
		super.readAdditional(nbt);
		
		this.ownerUUID = nbt.getUniqueId("owner");
	}

	@Override
	public void tick() {
		super.tick();
		if(!this.onGround) {
			if (this.world.isRemote()) {
				BLParticles.TAR_BEAST_DRIP.spawn(this.world, this.posX, this.posY, this.posZ);
			}
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (result.entity != null && result.entity instanceof EntityLivingBase) {
			if(!(result.entity instanceof EntityTarMinion)) {
				if(!this.world.isRemote()) {
					result.entity.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 2);

					if (this.isBurning() && !(result.entity instanceof EntityEnderman)) {
						result.entity.setFire(5);
					}
				}

				if(this.world.isRemote()) {
					for (int i = 0; i < 8; i++) {
						BLParticles.SPLASH_TAR.spawn(this.world, this.posX, this.posY, this.posZ);
					}
				}
			}

			if (!this.world.isRemote()) {
				EntityTarMinion tarminion = spawnTarminion();
				if(result.entity instanceof EntityMob) {
					tarminion.setAttackTarget((EntityLivingBase) result.entity);
				}
			}
		} else if (result.type == RayTraceResult.Type.BLOCK) {
			if (!this.world.isRemote()) {
				spawnTarminion();
			}
		}
	}

	private EntityTarMinion spawnTarminion() {
		if (!this.world.isRemote()) {
			this.remove();
			EntityTarMinion tarminion = new EntityTarMinion(this.world);
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