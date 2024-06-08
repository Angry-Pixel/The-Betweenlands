package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PillagerRenderer extends IllagerRenderer<Pillager> {
   private static final ResourceLocation PILLAGER = new ResourceLocation("textures/entity/illager/pillager.png");

   public PillagerRenderer(EntityRendererProvider.Context p_174354_) {
      super(p_174354_, new IllagerModel<>(p_174354_.bakeLayer(ModelLayers.PILLAGER)), 0.5F);
      this.addLayer(new ItemInHandLayer<>(this));
   }

   public ResourceLocation getTextureLocation(Pillager p_115720_) {
      return PILLAGER;
   }
}