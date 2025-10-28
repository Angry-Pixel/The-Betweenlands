package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.DecayPitTarget;
import thebetweenlands.common.entity.multipart.DecayPitTargetPart;

public class DecayPitTargetRenderer extends EntityRenderer<DecayPitTarget> {

	private static final ResourceLocation BEAM_TEXTURE = TheBetweenlands.prefix("textures/particle/chain_beam.png");

	public static final ResourceLocation PLUG_TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_plug.png");
	public static final ResourceLocation PLUG_TEXTURE_GLOW = TheBetweenlands.prefix("textures/entity/pit/decay_pit_plug_glow.png");
	private final ModelPart plug;

	public static final ResourceLocation SHIELD_TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_target_shield.png");
	private final ModelPart shield;

	public static final ResourceLocation TARGET_TEXTURE = TheBetweenlands.prefix("textures/entity/pit/decay_pit_target.png");
	public static final ResourceLocation TARGET_TEXTURE_GLOW = TheBetweenlands.prefix("textures/entity/pit/decay_pit_target_glow.png");
	private final ModelPart targets;

	public DecayPitTargetRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.plug = context.bakeLayer(BLModelLayers.DECAY_PIT_PLUG);
		this.shield = context.bakeLayer(BLModelLayers.DECAY_PIT_SHIELD);
		this.targets = context.bakeLayer(BLModelLayers.DECAY_PIT_TARGET);
	}

	@Override
	public void render(DecayPitTarget entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		double smoothedMainX = Mth.lerp(partialTick, entity.xo, entity.getX());
		double smoothedMainY = Mth.lerp(partialTick, entity.yo, entity.getY());
		double smoothedMainZ = Mth.lerp(partialTick, entity.zo, entity.getZ());

		this.renderBeams(entity, partialTick, stack, buffer);

		stack.pushPose();
		stack.translate(0.0F, 1.5F, 0.0F);
		stack.scale(1.0F, -1.0F, -1.0F);
		this.plug.render(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(PLUG_TEXTURE)), light, OverlayTexture.NO_OVERLAY, -1);
		this.plug.render(stack, buffer.getBuffer(RenderType.eyes(PLUG_TEXTURE_GLOW)), light, OverlayTexture.NO_OVERLAY, -1);
		stack.popPose();

		DecayPitTargetPart hitPart = null;

		if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
			hitPart = entity.rayTraceShields(Minecraft.getInstance().player.getEyePosition(partialTick), Minecraft.getInstance().player.getViewVector(partialTick));
		}

		for (DecayPitTargetPart part : entity.parts) {
			float floatate = Mth.lerp(partialTick, part.yRotO, part.getYRot());
			double smoothedX = Mth.lerp(partialTick, part.xo, part.getX());
			double smoothedY = Mth.lerp(partialTick, part.yo, part.getY());
			double smoothedZ = Mth.lerp(partialTick, part.zo, part.getZ());
			if (part != entity.target_north && part != entity.target_east && part != entity.target_south && part != entity.target_west && part != entity.bottom) {

				stack.pushPose();
				stack.translate(smoothedX - smoothedMainX, 1.5F + smoothedY - smoothedMainY, smoothedZ - smoothedMainZ);
				stack.mulPose(Axis.YP.rotationDegrees(-floatate + 180));
				stack.scale(1.0F, -1.0F, -1.0F);
				this.shield.render(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(SHIELD_TEXTURE)), light, OverlayTexture.NO_OVERLAY, hitPart == part ? FastColor.ARGB32.colorFromFloat(1.0F, 1.0F, 0.0F, 0.0F) : -1);
				stack.popPose();
			}
		}

		stack.pushPose();
		stack.translate(0.0F, 4.5F, 0.0F);
		stack.scale(1.0F, -1.0F, -1.0F);

		this.targets.render(stack, buffer.getBuffer(RenderType.entityTranslucent(TARGET_TEXTURE)), light, OverlayTexture.NO_OVERLAY, -1);
		this.targets.render(stack, buffer.getBuffer(RenderType.eyes(TARGET_TEXTURE_GLOW)), light, OverlayTexture.NO_OVERLAY, -1);
		stack.popPose();
	}

	private void renderBeams(DecayPitTarget entity, float partialTick, PoseStack stack, MultiBufferSource buffer) {
		float yStart = entity.getBbHeight() - 1.0F;

		for (Direction dir : Direction.Plane.HORIZONTAL) {
			int i = dir.ordinal() - 2;
			float beamAlpha = entity.beamTransparencyTicks[i] / 15.0F;

			float diffX2 = dir.getStepZ() * -1.7F;
			float diffY2 = 0;
			float diffZ2 = dir.getStepX() * -1.7F;

			float diffX = i == 0 ? 12 : i == 1 ? -12 : 0;
			float diffY = 2.0F + entity.getProgress() * entity.MOVE_UNIT - yStart;
			float diffZ = i == 2 ? 12 : i == 3 ? -12 : 0;

			stack.pushPose();
			stack.translate(0.0F, yStart, 0.0F);

			VertexConsumer consumer = buffer.getBuffer(BLRenderTypes.pitChains(BEAM_TEXTURE,-(entity.tickCount + partialTick) * 0.15F, 0.0F));
			this.renderBeam(stack.last(), consumer, new Vector3f(0, 0, 0), new Vector3f(diffX2, diffY2, diffZ2), dir, beamAlpha);
			this.renderBeam(stack.last(), consumer, new Vector3f(diffX2, diffY2, diffZ2), new Vector3f(diffX, diffY, diffZ), dir, beamAlpha);

			stack.popPose();
		}
	}

	private void renderBeam(PoseStack.Pose pose, VertexConsumer builder, Vector3f start, Vector3f end, Direction dir, float alpha) {
		float angle = 0.45F;
		float xWidth = Math.abs(dir.getStepX()) * angle;
		float zWidth = Math.abs(dir.getStepZ()) * angle;
		float u = end.length() / (0.2F * 0.4F) / 10.0F;

		//beam diagonal 1
		//start top -> end top -> end bottom -> start bottom
		builder.addVertex(pose, start.x() + xWidth, start.y() + angle, start.z() + zWidth).setUv(0.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		builder.addVertex(pose, end.x() + xWidth, end.y() + angle, end.z() + zWidth).setUv(u, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		builder.addVertex(pose, end.x() - xWidth, end.y() - angle, end.z() - zWidth).setUv(u, 1.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		builder.addVertex(pose, start.x() - xWidth, start.y() - angle, start.z() - zWidth).setUv(0.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);

		//beam diagonal 2
		builder.addVertex(pose, start.x() - xWidth, start.y() + angle, start.z() - zWidth).setUv(0.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		builder.addVertex(pose, end.x() - xWidth, end.y() + angle, end.z() - zWidth).setUv(u, 0.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		builder.addVertex(pose, end.x() + xWidth, end.y() - angle, end.z() + zWidth).setUv(u, 1.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		builder.addVertex(pose, start.x() + xWidth, start.y() - angle, start.z() + zWidth).setUv(0.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, alpha).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(DecayPitTarget entity) {
		return null;
	}
}
