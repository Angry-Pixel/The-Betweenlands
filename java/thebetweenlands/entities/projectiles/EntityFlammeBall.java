package thebetweenlands.entities.projectiles;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityFlammeBall extends EntitySmallFireball {

	public EntityFlammeBall(World world) {
		super(world);
	}

	public EntityFlammeBall(World world, EntityLivingBase entity, double x, double y, double z) {
		super(world, entity, x, y, z);
		setSize(0.3125F, 0.3125F);
	}

	public EntityFlammeBall(World world, double x, double y, double z, double targetX, double targetY, double targetZ) {
		super(world, x, y, z, targetX, targetY, targetZ);
		setSize(0.3125F, 0.3125F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!worldObj.isRemote)
			if (ticksExisted >= 1200)
				setDead();

		if (worldObj.isRemote)
			trailParticles(worldObj, prevPosX, prevPosY, prevPosZ, rand);
		
		if (isBurning())
			extinguish();
	}

	@SideOnly(Side.CLIENT)
	public void trailParticles(World world, double x, double y, double z, Random rand) {
			double velX = 0.0D;
			double velY = 0.0D;
			double velZ = 0.0D;
			int motionX = rand.nextInt(2) * 2 - 1;
			int motionZ = rand.nextInt(2) * 2 - 1;
			velY = (rand.nextFloat() - 0.5D) * 0.125D;
			velZ = rand.nextFloat() * 0.1F * motionZ;
			velX = rand.nextFloat() * 0.1F * motionX;
			BLParticle.FLAME.spawn(worldObj, x, y, z, velX, velY, velZ, 0);
	}

}
