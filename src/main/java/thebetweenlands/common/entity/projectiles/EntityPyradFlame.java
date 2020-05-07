package thebetweenlands.common.entity.projectiles;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class EntityPyradFlame extends EntitySmallFireball {
	public EntityPyradFlame(World world) {
		super(world);
	}

	public EntityPyradFlame(World world, EntityLivingBase entity, double x, double y, double z) {
		super(world, entity, x, y, z);
		this.setSize(0.3125F, 0.3125F);
	}

	public EntityPyradFlame(World world, double x, double y, double z, double targetX, double targetY, double targetZ) {
		super(world, x, y, z, targetX, targetY, targetZ);
		this.setSize(0.3125F, 0.3125F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (!this.world.isRemote)
			if (this.ticksExisted >= 1200)
				this.setDead();

		if (this.world.isRemote)
			this.trailParticles(this.world, this.prevPosX, this.prevPosY, this.prevPosZ, this.rand);

		if (this.isBurning())
			this.extinguish();
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
		if(rand.nextInt(4) == 0) {
			BLParticles.FLAME.spawn(world, x, y, z, ParticleArgs.get().withMotion(velX, velY, velZ));
		}
		BLParticles.WEEDWOOD_LEAF.spawn(world, x, y + this.height / 2.0F, z, ParticleArgs.get().withMotion(velX, velY, velZ).withColor(1F, 0.25F, 0.0F, 1.0F).withData(40));
	}
}
