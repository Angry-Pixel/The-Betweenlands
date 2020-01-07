package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleRingOfRecruitmentState extends Particle implements IParticleSpriteReceiver {
	private double offsetX, offsetY, offsetZ, radius;
	private final EntityLivingBase entity;
	private float maxAlpha = 1.0f;

	public ParticleRingOfRecruitmentState(World world, EntityLivingBase entity, double offsetX, double offsetY, double offsetZ, float scale) {
		super(world, entity.posX + offsetX, entity.posY + offsetY, entity.posZ + offsetZ);
		this.motionX = this.motionY = this.motionZ = 0;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
		this.entity = entity;
		this.particleMaxAge = 60;
		this.particleScale = scale;
		this.canCollide = false;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void setAlphaF(float alpha) {
		this.maxAlpha = alpha;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if(this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}

		this.setPosition(this.entity.posX + this.offsetX, this.entity.posY + this.offsetY, this.entity.posZ + this.offsetZ);
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(this.particleAge > this.particleMaxAge - 10) {
			this.particleAlpha = (this.particleMaxAge - this.particleAge) / 10.0F * this.maxAlpha;
		} else if(this.particleAge < 10) {
			this.particleAlpha = this.particleAge / 10.0F * this.maxAlpha;
		} else {
			this.particleAlpha = this.maxAlpha;
		}

		double prevPosY = this.posY;
		double prevPrevPosY = this.prevPosY;

		double floatOffset = Math.sin((entityIn.ticksExisted + partialTicks) * 0.05f) * 0.05f;

		this.posY += floatOffset;
		this.prevPosY += floatOffset;

		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);

		this.posY = prevPosY;
		this.prevPosY = prevPrevPosY;
	}

	public static final class FactoryFollow extends ParticleFactory<FactoryFollow, ParticleRingOfRecruitmentState> {
		public FactoryFollow() {
			super(ParticleRingOfRecruitmentState.class, ParticleTextureStitcher.create(ParticleRingOfRecruitmentState.class, new ResourceLocation("thebetweenlands:particle/ring_of_recruitment_follow")));
		}

		@Override
		public ParticleRingOfRecruitmentState createParticle(ImmutableParticleArgs args) {
			return new ParticleRingOfRecruitmentState(args.world, args.data.getObject(EntityLivingBase.class, 0), args.x, args.y, args.z, args.scale);
		}
	}

	public static final class FactoryGuard extends ParticleFactory<FactoryGuard, ParticleRingOfRecruitmentState> {
		public FactoryGuard() {
			super(ParticleRingOfRecruitmentState.class, ParticleTextureStitcher.create(ParticleRingOfRecruitmentState.class, new ResourceLocation("thebetweenlands:particle/ring_of_recruitment_guard")));
		}

		@Override
		public ParticleRingOfRecruitmentState createParticle(ImmutableParticleArgs args) {
			return new ParticleRingOfRecruitmentState(args.world, args.data.getObject(EntityLivingBase.class, 0), args.x, args.y, args.z, args.scale);
		}
	}

	public static final class FactoryStay extends ParticleFactory<FactoryStay, ParticleRingOfRecruitmentState> {
		public FactoryStay() {
			super(ParticleRingOfRecruitmentState.class, ParticleTextureStitcher.create(ParticleRingOfRecruitmentState.class, new ResourceLocation("thebetweenlands:particle/ring_of_recruitment_stay")));
		}

		@Override
		public ParticleRingOfRecruitmentState createParticle(ImmutableParticleArgs args) {
			return new ParticleRingOfRecruitmentState(args.world, args.data.getObject(EntityLivingBase.class, 0), args.x, args.y, args.z, args.scale);
		}
	}
}
