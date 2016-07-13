package thebetweenlands.client.particle;

import javax.annotation.Nullable;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticleFactory.ParticleArgs;
import thebetweenlands.client.particle.entity.ParticlePortalBL;
import thebetweenlands.client.particle.entity.ParticleSpellBL;

public enum BLParticles {
	SULFUR_ORE(new BLParticleFactory(ParticleSpellBL.class) {
		@Override
		public Particle createParticle(ImmutableParticleArgs args) {
			return new ParticleSpellBL(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ);
		}
	}),
	PORTAL(new BLParticleFactory(ParticlePortalBL.class, ParticleTextureStitcher.create(ParticlePortalBL.class, 
			new ResourceLocation("thebetweenlands:particle/portal"))) {
		@Override
		public Particle createParticle(ImmutableParticleArgs args) {
			return new ParticlePortalBL(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, (int)args.data[0], args.scale, args.color);
		}
		@Override
		protected void setDefaultArguments(ParticleArgs args) {
			args.withData(new Object[]{ 40 });
		}
	});

	private BLParticleFactory factory;

	private BLParticles(BLParticleFactory factory) {
		this.factory = factory;
	}

	public Class<? extends Particle> getType() {
		return this.factory.getType();
	}

	public BLParticleFactory getFactory() {
		return this.factory;
	}

	public Particle create(World world, double x, double y, double z, @Nullable ParticleArgs args) {
		return BLParticleManager.INSTANCE.create(this.getType(), world, x, y, z, args);
	}

	public Particle spawn(World world, double x, double y, double z, @Nullable ParticleArgs args) {
		return BLParticleManager.INSTANCE.spawn(this.getType(), world, x, y, z, args);
	}

	public Particle create(World world, double x, double y, double z) {
		return BLParticleManager.INSTANCE.create(this.getType(), world, x, y, z, null);
	}

	public Particle spawn(World world, double x, double y, double z) {
		return BLParticleManager.INSTANCE.spawn(this.getType(), world, x, y, z, null);
	}
}
