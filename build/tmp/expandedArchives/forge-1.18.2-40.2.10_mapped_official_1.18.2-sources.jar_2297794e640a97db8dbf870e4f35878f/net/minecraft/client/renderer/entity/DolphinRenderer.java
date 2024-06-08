package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.DolphinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.DolphinCarryingItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DolphinRenderer extends MobRenderer<Dolphin, DolphinModel<Dolphin>> {
   private static final ResourceLocation DOLPHIN_LOCATION = new ResourceLocation("textures/entity/dolphin.png");

   public DolphinRenderer(EntityRendererProvider.Context p_173960_) {
      super(p_173960_, new DolphinModel<>(p_173960_.bakeLayer(ModelLayers.DOLPHIN)), 0.7F);
      this.addLayer(new DolphinCarryingItemLayer(this));
   }

   public ResourceLocation getTextureLocation(Dolphin p_114059_) {
      return DOLPHIN_LOCATION;
   }
}