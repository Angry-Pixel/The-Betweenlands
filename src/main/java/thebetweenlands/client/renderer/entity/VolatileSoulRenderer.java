package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.VolatileSoul;

public class VolatileSoulRenderer extends EntityRenderer<VolatileSoul> {

	protected static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/wight_face.png");
	protected static final ResourceLocation TEXTURE_TRAIL = TheBetweenlands.prefix("textures/entity/volatile_soul_trail.png");

	public VolatileSoulRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(VolatileSoul entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.scale(0.5F, 0.5F, 0.5F);
		stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		PoseStack.Pose pose = stack.last();
		VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(this.getTextureLocation(entity)));

		consumer.addVertex(pose, -0.5F, -0.25F, 0.0F).setUv(0.0F, 1.0F).setColor(-1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, 0.5F, -0.25F, 0.0F).setUv(1.0F, 1.0F).setColor(-1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, 0.5F, 0.75F, 0.0F).setUv(1.0F, 0.0F).setColor(-1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, -0.5F, 0.75F, 0.0F).setUv(0.0F, 0.0F).setColor(-1).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);

		stack.popPose();

		//TODO render trail
	}

	@Override
	public ResourceLocation getTextureLocation(VolatileSoul entity) {
		return TEXTURE;
	}
}
