package thebetweenlands.client.render.entity;

import java.util.AbstractMap.SimpleEntry;
import java.util.Random;

import javax.vecmath.Vector3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.particles.EntityWispFX;
import thebetweenlands.event.render.FireflyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFirefly extends Render {

	public static final Random rnd = new Random();
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		FireflyHandler.INSTANCE.fireflyList.add(new SimpleEntry(new SimpleEntry(this, (EntityFirefly)entity), new Vector3d(x, y, z)));
	}

	public void doRenderCallback(Entity entity, double x, double y, double z, float partialTicks) {
		Tessellator tessellator = Tessellator.instance;

		GL11.glPushMatrix();
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(EntityWispFX.TEXTURE);

		tessellator.startDrawingQuads();

		float rx = ActiveRenderInfo.rotationX;
		float rxz = ActiveRenderInfo.rotationXZ;
		float rz = ActiveRenderInfo.rotationZ;
		float ryz = ActiveRenderInfo.rotationYZ;
		float rxy = ActiveRenderInfo.rotationXY;

		double ipx = x;
		double ipy = y;
		double ipz = z;
		double scale = 1.0D;

		tessellator.setBrightness(255);

		tessellator.setColorRGBA_F(0.4F, 0.2F, 0, 0.2F);
		if(rnd.nextInt(10) <= 2) {
			tessellator.setColorRGBA_F(rnd.nextInt(10) / 20.0F + 0.25F, 0.2F, 0, 0.2F);
		}
		double cScale = scale;
		for(int i = 0; i < (rnd.nextInt(10) <= 2 ? rnd.nextInt(10) : 10); i++) {
			cScale -= 0.15D;
			tessellator.addVertexWithUV(ipx - rx * cScale - ryz * cScale, ipy - rxz * cScale, ipz - rz * cScale - rxy * cScale, 0.0D, 1.0D);
			tessellator.addVertexWithUV(ipx - rx * cScale + ryz * cScale, ipy + rxz * cScale, ipz - rz * cScale + rxy * cScale, 1.0D, 1.0D);
			tessellator.addVertexWithUV(ipx + rx * cScale + ryz * cScale, ipy + rxz * cScale, ipz + rz * cScale + rxy * cScale, 1.0D, 0.0D);
			tessellator.addVertexWithUV(ipx + rx * cScale - ryz * cScale, ipy - rxz * cScale, ipz + rz * cScale - rxy * cScale, 0.0D, 0.0D);
		}

		tessellator.setColorRGBA_F(0.6F, 0.6F, 0.6F, 0.2F);
		cScale = scale / 4.0D;
		for(int i = 0; i < 10; i++) {
			cScale -= 0.15D / 4.0D;
			tessellator.addVertexWithUV(ipx - rx * cScale - ryz * cScale, ipy - rxz * cScale, ipz - rz * cScale - rxy * cScale, 0.0D, 1.0D);
			tessellator.addVertexWithUV(ipx - rx * cScale + ryz * cScale, ipy + rxz * cScale, ipz - rz * cScale + rxy * cScale, 1.0D, 1.0D);
			tessellator.addVertexWithUV(ipx + rx * cScale + ryz * cScale, ipy + rxz * cScale, ipz + rz * cScale + rxy * cScale, 1.0D, 0.0D);
			tessellator.addVertexWithUV(ipx + rx * cScale - ryz * cScale, ipy - rxz * cScale, ipz + rz * cScale - rxy * cScale, 0.0D, 0.0D);
		}

		tessellator.draw();

		GL11.glDepthMask(true);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
