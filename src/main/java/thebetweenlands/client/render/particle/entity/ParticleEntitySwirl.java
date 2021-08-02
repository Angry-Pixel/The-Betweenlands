package thebetweenlands.client.render.particle.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticleEntitySwirl extends ParticleSwirl implements IParticleSpriteReceiver {
	protected TextureAnimation animation;
	protected Entity target;
	protected double targetOffsetX, targetOffsetY, targetOffsetZ;

	public ParticleEntitySwirl(World world, double x, double y, double z, int maxAge, float scale, float progress, Entity target) {
		super(world, x, y, z, maxAge, scale, progress);

		this.animation = new TextureAnimation().setRandomStart(this.rand);

		this.target = target;

		//this.setOffset(0, -1.6D, 0);
		this.updateTarget();
	}
	
	public ParticleEntitySwirl setTargetOffset(double x, double y, double z) {
		this.targetOffsetX = x;
		this.targetOffsetY = y;
		this.targetOffsetZ = z;
		return this;
	}
	
	public void updateTarget() {
		this.setTargetMotion(this.target.posX - this.target.lastTickPosX, this.target.posY - this.target.lastTickPosY, this.target.posZ - this.target.lastTickPosZ);
		this.setTarget(this.target.posX + this.targetOffsetX, this.target.posY + this.target.getEyeHeight() / 2.0D + this.targetOffsetY, this.target.posZ + this.targetOffsetZ);
	}

	@Override
	public void onUpdate() {
		if(!this.target.isEntityAlive()) {
			this.setExpired();
		}

		this.updateTarget();

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

	public static final class Factory extends ParticleFactory<Factory, ParticleEntitySwirl> {
		public Factory() {
			super(ParticleEntitySwirl.class, ParticleTextureStitcher.create(ParticleEntitySwirl.class, new ResourceLocation("thebetweenlands:particle/leaf")).setSplitAnimations(true));
		}

		@Override
		public ParticleEntitySwirl createParticle(ImmutableParticleArgs args) {
			return new ParticleEntitySwirl(args.world, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2)).setTargetOffset(0D, -1.6D, 0D);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}
	
	public static final class FactoryEmberSwirl extends ParticleFactory<FactoryEmberSwirl, ParticleEntitySwirl> {
		public FactoryEmberSwirl() {
			super(ParticleEntitySwirl.class, ParticleTextureStitcher.create(ParticleEntitySwirl.class, new ResourceLocation("thebetweenlands:particle/ember_swirl")).setSplitAnimations(true));
		}

		@Override
		public ParticleEntitySwirl createParticle(ImmutableParticleArgs args) {
			return new ParticleEntitySwirl(args.world, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2)).setTargetOffset(0D, -1.6D, 0D);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}
	
	public static final class FactorySludgeSwirl extends ParticleFactory<FactorySludgeSwirl, ParticleEntitySwirl> {
		public FactorySludgeSwirl() {
			super(ParticleEntitySwirl.class, ParticleTextureStitcher.create(ParticleEntitySwirl.class, new ResourceLocation("thebetweenlands:particle/smooth_smoke")));
		}

		@Override
		public ParticleEntitySwirl createParticle(ImmutableParticleArgs args) {
			return new ParticleEntitySwirl(args.world, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2)).setTargetOffset(0D, -1.6D, 0D);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}
	
	public static final class FactoryChiromawSwirl extends ParticleFactory<FactoryChiromawSwirl, ParticleEntitySwirl> {
		public FactoryChiromawSwirl() {
			super(ParticleEntitySwirl.class, ParticleTextureStitcher.create(ParticleEntitySwirl.class, new ResourceLocation("thebetweenlands:particle/chiromaw_transform")).setSplitAnimations(true));
		}

		@Override
		public ParticleEntitySwirl createParticle(ImmutableParticleArgs args) {
			return new ParticleEntitySwirl(args.world, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2)).setTargetOffset(0D, -1.6D, 0D);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}

	public static final class FactoryFishSwirl extends ParticleFactory<FactoryFishSwirl, ParticleEntitySwirl> {
		public FactoryFishSwirl() {
			super(ParticleEntitySwirl.class, ParticleTextureStitcher.create(ParticleEntitySwirl.class, new ResourceLocation("thebetweenlands:particle/fish_swirl")).setSplitAnimations(true));
		}

		@Override
		public ParticleEntitySwirl createParticle(ImmutableParticleArgs args) {
			return new ParticleEntitySwirl(args.world, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2)).setTargetOffset(0D, 0D, 0D);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}
}
