package thebetweenlands.client.render.entity.boss.fortress;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import thebetweenlands.entities.mobs.boss.fortress.EntityFortressBossBlockade;
import thebetweenlands.utils.LightingUtil;

public class RenderFortressBossBlockade extends Render {
	private static final ResourceLocation SHIELD_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		EntityFortressBossBlockade blockade = (EntityFortressBossBlockade) entity;

		Vec3[] vertices = blockade.getTriangleVertices();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1, 1, 1, 0.8F);

		GL11.glPushMatrix();
		GL11.glTranslated(x, y + 0.2F, z);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		Tessellator tessellator = Tessellator.instance;

		LightingUtil.INSTANCE.setLighting(255);

		float ticks = (float)entity.ticksExisted + partialTicks;
		this.bindTexture(SHIELD_TEXTURE);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glPushMatrix();
		float uOffset = (ticks * 0.01F) % 1.0F;
		float vOffset = (ticks * 0.01F) % 1.0F;
		GL11.glTranslatef(uOffset, vOffset, 0.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glDepthMask(false);
		tessellator.startDrawing(4);
		tessellator.setBrightness(240);
		tessellator.setColorRGBA_F(0.8F, 0.0F, 1F, 0.5F);
		double textureScale = 4.0D;
		double cu = textureScale / 2.0D;
		double cv = textureScale * Math.sqrt(2) / 2.0D;
		int layers = 8;
		for(int l = 0; l < layers; l++) {
			double cos = Math.cos(2.0D * Math.PI / layers * l);
			double sin = 1+Math.sin(2.0D * Math.PI / layers * l);
			double tu1 = 0.0D - cu;
			double tv1 = 0.0D - cv;
			double tu2 = textureScale / 2.0D / layers * l - cu;
			double tv2 = textureScale / layers * l * Math.sqrt(2) - cv;
			double tu3 = textureScale / layers * l - cu;
			double tv3 = 0.0D - cv;
			tu1 = cu + tu1 * sin;
			tv1 = cv + tv1 * cos;
			tu2 = cu + tu2 * sin;
			tv2 = cv + tv2 * cos;
			tu3 = cu + tu3 * sin;
			tv3 = cv + tv3 * cos;
			tessellator.addVertexWithUV(vertices[0].xCoord, vertices[0].yCoord, vertices[0].zCoord, tu1, tv1);
			tessellator.addVertexWithUV(vertices[1].xCoord, vertices[1].yCoord, vertices[1].zCoord, tu2, tv2);
			tessellator.addVertexWithUV(vertices[2].xCoord, vertices[2].yCoord, vertices[2].zCoord, tu3, tv3);
		}
		tessellator.draw();
		GL11.glDepthMask(true);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		tessellator.startDrawing(4);
		tessellator.setColorRGBA_F(0.8F, 0.0F, 1F, 0.5F);
		tessellator.setBrightness(240);
		tessellator.addVertex(vertices[0].xCoord, vertices[0].yCoord, vertices[0].zCoord);
		tessellator.addVertex(vertices[1].xCoord, vertices[1].yCoord, vertices[1].zCoord);
		tessellator.addVertex(vertices[2].xCoord, vertices[2].yCoord, vertices[2].zCoord);
		tessellator.draw();

		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDepthMask(true);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(1.0F);

		tessellator.startDrawing(1);
		tessellator.setColorRGBA_F(1F, 0.0F, 1F, 1.0F);
		tessellator.addVertex(vertices[0].xCoord, vertices[0].yCoord, vertices[0].zCoord);
		tessellator.addVertex(vertices[1].xCoord, vertices[1].yCoord, vertices[1].zCoord);
		tessellator.addVertex(vertices[1].xCoord, vertices[1].yCoord, vertices[1].zCoord);
		tessellator.addVertex(vertices[2].xCoord, vertices[2].yCoord, vertices[2].zCoord);
		tessellator.addVertex(vertices[2].xCoord, vertices[2].yCoord, vertices[2].zCoord);
		tessellator.addVertex(vertices[0].xCoord, vertices[0].yCoord, vertices[0].zCoord);
		tessellator.draw();

		GL11.glPopMatrix();

		LightingUtil.INSTANCE.revert();

		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}
}
