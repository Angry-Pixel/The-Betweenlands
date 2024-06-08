package net.minecraft.client.model.geom;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ModelLayerLocation {
   private final ResourceLocation model;
   private final String layer;

   public ModelLayerLocation(ResourceLocation p_171121_, String p_171122_) {
      this.model = p_171121_;
      this.layer = p_171122_;
   }

   public ResourceLocation getModel() {
      return this.model;
   }

   public String getLayer() {
      return this.layer;
   }

   public boolean equals(Object p_171126_) {
      if (this == p_171126_) {
         return true;
      } else if (!(p_171126_ instanceof ModelLayerLocation)) {
         return false;
      } else {
         ModelLayerLocation modellayerlocation = (ModelLayerLocation)p_171126_;
         return this.model.equals(modellayerlocation.model) && this.layer.equals(modellayerlocation.layer);
      }
   }

   public int hashCode() {
      int i = this.model.hashCode();
      return 31 * i + this.layer.hashCode();
   }

   public String toString() {
      return this.model + "#" + this.layer;
   }
}