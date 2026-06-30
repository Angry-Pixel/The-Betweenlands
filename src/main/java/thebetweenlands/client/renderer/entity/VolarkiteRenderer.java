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
import thebetweenlands.client.model.entity.VolarkiteModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.VolarkiteEntity;

public class VolarkiteRenderer extends EntityRenderer<VolarkiteEntity> {
    
    protected static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/volarkite.png");
    private final VolarkiteModel<VolarkiteEntity> model;

    public VolarkiteRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new VolarkiteModel<VolarkiteEntity>(context.bakeLayer(BLModelLayers.VOLARKITE));
    }

    @Override
    public void render(VolarkiteEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, 0.95D, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) + 180));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-Mth.lerp(partialTicks, entity.prevRotationRoll, entity.rotationRoll)));
        poseStack.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        poseStack.translate(0, 0.5D, 0);
        poseStack.translate(0, 0, 0.14D);
        poseStack.scale(-1, -1, 1);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(VolarkiteEntity entity) {
        return TEXTURE;
    }

}

