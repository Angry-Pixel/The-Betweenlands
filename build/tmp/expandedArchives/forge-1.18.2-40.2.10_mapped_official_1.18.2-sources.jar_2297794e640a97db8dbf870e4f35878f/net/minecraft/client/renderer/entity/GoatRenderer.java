package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoatRenderer extends MobRenderer<Goat, GoatModel<Goat>> {
   private static final ResourceLocation GOAT_LOCATION = new ResourceLocation("textures/entity/goat/goat.png");

   public GoatRenderer(EntityRendererProvider.Context p_174153_) {
      super(p_174153_, new GoatModel<>(p_174153_.bakeLayer(ModelLayers.GOAT)), 0.7F);
   }

   public ResourceLocation getTextureLocation(Goat p_174157_) {
      return GOAT_LOCATION;
   }
}