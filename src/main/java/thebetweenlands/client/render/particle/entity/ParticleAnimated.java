package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticleAnimated extends Particle implements IParticleSpriteReceiver {
	protected TextureAnimation animation;

	protected final int lightLevel;
	
	public ParticleAnimated(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale) {
		this(world, x, y, z, mx, my, mz, maxAge, scale, true, 0);
	}

	public ParticleAnimated(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale, boolean randomStart) {
		this(world, x, y, z, mx, my, mz, maxAge, scale, randomStart, 0);
	}
	
	public ParticleAnimated(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale, int lightLevel) {
		this(world, x, y, z, mx, my, mz, maxAge, scale, true, lightLevel);
	}

	public ParticleAnimated(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale, boolean randomStart, int lightLevel) {
		super(world, x, y, z);
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.particleScale = scale;
		this.animation = new TextureAnimation();
		if(randomStart) {
			this.animation.setRandomStart(this.rand);
		}
		this.particleMaxAge = maxAge;
		this.lightLevel = lightLevel;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		if (this.animation != null && frames != null) {
			this.animation.setFrames(frames[0]);
			if(this.particleMaxAge < 0) {
				this.particleMaxAge = this.animation.getTotalDuration() - 1;
			}
			if (this.particleTexture == null) {
				this.setParticleTexture(frames[0][0].getSprite());
			}
		}
	}
	
	@Override
	public int getBrightnessForRender(float partialTick) {
		BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
        return this.world.isBlockLoaded(blockpos) ? this.world.getCombinedLight(blockpos, this.lightLevel) : 0;
	}

	@Override
	public void onUpdate() {
		this.animation.update();
		this.setParticleTexture(this.animation.getCurrentSprite());

		super.onUpdate();
	}

	public static final class GenericFactory extends ParticleFactory<GenericFactory, ParticleAnimated> {
		public GenericFactory(ResourceLocation texture) {
			super(ParticleAnimated.class, ParticleTextureStitcher.create(ParticleAnimated.class, texture).setSplitAnimations(true));
		}

		@Override
		public ParticleAnimated createParticle(ImmutableParticleArgs args) {
			return new ParticleAnimated(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale, args.data.getBool(1), args.data.getInt(2));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(-1, false, 0);
		}
	}

	public static final class PortalFactory extends ParticleFactory<PortalFactory, ParticleAnimated> {
		public PortalFactory() {
			super(ParticleAnimated.class, ParticleTextureStitcher.create(ParticleAnimated.class, new ResourceLocation("thebetweenlands:particle/portal")).setSplitAnimations(true));
		}

		@Override
		public ParticleAnimated createParticle(ImmutableParticleArgs args) {
			return new ParticleAnimated(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale, 0);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(40);
		}
	}

	public static final class SpawnerFactory extends ParticleFactory<PortalFactory, ParticleAnimated> {
		public SpawnerFactory() {
			super(ParticleAnimated.class, ParticleTextureStitcher.create(ParticleAnimated.class, new ResourceLocation("thebetweenlands:particle/spawner")).setSplitAnimations(true));
		}

		@Override
		public ParticleAnimated createParticle(ImmutableParticleArgs args) {
			return new ParticleAnimated(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale) {
				private float startAlpha;

				{
					this.startAlpha = this.particleAlpha;
				}

				@Override
				public void setAlphaF(float alpha) {
					super.setAlphaF(alpha);
					this.startAlpha = alpha;
				}

				@Override
				public void onUpdate() {
					super.onUpdate();

					if(this.particleAge > this.particleMaxAge - 40) {
						this.particleAlpha = (this.startAlpha * (this.particleMaxAge - this.particleAge) / 40.0F);
					}
				}
			};
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(80);
		}
	}
	
	public static final class ShadowGhostFactory extends ParticleFactory<PortalFactory, ParticleAnimated> {
		public ShadowGhostFactory() {
			super(ParticleAnimated.class, ParticleTextureStitcher.create(ParticleAnimated.class, new ResourceLocation("thebetweenlands:particle/shadow_ghosts")).setSplitAnimations(true));
		}

		@Override
		public ParticleAnimated createParticle(ImmutableParticleArgs args) {
			return new ParticleAnimated(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(40);
		}
	}
}
