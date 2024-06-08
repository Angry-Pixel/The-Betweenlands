package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZoglinRenderer extends MobRenderer<Zoglin, HoglinModel<Zoglin>> {
   private static final ResourceLocation ZOGLIN_LOCATION = new ResourceLocation("textures/entity/hoglin/zoglin.png");

   public ZoglinRenderer(EntityRendererProvider.Context p_174454_) {
      super(p_174454_, new HoglinModel<>(p_174454_.bakeLayer(ModelLayers.ZOGLIN)), 0.7F);
   }

   public ResourceLocation getTextureLocation(Zoglin p_116544_) {
      return ZOGLIN_LOCATION;
   }
}