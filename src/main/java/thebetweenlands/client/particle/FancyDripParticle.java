package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import thebetweenlands.client.particle.options.DripParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ParticleRegistry;

public class FancyDripParticle extends TextureSheetParticle {
	private final boolean spawnSplashes;
	private final boolean spawnRipples;

	protected FancyDripParticle(ClientLevel level, double x, double y, double z, double vecX, double vecY, double vecZ, DripParticleOptions options, float scale) {
		super(level, x, y, z, vecX, vecY, vecZ);
		this.quadSize = scale;
		this.xd = vecX;
		this.yd = vecY;
		this.zd = vecZ;
		this.setSize(0.01F, 0.01F);
		this.gravity = 0.06F;
		this.lifetime = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
		this.spawnRipples = options.spawnRipples();
		this.spawnSplashes = options.spawnSplashes();
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		this.yd -= this.gravity;

		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.9800000190734863D;
		this.yd *= 0.9800000190734863D;
		this.zd *= 0.9800000190734863D;

		if(this.lifetime-- <= 0) {
			this.remove();
		}

		if(this.onGround) {
			this.remove();

			if(this.spawnSplashes) {
				TheBetweenlands.createParticle(ParticleRegistry.RAIN.get(), this.level, this.x, this.y, this.z, ParticleFactory.ParticleArgs.get()
					.withColor(this.rCol, this.gCol, this.bCol, this.alpha));
			}

			if(this.spawnRipples) {
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR,
//					BLParticles.WATER_RIPPLE.create(world, this.posX, this.posY + 0.1f, this.posZ,
//						ParticleArgs.get()
//							.withScale(this.particleScale * 1.5f)
//							.withColor(this.particleRed * 1.25f, this.particleGreen * 1.25f, this.particleBlue * 1.5f, Math.min(1, 2 * this.particleAlpha))
//					));
			}

			this.xd *= 0.699999988079071D;
			this.zd *= 0.699999988079071D;
		}

		BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
		FluidState fluidstate = this.level.getFluidState(pos);
		if (!fluidstate.isEmpty()) {
			double d3 = fluidstate.getHeight(this.level, pos);

			if (this.y < d3) {
				this.remove();
			}
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static final class Factory extends ParticleFactory<Factory, DripParticleOptions> {

		private final SpriteSet sprite;

		public Factory(SpriteSet sprites) {
			this.sprite = sprites;
		}

		@Override
		public FancyDripParticle createParticle(DripParticleOptions options, ImmutableParticleArgs args) {
			var drip = new FancyDripParticle(args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, options, args.scale);
			drip.pickSprite(this.sprite);
			return drip;
		}
	}
}
