package thebetweenlands.client.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.ParticleTextureStitcher;

public class ParticleGasCloud extends Particle implements ParticleTextureStitcher.IParticleSpriteReceiver {
	private int rotation = 0;

	protected ParticleGasCloud(World world, double x, double y, double z, double vecX, double vecY, double vecZ) {
		super(world, x, y, z, vecX, vecY, vecZ);
		motionX = motionX * 0.01D + vecX;
		motionY = motionY * 0.01D + vecY;
		motionZ = motionZ * 0.01D + vecZ;
		x += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		y += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		z += (rand.nextFloat() - rand.nextFloat()) * 0.05F;
		posX = prevPosX = x;
		posY = prevPosY = y;
		posZ = prevPosZ = z;
		particleMaxAge = 60;
		field_190017_n = false;
		rotation = this.rand.nextInt(4);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleGasCloud> {
		public Factory() {
			super(ParticleGasCloud.class, ParticleTextureStitcher.create(ParticleGasCloud.class, new ResourceLocation("thebetweenlands:particle/gas_cloud")));
		}

		@Override
		public ParticleGasCloud createParticle(ImmutableParticleArgs args) {
			return new ParticleGasCloud(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ);
		}
	}
}
