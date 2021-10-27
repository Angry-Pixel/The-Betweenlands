package thebetweenlands.common.entity.projectiles;

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
			if(this.world.isRemote) {
				double particleX = MathHelper.floor(this.posX) + this.rand.nextFloat();
				double particleY = MathHelper.floor(this.posY) + this.rand.nextFloat();
				double particleZ = MathHelper.floor(this.posZ) + this.rand.nextFloat();
				for (int count = 0; count < 10; count++) {
					BLParticles.FLAME.spawn(this.world, particleX, particleY, particleZ);
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
		boolean blockDamage = this.world.getGameRules().getBoolean("mobGriefing");
		this.world.createExplosion(this, this.posX, this.posY, this.posZ, 4.5F, blockDamage);
	}
}