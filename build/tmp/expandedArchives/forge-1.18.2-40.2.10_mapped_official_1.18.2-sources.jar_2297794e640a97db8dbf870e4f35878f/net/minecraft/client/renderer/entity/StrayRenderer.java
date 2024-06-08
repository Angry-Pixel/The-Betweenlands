package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.StrayClothingLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StrayRenderer extends SkeletonRenderer {
   private static final ResourceLocation STRAY_SKELETON_LOCATION = new ResourceLocation("textures/entity/skeleton/stray.png");

   public StrayRenderer(EntityRendererProvider.Context p_174409_) {
      super(p_174409_, ModelLayers.STRAY, ModelLayers.STRAY_INNER_ARMOR, ModelLayers.STRAY_OUTER_ARMOR);
      this.addLayer(new StrayClothingLayer<>(this, p_174409_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(AbstractSkeleton p_116049_) {
      return STRAY_SKELETON_LOCATION;
   }
}