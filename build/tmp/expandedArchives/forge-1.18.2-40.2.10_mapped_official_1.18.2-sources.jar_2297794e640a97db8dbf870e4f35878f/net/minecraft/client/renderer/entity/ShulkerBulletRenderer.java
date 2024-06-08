package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerBulletRenderer extends EntityRenderer<ShulkerBullet> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/spark.png");
   private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);
   private final ShulkerBulletModel<ShulkerBullet> model;

   public ShulkerBulletRenderer(EntityRendererProvider.Context p_174368_) {
      super(p_174368_);
      this.model = new ShulkerBulletModel<>(p_174368_.bakeLayer(ModelLayers.SHULKER_BULLET));
   }

   protected int getBlockLightLevel(ShulkerBullet p_115869_, BlockPos p_115870_) {
      return 15;
   }

   public void render(ShulkerBullet p_115862_, float p_115863_, float p_115864_, PoseStack p_115865_, MultiBufferSource p_115866_, int p_115867_) {
      p_115865_.pushPose();
      float f = Mth.rotlerp(p_115862_.yRotO, p_115862_.getYRot(), p_115864_);
      float f1 = Mth.lerp(p_115864_, p_115862_.xRotO, p_115862_.getXRot());
      float f2 = (float)p_115862_.tickCount + p_115864_;
      p_115865_.translate(0.0D, (double)0.15F, 0.0D);
      p_115865_.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f2 * 0.1F) * 180.0F));
      p_115865_.mulPose(Vector3f.XP.rotationDegrees(Mth.cos(f2 * 0.1F) * 180.0F));
      p_115865_.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(f2 * 0.15F) * 360.0F));
      p_115865_.scale(-0.5F, -0.5F, 0.5F);
      this.model.setupAnim(p_115862_, 0.0F, 0.0F, 0.0F, f, f1);
      VertexConsumer vertexconsumer = p_115866_.getBuffer(this.model.renderType(TEXTURE_LOCATION));
      this.model.renderToBuffer(p_115865_, vertexconsumer, p_115867_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      p_115865_.scale(1.5F, 1.5F, 1.5F);
      VertexConsumer vertexconsumer1 = p_115866_.getBuffer(RENDER_TYPE);
      this.model.renderToBuffer(p_115865_, vertexconsumer1, p_115867_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
      p_115865_.popPose();
      super.render(p_115862_, p_115863_, p_115864_, p_115865_, p_115866_, p_115867_);
   }

   public ResourceLocation getTextureLocation(ShulkerBullet p_115860_) {
      return TEXTURE_LOCATION;
   }
}