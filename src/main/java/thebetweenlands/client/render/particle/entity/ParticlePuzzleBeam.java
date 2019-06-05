package thebetweenlands.client.render.particle.entity;

import java.util.Random;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;

@SideOnly(Side.CLIENT)
public class ParticlePuzzleBeam extends Particle implements IParticleSpriteReceiver {
	private float initScale = 0;
	private float initAlpha = 0;

	public ParticlePuzzleBeam(World worldIn, double x, double y, double z, double vx, double vy, double vz, float scale, int lifetime) {
		super(worldIn, x, y, z, 0, 0, 0);
		this.particleMaxAge = (int) ((float) lifetime * 0.5f);
		this.particleScale = scale;
		this.initScale = scale;
		this.motionX = vx * 2.0f;
		this.motionY = vy * 2.0f;
		this.motionZ = vz * 2.0f;
		this.particleAngle = 2.0f * (float) Math.PI;
	}

	@Override
	public void setAlphaF(float alpha) {
		super.setAlphaF(alpha);
		this.initAlpha = alpha;
	}

	@Override
	public int getBrightnessForRender(float pTicks) {
		return 255;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(this.particleRed > 1) {
			this.particleRed /= 255.0f;
		}

		if(this.particleGreen > 1) {
			this.particleGreen /= 255.0f;
		}

		if(this.particleBlue > 1) {
			this.particleBlue /= 255.0f;
		}
		
		if(this.particleAge > this.particleMaxAge - 10) {
			this.particleAlpha = (this.particleMaxAge - this.particleAge) / 10.0F * this.initAlpha;
		} else if(this.particleAge < 10) {
			this.particleAlpha = this.particleAge / 10.0F * this.initAlpha;
		} else {
			this.particleAlpha = this.initAlpha;
		}
		
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			double distFromCam = entityIn.getDistance(this.posX, this.posY, this.posZ);
			if(distFromCam < 8) {
	        	ShaderHelper.INSTANCE.require();
	        	
	        	float strength = 1.0f * Math.min(1, (2.0f - (float)(distFromCam - 6.0f)) / 2.0f) * this.particleAlpha * 3.5f;
	        	
	        	double rx = this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
				double ry = this.prevPosY + (this.posY - this.prevPosY) * partialTicks;
				double rz = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks;
	            ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry, rz,
	                    2.6f, this.particleRed * strength, this.particleGreen * strength, this.particleBlue * strength));
			}
        }
		
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}
	
	public static Random random = new Random();

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (random.nextInt(6) == 0) {
			this.particleAge++;
		}
		float lifeCoeff = (float) this.particleAge / (float) this.particleMaxAge;
		this.particleScale = initScale - initScale * lifeCoeff;
		this.prevParticleAngle = particleAngle;
		particleAngle += 0.5f;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticlePuzzleBeam> {
		public Factory() {
			super(ParticlePuzzleBeam.class, ParticleTextureStitcher.create(ParticlePuzzleBeam.class, new ResourceLocation("thebetweenlands:particle/particle_beam")));
		}

		@Override
		public ParticlePuzzleBeam createParticle(ImmutableParticleArgs args) {
			return new ParticlePuzzleBeam(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale, args.data.getInt(0));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(20);
		}
	}
}
