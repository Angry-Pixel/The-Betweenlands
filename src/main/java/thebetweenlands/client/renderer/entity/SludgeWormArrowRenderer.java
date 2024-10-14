package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.SludgeWormArrowModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.arrow.SludgeWormArrow;

public class SludgeWormArrowRenderer extends ArrowRenderer<SludgeWormArrow> {

	private final SludgeWormArrowModel model;

	public SludgeWormArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new SludgeWormArrowModel(context.bakeLayer(BLModelLayers.SLUDGE_WORM_ARROW));
	}

	@Override
	public void render(SludgeWormArrow entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
		stack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
		float f9 = (float)entity.shakeTime - partialTicks;
		if (f9 > 0.0F) {
			float f10 = -Mth.sin(f9 * 3.0F) * f9;
			stack.mulPose(Axis.ZP.rotationDegrees(f10));
		}
		stack.mulPose(Axis.XN.rotationDegrees(90.0F));
		stack.mulPose(Axis.ZN.rotationDegrees(90.0F));
		stack.scale(-1.0F, -1.0F, 1.0F);
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), packedLight, OverlayTexture.NO_OVERLAY);
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(SludgeWormArrow entity) {
		return TheBetweenlands.prefix("textures/entity/sludge_worm_tiny.png");
	}
}
