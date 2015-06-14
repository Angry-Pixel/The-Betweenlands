package thebetweenlands.client.render.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

import thebetweenlands.event.render.FogHandler;
import thebetweenlands.world.WorldProviderBetweenlands;

import java.util.Random;

public class SkyRendererStars extends IRenderHandler
{
	private int starGLCallList;
	private int glSkyList;
	private int glSkyList2;

	private static final ResourceLocation SKY_TEXTURE_RES = new ResourceLocation("thebetweenlands:textures/sky/sky_texture.png");

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		RenderGlobal renderGlobal = mc.renderGlobal;

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Vec3 vec3 = world.getSkyColor(mc.renderViewEntity, partialTicks);
		float f1 = (float)vec3.xCoord;
		float f2 = (float)vec3.yCoord;
		float f3 = (float)vec3.zCoord;
		float f6;

		if (mc.gameSettings.anaglyph) {
			float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
			float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
			f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
			f1 = f4;
			f2 = f5;
			f3 = f6;
		}

		GL11.glColor3f(f1, f2, f3);
		Tessellator tessellator = Tessellator.instance;
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glFogf(GL11.GL_FOG_START, FogHandler.INSTANCE.getCurrentFogStart());
		GL11.glFogf(GL11.GL_FOG_END, FogHandler.INSTANCE.getCurrentFogEnd()*FogHandler.INSTANCE.getCurrentFogEnd()/15.0F);
		GL11.glColor3f(f1, f2, f3);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		RenderHelper.disableStandardItemLighting();
		float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
		float f7;
		float f8;
		float f9;
		float f10;

		if (afloat != null) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glPushMatrix();
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
			f6 = afloat[0];
			f7 = afloat[1];
			f8 = afloat[2];
			float f11;

			if (mc.gameSettings.anaglyph) {
				f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
				f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
				f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
				f6 = f9;
				f7 = f10;
				f8 = f11;
			}

			tessellator.startDrawing(6);
			tessellator.setColorRGBA_F(f6, f7, f8, afloat[3]);
			tessellator.addVertex(0.0D, 100.0D, 0.0D);
			byte b0 = 16;
			tessellator.setColorRGBA_F(afloat[0], afloat[1], afloat[2], 0.0F);

