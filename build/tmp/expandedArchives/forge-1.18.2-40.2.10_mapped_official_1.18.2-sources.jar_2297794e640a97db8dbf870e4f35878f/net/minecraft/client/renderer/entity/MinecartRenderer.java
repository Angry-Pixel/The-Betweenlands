package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartRenderer<T extends AbstractMinecart> extends EntityRenderer<T> {
   private static final ResourceLocation MINECART_LOCATION = new ResourceLocation("textures/entity/minecart.png");
   protected final EntityModel<T> model;

   public MinecartRenderer(EntityRendererProvider.Context p_174300_, ModelLayerLocation p_174301_) {
      super(p_174300_);
      this.shadowRadius = 0.7F;
      this.model = new MinecartModel<>(p_174300_.bakeLayer(p_174301_));
   }

   public void render(T p_115418_, float p_115419_, float p_115420_, PoseStack p_115421_, MultiBufferSource p_115422_, int p_115423_) {
      super.render(p_115418_, p_115419_, p_115420_, p_115421_, p_115422_, p_115423_);
      p_115421_.pushPose();
      long i = (long)p_115418_.getId() * 493286711L;
      i = i * i * 4392167121L + i * 98761L;
      float f = (((float)(i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float f1 = (((float)(i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float f2 = (((float)(i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      p_115421_.translate((double)f, (double)f1, (double)f2);
      double d0 = Mth.lerp((double)p_115420_, p_115418_.xOld, p_115418_.getX());
      double d1 = Mth.lerp((double)p_115420_, p_115418_.yOld, p_115418_.getY());
      double d2 = Mth.lerp((double)p_115420_, p_115418_.zOld, p_115418_.getZ());
      double d3 = (double)0.3F;
      Vec3 vec3 = p_115418_.getPos(d0, d1, d2);
      float f3 = Mth.lerp(p_115420_, p_115418_.xRotO, p_115418_.getXRot());
      if (vec3 != null) {
         Vec3 vec31 = p_115418_.getPosOffs(d0, d1, d2, (double)0.3F);
         Vec3 vec32 = p_115418_.getPosOffs(d0, d1, d2, (double)-0.3F);
         if (vec31 == null) {
            vec31 = vec3;
         }

         if (vec32 == null) {
            vec32 = vec3;
         }

         p_115421_.translate(vec3.x - d0, (vec31.y + vec32.y) / 2.0D - d1, vec3.z - d2);
         Vec3 vec33 = vec32.add(-vec31.x, -vec31.y, -vec31.z);
         if (vec33.length() != 0.0D) {
            vec33 = vec33.normalize();
            p_115419_ = (float)(Math.atan2(vec33.z, vec33.x) * 180.0D / Math.PI);
            f3 = (float)(Math.atan(vec33.y) * 73.0D);
         }
      }

      p_115421_.translate(0.0D, 0.375D, 0.0D);
      p_115421_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_115419_));
      p_115421_.mulPose(Vector3f.ZP.rotationDegrees(-f3));
      float f5 = (float)p_115418_.getHurtTime() - p_115420_;
      float f6 = p_115418_.getDamage() - p_115420_;
      if (f6 < 0.0F) {
         f6 = 0.0F;
      }

      if (f5 > 0.0F) {
         p_115421_.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(f5) * f5 * f6 / 10.0F * (float)p_115418_.getHurtDir()));
      }

      int j = p_115418_.getDisplayOffset();
      BlockState blockstate = p_115418_.getDisplayBlockState();
      if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
         p_115421_.pushPose();
         float f4 = 0.75F;
         p_115421_.scale(0.75F, 0.75F, 0.75F);
         p_115421_.translate(-0.5D, (double)((float)(j - 8) / 16.0F), 0.5D);
         p_115421_.mulPose(Vector3f.YP.rotationDegrees(90.0F));
         this.renderMinecartContents(p_115418_, p_115420_, blockstate, p_115421_, p_115422_, p_115423_);
         p_115421_.popPose();
      }

      p_115421_.scale(-1.0F, -1.0F, 1.0F);
      this.model.setupAnim(p_115418_, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      VertexConsumer vertexconsumer = p_115422_.getBuffer(this.model.renderType(this.getTextureLocation(p_115418_)));
      this.model.renderToBuffer(p_115421_, vertexconsumer, p_115423_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      p_115421_.popPose();
   }

   public ResourceLocation getTextureLocation(T p_115416_) {
      return MINECART_LOCATION;
   }

   protected void renderMinecartContents(T p_115424_, float p_115425_, BlockState p_115426_, PoseStack p_115427_, MultiBufferSource p_115428_, int p_115429_) {
      Minecraft.getInstance().getBlockRenderer().renderSingleBlock(p_115426_, p_115427_, p_115428_, p_115429_, OverlayTexture.NO_OVERLAY);
   }
}