package thebetweenlands.client.render.particle.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.StalactiteHelper;

@SideOnly(Side.CLIENT)
public class ParticleRootSpike extends Particle {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "blocks/log_spirit_tree");

	private RootRenderer renderer;

	private int length;
	private float scale, width;
	private long seed;

	private double prevMotionX, prevMotionY, prevMotionZ;

	private boolean sound = false;
	
	protected ParticleRootSpike(World worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int length, float width, float scale, long seed) {
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
			this.renderer = new RootRenderer(this.length, this.width, 1.0F, this.seed).build(DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(TEXTURE.toString()));
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

	public static final class Factory extends ParticleFactory<Factory, ParticleRootSpike> {
		public Factory() {
			super(ParticleRootSpike.class, null);
		}

		@Override
		public ParticleRootSpike createParticle(ImmutableParticleArgs args) {
			return new ParticleRootSpike(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.data.getFloat(2), args.scale, args.data.getLong(1));
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

	@SideOnly(Side.CLIENT)
	public static class RootRenderer {
		private List<BakedQuad> quads = new ArrayList<>();

		public final int length;
		public final float widthScale;
		public final float heightScale;
		public final int bx, by, bz;
		public final double x, y, z;

		private VertexFormat format;

		private TextureAtlasSprite sprite;

		public RootRenderer(int length, float widthScale, float heightScale, long seed) {
			this(length, widthScale, heightScale, seed, 0, 0, 0);
		}

		public RootRenderer(int length, float widthScale, float heightScale, long seed, double x, double y, double z) {
			this.length = length;
			this.widthScale = widthScale;
			this.heightScale = heightScale;
			Random rand = new Random();
			rand.setSeed(seed);
			this.bx = rand.nextInt();
			this.by = rand.nextInt(128);
			this.bz = rand.nextInt();
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public TextureAtlasSprite getSprite() {
			return this.sprite;
		}

		public VertexFormat getFormat() {
			return this.format;
		}

		public RootRenderer build(VertexFormat format, TextureAtlasSprite sprite) {
			this.sprite = sprite;
			this.format = format;

			QuadBuilder builder = new QuadBuilder(format);

			for(int y = 0; y < this.length; y++) {
				int distUp = this.length - 1 - y;
				int distDown = y;
				boolean noTop = true;
				boolean noBottom = false;
				float height = 1.0F;

				int totalHeight = 1 + distDown + distUp;
				float distToMidBottom, distToMidTop;

				double squareAmount = 1.2D;
				double halfTotalHeightSQ;

				if(noTop) {
					halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
					distToMidBottom = Math.abs(distUp + 1);
					distToMidTop = Math.abs(distUp);
				} else if(noBottom) {
					halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
					distToMidBottom = Math.abs(distDown);
					distToMidTop = Math.abs(distDown + 1);
				} else {
					float halfTotalHeight = totalHeight * 0.5F;
					halfTotalHeightSQ = Math.pow(halfTotalHeight, squareAmount);
					distToMidBottom = Math.abs(halfTotalHeight - distUp - 1);
					distToMidTop = Math.abs(halfTotalHeight - distUp);
				}

				int minValBottom = (noBottom && distDown == 0) ? 0 : 1;
				int minValTop = (noTop && distUp == 0) ? 0 : 1;
				int scaledValBottom = (int) (Math.pow(distToMidBottom, squareAmount) / halfTotalHeightSQ * (8 - minValBottom)) + minValBottom;
				int scaledValTop = (int) (Math.pow(distToMidTop, squareAmount) / halfTotalHeightSQ * (8 - minValTop)) + minValTop;

				float umin = 0;
				float umax = 16;
				float vmin = 0;
				float vmax = 16;

				float halfSize = (float) scaledValBottom / 16 * this.widthScale;
				float halfSizeTexW = halfSize * (umax - umin);
				float halfSize1 = (float) (scaledValTop) / 16 * this.widthScale;
				float halfSizeTex1 = halfSize1 * (umax - umin);

				StalactiteHelper core = StalactiteHelper.getValsFor(this.bx, this.by + y, this.bz);

				if(distDown == 0 && !noBottom) {
					core.bX = 0.5D;
					core.bZ = 0.5D;
				}
				if(distUp == 0 && !noTop) {
					core.tX = 0.5D;
					core.tZ = 0.5D;
				}

				core.bX += this.x;
				core.tX += this.x;
				core.bZ += this.z;
				core.tZ += this.z;

				builder.setSprite(sprite);

				// front
				builder.addVertex(core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize, umin + halfSizeTexW * 2, vmax);
				builder.addVertex(core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize, umin, vmax);
				builder.addVertex(core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1, umin, vmin);
				builder.addVertex(core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1, umin + halfSizeTex1 * 2, vmin);
				// back
				builder.addVertex(core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize, umin + halfSizeTexW * 2, vmax);
				builder.addVertex(core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize, umin, vmax);
				builder.addVertex(core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1, umin, vmin);
				builder.addVertex(core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin);
				// left
				builder.addVertex(core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize, umin + halfSizeTexW * 2, vmax);
				builder.addVertex(core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ - halfSize, umin, vmax);
				builder.addVertex(core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1, umin, vmin);
				builder.addVertex(core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ - halfSize1, umin + halfSizeTex1 * 2, vmin);
				// right
				builder.addVertex(core.bX - halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize, umin + halfSizeTexW * 2, vmax);
				builder.addVertex(core.bX + halfSize, this.y + (y) * this.heightScale, core.bZ + halfSize, umin, vmax);
				builder.addVertex(core.tX + halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1, umin, vmin);
				builder.addVertex(core.tX - halfSize1, this.y + (y + height) * this.heightScale, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin);

				// top
				if(distUp == 0) {
					builder.addVertex(core.tX - halfSize1, this.y + y + height, core.tZ - halfSize1, umin, vmin);
					builder.addVertex(core.tX - halfSize1, this.y + y + height, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin);
					builder.addVertex(core.tX + halfSize1, this.y + y + height, core.tZ + halfSize1, umin + halfSizeTex1 * 2, vmin + halfSizeTex1 * 2);
					builder.addVertex(core.tX + halfSize1, this.y + y + height, core.tZ - halfSize1, umin, vmin + halfSizeTex1 * 2);
				}

				// bottom
				if(distDown == 0) {
					builder.addVertex(core.bX - halfSize, this.y + y, core.bZ + halfSize, umin + halfSizeTexW * 2, vmin);
					builder.addVertex(core.bX - halfSize, this.y + y, core.bZ - halfSize, umin, vmin);
					builder.addVertex(core.bX + halfSize, this.y + y, core.bZ - halfSize, umin, vmin + halfSizeTexW * 2);
					builder.addVertex(core.bX + halfSize, this.y + y, core.bZ + halfSize, umin + halfSizeTexW * 2, vmin + halfSizeTexW * 2);
				}
			}

			this.quads = builder.build().nonCulledQuads;

			return this;
		}

		public void upload(BufferBuilder buffer) {
			for(BakedQuad quad : this.quads) {
				buffer.addVertexData(quad.getVertexData());
			}
		}

		public void render() {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			buffer.begin(GL11.GL_QUADS, this.format);

			this.upload(buffer);

			tessellator.draw();
		}
	}
}