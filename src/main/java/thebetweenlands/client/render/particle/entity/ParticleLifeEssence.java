package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticleLifeEssence extends Particle implements IParticleSpriteReceiver {
	private static final int MAX_PARTICLES = 9;

	private double offsetX, offsetY, offsetZ, radius;
	private final EntityLivingBase entity;

	private TextureAtlasSprite glowSprite;
	private TextureAnimation[] animations;
	private int rotationTicks;
	private int particles = MAX_PARTICLES;

	public ParticleLifeEssence(World world, EntityLivingBase entity, double offsetX, double offsetY, double offsetZ, double radius, int rotationTicks) {
		super(world, entity.posX + offsetX, entity.posY + offsetY, entity.posZ + offsetZ);
		this.motionX = this.motionY = this.motionZ = 0;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.radius = radius;
		this.entity = entity;
		this.particles = MathHelper.clamp(MathHelper.ceil(entity.getHealth() / entity.getMaxHealth() * MAX_PARTICLES), 0, MAX_PARTICLES);
		this.rotationTicks = rotationTicks;
		this.particleMaxAge = 60;
		this.canCollide = false;
		this.animations = new TextureAnimation[MAX_PARTICLES];
		for(int i = 0; i < MAX_PARTICLES; i++) {
			this.animations[i] = new TextureAnimation().setRandomStart(entity.getRNG());
		}
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		for(int i = 0; i < MAX_PARTICLES; i++) {
			this.animations[i].setFrames(frames[0]);
		}
		this.glowSprite = frames[1][0].getSprite();
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if(this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}

		this.particles = MathHelper.clamp(MathHelper.ceil(this.entity.getHealth() / this.entity.getMaxHealth() * MAX_PARTICLES), 0, MAX_PARTICLES);

		this.rotationTicks++;

		for(int i = 0; i < this.particles; i++) {
			this.animations[i].update();
		}

		this.setPosition(this.entity.posX + this.offsetX, this.entity.posY + this.offsetY, this.entity.posZ + this.offsetZ);
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(this.particleAge > this.particleMaxAge - 10) {
			this.particleAlpha = (this.particleMaxAge - this.particleAge) / 10.0F;
		} else if(this.particleAge < 10) {
			this.particleAlpha = this.particleAge / 10.0F;
		} else {
			this.particleAlpha = 1.0F;
		}

		double px = this.posX;
		double py = this.posY;
		double pz = this.posZ;
		double ppx = this.prevPosX;
		double ppy = this.prevPosY;
		double ppz = this.prevPosZ;

		float interval = (float)(Math.PI) * 2.0F / this.particles;
		float prevAngle = (this.rotationTicks - 1) / 120.0F * (float)(Math.PI) * 2.0F;
		float angle = this.rotationTicks / 120.0F * (float)(Math.PI) * 2.0F;

		float scale = (float) (this.radius) * 2.0F * (0.25F + this.particles / (float)MAX_PARTICLES * 0.75F);

		for(int i = 0; i < this.particles; i++) {
			double prevXo = Math.cos(prevAngle + interval * i) * this.radius;
			double prevYo = Math.cos(prevAngle + interval * i * 5.678F) * 0.05F;
			double prevZo = Math.sin(prevAngle + interval * i) * this.radius;
			double xo = Math.cos(angle + interval * i) * this.radius;
			double yo = Math.cos(angle + interval * i * 5.678F) * 0.05F;
			double zo = Math.sin(angle + interval * i) * this.radius;

			this.prevPosX = ppx + prevXo;
			this.prevPosZ = ppz + prevZo;
			this.posX = px + xo;
			this.posZ = pz + zo;

			this.prevPosY = ppy + prevYo - scale * 0.025f;
			this.posY = py + yo - scale * 0.025f;

			this.particleRed = 0.1F;
			this.particleGravity = 0.1F;
			this.particleBlue = 1.0F;
			this.particleScale = scale * 2.5F;
			this.particleTexture = this.glowSprite;
			float prevAlpha = this.particleAlpha;
			this.particleAlpha *= 0.05F;

			super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);

			this.particleAlpha *= 2.0F;
			this.particleRed = 1.0F;
			this.particleGravity = 1.0F;
			this.particleBlue = 1.0F;
			this.particleScale = scale * 1.25F;

			super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);

			this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
			this.particleAlpha = prevAlpha;

			this.prevPosY = ppy + prevYo;
			this.posY = py + yo;

			this.particleScale = scale;
			this.particleTexture = this.animations[i].getCurrentSprite();

			super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
		}

		this.posX = px;
		this.posY = py;
		this.posZ = pz;
		this.prevPosX = ppx;
		this.prevPosY = ppy;
		this.prevPosZ = ppz;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleLifeEssence> {
		public Factory() {
			super(ParticleLifeEssence.class, ParticleTextureStitcher.create(ParticleLifeEssence.class,
					new ResourceLocation("thebetweenlands:particle/life_essence"),
					new ResourceLocation("thebetweenlands:particle/wisp"))
					.setSplitAnimations(true));
		}

		@Override
		public ParticleLifeEssence createParticle(ImmutableParticleArgs args) {
			return new ParticleLifeEssence(args.world, args.data.getObject(EntityLivingBase.class, 0), args.x, args.y, args.z, args.scale, args.data.getInt(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withDataBuilder().setData(1, 20).buildData();
		}
	}
}
