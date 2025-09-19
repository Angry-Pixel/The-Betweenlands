package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.ChiromawDroppingsModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.ChiromawDroppings;

public class ChiromawDroppingsRenderer extends EntityRenderer<ChiromawDroppings> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/chiromaw/chiromaw_droppings.png");
	private final ChiromawDroppingsModel model;

	public ChiromawDroppingsRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new ChiromawDroppingsModel(context.bakeLayer(BLModelLayers.CHIROMAW_DROPPINGS));
	}

	@Override
	public void render(ChiromawDroppings entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.mulPose(Axis.XP.rotationDegrees(entity.getAnimationRotation(partialTick)));
		stack.mulPose(Axis.YP.rotationDegrees(entity.getAnimationRotation(partialTick)));
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
		stack.popPose();

		super.render(entity, entityYaw, partialTick, stack, buffer, light);
	}

	@Override
	public ResourceLocation getTextureLocation(ChiromawDroppings entity) {
		return TEXTURE;
	}
}
