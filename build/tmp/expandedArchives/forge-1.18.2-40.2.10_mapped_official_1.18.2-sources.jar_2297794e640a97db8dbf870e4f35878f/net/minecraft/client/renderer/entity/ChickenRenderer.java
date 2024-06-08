package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChickenRenderer extends MobRenderer<Chicken, ChickenModel<Chicken>> {
   private static final ResourceLocation CHICKEN_LOCATION = new ResourceLocation("textures/entity/chicken.png");

   public ChickenRenderer(EntityRendererProvider.Context p_173952_) {
      super(p_173952_, new ChickenModel<>(p_173952_.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Chicken p_113998_) {
      return CHICKEN_LOCATION;
   }

   protected float getBob(Chicken p_114000_, float p_114001_) {
      float f = Mth.lerp(p_114001_, p_114000_.oFlap, p_114000_.flap);
      float f1 = Mth.lerp(p_114001_, p_114000_.oFlapSpeed, p_114000_.flapSpeed);
      return (Mth.sin(f) + 1.0F) * f1;
   }
}