package thebetweenlands.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.particle.BLParticle;

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
		if (ticksExisted > 400)
			setDead();
	}

	@Override
	protected void onImpact(MovingObjectPosition mop) {
		if (mop.typeOfHit != null) {
			if (worldObj.isRemote) {
				double particleX = MathHelper.floor_double(posX) + rand.nextFloat();
				double particleY = MathHelper.floor_double(posY) + rand.nextFloat();
				double particleZ = MathHelper.floor_double(posZ) + rand.nextFloat();
				for (int count = 0; count < 10; count++)
					BLParticle.FLAME.spawn(worldObj, particleX, particleY, particleZ);
			}
			
			if (!worldObj.isRemote) {
				explode();
				setDead();
			}
		}
	}

	private void explode() {
		boolean blockDamage = worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
		worldObj.createExplosion(this, posX, posY, posZ, 4.5F, blockDamage);
	}
}