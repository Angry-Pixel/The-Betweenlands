package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VindicatorRenderer extends IllagerRenderer<Vindicator> {
   private static final ResourceLocation VINDICATOR = new ResourceLocation("textures/entity/illager/vindicator.png");

   public VindicatorRenderer(EntityRendererProvider.Context p_174439_) {
      super(p_174439_, new IllagerModel<>(p_174439_.bakeLayer(ModelLayers.VINDICATOR)), 0.5F);
      this.addLayer(new ItemInHandLayer<Vindicator, IllagerModel<Vindicator>>(this) {
         public void render(PoseStack p_116352_, MultiBufferSource p_116353_, int p_116354_, Vindicator p_116355_, float p_116356_, float p_116357_, float p_116358_, float p_116359_, float p_116360_, float p_116361_) {
            if (p_116355_.isAggressive()) {
               super.render(p_116352_, p_116353_, p_116354_, p_116355_, p_116356_, p_116357_, p_116358_, p_116359_, p_116360_, p_116361_);
            }

         }
      });
   }

   public ResourceLocation getTextureLocation(Vindicator p_116324_) {
      return VINDICATOR;
   }
}