package thebetweenlands.client.render.sky;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.opengl.GLContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.sky.IRiftSkyRenderer;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.WorldProviderBetweenlands;

@SideOnly(Side.CLIENT)
public class OverworldRiftSkyRenderer implements IRiftSkyRenderer {
	private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
	private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");
	private static final ResourceLocation CLOUDS_TEXTURES = new ResourceLocation("textures/environment/clouds.png");

	private final FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);

	private final VertexFormat vertexBufferFormat;
	private int starGLCallList = -1;
	private int glSkyList = -1;
	private int glSkyList2 = -1;
	private VertexBuffer starVBO;
	private VertexBuffer skyVBO;
	private VertexBuffer sky2VBO;

	private boolean vboEnabled;

	private float fogColorRed;
	private float fogColorGreen;
	private float fogColorBlue;

	private boolean cloudFog;

	private final float[] colorsSunriseSunset = new float[4];
	private Object entityIn;

	public OverworldRiftSkyRenderer() {
		this.vboEnabled = OpenGlHelper.useVbo();

		this.vertexBufferFormat = new VertexFormat();
		this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));

		this.generateStars();
		this.generateSky();
		this.generateSky2();
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		this.setupFog(-1, partialTicks, mc);

		TextureManager textureManager = mc.renderEngine;

		long worldTime = world.getWorldTime();

		GlStateManager.disableTexture2D();
		Vec3d vec3d = this.getSkyColor(world, mc.getRenderViewEntity(), partialTicks);
		float f = (float)vec3d.x;
		float f1 = (float)vec3d.y;
		float f2 = (float)vec3d.z;

		if (mc.gameSettings.anaglyph)
		{
			float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
			float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
			float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
			f = f3;
			f1 = f4;
			f2 = f5;
		}

		GlStateManager.color(f, f1, f2);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.depthMask(false);
		GlStateManager.enableFog();
		GlStateManager.color(f, f1, f2);

		if (this.vboEnabled)
		{
			this.skyVBO.bindBuffer();
			GlStateManager.glEnableClientState(32884);
			GlStateManager.glVertexPointer(3, 5126, 12, 0);
			this.skyVBO.drawArrays(7);
			this.skyVBO.unbindBuffer();
			GlStateManager.glDisableClientState(32884);
		}
		else
		{
			GlStateManager.callList(this.glSkyList);
		}

		GlStateManager.disableFog();
		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderHelper.disableStandardItemLighting();
		float[] afloat = this.calcSunriseSunsetColors(world, this.getCelestialAngle(worldTime, partialTicks), partialTicks);

		if (afloat != null)
		{
			GlStateManager.disableTexture2D();
			GlStateManager.shadeModel(7425);
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(MathHelper.sin(this.getCelestialAngleRadians(worldTime, partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
			float f6 = afloat[0];
			float f7 = afloat[1];
			float f8 = afloat[2];

			if (mc.gameSettings.anaglyph)
			{
				float f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
				float f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
				float f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
				f6 = f9;
				f7 = f10;
				f8 = f11;
			}

			bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(0.0D, 100.0D, 0.0D).color(f6, f7, f8, afloat[3]).endVertex();

			for (int j2 = 0; j2 <= 16; ++j2)
			{
				float f21 = (float)j2 * ((float)Math.PI * 2F) / 16.0F;
				float f12 = MathHelper.sin(f21);
				float f13 = MathHelper.cos(f21);
				bufferbuilder.pos((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
			}

			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.shadeModel(7424);
		}

		GlStateManager.enableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.pushMatrix();
		float f16 = 1.0F - this.getRainStrength(world, partialTicks);
		GlStateManager.color(1.0F, 1.0F, 1.0F, f16);
		GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(this.getCelestialAngle(worldTime, partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
		float f17 = 30.0F;
		textureManager.bindTexture(SUN_TEXTURES);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double)(-f17), 100.0D, (double)(-f17)).tex(0.0D, 0.0D).endVertex();
		bufferbuilder.pos((double)f17, 100.0D, (double)(-f17)).tex(1.0D, 0.0D).endVertex();
		bufferbuilder.pos((double)f17, 100.0D, (double)f17).tex(1.0D, 1.0D).endVertex();
		bufferbuilder.pos((double)(-f17), 100.0D, (double)f17).tex(0.0D, 1.0D).endVertex();
		tessellator.draw();
		f17 = 20.0F;
		textureManager.bindTexture(MOON_PHASES_TEXTURES);
		int k1 = this.getMoonPhase(worldTime);
		int i2 = k1 % 4;
		int k2 = k1 / 4 % 2;
		float f22 = (float)(i2 + 0) / 4.0F;
		float f23 = (float)(k2 + 0) / 2.0F;
		float f24 = (float)(i2 + 1) / 4.0F;
		float f14 = (float)(k2 + 1) / 2.0F;
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double)(-f17), -100.0D, (double)f17).tex((double)f24, (double)f14).endVertex();
		bufferbuilder.pos((double)f17, -100.0D, (double)f17).tex((double)f22, (double)f14).endVertex();
		bufferbuilder.pos((double)f17, -100.0D, (double)(-f17)).tex((double)f22, (double)f23).endVertex();
		bufferbuilder.pos((double)(-f17), -100.0D, (double)(-f17)).tex((double)f24, (double)f23).endVertex();
		tessellator.draw();
		GlStateManager.disableTexture2D();
		float f15 = this.getStarBrightness(world, partialTicks) * f16;

		if (f15 > 0.0F)
		{
			GlStateManager.color(f15, f15, f15, f15);

			if (this.vboEnabled)
			{
				this.starVBO.bindBuffer();
				GlStateManager.glEnableClientState(32884);
				GlStateManager.glVertexPointer(3, 5126, 12, 0);
				this.starVBO.drawArrays(7);
				this.starVBO.unbindBuffer();
				GlStateManager.glDisableClientState(32884);
			}
			else
			{
				GlStateManager.callList(this.starGLCallList);
			}
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableFog();
		GlStateManager.popMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.color(0.0F, 0.0F, 0.0F);
		double d3 = mc.player.getPositionEyes(partialTicks).y - this.getHorizon();

		if (d3 < 0.0D)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 12.0F, 0.0F);

			if (this.vboEnabled)
			{
				this.sky2VBO.bindBuffer();
				GlStateManager.glEnableClientState(32884);
				GlStateManager.glVertexPointer(3, 5126, 12, 0);
				this.sky2VBO.drawArrays(7);
				this.sky2VBO.unbindBuffer();
				GlStateManager.glDisableClientState(32884);
			}
			else
			{
				GlStateManager.callList(this.glSkyList2);
			}

			GlStateManager.popMatrix();
			float f19 = -((float)(d3 + 65.0D));
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(-1.0D, (double)f19, -1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(-1.0D, (double)f19, 1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
			tessellator.draw();
		}

		if (this.isSkyColored())
		{
			GlStateManager.color(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
		}
		else
		{
			GlStateManager.color(f, f1, f2);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, -((float)(d3 - 16.0D)), 0.0F);
		GlStateManager.callList(this.glSkyList2);
		GlStateManager.popMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(true);

		if(BetweenlandsConfig.RENDERING.skyRiftClouds) {
			Entity entity = mc.getRenderViewEntity();

			WorldProviderBetweenlands providerBl = world.provider instanceof WorldProviderBetweenlands ? (WorldProviderBetweenlands) world.provider : null;

			if(providerBl != null) providerBl.setShowClouds(true);

			double rx = entity.posX;
			double prx = entity.prevPosX;
			double lrx = entity.lastTickPosX;
			double ry = entity.posY;
			double pry = entity.prevPosY;
			double lry = entity.lastTickPosY;
			double rz = entity.posZ;
			double prz = entity.prevPosZ;
			double lrz = entity.lastTickPosZ;

			entity.posX = entity.prevPosX = entity.lastTickPosX = 0;
			entity.posY = entity.prevPosY = entity.lastTickPosY = 50;
			entity.posZ = entity.prevPosZ = entity.lastTickPosZ = 0;

			this.setupFog(0, partialTicks, mc);

			GlStateManager.pushMatrix();
			mc.renderGlobal.renderClouds(partialTicks, 2, 0, 50, 0);
			GlStateManager.popMatrix();

			entity.posX = rx;
			entity.prevPosX = prx;
			entity.lastTickPosX = lrx;
			entity.posY = ry;
			entity.prevPosY = pry;
			entity.lastTickPosY = lry;
			entity.posZ = rz;
			entity.prevPosZ = prz;
			entity.lastTickPosZ = lrz;

			if(providerBl != null) providerBl.setShowClouds(false);
		}
	}

	@Override
	public void setClearColor(float partialTicks, WorldClient world, Minecraft mc) {
		this.updateFogColor(world, partialTicks, mc);
	}

	private void generateSky2()
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		if (this.sky2VBO != null)
		{
			this.sky2VBO.deleteGlBuffers();
		}

		if (this.glSkyList2 >= 0)
		{
			GLAllocation.deleteDisplayLists(this.glSkyList2);
			this.glSkyList2 = -1;
		}

		if (this.vboEnabled)
		{
			this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
			this.renderSky(bufferbuilder, -16.0F, true);
			bufferbuilder.finishDrawing();
			bufferbuilder.reset();
			this.sky2VBO.bufferData(bufferbuilder.getByteBuffer());
		}
		else
		{
			this.glSkyList2 = GLAllocation.generateDisplayLists(1);
			GlStateManager.glNewList(this.glSkyList2, 4864);
			this.renderSky(bufferbuilder, -16.0F, true);
			tessellator.draw();
			GlStateManager.glEndList();
		}
	}

	private void generateSky()
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		if (this.skyVBO != null)
		{
			this.skyVBO.deleteGlBuffers();
		}

		if (this.glSkyList >= 0)
		{
			GLAllocation.deleteDisplayLists(this.glSkyList);
			this.glSkyList = -1;
		}

		if (this.vboEnabled)
		{
			this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
			this.renderSky(bufferbuilder, 16.0F, false);
			bufferbuilder.finishDrawing();
			bufferbuilder.reset();
			this.skyVBO.bufferData(bufferbuilder.getByteBuffer());
		}
		else
		{
			this.glSkyList = GLAllocation.generateDisplayLists(1);
			GlStateManager.glNewList(this.glSkyList, 4864);
			this.renderSky(bufferbuilder, 16.0F, false);
			tessellator.draw();
			GlStateManager.glEndList();
		}
	}

	private void renderSky(BufferBuilder bufferBuilderIn, float posY, boolean reverseX)
	{
		bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

		for (int k = -384; k <= 384; k += 64)
		{
			for (int l = -384; l <= 384; l += 64)
			{
				float f = (float)k;
				float f1 = (float)(k + 64);

				if (reverseX)
				{
					f1 = (float)k;
					f = (float)(k + 64);
				}

				bufferBuilderIn.pos((double)f, (double)posY, (double)l).endVertex();
				bufferBuilderIn.pos((double)f1, (double)posY, (double)l).endVertex();
				bufferBuilderIn.pos((double)f1, (double)posY, (double)(l + 64)).endVertex();
				bufferBuilderIn.pos((double)f, (double)posY, (double)(l + 64)).endVertex();
			}
		}
	}

	private void renderStars(BufferBuilder bufferBuilderIn)
	{
		Random random = new Random(10842L);
		bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

		for (int i = 0; i < 1500; ++i)
		{
			double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d4 < 1.0D && d4 > 0.01D)
			{
				d4 = 1.0D / Math.sqrt(d4);
				d0 = d0 * d4;
				d1 = d1 * d4;
				d2 = d2 * d4;
				double d5 = d0 * 100.0D;
				double d6 = d1 * 100.0D;
				double d7 = d2 * 100.0D;
				double d8 = Math.atan2(d0, d2);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double d15 = Math.sin(d14);
				double d16 = Math.cos(d14);

				for (int j = 0; j < 4; ++j)
				{
					double d18 = (double)((j & 2) - 1) * d3;
					double d19 = (double)((j + 1 & 2) - 1) * d3;
					double d21 = d18 * d16 - d19 * d15;
					double d22 = d19 * d16 + d18 * d15;
					double d23 = d21 * d12 + 0.0D * d13;
					double d24 = 0.0D * d12 - d21 * d13;
					double d25 = d24 * d9 - d22 * d10;
					double d26 = d22 * d9 + d24 * d10;
					bufferBuilderIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
				}
			}
		}
	}

	private void generateStars()
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		if (this.starVBO != null)
		{
			this.starVBO.deleteGlBuffers();
		}

		if (this.starGLCallList >= 0)
		{
			GLAllocation.deleteDisplayLists(this.starGLCallList);
			this.starGLCallList = -1;
		}

		if (this.vboEnabled)
		{
			this.starVBO = new VertexBuffer(this.vertexBufferFormat);
			this.renderStars(bufferbuilder);
			bufferbuilder.finishDrawing();
			bufferbuilder.reset();
			this.starVBO.bufferData(bufferbuilder.getByteBuffer());
		}
		else
		{
			this.starGLCallList = GLAllocation.generateDisplayLists(1);
			GlStateManager.pushMatrix();
			GlStateManager.glNewList(this.starGLCallList, 4864);
			this.renderStars(bufferbuilder);
			tessellator.draw();
			GlStateManager.glEndList();
			GlStateManager.popMatrix();
		}
	}

	public void updateFogColor(WorldClient world, float partialTicks, Minecraft mc)
	{
		long worldTime = world.getWorldTime();

		Entity entity = mc.getRenderViewEntity();
		float f = 0.25F + 0.75F * (float)mc.gameSettings.renderDistanceChunks / 32.0F;
		f = 1.0F - (float)Math.pow((double)f, 0.25D);
		Vec3d vec3d = this.getSkyColor(world, mc.getRenderViewEntity(), partialTicks);
		float f1 = (float)vec3d.x;
		float f2 = (float)vec3d.y;
		float f3 = (float)vec3d.z;
		Vec3d vec3d1 = this.getFogColor(this.getCelestialAngle(worldTime, partialTicks), partialTicks);
		this.fogColorRed = (float)vec3d1.x;
		this.fogColorGreen = (float)vec3d1.y;
		this.fogColorBlue = (float)vec3d1.z;

		if (mc.gameSettings.renderDistanceChunks >= 4)
		{
			double d0 = MathHelper.sin(this.getCelestialAngleRadians(worldTime, partialTicks)) > 0.0F ? -1.0D : 1.0D;
			Vec3d vec3d2 = new Vec3d(d0, 0.0D, 0.0D);
			float f5 = (float)entity.getLook(partialTicks).dotProduct(vec3d2);

			if (f5 < 0.0F)
			{
				f5 = 0.0F;
			}

			if (f5 > 0.0F)
			{
				float[] afloat = this.calcSunriseSunsetColors(world, this.getCelestialAngle(worldTime, partialTicks), partialTicks);

				if (afloat != null)
				{
					f5 = f5 * afloat[3];
					this.fogColorRed = this.fogColorRed * (1.0F - f5) + afloat[0] * f5;
					this.fogColorGreen = this.fogColorGreen * (1.0F - f5) + afloat[1] * f5;
					this.fogColorBlue = this.fogColorBlue * (1.0F - f5) + afloat[2] * f5;
				}
			}
		}

		this.fogColorRed += (f1 - this.fogColorRed) * f;
		this.fogColorGreen += (f2 - this.fogColorGreen) * f;
		this.fogColorBlue += (f3 - this.fogColorBlue) * f;
		float f8 = this.getRainStrength(world, partialTicks);

		if (f8 > 0.0F)
		{
			float f4 = 1.0F - f8 * 0.5F;
			float f10 = 1.0F - f8 * 0.4F;
			this.fogColorRed *= f4;
			this.fogColorGreen *= f4;
			this.fogColorBlue *= f10;
		}

		/*float f13 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks;
        this.fogColorRed *= f13;
        this.fogColorGreen *= f13;
        this.fogColorBlue *= f13;*/

		if (mc.gameSettings.anaglyph)
		{
			float f16 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
			float f17 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
			float f7 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
			this.fogColorRed = f16;
			this.fogColorGreen = f17;
			this.fogColorBlue = f7;
		}

		GlStateManager.clearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
	}

	private void setupFog(int startCoords, float partialTicks, Minecraft mc)
	{
		float farPlaneDistance = (float)(mc.gameSettings.renderDistanceChunks * 16);
		this.setupFogColor(false);
		GlStateManager.glNormal3f(0.0F, -1.0F, 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		if (this.cloudFog)
		{
			GlStateManager.setFog(GlStateManager.FogMode.EXP);
			GlStateManager.setFogDensity(0.1F);
		}
		else
		{
			float f = farPlaneDistance;
			GlStateManager.setFog(GlStateManager.FogMode.LINEAR);

			if (startCoords == -1)
			{
				GlStateManager.setFogStart(0.0F);
				GlStateManager.setFogEnd(f);
			}
			else
			{
				GlStateManager.setFogStart(f * 0.75F);
				GlStateManager.setFogEnd(f);
			}

			if (GLContext.getCapabilities().GL_NV_fog_distance)
			{
				GlStateManager.glFogi(34138, 34139);
			}
		}

		GlStateManager.enableColorMaterial();
		GlStateManager.enableFog();
		GlStateManager.colorMaterial(1028, 4608);
	}

	private void setupFogColor(boolean black)
	{
		if (black)
		{
			GlStateManager.glFog(2918, this.setFogColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		}
		else
		{
			GlStateManager.glFog(2918, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
		}
	}

	private FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha)
	{
		this.fogColorBuffer.clear();
		this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
		this.fogColorBuffer.flip();
		return this.fogColorBuffer;
	}

	protected boolean isSkyColored() {
		return true;
	}

	protected double getHorizon() {
		return 63.0D;
	}

	protected float getStarBrightness(World world, float partialTicks) {
		float f = this.getCelestialAngle(world.getWorldTime(), partialTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.25F);
        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        return f1 * f1 * 0.5F;
	}

	protected int getMoonPhase(long worldTime) {
		return (int)(worldTime / 24000L % 8L + 8L) % 8;
	}

	protected float getCelestialAngle(long worldTime, float partialTicks) {
		int i = (int)(worldTime % 24000L);
		float f = ((float)i + partialTicks) / 24000.0F - 0.25F;

		if (f < 0.0F)
		{
			++f;
		}

		if (f > 1.0F)
		{
			--f;
		}

		float f1 = 1.0F - (float)((Math.cos((double)f * Math.PI) + 1.0D) / 2.0D);
		f = f + (f1 - f) / 3.0F;
		return f;
	}

	protected float getCelestialAngleRadians(long worldTime, float partialTicks) {
		return this.getCelestialAngle(worldTime, partialTicks) * ((float)Math.PI * 2F);
	}

	protected float getRainStrength(World world, float partialTicks) {
		return 0.0F;
	}

	protected float[] calcSunriseSunsetColors(World world, float celestialAngle, float partialTicks) {
		float f1 = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F)) - 0.0F;

		if (f1 >= -0.4F && f1 <= 0.4F)
		{
			float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
			float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * (float)Math.PI)) * 0.99F;
			f4 = f4 * f4;
			this.colorsSunriseSunset[0] = f3 * 0.3F + 0.7F;
			this.colorsSunriseSunset[1] = f3 * f3 * 0.7F + 0.2F;
			this.colorsSunriseSunset[2] = f3 * f3 * 0.0F + 0.2F;
			this.colorsSunriseSunset[3] = f4;
			return this.colorsSunriseSunset;
		}
		else
		{
			return null;
		}
	}

	protected Vec3d getSkyColor(World world, Entity renderViewEntity, float partialTicks) {
		float f = this.getCelestialAngle(world.getWorldTime(), partialTicks);
		float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
		f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
		int i = MathHelper.floor(renderViewEntity.posX);
		int j = MathHelper.floor(renderViewEntity.posY);
		int k = MathHelper.floor(renderViewEntity.posZ);
		BlockPos blockpos = new BlockPos(i, j, k);
		int l = net.minecraftforge.client.ForgeHooksClient.getSkyBlendColour(world, blockpos);
		float f3 = (float)(l >> 16 & 255) / 255.0F;
		float f4 = (float)(l >> 8 & 255) / 255.0F;
		float f5 = (float)(l & 255) / 255.0F;
		f3 = f3 * f1;
		f4 = f4 * f1;
		f5 = f5 * f1;

		return new Vec3d((double)f3, (double)f4, (double)f5);
	}

	protected Vec3d getFogColor(float celestialAngle, float partialTicks) {
		float f = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		float f1 = 0.7529412F;
		float f2 = 0.84705883F;
		float f3 = 1.0F;
		f1 = f1 * (f * 0.94F + 0.06F);
		f2 = f2 * (f * 0.94F + 0.06F);
		f3 = f3 * (f * 0.91F + 0.09F);
		return new Vec3d((double)f1, (double)f2, (double)f3);
	}

	@Override
	public float getSkyBrightness(float partialTicks, WorldClient world, Minecraft mc) {
		float f = this.getCelestialAngle(world.getWorldTime(), partialTicks);
		float f1 = MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
		f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
		return f1;
	}
}
