package thebetweenlands.entities.particles;

import java.util.List;

import javax.vecmath.Vector3d;

import net.minecraft.world.World;

public class EntityAnimatorFX2 extends EntityPathParticle {
	private int ticks = 0;

	public EntityAnimatorFX2(World world, double x, double y, double z,
			double motionX, double motionY, double motionZ,
			List<Vector3d> targetPoints) {
		super(world, x, y, z, motionX, motionY, motionZ, targetPoints);

		this.particleScale = world.rand.nextFloat() / 2.0F;
		this.particleMaxAge = 10000000;
		this.particleAge = 0;

		this.setParticleTextureIndex((int)(Math.random() * 26.0D + 1.0D + 224.0D));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.ticks++;
		if(this.ticks >= 200 || this.particleScale <= 0.0F) {
			this.setDead();
		}

		double t = 1.0D / 200.0D * this.ticks;

		Vector3d pos = this.getPosition(t);

		this.setPosition(pos.x, pos.y, pos.z);
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}
}
