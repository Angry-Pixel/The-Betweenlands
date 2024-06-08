package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Deadmau5EarsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   public Deadmau5EarsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> p_116860_) {
      super(p_116860_);
   }

   public void render(PoseStack p_116873_, MultiBufferSource p_116874_, int p_116875_, AbstractClientPlayer p_116876_, float p_116877_, float p_116878_, float p_116879_, float p_116880_, float p_116881_, float p_116882_) {
      if ("deadmau5".equals(p_116876_.getName().getString()) && p_116876_.isSkinLoaded() && !p_116876_.isInvisible()) {
         VertexConsumer vertexconsumer = p_116874_.getBuffer(RenderType.entitySolid(p_116876_.getSkinTextureLocation()));
         int i = LivingEntityRenderer.getOverlayCoords(p_116876_, 0.0F);

         for(int j = 0; j < 2; ++j) {
            float f = Mth.lerp(p_116879_, p_116876_.yRotO, p_116876_.getYRot()) - Mth.lerp(p_116879_, p_116876_.yBodyRotO, p_116876_.yBodyRot);
            float f1 = Mth.lerp(p_116879_, p_116876_.xRotO, p_116876_.getXRot());
            p_116873_.pushPose();
            p_116873_.mulPose(Vector3f.YP.rotationDegrees(f));
            p_116873_.mulPose(Vector3f.XP.rotationDegrees(f1));
            p_116873_.translate((double)(0.375F * (float)(j * 2 - 1)), 0.0D, 0.0D);
            p_116873_.translate(0.0D, -0.375D, 0.0D);
            p_116873_.mulPose(Vector3f.XP.rotationDegrees(-f1));
            p_116873_.mulPose(Vector3f.YP.rotationDegrees(-f));
            float f2 = 1.3333334F;
            p_116873_.scale(1.3333334F, 1.3333334F, 1.3333334F);
            this.getParentModel().renderEars(p_116873_, vertexconsumer, p_116875_, i);
            p_116873_.popPose();
         }

      }
   }
}