package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.renderer.BeamRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.PredatorArrowGuide;

public class PredatorArrowGuideRenderer extends EntityRenderer<PredatorArrowGuide> {

	private static final ResourceLocation BEAM_TEXTURE = TheBetweenlands.prefix("textures/particle/beam.png");

	public PredatorArrowGuideRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(PredatorArrowGuide entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

		Entity target = entity.getTarget();
		Entity ridingEntity = entity.getVehicle();

		if(target != null) {
			double ix = Mth.lerp(partialTicks, entity.xo, entity.getX());
			double iy = Mth.lerp(partialTicks, entity.yo, entity.getY());
			double iz = Mth.lerp(partialTicks, entity.zo, entity.getZ());

			double targetIx = Mth.lerp(partialTicks, target.xo, target.getX());
			double targetIy = Mth.lerp(partialTicks, target.yo, target.getY()) + target.getEyeHeight();
			double targetIz = Mth.lerp(partialTicks, target.zo, target.getZ());

			double diffX = targetIx - ix;
			double diffY = targetIy - iy;
			double diffZ = targetIz - iz;

			poseStack.pushPose();
			poseStack.translate(0.0D, -(ridingEntity != null ? (entity.getY() - ridingEntity.getY()) : 0), 0.0D);
			VertexConsumer consumer = bufferSource.getBuffer(RenderType.energySwirl(BEAM_TEXTURE, 0.0F, 0.0F));

			var rot = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
			BeamRenderer.buildBeam(diffX, diffY, diffZ, new Vec3(-diffX, -diffY, -diffZ), 0.05F, 0, 2F,
				rot.x(), rot.z(), rot.y() * rot.z(), rot.x() * rot.y(), rot.y(),
				(vx, vy, vz, u, v) -> consumer.addVertex(poseStack.last(), vx, vy, vz).setUv(u, v).setColor(35, 80, 110, 255).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(poseStack.last(), 0.0F, 1.0F, 0.0F));

			poseStack.popPose();
		}
	}

	@Override
	public ResourceLocation getTextureLocation(PredatorArrowGuide entity) {
		return null;
	}
}
