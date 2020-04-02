package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.capability.ProtectionShield;
import thebetweenlands.client.render.model.entity.ModelFortressBoss;
import thebetweenlands.client.render.model.entity.ModelSwordEnergy;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.common.entity.mobs.EntityFortressBoss;
import thebetweenlands.util.LightingUtil;

public class RenderFortressBoss extends Render<EntityFortressBoss> {
	public RenderFortressBoss(RenderManager renderManager) {
		super(renderManager);
	}

	private static double vertices[][] = EntityFortressBoss.ICOSAHEDRON_VERTICES;
	private static int indices[][] = EntityFortressBoss.ICOSAHEDRON_INDICES;

	private static final ResourceLocation SHIELD_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
	private static final ModelSwordEnergy BULLET_MODEL = new ModelSwordEnergy();

	private static final ResourceLocation MODEL_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/fortress_boss.png");
	private static final ModelFortressBoss MODEL = new ModelFortressBoss();

	@Override
	public void doRender(EntityFortressBoss entity, double x, double y, double z, float yaw, float partialTicks) {
		EntityFortressBoss boss = (EntityFortressBoss) entity;

		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();
			if(boss.hurtResistantTime == 0) {
				float lightIntensity = 0.0F;
				for(int i = 0; i <= 19; i++) {
					float shieldAnimationTicks = boss.shield.getAnimationTicks(i) - 1.0F + partialTicks;
					if(shieldAnimationTicks > 0 && shieldAnimationTicks <= 20) {
						lightIntensity += shieldAnimationTicks / 20.0F * 2.0F;
					}
				}
				if(lightIntensity > 0.0F) {
					ShaderHelper.INSTANCE.require();
					shader.addLight(new LightSource(boss.posX, boss.posY, boss.posZ, 16.0F, 3.4F / 4.0F * MathHelper.clamp(lightIntensity, 0.0F, 4.0F), 0.0F / 4.0F * MathHelper.clamp(lightIntensity, 0.0F, 4.0F), 3.6F / 4.0F * MathHelper.clamp(lightIntensity, 0.0F, 4.0F)));
				}
			} else {
				ShaderHelper.INSTANCE.require();
				shader.addLight(new LightSource(boss.posX, boss.posY, boss.posZ, 16.0F, 1.5F / boss.maxHurtResistantTime * (boss.hurtResistantTime + partialTicks), 0, 0));
			}
		}

		/*if(this.getRenderManager().isDebugBoundingBox()) {
			GlStateManager.depthMask(false);
			GlStateManager.disableTexture2D();
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			RenderGlobal.func_189697_a(boss.coreBoundingBox.offset(boss.posX - x, boss.posY - y, boss.posZ - z), 1, 1, 1, 1);
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		}*/

