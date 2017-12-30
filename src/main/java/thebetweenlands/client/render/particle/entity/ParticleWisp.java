package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.util.MathUtils;

//TODO: Still a WIP
public class ParticleWisp extends Particle implements IParticleSpriteReceiver {
	private float prevFlameScale;
	private float flameScale;
	private int brightness;

	protected ParticleWisp(World world, double x, double y, double z, double mx, double my, double mz, float scale, int bright) {
		super(world, x, y, z, mx, my, mz);
		this.motionX = this.motionX * 0.01D + mx;
		this.motionY = this.motionY * 0.01D + my;
		this.motionZ = this.motionZ * 0.01D + mz;
		x += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
		y += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
		z += (this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F;
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.flameScale = scale;
		this.particleMaxAge = (int) (8 / (Math.random() * 0.8 + 0.2)) + 1000;
		this.brightness = bright;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float currentX = (float) (prevPosX + (posX - prevPosX) * partialTicks);
		float currentY = (float) (prevPosY + (posY - prevPosY) * partialTicks);
		float currentZ = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks);
		this.particleScale = (this.prevFlameScale + (this.flameScale - this.prevFlameScale) * partialTicks);
		float distance = 0.0F;
		if(!BlockWisp.canSee(this.world, new BlockPos(this.posX, this.posY, this.posZ))) {
			distance = MathHelper.clamp(getDistanceToViewer(currentX, currentY, currentZ, partialTicks), 10, 20);
		}
		this.setAlphaF(1.0F - MathHelper.sin(MathUtils.PI / 20 * distance));

		float scale = 0.1F * this.particleScale;

		float minU = this.particleTexture.getMinU();
		float maxU = this.particleTexture.getMaxU();
		float minV = this.particleTexture.getMinV();
		float maxV = this.particleTexture.getMaxV();
		
		//remove 1px border to avoid artifacts from smooth filtering
		float borderU = (maxU - minU) / this.particleTexture.getIconWidth();
		float borderV = (maxV - minV) / this.particleTexture.getIconHeight();

		minU += borderU;
		maxU -= borderU;
		minV += borderV;
		maxV -= borderV;
		
		float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;
		Vec3d[] avec3d = new Vec3d[] {new Vec3d((double)(-rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(-rotationYZ * scale - rotationXZ * scale)), new Vec3d((double)(-rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(-rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(rotationYZ * scale - rotationXZ * scale))};

		if (this.particleAngle != 0.0F)
		{
			float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
			float f9 = MathHelper.cos(f8 * 0.5F);
			float f10 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.x;
			float f11 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.y;
			float f12 = MathHelper.sin(f8 * 0.5F) * (float)cameraViewDir.z;
			Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

			for (int l = 0; l < 4; ++l)
			{
				avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
			}
		}

		buff.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		buff.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		buff.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
		buff.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		if(this.brightness < 0) {
			return super.getBrightnessForRender(partialTicks);
		}
		return this.brightness;
	}

	public static float getDistanceToViewer(double x, double y, double z, float partialRenderTicks) {
		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		if(entity != null) {
			double dx = (float) (entity.prevPosX + (entity.posX - entity.prevPosX) * partialRenderTicks) - x;
			double dy = (float) (entity.prevPosY + (entity.posY - entity.prevPosY) * partialRenderTicks) - y;
			double dz = (float) (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialRenderTicks) - z;
			return MathHelper.sqrt((float) (dx * dx + dy * dy + dz * dz));
		}
		return 0.0F;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevFlameScale = this.flameScale;

		move(this.motionX, this.motionY, this.motionZ);

		this.motionX *= 0.96;
		this.motionZ *= 0.96;

		if (this.particleAge++ >= this.particleMaxAge || this.flameScale <= 0) {
			this.setExpired();
		}
		if (this.particleAge != 0) {
			if (this.flameScale > 0) {
				this.flameScale -= 0.025;
			}
			this.motionY += 0.00008;
		}

		move(this.motionX, this.motionY, this.motionZ);
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleWisp> {
		public Factory() {
			super(ParticleWisp.class, ParticleTextureStitcher.create(ParticleWisp.class, new ResourceLocation("thebetweenlands:particle/wisp")));
		}

		@Override
		public ParticleWisp createParticle(ImmutableParticleArgs args) {
			return new ParticleWisp(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale, args.data.getInt(0));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(255);
		}
	}
}

