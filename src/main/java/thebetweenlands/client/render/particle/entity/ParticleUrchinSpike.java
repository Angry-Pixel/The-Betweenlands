package thebetweenlands.client.render.particle.entity;

import org.lwjgl.opengl.GL14;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.SpikeRenderer;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.SoundRegistry;

@SideOnly(Side.CLIENT)
public class ParticleUrchinSpike extends Particle implements IParticleSpriteReceiver {
	public static final ResourceLocation SPRITE = new ResourceLocation(ModInfo.ID, "entity/urchin_spike");

	private SpikeRenderer renderer;

	private int length;
	private float scale, width;
	private long seed;

	private double prevMotionX, prevMotionY, prevMotionZ;

	private boolean sound = false;
	
	protected ParticleUrchinSpike(World worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int length, float width, float scale, long seed) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.prevMotionX = this.motionX = xSpeedIn;
		this.prevMotionY = this.motionY = ySpeedIn;
		this.prevMotionZ = this.motionZ = zSpeedIn;
		this.length = length;
		this.width = width;
		this.scale = scale;
		this.seed = seed;
		this.particleGravity = 1;
		this.particleMaxAge = 20 * 3;
	}

	public void setUseSound(boolean sound) {
		this.sound = sound;
	}
	
	@Override
	public int getFXLayer() {
		return 3;
	}

	@Override
	public void onUpdate() {
		this.prevMotionX = this.motionX;
		this.prevMotionY = this.motionY;
		this.prevMotionZ = this.motionZ;

		super.onUpdate();

		if(this.onGround) {
			this.motionX *= 0.01F;
			this.motionY *= 0.01F;
			this.motionZ *= 0.01F;
			
			if(this.sound) {
				this.sound = false;
				this.world.playSound(this.posX, this.posY, this.posZ, SoundRegistry.ROOT_SPIKE_PARTICLE_HIT, SoundCategory.HOSTILE, 1, 0.9F + this.rand.nextFloat() * 0.2F, false);
			}
		}
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(this.renderer == null) {
			this.renderer = new SpikeRenderer(this.length, this.width, 1.0F, 1, this.seed).build(DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(SPRITE.toString()));
		}

		int i = this.getBrightnessForRender(partialTicks);
		int j = i % 65536;
		int k = i / 65536;

		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();

		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		float rx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float ry = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float rz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);

		GlStateManager.pushMatrix();

		float alpha = 1.0F;

		if((this.particleAge + partialTicks) >= this.particleMaxAge - 10) {
			alpha = 1.0F - ((this.particleAge + partialTicks) - (this.particleMaxAge - 10)) / 10.0F;
		}

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.CONSTANT_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
		GL14.glBlendColor(1, 1, 1, alpha);
		GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

		GlStateManager.translate(rx, ry, rz);

		double mx = this.prevMotionX + (this.motionX - this.prevMotionX) * partialTicks;
		double my = this.prevMotionY + (this.motionY - this.prevMotionY) * partialTicks;
		double mz = this.prevMotionZ + (this.motionZ - this.prevMotionZ) * partialTicks;

		GlStateManager.rotate(-(float)Math.toDegrees(Math.atan2(mz, mx)), 0, 1, 0);
		GlStateManager.rotate((float)Math.toDegrees(Math.atan2(Math.sqrt(mx * mx + mz * mz), -my)) + 180, 0, 0, 1);

		GlStateManager.translate(0, -0.5F * this.scale, 0);

		GlStateManager.scale(this.scale, this.scale, this.scale);

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		this.renderer.render();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GL14.glBlendColor(1, 1, 1, 1);

		GlStateManager.popMatrix();
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleUrchinSpike> {

		public Factory() {
			super(ParticleUrchinSpike.class, ParticleTextureStitcher.create(ParticleUrchinSpike.class, SPRITE));
		}
		
		@Override
		public ParticleUrchinSpike createParticle(ImmutableParticleArgs args) {
			return new ParticleUrchinSpike(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(2), args.scale, args.data.getLong(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(2, 1234L, 0.25F).withScale(0.5F);
		}

		@Override
		protected void setDefaultArguments(World world, double x, double y, double z, ParticleArgs<?> args) {
			args.withData(2, world.rand.nextLong(), 0.25F);
		}
	}
}