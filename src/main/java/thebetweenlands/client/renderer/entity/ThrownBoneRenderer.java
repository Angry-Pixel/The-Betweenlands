package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.ThrownBoneModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.ThrownBone;

public class ThrownBoneRenderer extends EntityRenderer<ThrownBone> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/block/bone_pile.png");
	private final ThrownBoneModel<ThrownBone> bone;

	public ThrownBoneRenderer(EntityRendererProvider.Context context) {
        super(context);
        bone = new ThrownBoneModel<ThrownBone>(context.bakeLayer(BLModelLayers.THROWN_BONE));
    }

	 @Override
	 public void render(ThrownBone entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
		poseStack.scale(1F, -1F, -1F);
		poseStack.pushPose();
		float ticks = Mth.lerp(partialTicks, entity.prevAnimationTicks, entity.animationTicks);
		poseStack.mulPose(Axis.XN.rotationDegrees(ticks));
		bone.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(ThrownBone entity) {
		return TEXTURE;
	}
}
