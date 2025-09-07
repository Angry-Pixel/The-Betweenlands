package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import thebetweenlands.api.attachment.ProtectionShield;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.PrimordialMalevolenceModel;
import thebetweenlands.client.model.entity.SwordEnergyModel;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.client.shader.postprocessing.WorldShader;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.boss.PrimordialMalevolence;

public class PrimordialMalevolenceRenderer extends MobRenderer<PrimordialMalevolence, PrimordialMalevolenceModel> {

	private static final double[][] VERTICES = PrimordialMalevolence.ICOSAHEDRON_VERTICES;
	private static final int[][] INDICES = PrimordialMalevolence.ICOSAHEDRON_INDICES;
	protected static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/primordial_malevolence.png");
	protected static final ResourceLocation SHIELD_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
	private final SwordEnergyModel bulletModel;

	public PrimordialMalevolenceRenderer(EntityRendererProvider.Context context) {
		super(context, new PrimordialMalevolenceModel(context.bakeLayer(BLModelLayers.PRIMORDIAL_MALEVOLENCE)), 0.0F);
		this.bulletModel = new SwordEnergyModel(context.bakeLayer(BLModelLayers.SWORD_ENERGY));
	}

	@Override
	public void render(PrimordialMalevolence entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();
			if (entity.invulnerableTime == 0) {
				float lightIntensity = 0.0F;
				for (int i = 0; i <= 19; i++) {
					float shieldAnimationTicks = entity.shield.getAnimationTicks(i) - 1.0F + partialTicks;
					if (shieldAnimationTicks > 0 && shieldAnimationTicks <= 20) {
						lightIntensity += shieldAnimationTicks / 20.0F * 2.0F;
					}
				}
				if (lightIntensity > 0.0F) {
					ShaderHelper.INSTANCE.require();
					shader.addLight(new LightSource(entity.getX(), entity.getY(), entity.getZ(), 16.0F, 3.4F / 4.0F * Mth.clamp(lightIntensity, 0.0F, 4.0F), 0.0F / 4.0F * Mth.clamp(lightIntensity, 0.0F, 4.0F), 3.6F / 4.0F * Mth.clamp(lightIntensity, 0.0F, 4.0F)));
				}
			} else {
				ShaderHelper.INSTANCE.require();
				shader.addLight(new LightSource(entity.getX(), entity.getY(), entity.getZ(), 16.0F, 1.5F / entity.invulnerableDuration * (entity.invulnerableTime + partialTicks), 0, 0));
			}
		}

