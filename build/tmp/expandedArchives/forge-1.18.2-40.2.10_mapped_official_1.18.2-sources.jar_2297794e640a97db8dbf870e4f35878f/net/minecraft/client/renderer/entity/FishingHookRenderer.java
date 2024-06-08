package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FishingHookRenderer extends EntityRenderer<FishingHook> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/fishing_hook.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
   private static final double VIEW_BOBBING_SCALE = 960.0D;

   public FishingHookRenderer(EntityRendererProvider.Context p_174117_) {
      super(p_174117_);
   }

   public void render(FishingHook p_114705_, float p_114706_, float p_114707_, PoseStack p_114708_, MultiBufferSource p_114709_, int p_114710_) {
      Player player = p_114705_.getPlayerOwner();
      if (player != null) {
         p_114708_.pushPose();
         p_114708_.pushPose();
         p_114708_.scale(0.5F, 0.5F, 0.5F);
         p_114708_.mulPose(this.entityRenderDispatcher.cameraOrientation());
         p_114708_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
         PoseStack.Pose posestack$pose = p_114708_.last();
         Matrix4f matrix4f = posestack$pose.pose();
         Matrix3f matrix3f = posestack$pose.normal();
         VertexConsumer vertexconsumer = p_114709_.getBuffer(RENDER_TYPE);
         vertex(vertexconsumer, matrix4f, matrix3f, p_114710_, 0.0F, 0, 0, 1);
         vertex(vertexconsumer, matrix4f, matrix3f, p_114710_, 1.0F, 0, 1, 1);
         vertex(vertexconsumer, matrix4f, matrix3f, p_114710_, 1.0F, 1, 1, 0);
         vertex(vertexconsumer, matrix4f, matrix3f, p_114710_, 0.0F, 1, 0, 0);
         p_114708_.popPose();
         int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
         ItemStack itemstack = player.getMainHandItem();
         if (!itemstack.is(Items.FISHING_ROD)) {
            i = -i;
         }

         float f = player.getAttackAnim(p_114707_);
         float f1 = Mth.sin(Mth.sqrt(f) * (float)Math.PI);
         float f2 = Mth.lerp(p_114707_, player.yBodyRotO, player.yBodyRot) * ((float)Math.PI / 180F);
         double d0 = (double)Mth.sin(f2);
         double d1 = (double)Mth.cos(f2);
         double d2 = (double)i * 0.35D;
         double d3 = 0.8D;
         double d4;
         double d5;
         double d6;
         float f3;
         if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && player == Minecraft.getInstance().player) {
            double d7 = 960.0D / this.entityRenderDispatcher.options.fov;
            Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)i * 0.525F, -0.1F);
            vec3 = vec3.scale(d7);
            vec3 = vec3.yRot(f1 * 0.5F);
            vec3 = vec3.xRot(-f1 * 0.7F);
            d4 = Mth.lerp((double)p_114707_, player.xo, player.getX()) + vec3.x;
            d5 = Mth.lerp((double)p_114707_, player.yo, player.getY()) + vec3.y;
            d6 = Mth.lerp((double)p_114707_, player.zo, player.getZ()) + vec3.z;
            f3 = player.getEyeHeight();
         } else {
            d4 = Mth.lerp((double)p_114707_, player.xo, player.getX()) - d1 * d2 - d0 * 0.8D;
            d5 = player.yo + (double)player.getEyeHeight() + (player.getY() - player.yo) * (double)p_114707_ - 0.45D;
            d6 = Mth.lerp((double)p_114707_, player.zo, player.getZ()) - d0 * d2 + d1 * 0.8D;
            f3 = player.isCrouching() ? -0.1875F : 0.0F;
         }

         double d9 = Mth.lerp((double)p_114707_, p_114705_.xo, p_114705_.getX());
         double d10 = Mth.lerp((double)p_114707_, p_114705_.yo, p_114705_.getY()) + 0.25D;
         double d8 = Mth.lerp((double)p_114707_, p_114705_.zo, p_114705_.getZ());
         float f4 = (float)(d4 - d9);
         float f5 = (float)(d5 - d10) + f3;
         float f6 = (float)(d6 - d8);
         VertexConsumer vertexconsumer1 = p_114709_.getBuffer(RenderType.lineStrip());
         PoseStack.Pose posestack$pose1 = p_114708_.last();
         int j = 16;

         for(int k = 0; k <= 16; ++k) {
            stringVertex(f4, f5, f6, vertexconsumer1, posestack$pose1, fraction(k, 16), fraction(k + 1, 16));
         }

         p_114708_.popPose();
         super.render(p_114705_, p_114706_, p_114707_, p_114708_, p_114709_, p_114710_);
      }
   }

   private static float fraction(int p_114691_, int p_114692_) {
      return (float)p_114691_ / (float)p_114692_;
   }

   private static void vertex(VertexConsumer p_114712_, Matrix4f p_114713_, Matrix3f p_114714_, int p_114715_, float p_114716_, int p_114717_, int p_114718_, int p_114719_) {
      p_114712_.vertex(p_114713_, p_114716_ - 0.5F, (float)p_114717_ - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)p_114718_, (float)p_114719_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_114715_).normal(p_114714_, 0.0F, 1.0F, 0.0F).endVertex();
   }

   private static void stringVertex(float p_174119_, float p_174120_, float p_174121_, VertexConsumer p_174122_, PoseStack.Pose p_174123_, float p_174124_, float p_174125_) {
      float f = p_174119_ * p_174124_;
      float f1 = p_174120_ * (p_174124_ * p_174124_ + p_174124_) * 0.5F + 0.25F;
      float f2 = p_174121_ * p_174124_;
      float f3 = p_174119_ * p_174125_ - f;
      float f4 = p_174120_ * (p_174125_ * p_174125_ + p_174125_) * 0.5F + 0.25F - f1;
      float f5 = p_174121_ * p_174125_ - f2;
      float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
      f3 /= f6;
      f4 /= f6;
      f5 /= f6;
      p_174122_.vertex(p_174123_.pose(), f, f1, f2).color(0, 0, 0, 255).normal(p_174123_.normal(), f3, f4, f5).endVertex();
   }

   public ResourceLocation getTextureLocation(FishingHook p_114703_) {
      return TEXTURE_LOCATION;
   }
}