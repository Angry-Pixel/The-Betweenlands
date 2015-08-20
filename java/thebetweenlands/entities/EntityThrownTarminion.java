package thebetweenlands.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.mobs.EntityTarminion;

public class EntityThrownTarminion extends EntityThrowable {
	public static float rotationticks;

	public EntityThrownTarminion(World world) {
		super(world);
	}

	public EntityThrownTarminion(World world, EntityLivingBase entity) {
		super(world, entity);
	}

	public EntityThrownTarminion(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	protected void onImpact(MovingObjectPosition target) {
		if (target.entityHit != null && target.entityHit instanceof EntityLivingBase && !(target.entityHit instanceof EntityTarminion)) {
			if (!target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 2))
				;
			if (isBurning() && !(target.entityHit instanceof EntityEnderman))
				target.entityHit.setFire(5);
			for (int i = 0; i < 8; i++)
				BLParticle.SPLASH_TAR_BEAST.spawn(worldObj, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 0);
			if (!worldObj.isRemote)
				spawnTarminion();
		} else if (target.typeOfHit == MovingObjectType.BLOCK) {
			if (!worldObj.isRemote)
				spawnTarminion();
		}
	}

	private void spawnTarminion() {
		if (!worldObj.isRemote) {
			setDead();
			EntityTarminion tarminion = new EntityTarminion(worldObj);
			tarminion.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
			worldObj.spawnEntityInWorld(tarminion);
		}
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