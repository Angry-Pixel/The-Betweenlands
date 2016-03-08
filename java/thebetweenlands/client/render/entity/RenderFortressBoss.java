package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.entities.mobs.EntityFortressBoss;
import thebetweenlands.utils.LightingUtil;

public class RenderFortressBoss extends Render {
	private static double vertices[][] = EntityFortressBoss.ICOSAHEDRON_VERTICES;
	private static int indices[][] = EntityFortressBoss.ICOSAHEDRON_INDICES;

	private static final ResourceLocation shieldTexture = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

	private static final ModelWight modelHeadOnly = new ModelWight().setRenderHeadOnly(true);

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		EntityFortressBoss boss = (EntityFortressBoss) entity;

		if(ShaderHelper.INSTANCE.canUseShaders()) {
			float lightIntensity = 0.0F;
			for(int i = 0; i <= 19; i++) {
				float shieldAnimationTicks = boss.shieldAnimationTicks[i] - 1.0F + partialTicks;
				if(shieldAnimationTicks > 0 && shieldAnimationTicks <= 20) {
					lightIntensity += shieldAnimationTicks / 20.0F * 2.0F;
				}
			}
			if(lightIntensity > 0.0F)
				ShaderHelper.INSTANCE.addDynLight(new LightSource(boss.posX, boss.posY, boss.posZ, 6.0F, 3.4F / 4.0F * MathHelper.clamp_float(lightIntensity, 0.0F, 4.0F), 0.0F / 4.0F * MathHelper.clamp_float(lightIntensity, 0.0F, 4.0F), 3.6F / 4.0F * MathHelper.clamp_float(lightIntensity, 0.0F, 4.0F)));
		}

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

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1, 1, 1, 0.8F);
		GL11.glTranslated(x, y + (boss.coreBoundingBox.maxY-boss.coreBoundingBox.minY) / 2.0D + 0.15D, z);

		this.bindTexture(new ResourceLocation("thebetweenlands:textures/entity/wight.png"));
		GL11.glRotated(180, 1, 0, 0);
		GL11.glRotated(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0, 1, 0);
		GL11.glTranslated(0, 0, 0.25D);
		GL11.glTranslated(Math.sin((entity.ticksExisted + partialTicks)/5.0D) * 0.1F, Math.cos((entity.ticksExisted + partialTicks)/7.0D) * 0.1F, Math.cos((entity.ticksExisted + partialTicks)/6.0D) * 0.1F);
		modelHeadOnly.render(entity, entity.distanceWalkedModified, 360, entity.ticksExisted + partialTicks, 0, 0, 0.065F);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glTranslated(x + EntityFortressBoss.SHIELD_OFFSET_X, y + EntityFortressBoss.SHIELD_OFFSET_Y, z + EntityFortressBoss.SHIELD_OFFSET_Z);

		//Rotate shield
		GL11.glRotated(boss.getShieldRotationPitch(partialTicks), 1, 0, 0);
		GL11.glRotated(boss.getShieldRotationYaw(partialTicks), 0, 1, 0);
		GL11.glRotated(boss.getShieldRotationRoll(partialTicks), 0, 0, 1);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		Tessellator tessellator = Tessellator.instance;

		LightingUtil.INSTANCE.setLighting(255);

		double explode = boss.SHIELD_EXPLOSION;

		float ticks = (float)entity.ticksExisted + partialTicks;
		this.bindTexture(shieldTexture);
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
		for(int i = 0; i <= 19; i++) {
			if(!boss.isShieldActive(i))
				continue;
			float shieldAnimationTicks = boss.shieldAnimationTicks[i] - 1.0F + partialTicks;
			if(shieldAnimationTicks > 0 && shieldAnimationTicks <= 20) {
				tessellator.setColorRGBA_F(0.4F - 0.4F / 20 * (shieldAnimationTicks), 1.0F / 20 * (shieldAnimationTicks), 1F - 1.0F / 20 * (shieldAnimationTicks), 0.8F);
			} else if(shieldAnimationTicks > 20 && shieldAnimationTicks <= 40) {
				tessellator.setColorRGBA_F(0.8F, 0.4F / 20 * (shieldAnimationTicks-20), 0.8F, 0.8F);
			} else {
				tessellator.setColorRGBA_F(0.8F, 0.0F, 1F, 0.5F);
			}
			double v3[] = this.vertices[this.indices[i][0]];
			double v2[] = this.vertices[this.indices[i][1]];
			double v1[] = this.vertices[this.indices[i][2]];
			double centerX = (v1[0]+v2[0]+v3[0])/3;
			double centerY = (v1[1]+v2[1]+v3[1])/3;
			double centerZ = (v1[2]+v2[2]+v3[2])/3;
			double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
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
				tessellator.addVertexWithUV(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode, tu1, tv1);
				tessellator.addVertexWithUV(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode, tu2, tv2);
				tessellator.addVertexWithUV(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode, tu3, tv3);
			}
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

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		//if(RenderManager.debugBoundingBox) {
		for(int p = 0; p < 2; p++) {
			if(p == 1) {
				GL11.glCullFace(GL11.GL_FRONT);
				GL11.glDepthMask(false);
			}
			tessellator.startDrawing(4);
			tessellator.setColorRGBA_F(0.05F, 0.05F, 0.05F, 0.45F);
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
				double a = len + explode;
				Vec3 center = Vec3.createVectorHelper(centerX, centerY, centerZ);
				Vec3 vert1 = Vec3.createVectorHelper(v1[0], v1[1], v1[2]);
				double b = vert1.dotProduct(center);
				double d = a * Math.tan(b);
				double vertexExplode = Math.sqrt(a*a + d*d) - 1;
				Vec3 v1Normalized = Vec3.createVectorHelper(v1[0], v1[1], v1[2]).normalize();
				Vec3 v2Normalized = Vec3.createVectorHelper(v2[0], v2[1], v2[2]).normalize();
				Vec3 v3Normalized = Vec3.createVectorHelper(v3[0], v3[1], v3[2]).normalize();
				tessellator.addVertex(v1[0]+v1Normalized.xCoord*vertexExplode, v1[1]+v1Normalized.yCoord*vertexExplode, v1[2]+v1Normalized.zCoord*vertexExplode);
				tessellator.addVertex(v2[0]+v2Normalized.xCoord*vertexExplode, v2[1]+v2Normalized.yCoord*vertexExplode, v2[2]+v2Normalized.zCoord*vertexExplode);
				tessellator.addVertex(v3[0]+v3Normalized.xCoord*vertexExplode, v3[1]+v3Normalized.yCoord*vertexExplode, v3[2]+v3Normalized.zCoord*vertexExplode);
			}
			tessellator.draw();
		}
		//}

		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDepthMask(true);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

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

		GL11.glDisable(GL11.GL_LINE_SMOOTH);

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
