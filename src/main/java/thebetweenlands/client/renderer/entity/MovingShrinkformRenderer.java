package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.MovingShrinkformModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.MovingShrinkform;

public class MovingShrinkformRenderer extends EntityRenderer<MovingShrinkform> {
	public final static ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/moving_shrinkform.png");

	private final MovingShrinkformModel model;

	public MovingShrinkformRenderer(Context context) {
		super(context);
		this.model = new MovingShrinkformModel(context.bakeLayer(BLModelLayers.MOVING_SHRINKFORM));
	}
	
	@Override
	public void render(MovingShrinkform entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		stack.translate(0.0D, -1.5D, 0.0D);
		//this.model.prepareMobModel(entity, 0.0F, 0.0F, partialTick);
		//this.model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount, 0.0F, 0.0F);
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
		stack.popPose();

		super.render(entity, entityYaw, partialTick, stack, buffer, light);
	}

	@Override
	public ResourceLocation getTextureLocation(MovingShrinkform entity) {
		return TEXTURE;
	}
}
