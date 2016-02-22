package thebetweenlands.client.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.entities.projectiles.EntityVolatileSoul;
import thebetweenlands.utils.LightingUtil;

@SideOnly(Side.CLIENT)
public class RenderVolatileSoul extends Render {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/wightFace.png");

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		if(entity instanceof EntityVolatileSoul) {
			float rx = ActiveRenderInfo.rotationX;
			float rxz = ActiveRenderInfo.rotationXZ;
			float rz = ActiveRenderInfo.rotationZ;
			float ryz = ActiveRenderInfo.rotationYZ;
			float rxy = ActiveRenderInfo.rotationXY;

			Tessellator tessellator = Tessellator.instance;

			float scale = 0.25F;

			float minU = 0;
			float maxU = 1;
			float minV = 0;
			float maxV = 1;

			this.bindTexture(TEXTURE);

			x += 0.125D;
			y += 0.125D;
			z += 0.125D;

			LightingUtil.INSTANCE.setLighting(255);
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_F(1, 1, 1, 1);
			tessellator.addVertexWithUV((double)(x - rx * scale - ryz * scale), (double)(y - rxz * scale), (double)(z - rz * scale - rxy * scale), (double)maxU, (double)maxV);
			tessellator.addVertexWithUV((double)(x - rx * scale + ryz * scale), (double)(y + rxz * scale), (double)(z - rz * scale + rxy * scale), (double)maxU, (double)minV);
			tessellator.addVertexWithUV((double)(x + rx * scale + ryz * scale), (double)(y + rxz * scale), (double)(z + rz * scale + rxy * scale), (double)minU, (double)minV);
			tessellator.addVertexWithUV((double)(x + rx * scale - ryz * scale), (double)(y - rxz * scale), (double)(z + rz * scale - rxy * scale), (double)minU, (double)maxV);
			tessellator.draw();
			LightingUtil.INSTANCE.revert();
		}
	}

	private double interpolate(double prev, double now, double partialTicks) {
		return prev + (now - prev) * partialTicks;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