			for (int j = 0; j <= b0; ++j) {
				f11 = (float)j * (float)Math.PI * 2.0F / (float)b0;
				float f12 = MathHelper.sin(f11);
				float f13 = MathHelper.cos(f11);
				tessellator.addVertex((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3]));
			}

			tessellator.draw();
			GL11.glPopMatrix();
			GL11.glShadeModel(GL11.GL_FLAT);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.glBlendFunc(770, 1, 1, 0);
		GL11.glPushMatrix();
		f6 = 1.0F - world.getRainStrength(partialTicks);
		f7 = 0.0F;
		f8 = 0.0F;
		f9 = 0.0F;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, f6);
		GL11.glTranslatef(f7, f8, f9);
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		float f18 = world.getStarBrightness(partialTicks) * f6;

		GL11.glDisable(GL11.GL_BLEND);
		
		if (f18 > 0.0F) {
			GL11.glColor4f(f18, f18, f18, f18);
			GL11.glCallList(starGLCallList);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(0.0F, 0.0F, 0.0F);
		double horizon = mc.thePlayer.getPosition(partialTicks).yCoord - world.getHorizon();

		if (horizon < 0.0D) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 12.0F, 0.0F);
			GL11.glCallList(glSkyList2);
			GL11.glPopMatrix();
			f8 = 1.0F;
			f9 = -((float)(horizon + 65.0D));
			f10 = -f8;
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(0, 255);
			tessellator.addVertex((double)(-f8), (double)f9, (double)f8);
			tessellator.addVertex((double)f8, (double)f9, (double)f8);
			tessellator.addVertex((double)f8, (double)f10, (double)f8);
			tessellator.addVertex((double)(-f8), (double)f10, (double)f8);
			tessellator.addVertex((double)(-f8), (double)f10, (double)(-f8));
			tessellator.addVertex((double)f8, (double)f10, (double)(-f8));
			tessellator.addVertex((double)f8, (double)f9, (double)(-f8));
			tessellator.addVertex((double)(-f8), (double)f9, (double)(-f8));
			tessellator.addVertex((double)f8, (double)f10, (double)(-f8));
			tessellator.addVertex((double)f8, (double)f10, (double)f8);
			tessellator.addVertex((double)f8, (double)f9, (double)f8);
			tessellator.addVertex((double)f8, (double)f9, (double)(-f8));
			tessellator.addVertex((double)(-f8), (double)f9, (double)(-f8));
			tessellator.addVertex((double)(-f8), (double)f9, (double)f8);
			tessellator.addVertex((double)(-f8), (double)f10, (double)f8);
			tessellator.addVertex((double)(-f8), (double)f10, (double)(-f8));
			tessellator.addVertex((double)(-f8), (double)f10, (double)(-f8));
			tessellator.addVertex((double)(-f8), (double)f10, (double)f8);
			tessellator.addVertex((double)f8, (double)f10, (double)f8);
			tessellator.addVertex((double)f8, (double)f10, (double)(-f8));
			tessellator.draw();
		}

		if (world.provider.isSkyColored()) GL11.glColor3f(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
		else GL11.glColor3f(f1, f2, f3);

		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, -((float)(horizon - 16.0D)), 0.0F);
		GL11.glCallList(glSkyList2);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
		renderSkyTexture(mc);
	}
	
	public SkyRendererStars()
	{
		this.starGLCallList = GLAllocation.generateDisplayLists(3);
		GL11.glPushMatrix();
		GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
		this.renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();
		Tessellator tessellator = Tessellator.instance;
		this.glSkyList = this.starGLCallList + 1;
		GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
		byte b2 = 64;
		int i = 256 / b2 + 2;
		float f = 16.0F;
		int j;
		int k;

		for (j = -b2 * i; j <= b2 * i; j += b2)
		{
			for (k = -b2 * i; k <= b2 * i; k += b2)
			{
				tessellator.startDrawingQuads();
				tessellator.addVertex((double)(j + 0), (double)f, (double)(k + 0));
				tessellator.addVertex((double)(j + b2), (double)f, (double)(k + 0));
				tessellator.addVertex((double)(j + b2), (double)f, (double)(k + b2));
				tessellator.addVertex((double)(j + 0), (double)f, (double)(k + b2));
				tessellator.draw();
			}
		}

		GL11.glEndList();
		this.glSkyList2 = this.starGLCallList + 2;
		GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
		f = -16.0F;
		tessellator.startDrawingQuads();

		for (j = -b2 * i; j <= b2 * i; j += b2)
		{
			for (k = -b2 * i; k <= b2 * i; k += b2)
			{
				tessellator.addVertex((double)(j + b2), (double)f, (double)(k + 0));
				tessellator.addVertex((double)(j + 0), (double)f, (double)(k + 0));
				tessellator.addVertex((double)(j + 0), (double)f, (double)(k + b2));
				tessellator.addVertex((double)(j + b2), (double)f, (double)(k + b2));
			}
		}

		tessellator.draw();
		GL11.glEndList();
	}

	private void renderStars()
	{
		Random random = new Random(10842L);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();

		for (int i = 0; i < 1500; ++i)
		{
			double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d1 = (double)(random.nextFloat() * 0.5F - 1.0F);
			double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
			double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
			double d4 = d0 * d0 + d1 * d1 + d2 * d2;

			if (d4 < 1.0D && d4 > 0.01D)
			{
				d4 = 1.0D / Math.sqrt(d4);
				d0 *= d4;
				d1 *= d4;
				d2 *= d4;
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

				if( random.nextInt(2) == 1 ) {
					tessellator.setColorOpaque(0, 96, 0);
				} else {
					tessellator.setColorOpaque(255, 255, 255);
				}

				for (int j = 0; j < 4; ++j)
				{
					double d17 = 0.0D;
					double d18 = (double)((j & 2) - 1) * d3;
					double d19 = (double)((j + 1 & 2) - 1) * d3;
					double d20 = d18 * d16 - d19 * d15;
					double d21 = d19 * d16 + d18 * d15;
					double d22 = d20 * d12 + d17 * d13;
					double d23 = d17 * d12 - d20 * d13;
					double d24 = d23 * d9 - d21 * d10;
					double d25 = d21 * d9 + d23 * d10;
					tessellator.addVertex(d5 + d24, d6 + d22, d7 + d25);
				}
			}
		}

		tessellator.draw();
	}

	private void renderSkyTexture(Minecraft mc) {
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		RenderHelper.disableStandardItemLighting();
		GL11.glDepthMask(false);
		mc.renderEngine.bindTexture(SKY_TEXTURE_RES);
		Tessellator tessellator = Tessellator.instance;

		GL11.glPushMatrix();
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA(256, 256, 256, 128);
		tessellator.addVertexWithUV(-90.0D, -40.0D, -90.0D, 0.0D, 0.0D);
		tessellator.addVertexWithUV(-90.0D, -40.0D, 90.0D, 0.0D, 1.0D);
		tessellator.addVertexWithUV(90.0D, -40.0D, 90.0D, 1.0D, 1.0D);
		tessellator.addVertexWithUV(90.0D, -40.0D, -90.0D, 1.0D, 0.0D);
		tessellator.draw();
		
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
	}
}
