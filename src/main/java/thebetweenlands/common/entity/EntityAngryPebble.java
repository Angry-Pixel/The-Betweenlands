package thebetweenlands.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;

public class EntityAngryPebble extends EntityThrowable {
	public EntityAngryPebble(World world) {
		super(world);
	}

	public EntityAngryPebble(World world, EntityLivingBase entity) {
		super(world, entity);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(this.ticksExisted > 400) {
			setDead();
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(result.typeOfHit != null) {
			if(this.worldObj.isRemote) {
				double particleX = MathHelper.floor_double(this.posX) + this.rand.nextFloat();
				double particleY = MathHelper.floor_double(this.posY) + this.rand.nextFloat();
				double particleZ = MathHelper.floor_double(this.posZ) + this.rand.nextFloat();
				for (int count = 0; count < 10; count++) {
					BLParticles.FLAME.spawn(this.worldObj, particleX, particleY, particleZ);
				}
			} else {
				this.explode();
				this.setDead();
			}
		}
	}

	/**
	 * Creates the explosion
	 */
	protected void explode() {
		boolean blockDamage = this.worldObj.getGameRules().getBoolean("mobGriefing");
		this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 4.5F, blockDamage);
	}
}