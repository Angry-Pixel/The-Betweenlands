package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.common.entity.mobs.EntityGasCloud;

public class ParticleGasCloud extends Particle implements ParticleTextureStitcher.IParticleSpriteReceiver {
	protected boolean rotateReversed = false;
	protected EntityGasCloud cloud;

	protected ParticleGasCloud(World world, double x, double y, double z, double vecX, double vecY, double vecZ, float startRotation, float scale) {
		this(world, null, x, y,  z, vecX, vecY, vecZ, startRotation, scale);
	}
	
	protected ParticleGasCloud(World world, EntityGasCloud cloud, double x, double y, double z, double vecX, double vecY, double vecZ, float startRotation, float scale) {
		super(world, x, y, z, vecX, vecY, vecZ);
		this.cloud = cloud;
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
		particleScale = scale;
		canCollide = false; //Collision
		prevParticleAngle = particleAngle = startRotation; //Rotation
		if(startRotation < 0.0F) {
			rotateReversed = true;
		}
	}

	/**
	 * Renders this particle with the UVs [0, 0] to [1, 1]
	 */
	@Override
	public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		if(this.cloud != null && !this.cloud.isEntityAlive()) {
			int[] gasColor = this.cloud.getGasColor();
			float fade = (1.0F - (this.cloud.deathTime / 80.0F));
			this.setAlphaF((float)Math.pow(fade, 2) * gasColor[3] / 255.0F);
			this.setRBGColorF(gasColor[0] / 255.0F * fade, gasColor[1] / 255.0F * fade, gasColor[2] / 255.0F * fade);
		}
		
		float minU = 0.0F;
		float maxU = 1.0F;
		float minV = 0.0F;
		float maxV = 1.0F;
		float scale = 0.1F * this.particleScale;

		float interpX = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float interpY = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float interpZ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		int brightness = this.getBrightnessForRender(partialTicks);
		int lightmapX = brightness >> 16 & 65535;
		int lightmapY = brightness & 65535;
		Vec3d[] scaledRotations = new Vec3d[] {new Vec3d((double)(-rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(-rotationYZ * scale - rotationXZ * scale)), new Vec3d((double)(-rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(-rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(rotationYZ * scale - rotationXZ * scale))};

		if (this.particleAngle != 0.0F) {
			float interpRoll = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
			float f9 = MathHelper.cos(interpRoll * 0.5F);
			float f10 = MathHelper.sin(interpRoll * 0.5F) * (float)cameraViewDir.x;
			float f11 = MathHelper.sin(interpRoll * 0.5F) * (float)cameraViewDir.y;
			float f12 = MathHelper.sin(interpRoll * 0.5F) * (float)cameraViewDir.z;
			Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

			for (int l = 0; l < 4; ++l) {
				scaledRotations[l] = vec3d.scale(2.0D * scaledRotations[l].dotProduct(vec3d)).add(scaledRotations[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(scaledRotations[l]).scale((double)(2.0F * f9)));
			}
		}

		int fadeInDuration = 15;
		int fadeOutStart = 45;
		int fadeOutDuration = this.particleMaxAge - fadeOutStart;
		
		float alpha;
		
		if(this.particleAge < fadeOutStart) {
			alpha = this.particleAlpha * Math.min((float)this.particleAge / (float)fadeInDuration, 1.0F);
		} else {
			alpha = this.particleAlpha * (1.0F - Math.min((float)(this.particleAge - fadeOutStart) / (float)fadeOutDuration, 1.0F));
		}
		
		worldRendererIn.pos((double)interpX + scaledRotations[0].x, (double)interpY + scaledRotations[0].y, (double)interpZ + scaledRotations[0].z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
		worldRendererIn.pos((double)interpX + scaledRotations[1].x, (double)interpY + scaledRotations[1].y, (double)interpZ + scaledRotations[1].z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
		worldRendererIn.pos((double)interpX + scaledRotations[2].x, (double)interpY + scaledRotations[2].y, (double)interpZ + scaledRotations[2].z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
		worldRendererIn.pos((double)interpX + scaledRotations[3].x, (double)interpY + scaledRotations[3].y, (double)interpZ + scaledRotations[3].z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevParticleAngle = this.particleAngle;
		this.particleAngle += this.rotateReversed ? -0.015F : 0.015F;
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
		protected void setDefaultArguments(World world, double x, double y, double z, ParticleArgs<?> args) { 
			args.withData(null, (world.rand.nextFloat() * 2.0F * (float)Math.PI) * 2.0F - 2.0F * (float)Math.PI).withScale((world.rand.nextFloat() * 0.75F + 0.6F) * 10.0F);
		}

		@Override
		public ParticleGasCloud createParticle(ImmutableParticleArgs args) {
			return new ParticleGasCloud(args.world, args.data.getObject(EntityGasCloud.class, 0), args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getFloat(1), args.scale);
		}
	}
}
