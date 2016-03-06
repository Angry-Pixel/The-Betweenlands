package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import thebetweenlands.entities.mobs.EntityFortressBoss;
import thebetweenlands.utils.LightingUtil;

public class RenderFortressBoss extends Render {
	private static double vertices[][] = EntityFortressBoss.ICOSAHEDRON_VERTICES;
	private static int indices[][] = EntityFortressBoss.ICOSAHEDRON_INDICES;

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		EntityFortressBoss boss = (EntityFortressBoss) entity;

		if(RenderManager.debugBoundingBox) {
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			RenderGlobal.drawOutlinedBoundingBox(boss.coreBoundingBox.getOffsetBoundingBox(x, y, z), 16777215);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDepthMask(true);
		}

		GL11.glPushMatrix();
		GL11.glTranslated(x + EntityFortressBoss.SHIELD_OFFSET_X, y + EntityFortressBoss.SHIELD_OFFSET_Y, z + EntityFortressBoss.SHIELD_OFFSET_Z);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);

		Tessellator tessellator = Tessellator.instance;

		LightingUtil.INSTANCE.setLighting(255);

		double explode = boss.getShieldExplosion();

		tessellator.startDrawing(4);
		tessellator.setColorRGBA_F(0.8F, 0.0F, 1F, 0.5F);
		tessellator.setBrightness(240);
		for(int i = 0; i <= 19; i++) {
			if(!boss.isShieldActive(i))
				continue;
			double v3[] = this.vertices[this.indices[i][0]];
			double v2[] = this.vertices[this.indices[i][1]];
			double v1[] = this.vertices[this.indices[i][2]];
			double centerX = (v1[0]+v2[0]+v3[0])/3;
			double centerY = (v1[1]+v2[1]+v3[1])/3;
			double centerZ = (v1[2]+v2[2]+v3[2])/3;
			double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
			tessellator.addVertex(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode);
			tessellator.addVertex(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode);
			tessellator.addVertex(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode);
		}
		tessellator.draw();

		if(RenderManager.debugBoundingBox) {
			tessellator.startDrawing(1);
			tessellator.setColorRGBA_F(0.8F, 0.0F, 1F, 0.5F);
			tessellator.setBrightness(240);
			for(int i = 0; i <= 19; i++) {
				if(!boss.isShieldActive(i))
					continue;
				double v3[] = this.vertices[this.indices[i][0]];
				double v2[] = this.vertices[this.indices[i][1]];
				double v1[] = this.vertices[this.indices[i][2]];
				double centerX = (v1[0]+v2[0]+v3[0])/3;
				double centerY = (v1[1]+v2[1]+v3[1])/3;
				double centerZ = (v1[2]+v2[2]+v3[2])/3;
				double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
				Vec3 vec1 = Vec3.createVectorHelper(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode);
				Vec3 vec2 = Vec3.createVectorHelper(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode);
				Vec3 vec3 = Vec3.createVectorHelper(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode);
				Vec3 normal = vec2.subtract(vec1).crossProduct(vec3.subtract(vec1));
				tessellator.addVertex(centerX+centerX/len*explode, centerY+centerY/len*explode, centerZ+centerZ/len*explode);
				tessellator.addVertex(normal.xCoord+centerX+centerX/len*explode, normal.yCoord+centerY+centerY/len*explode, normal.zCoord+centerZ+centerZ/len*explode);
			}
			tessellator.draw();
		}

		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glLineWidth(5.0F);
		tessellator.startDrawing(1);
		tessellator.setColorRGBA_F(0, 0, 0, 1.0F);
		for(int i = 0; i <= 19; i++) {
			if(!boss.isShieldActive(i))
				continue;
			double v3[] = this.vertices[this.indices[i][0]];
			double v2[] = this.vertices[this.indices[i][1]];
			double v1[] = this.vertices[this.indices[i][2]];
			double centerX = (v1[0]+v2[0]+v3[0])/3;
			double centerY = (v1[1]+v2[1]+v3[1])/3;
			double centerZ = (v1[2]+v2[2]+v3[2])/3;
			double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
			tessellator.addVertex(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode);
			tessellator.addVertex(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode);
			tessellator.addVertex(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode);
			tessellator.addVertex(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode);
			tessellator.addVertex(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode);
			tessellator.addVertex(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode);
		}
		tessellator.draw();
		GL11.glDepthMask(true);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		GL11.glLineWidth(1.0F);
		tessellator.startDrawing(1);
		tessellator.setColorRGBA_F(1F, 0.0F, 1F, 1.0F);
		for(int i = 0; i <= 19; i++) {
			if(!boss.isShieldActive(i))
				continue;
			double v3[] = this.vertices[this.indices[i][0]];
			double v2[] = this.vertices[this.indices[i][1]];
			double v1[] = this.vertices[this.indices[i][2]];
			double centerX = (v1[0]+v2[0]+v3[0])/3;
			double centerY = (v1[1]+v2[1]+v3[1])/3;
			double centerZ = (v1[2]+v2[2]+v3[2])/3;
			double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
			tessellator.addVertex(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode);
			tessellator.addVertex(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode);
			tessellator.addVertex(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode);
			tessellator.addVertex(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode);
			tessellator.addVertex(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode);
			tessellator.addVertex(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode);
		}
		tessellator.draw();

		if(RenderManager.debugBoundingBox) {
			GL11.glDisable(GL11.GL_CULL_FACE);
			Vec3 pos = Minecraft.getMinecraft().thePlayer.getPosition(partialTicks);
			Vec3 ray = Minecraft.getMinecraft().thePlayer.getLook(partialTicks);
			ray.xCoord = ray.xCoord * 640.0D;
			ray.yCoord = ray.yCoord * 640.0D;
			ray.zCoord = ray.zCoord * 640.0D;
			int hitShield = boss.rayTraceShield(pos, ray, false);
			if(hitShield >= 0) {
				double v3[] = this.vertices[this.indices[hitShield][0]];
				double v2[] = this.vertices[this.indices[hitShield][1]];
				double v1[] = this.vertices[this.indices[hitShield][2]];
				double centerX = (v1[0]+v2[0]+v3[0])/3;
				double centerY = (v1[1]+v2[1]+v3[1])/3;
				double centerZ = (v1[2]+v2[2]+v3[2])/3;
				double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
				Vec3 vec1 = Vec3.createVectorHelper(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode);
				Vec3 vec2 = Vec3.createVectorHelper(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode);
				Vec3 vec3 = Vec3.createVectorHelper(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode);
				vec1 = vec1.addVector(entity.posX, entity.posY, entity.posZ);
				vec2 = vec2.addVector(entity.posX, entity.posY, entity.posZ);
				vec3 = vec3.addVector(entity.posX, entity.posY, entity.posZ);
				tessellator.startDrawing(4);
				tessellator.setColorRGBA_F(0F, 1F, 0F, 1.0F);
				tessellator.addVertex(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode);
				tessellator.addVertex(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode);
				tessellator.addVertex(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode);
				tessellator.draw();
			}
			GL11.glEnable(GL11.GL_CULL_FACE);
		}

		LightingUtil.INSTANCE.revert();

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}
}
