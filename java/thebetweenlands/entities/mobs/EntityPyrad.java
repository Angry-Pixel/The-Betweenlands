package thebetweenlands.entities.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.entities.projectiles.EntityPyradFlame;

public class EntityPyrad extends EntityBlaze {
	private int shouldFire;
	public EntityPyrad(World world) {
		super(world);
	}

	@Override
	protected void attackEntity(Entity entity, float distance) {
		if (attackTime <= 0 && distance < 2.0F && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY) {
			attackTime = 20;
			attackEntityAsMob(entity);
		} else if (distance < 30.0F) {
			double d0 = entity.posX - posX;
			double d1 = entity.boundingBox.minY + (double) (entity.height / 2.0F) - (posY + (double) (height / 2.0F));
			double d2 = entity.posZ - posZ;

			if (attackTime == 0) {
				++shouldFire;

				if (shouldFire == 1) {
					attackTime = 60;
					func_70844_e(true);
				} else if (shouldFire <= 4) {
					attackTime = 6;
				} else {
					attackTime = 100;
					shouldFire = 0;
					func_70844_e(false);
				}

				if (shouldFire > 1) {
					float f1 = MathHelper.sqrt_float(distance) * 0.5F;
					worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1009, (int) posX, (int) posY, (int) posZ, 0);

					for (int i = 0; i < 1; ++i) {
						EntityPyradFlame flammeBall = new EntityPyradFlame(worldObj, this, d0 + rand.nextGaussian() * (double) f1, d1, d2 + rand.nextGaussian() * (double) f1);
						flammeBall.posY = posY + (double) (height / 2.0F) + 0.5D;
						worldObj.spawnEntityInWorld(flammeBall);
					}
				}
			}

			rotationYaw = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
			hasAttacked = true;
		}
	}
}
