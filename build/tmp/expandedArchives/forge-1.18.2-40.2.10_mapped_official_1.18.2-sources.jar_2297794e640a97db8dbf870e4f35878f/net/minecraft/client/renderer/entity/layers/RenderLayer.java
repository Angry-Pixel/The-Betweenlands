package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderLayer<T extends Entity, M extends EntityModel<T>> {
   private final RenderLayerParent<T, M> renderer;

   public RenderLayer(RenderLayerParent<T, M> p_117346_) {
      this.renderer = p_117346_;
   }

   protected static <T extends LivingEntity> void coloredCutoutModelCopyLayerRender(EntityModel<T> p_117360_, EntityModel<T> p_117361_, ResourceLocation p_117362_, PoseStack p_117363_, MultiBufferSource p_117364_, int p_117365_, T p_117366_, float p_117367_, float p_117368_, float p_117369_, float p_117370_, float p_117371_, float p_117372_, float p_117373_, float p_117374_, float p_117375_) {
      if (!p_117366_.isInvisible()) {
         p_117360_.copyPropertiesTo(p_117361_);
         p_117361_.prepareMobModel(p_117366_, p_117367_, p_117368_, p_117372_);
         p_117361_.setupAnim(p_117366_, p_117367_, p_117368_, p_117369_, p_117370_, p_117371_);
         renderColoredCutoutModel(p_117361_, p_117362_, p_117363_, p_117364_, p_117365_, p_117366_, p_117373_, p_117374_, p_117375_);
      }

   }

   protected static <T extends LivingEntity> void renderColoredCutoutModel(EntityModel<T> p_117377_, ResourceLocation p_117378_, PoseStack p_117379_, MultiBufferSource p_117380_, int p_117381_, T p_117382_, float p_117383_, float p_117384_, float p_117385_) {
      VertexConsumer vertexconsumer = p_117380_.getBuffer(RenderType.entityCutoutNoCull(p_117378_));
      p_117377_.renderToBuffer(p_117379_, vertexconsumer, p_117381_, LivingEntityRenderer.getOverlayCoords(p_117382_, 0.0F), p_117383_, p_117384_, p_117385_, 1.0F);
   }

   public M getParentModel() {
      return this.renderer.getModel();
   }

   protected ResourceLocation getTextureLocation(T p_117348_) {
      return this.renderer.getTextureLocation(p_117348_);
   }

   public abstract void render(PoseStack p_117349_, MultiBufferSource p_117350_, int p_117351_, T p_117352_, float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_);
}