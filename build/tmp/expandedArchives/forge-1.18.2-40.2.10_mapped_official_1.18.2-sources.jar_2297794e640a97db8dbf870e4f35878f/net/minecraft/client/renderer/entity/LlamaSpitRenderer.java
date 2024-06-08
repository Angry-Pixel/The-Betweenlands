package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LlamaSpitRenderer extends EntityRenderer<LlamaSpit> {
   private static final ResourceLocation LLAMA_SPIT_LOCATION = new ResourceLocation("textures/entity/llama/spit.png");
   private final LlamaSpitModel<LlamaSpit> model;

   public LlamaSpitRenderer(EntityRendererProvider.Context p_174296_) {
      super(p_174296_);
      this.model = new LlamaSpitModel<>(p_174296_.bakeLayer(ModelLayers.LLAMA_SPIT));
   }

   public void render(LlamaSpit p_115373_, float p_115374_, float p_115375_, PoseStack p_115376_, MultiBufferSource p_115377_, int p_115378_) {
      p_115376_.pushPose();
      p_115376_.translate(0.0D, (double)0.15F, 0.0D);
      p_115376_.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(p_115375_, p_115373_.yRotO, p_115373_.getYRot()) - 90.0F));
      p_115376_.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(p_115375_, p_115373_.xRotO, p_115373_.getXRot())));
      this.model.setupAnim(p_115373_, p_115375_, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer vertexconsumer = p_115377_.getBuffer(this.model.renderType(LLAMA_SPIT_LOCATION));
      this.model.renderToBuffer(p_115376_, vertexconsumer, p_115378_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      p_115376_.popPose();
      super.render(p_115373_, p_115374_, p_115375_, p_115376_, p_115377_, p_115378_);
   }

   public ResourceLocation getTextureLocation(LlamaSpit p_115371_) {
      return LLAMA_SPIT_LOCATION;
   }
}