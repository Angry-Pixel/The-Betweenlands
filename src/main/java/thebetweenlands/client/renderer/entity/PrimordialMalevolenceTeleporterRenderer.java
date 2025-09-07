package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.PrimordialMalevolenceModel;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceTeleporter;

public class PrimordialMalevolenceTeleporterRenderer extends EntityRenderer<PrimordialMalevolenceTeleporter> {

	private final PrimordialMalevolenceModel model;

	public PrimordialMalevolenceTeleporterRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new PrimordialMalevolenceModel(context.bakeLayer(BLModelLayers.PRIMORDIAL_MALEVOLENCE));
	}

	@Override
	public void render(PrimordialMalevolenceTeleporter entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0, -0.25D, 0.1D);
		stack.translate(this.model.eye.x * 0.065F / 2.0F, this.model.eye.y * 0.065F / 2.0F, this.model.eye.z * 0.065F / 2.0F);
		if (entity.isLookingAtPlayer || entity.getTarget() != null) {
			stack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot())));
			stack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
		} else {
			stack.mulPose(Axis.XP.rotationDegrees(88.0F));
		}
		stack.translate(-this.model.eye.x * 0.065F / 2.0F, -this.model.eye.y * 0.065F / 2.0F + 0.3D, -this.model.eye.z * 0.065F / 2.0F);
		if (entity.getTarget() != null)
			stack.translate(Math.sin((entity.tickCount + partialTick) / 5.0D) * 0.1F, Math.cos((entity.tickCount + partialTick) / 7.0D) * 0.1F, Math.cos((entity.tickCount + partialTick) / 6.0D) * 0.1F);
		if (entity.getTarget() == null || entity.getTarget() != Minecraft.getInstance().getCameraEntity()) {
			stack.scale(0.8F, 0.8F, 0.8F);
		} else {
			float scale = (float) Math.pow(entity.getTeleportProgress(), 3) * 2.5F;
			stack.translate(0, scale / 2.0F, 0);
			stack.scale(0.8F + scale, 0.8F + scale, 0.8F);
		}
		if (entity.getTarget() != null) {
			this.model.eye.render(stack, buffer.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity))), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(0.5F, 1.0F, 1.0F, 1.0F));
			this.model.eye.render(stack, buffer.getBuffer(RenderType.EYES.apply(this.getTextureLocation(entity), BLRenderTypes.EYE_TRANSPARENCY)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

			if (entity.getTarget() == Minecraft.getInstance().getCameraEntity()) {
				float alpha = (float) Math.pow(entity.getTeleportProgress(), 2.5D);
				this.model.clothes.render(stack, buffer.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity))), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F));
			}
		} else {
			this.model.eye.render(stack, buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity))), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(0.15F, 1.0F, 1.0F, 1.0F));
		}
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(PrimordialMalevolenceTeleporter entity) {
		return PrimordialMalevolenceRenderer.TEXTURE;
	}
}
