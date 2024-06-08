package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreeperRenderer extends MobRenderer<Creeper, CreeperModel<Creeper>> {
   private static final ResourceLocation CREEPER_LOCATION = new ResourceLocation("textures/entity/creeper/creeper.png");

   public CreeperRenderer(EntityRendererProvider.Context p_173958_) {
      super(p_173958_, new CreeperModel<>(p_173958_.bakeLayer(ModelLayers.CREEPER)), 0.5F);
      this.addLayer(new CreeperPowerLayer(this, p_173958_.getModelSet()));
   }

   protected void scale(Creeper p_114046_, PoseStack p_114047_, float p_114048_) {
      float f = p_114046_.getSwelling(p_114048_);
      float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
      f = Mth.clamp(f, 0.0F, 1.0F);
      f *= f;
      f *= f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      p_114047_.scale(f2, f3, f2);
   }

   protected float getWhiteOverlayProgress(Creeper p_114043_, float p_114044_) {
      float f = p_114043_.getSwelling(p_114044_);
      return (int)(f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
   }

   public ResourceLocation getTextureLocation(Creeper p_114041_) {
      return CREEPER_LOCATION;
   }
}