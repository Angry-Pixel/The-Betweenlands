package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Saddleable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SaddleLayer<T extends Entity & Saddleable, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private final ResourceLocation textureLocation;
   private final M model;

   public SaddleLayer(RenderLayerParent<T, M> p_117390_, M p_117391_, ResourceLocation p_117392_) {
      super(p_117390_);
      this.model = p_117391_;
      this.textureLocation = p_117392_;
   }

   public void render(PoseStack p_117394_, MultiBufferSource p_117395_, int p_117396_, T p_117397_, float p_117398_, float p_117399_, float p_117400_, float p_117401_, float p_117402_, float p_117403_) {
      if (p_117397_.isSaddled()) {
         this.getParentModel().copyPropertiesTo(this.model);
         this.model.prepareMobModel(p_117397_, p_117398_, p_117399_, p_117400_);
         this.model.setupAnim(p_117397_, p_117398_, p_117399_, p_117401_, p_117402_, p_117403_);
         VertexConsumer vertexconsumer = p_117395_.getBuffer(RenderType.entityCutoutNoCull(this.textureLocation));
         this.model.renderToBuffer(p_117394_, vertexconsumer, p_117396_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}