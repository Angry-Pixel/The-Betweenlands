package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeStingerLayer<T extends LivingEntity, M extends PlayerModel<T>> extends StuckInBodyLayer<T, M> {
   private static final ResourceLocation BEE_STINGER_LOCATION = new ResourceLocation("textures/entity/bee/bee_stinger.png");

   public BeeStingerLayer(LivingEntityRenderer<T, M> p_116580_) {
      super(p_116580_);
   }

   protected int numStuck(T p_116582_) {
      return p_116582_.getStingerCount();
   }

   protected void renderStuckItem(PoseStack p_116584_, MultiBufferSource p_116585_, int p_116586_, Entity p_116587_, float p_116588_, float p_116589_, float p_116590_, float p_116591_) {
      float f = Mth.sqrt(p_116588_ * p_116588_ + p_116590_ * p_116590_);
      float f1 = (float)(Math.atan2((double)p_116588_, (double)p_116590_) * (double)(180F / (float)Math.PI));
      float f2 = (float)(Math.atan2((double)p_116589_, (double)f) * (double)(180F / (float)Math.PI));
      p_116584_.translate(0.0D, 0.0D, 0.0D);
      p_116584_.mulPose(Vector3f.YP.rotationDegrees(f1 - 90.0F));
      p_116584_.mulPose(Vector3f.ZP.rotationDegrees(f2));
      float f3 = 0.0F;
      float f4 = 0.125F;
      float f5 = 0.0F;
      float f6 = 0.0625F;
      float f7 = 0.03125F;
      p_116584_.mulPose(Vector3f.XP.rotationDegrees(45.0F));
      p_116584_.scale(0.03125F, 0.03125F, 0.03125F);
      p_116584_.translate(2.5D, 0.0D, 0.0D);
      VertexConsumer vertexconsumer = p_116585_.getBuffer(RenderType.entityCutoutNoCull(BEE_STINGER_LOCATION));

      for(int i = 0; i < 4; ++i) {
         p_116584_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
         PoseStack.Pose posestack$pose = p_116584_.last();
         Matrix4f matrix4f = posestack$pose.pose();
         Matrix3f matrix3f = posestack$pose.normal();
         vertex(vertexconsumer, matrix4f, matrix3f, -4.5F, -1, 0.0F, 0.0F, p_116586_);
         vertex(vertexconsumer, matrix4f, matrix3f, 4.5F, -1, 0.125F, 0.0F, p_116586_);
         vertex(vertexconsumer, matrix4f, matrix3f, 4.5F, 1, 0.125F, 0.0625F, p_116586_);
         vertex(vertexconsumer, matrix4f, matrix3f, -4.5F, 1, 0.0F, 0.0625F, p_116586_);
      }

   }

   private static void vertex(VertexConsumer p_116593_, Matrix4f p_116594_, Matrix3f p_116595_, float p_116596_, int p_116597_, float p_116598_, float p_116599_, int p_116600_) {
      p_116593_.vertex(p_116594_, p_116596_, (float)p_116597_, 0.0F).color(255, 255, 255, 255).uv(p_116598_, p_116599_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_116600_).normal(p_116595_, 0.0F, 1.0F, 0.0F).endVertex();
   }
}