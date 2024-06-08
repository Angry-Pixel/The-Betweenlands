package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.StriderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Strider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StriderRenderer extends MobRenderer<Strider, StriderModel<Strider>> {
   private static final ResourceLocation STRIDER_LOCATION = new ResourceLocation("textures/entity/strider/strider.png");
   private static final ResourceLocation COLD_LOCATION = new ResourceLocation("textures/entity/strider/strider_cold.png");

   public StriderRenderer(EntityRendererProvider.Context p_174411_) {
      super(p_174411_, new StriderModel<>(p_174411_.bakeLayer(ModelLayers.STRIDER)), 0.5F);
      this.addLayer(new SaddleLayer<>(this, new StriderModel<>(p_174411_.bakeLayer(ModelLayers.STRIDER_SADDLE)), new ResourceLocation("textures/entity/strider/strider_saddle.png")));
   }

   public ResourceLocation getTextureLocation(Strider p_116064_) {
      return p_116064_.isSuffocating() ? COLD_LOCATION : STRIDER_LOCATION;
   }

   protected void scale(Strider p_116066_, PoseStack p_116067_, float p_116068_) {
      if (p_116066_.isBaby()) {
         p_116067_.scale(0.5F, 0.5F, 0.5F);
         this.shadowRadius = 0.25F;
      } else {
         this.shadowRadius = 0.5F;
      }

   }

   protected boolean isShaking(Strider p_116070_) {
      return super.isShaking(p_116070_) || p_116070_.isSuffocating();
   }
}