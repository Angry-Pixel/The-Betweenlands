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
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.ThrownBoneModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.ThrownBone;

public class ThrownBoneRenderer extends EntityRenderer<ThrownBone> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/blocks/bone_pile.png");
	private final ThrownBoneModel<ThrownBone> bone;

	public ThrownBoneRenderer(EntityRendererProvider.Context context) {
        super(context);
        bone = new ThrownBoneModel<ThrownBone>(context.bakeLayer(BLModelLayers.THROWN_BONE));
    }

	 @Override
	 public void render(ThrownBone entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entitySolid(TEXTURE));
		poseStack.pushPose();
		float size = 0.5F;
		poseStack.scale(size, size, size);
		poseStack.translate(0F, 0.5F, 0F);
		poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() + 90F));
		poseStack.pushPose();
		poseStack.mulPose(Axis.XP.rotationDegrees(90F));
		poseStack.translate(0F, -1F, 0F);
		poseStack.pushPose();
		float ticks = entity.animationTicks + (entity.animationTicks - entity.prevAnimationTicks) * partialTicks;
		poseStack.mulPose(Axis.YP.rotationDegrees(ticks * 0.25F));
		bone.renderToBuffer(poseStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		poseStack.popPose();
		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(ThrownBone entity) {
		return TEXTURE;
	}
}
