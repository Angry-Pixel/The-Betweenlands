package thebetweenlands.client.render.particle.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticleLeafSwirl extends ParticleSwirl implements IParticleSpriteReceiver {
	protected TextureAnimation animation;
	protected Entity target;

	public ParticleLeafSwirl(World world, double x, double y, double z, int maxAge, float scale, float progress, Entity target) {
		super(world, x, y, z, maxAge, scale, progress);

		this.animation = new TextureAnimation().setRandomStart(this.rand);

		this.target = target;

		this.setOffset(0, -1.6D, 0);
		this.setTargetMotion(this.target.posX - this.target.lastTickPosX, this.target.posY - this.target.lastTickPosY, this.target.posZ - this.target.lastTickPosZ);
		this.setTarget(this.target.posX, this.target.posY + this.target.getEyeHeight() / 2.0D, this.target.posZ);
	}

	@Override
	public void onUpdate() {
		if(!this.target.isEntityAlive()) {
			this.setExpired();
		}

		this.setTargetMotion(this.target.posX - this.target.lastTickPosX, this.target.posY - this.target.lastTickPosY, this.target.posZ - this.target.lastTickPosZ);
		this.setTarget(this.target.posX, this.target.posY + this.target.getEyeHeight() / 2.0D, this.target.posZ);

		this.animation.update();
		this.setParticleTexture(this.animation.getCurrentSprite());
		
		super.onUpdate();
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		this.animation.setFrames(frames[0]);
		if(this.particleTexture == null) {
			this.setParticleTexture(frames[0][0].getSprite());
		}
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleLeafSwirl> {
		public Factory() {
			super(ParticleLeafSwirl.class, ParticleTextureStitcher.create(ParticleLeafSwirl.class, new ResourceLocation("thebetweenlands:particle/leaf")).setSplitAnimations(true));
		}

		@Override
		public ParticleLeafSwirl createParticle(ImmutableParticleArgs args) {
			return new ParticleLeafSwirl(args.world, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}
	
	public static final class FactoryEmberSwirl extends ParticleFactory<FactoryEmberSwirl, ParticleLeafSwirl> {
		public FactoryEmberSwirl() {
			super(ParticleLeafSwirl.class, ParticleTextureStitcher.create(ParticleLeafSwirl.class, new ResourceLocation("thebetweenlands:particle/ember_swirl")).setSplitAnimations(true));
		}

		@Override
		public ParticleLeafSwirl createParticle(ImmutableParticleArgs args) {
			return new ParticleLeafSwirl(args.world, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}
	
	public static final class FactorySludgeSwirl extends ParticleFactory<FactorySludgeSwirl, ParticleLeafSwirl> {
		public FactorySludgeSwirl() {
			super(ParticleLeafSwirl.class, ParticleTextureStitcher.create(ParticleLeafSwirl.class, new ResourceLocation("thebetweenlands:particle/smooth_smoke")));
		}

		@Override
		public ParticleLeafSwirl createParticle(ImmutableParticleArgs args) {
			return new ParticleLeafSwirl(args.world, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}
}
