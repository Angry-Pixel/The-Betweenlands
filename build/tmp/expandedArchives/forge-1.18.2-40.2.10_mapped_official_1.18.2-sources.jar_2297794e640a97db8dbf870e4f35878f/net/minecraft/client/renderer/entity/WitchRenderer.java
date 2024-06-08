package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.WitchItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Witch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchRenderer extends MobRenderer<Witch, WitchModel<Witch>> {
   private static final ResourceLocation WITCH_LOCATION = new ResourceLocation("textures/entity/witch.png");

   public WitchRenderer(EntityRendererProvider.Context p_174443_) {
      super(p_174443_, new WitchModel<>(p_174443_.bakeLayer(ModelLayers.WITCH)), 0.5F);
      this.addLayer(new WitchItemLayer<>(this));
   }

   public void render(Witch p_116412_, float p_116413_, float p_116414_, PoseStack p_116415_, MultiBufferSource p_116416_, int p_116417_) {
      this.model.setHoldingItem(!p_116412_.getMainHandItem().isEmpty());
      super.render(p_116412_, p_116413_, p_116414_, p_116415_, p_116416_, p_116417_);
   }

   public ResourceLocation getTextureLocation(Witch p_116410_) {
      return WITCH_LOCATION;
   }

   protected void scale(Witch p_116419_, PoseStack p_116420_, float p_116421_) {
      float f = 0.9375F;
      p_116420_.scale(0.9375F, 0.9375F, 0.9375F);
   }
}