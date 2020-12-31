package thebetweenlands.client.render.particle.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.util.MathUtils;

public class ParticleWisp extends Particle implements IParticleSpriteReceiver {
	private float prevFlameScale;
	private float flameScale;
	private int brightness;

	private float alphaMultiplier;
	private BlockPos wisp;
	private boolean visible;
	private boolean hidden;

	protected ParticleWisp(World world, double x, double y, double z, double mx, double my, double mz, float scale, int bright, boolean hidden) {
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
		this.wisp = new BlockPos(x, y, z);
		this.hidden = hidden;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		this.particleScale = (this.prevFlameScale + (this.flameScale - this.prevFlameScale) * partialTicks);

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

		float rpx = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
		float rpy = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
		float rpz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

		int brightness = this.getBrightnessForRender(partialTicks);
		int lightmapX = (brightness >> 16) & 65535;
		int lightmapY = brightness & 65535;

		float v1x = -rotationX * scale - rotationXY * scale;
		float v1y = -rotationZ * scale;
		float v1z = -rotationYZ * scale - rotationXZ * scale;
		float v2x = -rotationX * scale + rotationXY * scale;
		float v2y = rotationZ * scale;
		float v2z = -rotationYZ * scale + rotationXZ * scale;
		float v3x = rotationX * scale + rotationXY * scale;
		float v3y = rotationZ * scale;
		float v3z = rotationYZ * scale + rotationXZ * scale;
		float v4x = rotationX * scale - rotationXY * scale;
		float v4y = -rotationZ * scale;
		float v4z = rotationYZ * scale - rotationXZ * scale;

		if(this.particleAngle != 0.0F) {
			float angle = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
			float cos = MathHelper.cos(angle * 0.5F);
			float rdx = MathHelper.sin(angle * 0.5F) * (float) cameraViewDir.x;
			float rdy = MathHelper.sin(angle * 0.5F) * (float) cameraViewDir.y;
			float rdz = MathHelper.sin(angle * 0.5F) * (float) cameraViewDir.z;

			float dotrdrd = cos * cos - dot(rdx, rdy, rdz, rdx, rdy, rdz);

			float dotvrd = 2 * dot(v1x, v1y, v1z, rdx, rdy, rdz);
			float nx = rdx * dotvrd + v1x * dotrdrd + crossX(rdx, rdy, rdz, v1x, v1y, v1z) * 2 * cos;
			float ny = rdy * dotvrd + v1y * dotrdrd + crossY(rdx, rdy, rdz, v1x, v1y, v1z) * 2 * cos;
			float nz = rdz * dotvrd + v1z * dotrdrd + crossZ(rdx, rdy, rdz, v1x, v1y, v1z) * 2 * cos;
			v1x = nx;
			v1y = ny;
			v1z = nz;

			dotvrd = 2 * dot(v2x, v2y, v2z, rdx, rdy, rdz);
			nx = rdx * dotvrd + v2x * dotrdrd + crossX(rdx, rdy, rdz, v2x, v2y, v2z) * 2 * cos;
			ny = rdy * dotvrd + v2y * dotrdrd + crossY(rdx, rdy, rdz, v2x, v2y, v2z) * 2 * cos;
			nz = rdz * dotvrd + v2z * dotrdrd + crossZ(rdx, rdy, rdz, v2x, v2y, v2z) * 2 * cos;
			v2x = nx;
			v2y = ny;
			v2z = nz;

			dotvrd = 2 * dot(v3x, v3y, v3z, rdx, rdy, rdz);
			nx = rdx * dotvrd + v3x * dotrdrd + crossX(rdx, rdy, rdz, v3x, v3y, v3z) * 2 * cos;
			ny = rdy * dotvrd + v3y * dotrdrd + crossY(rdx, rdy, rdz, v3x, v3y, v3z) * 2 * cos;
			nz = rdz * dotvrd + v3z * dotrdrd + crossZ(rdx, rdy, rdz, v3x, v3y, v3z) * 2 * cos;
			v3x = nx;
			v3y = ny;
			v3z = nz;

			dotvrd = 2 * dot(v4x, v4y, v4z, rdx, rdy, rdz);
			nx = rdx * dotvrd + v4x * dotrdrd + crossX(rdx, rdy, rdz, v4x, v4y, v4z) * 2 * cos;
			ny = rdy * dotvrd + v4y * dotrdrd + crossY(rdx, rdy, rdz, v4x, v4y, v4z) * 2 * cos;
			nz = rdz * dotvrd + v4z * dotrdrd + crossZ(rdx, rdy, rdz, v4x, v4y, v4z) * 2 * cos;
			v4x = nx;
			v4y = ny;
			v4z = nz;
		}

		buff.pos((double)rpx + v1x, (double)rpy + v1y, (double)rpz + v1z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * this.alphaMultiplier).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + v2x, (double)rpy + v2y, (double)rpz + v2z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * this.alphaMultiplier).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + v3x, (double)rpy + v3y, (double)rpz + v3z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * this.alphaMultiplier).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + v4x, (double)rpy + v4y, (double)rpz + v4z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha * this.alphaMultiplier).lightmap(lightmapX, lightmapY).endVertex();
	}

	private static float crossX(float x1, float y1, float z1, float x2, float y2, float z2) {
		return y1 * z2 - z1 * y2;
	}

	private static float crossY(float x1, float y1, float z1, float x2, float y2, float z2) {
		return z1 * x2 - x1 * z2;
	}

	private static float crossZ(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * y2 - y1 * x2;
	}

	private static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		if(this.brightness < 0) {
			return super.getBrightnessForRender(partialTicks);
		}
		return this.brightness;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevFlameScale = this.flameScale;

		this.move(this.motionX, this.motionY, this.motionZ);

		this.motionX *= 0.96;
		this.motionZ *= 0.96;

		if(this.particleAge++ >= this.particleMaxAge || this.flameScale <= 0) {
			this.setExpired();
		}
		if(this.particleAge != 0) {
			if(this.flameScale > 0) {
				this.flameScale -= 0.025;
			}
			this.motionY += 0.00008;
		}

		this.move(this.motionX, this.motionY, this.motionZ);

		if(this.hidden) {
			IBlockState state = this.world.getBlockState(this.wisp);
			this.visible = state.getBlock() instanceof BlockWisp ? state.getValue(BlockWisp.VISIBLE) : false;
	
			if(!this.visible) {
				this.alphaMultiplier = 1.0f - MathHelper.sin(MathUtils.PI / 20 * MathHelper.clamp(getDistanceToViewer(this.posX, this.posY, this.posZ), 10, 20));
			} else {
				this.alphaMultiplier = 1.0f;
			}
		} else {
			this.alphaMultiplier = 1.0f;
		}
		this.alphaMultiplier *= Math.min(this.particleAge * 0.2f, 1);
	}

	public static float getDistanceToViewer(double x, double y, double z) {
		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		if(entity != null) {
			double dx = entity.posX - x;
			double dy = entity.posY - y;
			double dz = entity.posZ - z;
			return MathHelper.sqrt((float) (dx * dx + dy * dy + dz * dz));
		}
		return 0.0F;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleWisp> {
		public Factory() {
			super(ParticleWisp.class, ParticleTextureStitcher.create(ParticleWisp.class, new ResourceLocation("thebetweenlands:particle/wisp")));
		}

		@Override
		public ParticleWisp createParticle(ImmutableParticleArgs args) {
			return new ParticleWisp(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale, args.data.getInt(0), args.data.getBool(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(255, true);
		}
	}
}