		GlStateManager.enableBlend();
		GlStateManager.pushMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.translate(x, y + (boss.coreBoundingBox.maxY-boss.coreBoundingBox.minY) / 2.0D + 0.15D, z);

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		this.bindTexture(MODEL_TEXTURE);
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0, 1, 0);
		GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1, 0, 0);
		GlStateManager.translate(0, -0.2D, 0);
		GlStateManager.translate(Math.sin((entity.ticksExisted + partialTicks)/5.0D) * 0.1F, Math.cos((entity.ticksExisted + partialTicks)/7.0D) * 0.1F, Math.cos((entity.ticksExisted + partialTicks)/6.0D) * 0.1F);
		GlStateManager.scale(0.55F, 0.55F, 0.55F);
		GlStateManager.disableCull();
		LightingUtil.INSTANCE.setLighting(255);
		MODEL.render(entity, entity.distanceWalkedModified, 360, entity.ticksExisted + partialTicks, 0, 0, 0.065F);
		LightingUtil.INSTANCE.revert();
		GlStateManager.enableCull();
		GlStateManager.popMatrix();

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x + EntityFortressBoss.SHIELD_OFFSET_X, y + EntityFortressBoss.SHIELD_OFFSET_Y, z + EntityFortressBoss.SHIELD_OFFSET_Z);
		
		renderShield(boss.shield,
				new Vec3d(boss.posX + EntityFortressBoss.SHIELD_OFFSET_X, boss.posY + EntityFortressBoss.SHIELD_OFFSET_Y, boss.posZ + EntityFortressBoss.SHIELD_OFFSET_Z),
				boss.getShieldRotationYaw(partialTicks), boss.getShieldRotationPitch(partialTicks), boss.getShieldRotationRoll(partialTicks), boss.getShieldExplosion(partialTicks),
				boss.ticksExisted, partialTicks,
				true, true, 1.0f, 1.0f, 1.0f, true);
		
		GlStateManager.popMatrix();
		
		if(boss.getGroundAttackTicks() > 0) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y - 2.8D, z);
			GlStateManager.enableTexture2D();
			this.bindTexture(SHIELD_TEXTURE);
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.loadIdentity();
			float interpTicks = entity.ticksExisted + partialTicks;
			float uOffsetAttack = interpTicks * 0.01F;
			float vOffsetAttack = interpTicks * 0.01F;
			GlStateManager.translate(uOffsetAttack, vOffsetAttack, 0.0F);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
			GlStateManager.scale(3.8D, 3.8D, 3.8D);
			GlStateManager.color(0.8F / 20.0F * boss.getGroundAttackTicks(), 0.6F / 20.0F * boss.getGroundAttackTicks(), 0.4F / 20.0F * boss.getGroundAttackTicks(), 1.0F);
			BULLET_MODEL.render(0.0625F);
			GlStateManager.enableCull();
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}

		LightingUtil.INSTANCE.revert();

		GlStateManager.enableLighting();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();
	}
	
	public static void renderShield(ProtectionShield shield, Vec3d centerPos, float shieldRotationYaw, float shieldRotationPitch, float shieldRotationRoll, float shieldExplosion, int tick, float partialTicks, boolean renderOutlines, boolean renderInside, float lineAlpha, float insideAlpha, float alpha, boolean depthMask) {
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
		
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
		
		GlStateManager.pushMatrix();

		//Rotate shield
		GlStateManager.rotate(shieldRotationPitch, 1, 0, 0);
		GlStateManager.rotate(shieldRotationYaw, 0, 1, 0);
		GlStateManager.rotate(shieldRotationRoll, 0, 0, 1);

		GlStateManager.disableTexture2D();		
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		LightingUtil.INSTANCE.setLighting(255);

		double explode = shieldExplosion;

		Minecraft.getMinecraft().getTextureManager().bindTexture(SHIELD_TEXTURE);

		float ticks = tick + partialTicks;
		
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.pushMatrix();
		float uOffset = (ticks * 0.01F) % 1.0F;
		float vOffset = (ticks * 0.01F) % 1.0F;
		GlStateManager.translate(uOffset, vOffset, 0.0F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableTexture2D();

		GlStateManager.depthMask(false);
		for(int i = 0; i <= 19; i++) {
			if(!shield.isActive(i)) {
				continue;
			}

			float shieldAnimationTicks = shield.getAnimationTicks(i) - 1.0F + partialTicks;
			float r, g, b, a;
			if(shieldAnimationTicks > 0 && shieldAnimationTicks <= 20) {
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				r = 1F / 20 * (shieldAnimationTicks); g = 00.5F - 0.5F / 20 * (shieldAnimationTicks); b = 1F - 1F / 20 * (shieldAnimationTicks); a = 1F;
			} else if(shieldAnimationTicks > 20 && shieldAnimationTicks <= 40) {
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
				r = 0.4F; g = 1F; b = 1F - 0.9F / 20 * (shieldAnimationTicks-20); a = 0.95F;
			} else {
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
				r = 0.4F; g = 0.8F; b = 0.9F; a = 0.25F;
			}
			double v3[] = vertices[indices[i][0]];
			double v2[] = vertices[indices[i][1]];
			double v1[] = vertices[indices[i][2]];
			double centerX = (v1[0]+v2[0]+v3[0])/3;
			double centerY = (v1[1]+v2[1]+v3[1])/3;
			double centerZ = (v1[2]+v2[2]+v3[2])/3;
			double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
			double textureScale = 4.0D;
			double cu = textureScale / 2.0D;
			double cv = textureScale * Math.sqrt(2) / 2.0D;
			int layers = 8;

			buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR);

			for(int l = 0; l < ((shieldAnimationTicks > 0 && shieldAnimationTicks <= 20) ? 4 : layers); l++) {
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
				buffer.pos(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode).tex(tu1, tv1).color(r, g, b, a * alpha).endVertex();
				buffer.pos(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode).tex(tu2, tv2).color(r, g, b, a * alpha).endVertex();
				buffer.pos(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode).tex(tu3, tv3).color(r, g, b, a * alpha).endVertex();
			}

			tessellator.draw();
		}
		if(depthMask) GlStateManager.depthMask(true);

		GlStateManager.disableTexture2D();
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
		for(int i = 0; i <= 19; i++) {
			if(!shield.isActive(i))
				continue;
			double v3[] = vertices[indices[i][0]];
			double v2[] = vertices[indices[i][1]];
			double v1[] = vertices[indices[i][2]];
			double centerX = (v1[0]+v2[0]+v3[0])/3;
			double centerY = (v1[1]+v2[1]+v3[1])/3;
			double centerZ = (v1[2]+v2[2]+v3[2])/3;
			double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
			buffer.pos(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode).color(0.5F, 0.6F, 1F, 0.5F * alpha).endVertex();
			buffer.pos(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode).color(0.5F, 0.6F, 1F, 0.5F * alpha).endVertex();
			buffer.pos(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode).color(0.5F, 0.6F, 1F, 0.5F * alpha).endVertex();
		}
		tessellator.draw();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		for(int p = 0; p < (renderInside ? 2 : 1); p++) {
			if(p == 1) {
				GlStateManager.cullFace(CullFace.FRONT);
				GlStateManager.depthMask(false);
			}
			buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
			for(int i = 0; i <= 19; i++) {
				if(!shield.isActive(i))
					continue;
				double v3[] = vertices[indices[i][0]];
				double v2[] = vertices[indices[i][1]];
				double v1[] = vertices[indices[i][2]];
				double centerX = (v1[0]+v2[0]+v3[0])/3;
				double centerY = (v1[1]+v2[1]+v3[1])/3;
				double centerZ = (v1[2]+v2[2]+v3[2])/3;
				double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
				double a = len + explode;
				Vec3d center = new Vec3d(centerX, centerY, centerZ);
				Vec3d vert1 = new Vec3d(v1[0], v1[1], v1[2]);
				double b = vert1.dotProduct(center);
				double d = a * Math.tan(b);
				double vertexExplode = Math.sqrt(a*a + d*d) - 1;
				Vec3d v1Normalized = new Vec3d(v1[0], v1[1], v1[2]).normalize();
				Vec3d v2Normalized = new Vec3d(v2[0], v2[1], v2[2]).normalize();
				Vec3d v3Normalized = new Vec3d(v3[0], v3[1], v3[2]).normalize();
				buffer.pos(v1[0]+v1Normalized.x*vertexExplode, v1[1]+v1Normalized.y*vertexExplode, v1[2]+v1Normalized.z*vertexExplode).color(0.05F, 0.05F, 0.05F, (p == 0 ? 0.45F : insideAlpha) * alpha).endVertex();
				buffer.pos(v2[0]+v2Normalized.x*vertexExplode, v2[1]+v2Normalized.y*vertexExplode, v2[2]+v2Normalized.z*vertexExplode).color(0.05F, 0.05F, 0.05F, (p == 0 ? 0.45F : insideAlpha) * alpha).endVertex();
				buffer.pos(v3[0]+v3Normalized.x*vertexExplode, v3[1]+v3Normalized.y*vertexExplode, v3[2]+v3Normalized.z*vertexExplode).color(0.05F, 0.05F, 0.05F, (p == 0 ? 0.45F : insideAlpha) * alpha).endVertex();
			}
			tessellator.draw();
		}

		GlStateManager.cullFace(CullFace.BACK);
		if(depthMask) GlStateManager.depthMask(true);

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		if(Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox()) {
			buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			for(int i = 0; i <= 19; i++) {
				if(!shield.isActive(i))
					continue;
				double v3[] = vertices[indices[i][0]];
				double v2[] = vertices[indices[i][1]];
				double v1[] = vertices[indices[i][2]];
				double centerX = (v1[0]+v2[0]+v3[0])/3;
				double centerY = (v1[1]+v2[1]+v3[1])/3;
				double centerZ = (v1[2]+v2[2]+v3[2])/3;
				double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
				Vec3d vec1 = new Vec3d(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode);
				Vec3d vec2 = new Vec3d(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode);
				Vec3d vec3 = new Vec3d(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode);
				Vec3d normal = vec2.subtract(vec1).crossProduct(vec3.subtract(vec1));
				buffer.pos(centerX+centerX/len*explode, centerY+centerY/len*explode, centerZ+centerZ/len*explode).color(0.8F, 0.0F, 1F, 0.5F).endVertex();
				buffer.pos(normal.x+centerX+centerX/len*explode, normal.y+centerY+centerY/len*explode, normal.z+centerZ+centerZ/len*explode).color(0.8F, 0.0F, 1F, 0.5F).endVertex();
			}
			tessellator.draw();
		}

		if(renderOutlines) {
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glLineWidth(1.0F);
	
			buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			for(int i = 0; i <= 19; i++) {
				if(!shield.isActive(i))
					continue;
				double v3[] = vertices[indices[i][0]];
				double v2[] = vertices[indices[i][1]];
				double v1[] = vertices[indices[i][2]];
				double centerX = (v1[0]+v2[0]+v3[0])/3;
				double centerY = (v1[1]+v2[1]+v3[1])/3;
				double centerZ = (v1[2]+v2[2]+v3[2])/3;
				double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
				buffer.pos(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode).color(0.5F, 0.75F, 1F, lineAlpha * alpha).endVertex();
				buffer.pos(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode).color(0.5F, 0.75F, 1F, lineAlpha * alpha).endVertex();
				buffer.pos(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode).color(0.5F, 0.75F, 1F, lineAlpha * alpha).endVertex();
				buffer.pos(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode).color(0.5F, 0.75F, 1F, lineAlpha * alpha).endVertex();
				buffer.pos(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode).color(0.5F, 0.75F, 1F, lineAlpha * alpha).endVertex();
				buffer.pos(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode).color(0.5F, 0.75F, 1F, lineAlpha * alpha).endVertex();
			}
			tessellator.draw();
	
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
		}

		if(Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox()) {
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableCull();
			Vec3d pos = Minecraft.getMinecraft().player.getPositionEyes(partialTicks);
			Vec3d ray = Minecraft.getMinecraft().player.getLook(partialTicks);
			ray = ray.scale(64.0D);
			
			int hitShield = EntityFortressBoss.rayTraceShield(shield, centerPos, shieldRotationYaw, shieldRotationPitch, shieldRotationRoll, shieldExplosion, pos, ray, false);
			if(hitShield >= 0) {
				double v3[] = vertices[indices[hitShield][0]];
				double v2[] = vertices[indices[hitShield][1]];
				double v1[] = vertices[indices[hitShield][2]];
				double centerX = (v1[0]+v2[0]+v3[0])/3;
				double centerY = (v1[1]+v2[1]+v3[1])/3;
				double centerZ = (v1[2]+v2[2]+v3[2])/3;
				double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
				Vec3d vec1 = new Vec3d(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode);
				Vec3d vec2 = new Vec3d(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode);
				Vec3d vec3 = new Vec3d(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode);
				vec1 = vec1.add(centerPos);
				vec2 = vec2.add(centerPos);
				vec3 = vec3.add(centerPos);
				buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
				buffer.pos(v1[0]+centerX/len*explode, v1[1]+centerY/len*explode, v1[2]+centerZ/len*explode).color(0F, 0F, 0F, 1.0F).endVertex();
				buffer.pos(v2[0]+centerX/len*explode, v2[1]+centerY/len*explode, v2[2]+centerZ/len*explode).color(0F, 0F, 0F, 1.0F).endVertex();
				buffer.pos(v3[0]+centerX/len*explode, v3[1]+centerY/len*explode, v3[2]+centerZ/len*explode).color(0F, 0F, 0F, 1.0F).endVertex();
				tessellator.draw();
			}
			GlStateManager.enableCull();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		}

		GlStateManager.popMatrix();
		
		GlStateManager.enableCull();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		
		GlStateManager.enableLighting();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
		
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.depthMask(true);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFortressBoss entity) {
		return null;
	}
}
