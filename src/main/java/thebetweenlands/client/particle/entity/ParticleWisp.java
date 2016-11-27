package thebetweenlands.client.particle.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.ParticleTextureStitcher;
import thebetweenlands.client.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
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
	public void renderParticle(VertexBuffer buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
		float currentX = (float) (prevPosX + (posX - prevPosX) * partialTicks);
		float currentY = (float) (prevPosY + (posY - prevPosY) * partialTicks);
		float currentZ = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks);
		this.particleScale = (this.prevFlameScale + (this.flameScale - this.prevFlameScale) * partialTicks);
		float distance = 0.0F;
		if(!BlockWisp.canSee(this.worldObj)) {
			distance = MathHelper.clamp_float(getDistanceToViewer(currentX, currentY, currentZ, partialTicks), 10, 20);
		}
		this.setAlphaF(1.0F - MathHelper.sin(MathUtils.PI / 20 * distance));
		super.renderParticle(buff, entityIn, partialTicks, rx, rz, ryz, rxy, rxz);
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
		double dx = (float) (entity.prevPosX + (entity.posX - entity.prevPosX) * partialRenderTicks) - x;
		double dy = (float) (entity.prevPosY + (entity.posY - entity.prevPosY) * partialRenderTicks) - y;
		double dz = (float) (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialRenderTicks) - z;
		return MathHelper.sqrt_float((float) (dx * dx + dy * dy + dz * dz));
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevFlameScale = this.flameScale;

		moveEntity(this.motionX, this.motionY, this.motionZ);

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

		moveEntity(this.motionX, this.motionY, this.motionZ);
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

