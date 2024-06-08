package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SheepRenderer extends MobRenderer<Sheep, SheepModel<Sheep>> {
   private static final ResourceLocation SHEEP_LOCATION = new ResourceLocation("textures/entity/sheep/sheep.png");

   public SheepRenderer(EntityRendererProvider.Context p_174366_) {
      super(p_174366_, new SheepModel<>(p_174366_.bakeLayer(ModelLayers.SHEEP)), 0.7F);
      this.addLayer(new SheepFurLayer(this, p_174366_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(Sheep p_115840_) {
      return SHEEP_LOCATION;
   }
}