		stack.pushPose();
		stack.translate(0.0F, (entity.coreBoundingBox.maxY - entity.coreBoundingBox.minY) / 2.0D + 0.15D, 0.0F);

		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
		stack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
		stack.translate(0, -0.2D, 0);
		stack.translate(Math.sin((entity.tickCount + partialTicks) / 5.0D) * 0.1F, Math.cos((entity.tickCount + partialTicks) / 7.0D) * 0.1F, Math.cos((entity.tickCount + partialTicks) / 6.0D) * 0.1F);
		stack.scale(0.55F, 0.55F, 0.55F);
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity))), LightTexture.FULL_BRIGHT, LivingEntityRenderer.getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks)));
		stack.popPose();

		stack.pushPose();
		stack.translate(PrimordialMalevolence.SHIELD_OFFSET_X, PrimordialMalevolence.SHIELD_OFFSET_Y, PrimordialMalevolence.SHIELD_OFFSET_Z);

		renderShield(stack, buffer, entity.shield,
			new Vec3(entity.getX() + PrimordialMalevolence.SHIELD_OFFSET_X, entity.getY() + PrimordialMalevolence.SHIELD_OFFSET_Y, entity.getZ() + PrimordialMalevolence.SHIELD_OFFSET_Z),
			entity.getShieldRotationYaw(partialTicks), entity.getShieldRotationPitch(partialTicks), entity.getShieldRotationRoll(partialTicks), entity.getShieldExplosion(partialTicks),
			entity.tickCount, partialTicks,
			true, true, 1.0f, 1.0f, 1.0f, true);

		stack.popPose();

		if (entity.getGroundAttackTicks() > 0) {
			stack.pushPose();
			stack.translate(0.0F, -2.8D, 0.0F);
			float interpTicks = entity.tickCount + partialTicks;
			float uOffsetAttack = interpTicks * 0.01F;
			float vOffsetAttack = interpTicks * 0.01F;
			stack.scale(3.8F, 3.8F, 3.8F);
			this.bulletModel.renderToBuffer(stack, buffer.getBuffer(RenderType.energySwirl(SHIELD_TEXTURE, uOffsetAttack, vOffsetAttack)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(1.0F, 0.8F / 20.0F * entity.getGroundAttackTicks(), 0.6F / 20.0F * entity.getGroundAttackTicks(), 0.4F / 20.0F * entity.getGroundAttackTicks()));
			stack.popPose();
		}
	}

	public static void renderShield(PoseStack stack, MultiBufferSource source, ProtectionShield shield, Vec3 centerPos, float shieldRotationYaw, float shieldRotationPitch, float shieldRotationRoll, float shieldExplosion, int tick, float partialTicks, boolean renderOutlines, boolean renderInside, float lineAlpha, float insideAlpha, float alpha, boolean depthMask) {
		stack.pushPose();
		//Rotate shield
		stack.mulPose(new Quaternionf().rotateXYZ(shieldRotationPitch * Mth.DEG_TO_RAD, shieldRotationYaw * Mth.DEG_TO_RAD, shieldRotationRoll * Mth.DEG_TO_RAD));

		float ticks = tick + partialTicks;

		stack.pushPose();
		float uOffset = (ticks * 0.01F) % 1.0F;
		float vOffset = (ticks * 0.01F) % 1.0F;

		PoseStack.Pose pose = stack.last();

		//actual shields
		for (int i = 0; i <= 19; i++) {
			if (!shield.isActive(i)) {
				continue;
			}

			float shieldAnimationTicks = shield.getAnimationTicks(i) - 1.0F + partialTicks;
			float r, g, b, a;
			if (shieldAnimationTicks > 0 && shieldAnimationTicks <= 20) {
				r = 1F / 20 * (shieldAnimationTicks);
				g = 0.5F - 0.5F / 20 * (shieldAnimationTicks);
				b = 1F - 1F / 20 * (shieldAnimationTicks);
				a = 1F;
			} else if (shieldAnimationTicks > 20 && shieldAnimationTicks <= 40) {
				r = 0.4F;
				g = 1F;
				b = 1F - 0.9F / 20 * (shieldAnimationTicks - 20);
				a = 0.95F;
			} else {
				r = 0.4F;
				g = 0.8F;
				b = 0.9F;
				a = 0.25F;
			}
			double[] v3 = VERTICES[INDICES[i][0]];
			double[] v2 = VERTICES[INDICES[i][1]];
			double[] v1 = VERTICES[INDICES[i][2]];
			double centerX = (v1[0] + v2[0] + v3[0]) / 3;
			double centerY = (v1[1] + v2[1] + v3[1]) / 3;
			double centerZ = (v1[2] + v2[2] + v3[2]) / 3;
			double len = Math.sqrt(centerX * centerX + centerY * centerY + centerZ * centerZ);
			float textureScale = 4.0F;
			float cu = textureScale / 2.0F;
			float cv = textureScale * Mth.sqrt(2) / 2.0F;
			int layers = 8;

			for (int l = 0; l < ((shieldAnimationTicks > 0 && shieldAnimationTicks <= 20) ? 4 : layers); l++) {
				float cos = Mth.cos(2.0F * Mth.PI / layers * l);
				float sin = 1 + Mth.sin(2.0F * Mth.PI / layers * l);
				float tu1 = 0.0F - cu;
				float tv1 = 0.0F - cv;
				float tu2 = textureScale / 2.0F / layers * l - cu;
				float tv2 = textureScale / layers * l * Mth.sqrt(2) - cv;
				float tu3 = textureScale / layers * l - cu;
				float tv3 = 0.0F - cv;
				tu1 = cu + tu1 * sin;
				tv1 = cv + tv1 * cos;
				tu2 = cu + tu2 * sin;
				tv2 = cv + tv2 * cos;
				tu3 = cu + tu3 * sin;
				tv3 = cv + tv3 * cos;
				VertexConsumer consumer = source.getBuffer(BLRenderTypes.primordialShield(SHIELD_TEXTURE, uOffset, vOffset, depthMask));
				consumer.addVertex(pose, (float) (v1[0] + centerX / len * shieldExplosion), (float) (v1[1] + centerY / len * shieldExplosion), (float) (v1[2] + centerZ / len * shieldExplosion)).setUv(tu1, tv1).setColor(r, g, b, a * alpha);
				consumer.addVertex(pose, (float) (v2[0] + centerX / len * shieldExplosion), (float) (v2[1] + centerY / len * shieldExplosion), (float) (v2[2] + centerZ / len * shieldExplosion)).setUv(tu2, tv2).setColor(r, g, b, a * alpha);
				consumer.addVertex(pose, (float) (v3[0] + centerX / len * shieldExplosion), (float) (v3[1] + centerY / len * shieldExplosion), (float) (v3[2] + centerZ / len * shieldExplosion)).setUv(tu3, tv3).setColor(r, g, b, a * alpha);
			}
		}

		stack.popPose();

		//shield filler
		for (int i = 0; i <= 19; i++) {
			if (!shield.isActive(i))
				continue;
			double[] v3 = VERTICES[INDICES[i][0]];
			double[] v2 = VERTICES[INDICES[i][1]];
			double[] v1 = VERTICES[INDICES[i][2]];
			double centerX = (v1[0] + v2[0] + v3[0]) / 3;
			double centerY = (v1[1] + v2[1] + v3[1]) / 3;
			double centerZ = (v1[2] + v2[2] + v3[2]) / 3;
			double len = Math.sqrt(centerX * centerX + centerY * centerY + centerZ * centerZ);
			VertexConsumer consumer = source.getBuffer(BLRenderTypes.primordialShieldFiller());
			consumer.addVertex(pose, (float) (v1[0] + centerX / len * shieldExplosion), (float) (v1[1] + centerY / len * shieldExplosion), (float) (v1[2] + centerZ / len * shieldExplosion)).setColor(0.5F, 0.6F, 1F, 0.5F * alpha);
			consumer.addVertex(pose, (float) (v2[0] + centerX / len * shieldExplosion), (float) (v2[1] + centerY / len * shieldExplosion), (float) (v2[2] + centerZ / len * shieldExplosion)).setColor(0.5F, 0.6F, 1F, 0.5F * alpha);
			consumer.addVertex(pose, (float) (v3[0] + centerX / len * shieldExplosion), (float) (v3[1] + centerY / len * shieldExplosion), (float) (v3[2] + centerZ / len * shieldExplosion)).setColor(0.5F, 0.6F, 1F, 0.5F * alpha);
		}

		//shield filler 2 I guess?
		for (int p = 0; p < (renderInside ? 2 : 1); p++) {
			for (int i = 0; i <= 19; i++) {
				if (!shield.isActive(i))
					continue;
				double[] v3 = VERTICES[INDICES[i][0]];
				double[] v2 = VERTICES[INDICES[i][1]];
				double[] v1 = VERTICES[INDICES[i][2]];
				double centerX = (v1[0] + v2[0] + v3[0]) / 3;
				double centerY = (v1[1] + v2[1] + v3[1]) / 3;
				double centerZ = (v1[2] + v2[2] + v3[2]) / 3;
				double len = Math.sqrt(centerX * centerX + centerY * centerY + centerZ * centerZ);
				double a = len + shieldExplosion;
				Vec3 center = new Vec3(centerX, centerY, centerZ);
				Vec3 vert1 = new Vec3(v1[0], v1[1], v1[2]);
				double b = vert1.dot(center);
				double d = a * Math.tan(b);
				double vertexExplode = Math.sqrt(a * a + d * d) - 1;
				Vec3 v1Normalized = new Vec3(v1[0], v1[1], v1[2]).normalize();
				Vec3 v2Normalized = new Vec3(v2[0], v2[1], v2[2]).normalize();
				Vec3 v3Normalized = new Vec3(v3[0], v3[1], v3[2]).normalize();
				VertexConsumer consumer = source.getBuffer(BLRenderTypes.primordialShieldFiller());
				consumer.addVertex(pose, (float) (v1[0] + v1Normalized.x * vertexExplode), (float) (v1[1] + v1Normalized.y * vertexExplode), (float) (v1[2] + v1Normalized.z * vertexExplode)).setColor(0.05F, 0.05F, 0.05F, (p == 0 ? 0.45F : insideAlpha) * alpha);
				consumer.addVertex(pose, (float) (v2[0] + v2Normalized.x * vertexExplode), (float) (v2[1] + v2Normalized.y * vertexExplode), (float) (v2[2] + v2Normalized.z * vertexExplode)).setColor(0.05F, 0.05F, 0.05F, (p == 0 ? 0.45F : insideAlpha) * alpha);
				consumer.addVertex(pose, (float) (v3[0] + v3Normalized.x * vertexExplode), (float) (v3[1] + v3Normalized.y * vertexExplode), (float) (v3[2] + v3Normalized.z * vertexExplode)).setColor(0.05F, 0.05F, 0.05F, (p == 0 ? 0.45F : insideAlpha) * alpha);
			}
		}

		//shield "eye" lines
		if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
			for (int i = 0; i <= 19; i++) {
				if (!shield.isActive(i))
					continue;
				double[] v3 = VERTICES[INDICES[i][0]];
				double[] v2 = VERTICES[INDICES[i][1]];
				double[] v1 = VERTICES[INDICES[i][2]];
				double centerX = (v1[0] + v2[0] + v3[0]) / 3;
				double centerY = (v1[1] + v2[1] + v3[1]) / 3;
				double centerZ = (v1[2] + v2[2] + v3[2]) / 3;
				double len = Math.sqrt(centerX * centerX + centerY * centerY + centerZ * centerZ);
				Vec3 vec1 = new Vec3(v1[0] + centerX / len * shieldExplosion, v1[1] + centerY / len * shieldExplosion, v1[2] + centerZ / len * shieldExplosion);
				Vec3 vec2 = new Vec3(v2[0] + centerX / len * shieldExplosion, v2[1] + centerY / len * shieldExplosion, v2[2] + centerZ / len * shieldExplosion);
				Vec3 vec3 = new Vec3(v3[0] + centerX / len * shieldExplosion, v3[1] + centerY / len * shieldExplosion, v3[2] + centerZ / len * shieldExplosion);
				Vec3 normal = vec2.subtract(vec1).cross(vec3.subtract(vec1));
				VertexConsumer consumer = source.getBuffer(RenderType.debugLineStrip(4.0F));
				consumer.addVertex(pose, (float) (centerX + centerX / len * shieldExplosion), (float) (centerY + centerY / len * shieldExplosion), (float) (centerZ + centerZ / len * shieldExplosion)).setColor(0.8F, 0.0F, 1F, 0.5F);
				consumer.addVertex(pose, (float) (normal.x + centerX + centerX / len * shieldExplosion), (float) (normal.y + centerY + centerY / len * shieldExplosion), (float) (normal.z + centerZ + centerZ / len * shieldExplosion)).setColor(0.8F, 0.0F, 1F, 0.5F);
			}
		}

		//shield outlines
		if (renderOutlines) {
			for (int i = 0; i <= 19; i++) {
				if (!shield.isActive(i))
					continue;
				double[] v3 = VERTICES[INDICES[i][0]];
				double[] v2 = VERTICES[INDICES[i][1]];
				double[] v1 = VERTICES[INDICES[i][2]];
				double centerX = (v1[0] + v2[0] + v3[0]) / 3;
				double centerY = (v1[1] + v2[1] + v3[1]) / 3;
				double centerZ = (v1[2] + v2[2] + v3[2]) / 3;
				double len = Math.sqrt(centerX * centerX + centerY * centerY + centerZ * centerZ) - 0.01;
				VertexConsumer consumer = source.getBuffer(RenderType.lines());
				consumer.addVertex(pose, (float) (v1[0] + centerX / len * shieldExplosion), (float) (v1[1] + centerY / len * shieldExplosion), (float) (v1[2] + centerZ / len * shieldExplosion)).setColor(0.5F, 0.75F, 1F, lineAlpha * alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
				consumer.addVertex(pose, (float) (v2[0] + centerX / len * shieldExplosion), (float) (v2[1] + centerY / len * shieldExplosion), (float) (v2[2] + centerZ / len * shieldExplosion)).setColor(0.5F, 0.75F, 1F, lineAlpha * alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
				consumer.addVertex(pose, (float) (v2[0] + centerX / len * shieldExplosion), (float) (v2[1] + centerY / len * shieldExplosion), (float) (v2[2] + centerZ / len * shieldExplosion)).setColor(0.5F, 0.75F, 1F, lineAlpha * alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
				consumer.addVertex(pose, (float) (v3[0] + centerX / len * shieldExplosion), (float) (v3[1] + centerY / len * shieldExplosion), (float) (v3[2] + centerZ / len * shieldExplosion)).setColor(0.5F, 0.75F, 1F, lineAlpha * alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
				consumer.addVertex(pose, (float) (v3[0] + centerX / len * shieldExplosion), (float) (v3[1] + centerY / len * shieldExplosion), (float) (v3[2] + centerZ / len * shieldExplosion)).setColor(0.5F, 0.75F, 1F, lineAlpha * alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
				consumer.addVertex(pose, (float) (v1[0] + centerX / len * shieldExplosion), (float) (v1[1] + centerY / len * shieldExplosion), (float) (v1[2] + centerZ / len * shieldExplosion)).setColor(0.5F, 0.75F, 1F, lineAlpha * alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
			}
		}

		//selected shield
		if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
			Vec3 pos = Minecraft.getInstance().player.getEyePosition(partialTicks);
			Vec3 ray = Minecraft.getInstance().player.getViewVector(partialTicks);
			ray = ray.scale(64.0D);

			int hitShield = PrimordialMalevolence.rayTraceShield(shield, centerPos, shieldRotationYaw, shieldRotationPitch, shieldRotationRoll, shieldExplosion, pos, ray, false);
			if (hitShield >= 0) {
				double[] v3 = VERTICES[INDICES[hitShield][0]];
				double[] v2 = VERTICES[INDICES[hitShield][1]];
				double[] v1 = VERTICES[INDICES[hitShield][2]];
				double centerX = (v1[0] + v2[0] + v3[0]) / 3;
				double centerY = (v1[1] + v2[1] + v3[1]) / 3;
				double centerZ = (v1[2] + v2[2] + v3[2]) / 3;
				double len = Math.sqrt(centerX * centerX + centerY * centerY + centerZ * centerZ);

				VertexConsumer consumer = source.getBuffer(RenderType.debugFilledBox());
				consumer.addVertex(pose, (float) (v1[0] + centerX / len * shieldExplosion), (float) (v1[1] + centerY / len * shieldExplosion), (float) (v1[2] + centerZ / len * shieldExplosion)).setColor(1.0F, 0F, 0F, 0.5F);
				consumer.addVertex(pose, (float) (v2[0] + centerX / len * shieldExplosion), (float) (v2[1] + centerY / len * shieldExplosion), (float) (v2[2] + centerZ / len * shieldExplosion)).setColor(1.0F, 0F, 0F, 0.5F);
				consumer.addVertex(pose, (float) (v3[0] + centerX / len * shieldExplosion), (float) (v3[1] + centerY / len * shieldExplosion), (float) (v3[2] + centerZ / len * shieldExplosion)).setColor(1.0F, 0F, 0F, 0.5F);
			}
		}

		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(PrimordialMalevolence entity) {
		return TEXTURE;
	}
}
