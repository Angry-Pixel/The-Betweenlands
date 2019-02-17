package thebetweenlands.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.EntityRegistry;

public class EntityAngryPebble extends EntityThrowable {
	public EntityAngryPebble(World world) {
		super(EntityRegistry.ANGRY_PEBBLE, world);
	}

	public EntityAngryPebble(World world, EntityLivingBase entity) {
		super(EntityRegistry.ANGRY_PEBBLE, entity, world);
	}

	@Override
	public void tick() {
		super.tick();
		if(this.ticksExisted > 400) {
			remove();
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(result.type != null) {
			if(this.world.isRemote()) {
				double particleX = MathHelper.floor(this.posX) + this.rand.nextFloat();
				double particleY = MathHelper.floor(this.posY) + this.rand.nextFloat();
				double particleZ = MathHelper.floor(this.posZ) + this.rand.nextFloat();
				for (int count = 0; count < 10; count++) {
					BLParticles.FLAME.spawn(this.world, particleX, particleY, particleZ);
				}
			} else {
				this.explode();
				this.remove();
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