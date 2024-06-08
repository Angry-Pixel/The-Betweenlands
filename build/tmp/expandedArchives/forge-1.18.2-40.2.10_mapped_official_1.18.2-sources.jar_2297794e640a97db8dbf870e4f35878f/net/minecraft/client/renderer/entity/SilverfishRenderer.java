package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SilverfishRenderer extends MobRenderer<Silverfish, SilverfishModel<Silverfish>> {
   private static final ResourceLocation SILVERFISH_LOCATION = new ResourceLocation("textures/entity/silverfish.png");

   public SilverfishRenderer(EntityRendererProvider.Context p_174378_) {
      super(p_174378_, new SilverfishModel<>(p_174378_.bakeLayer(ModelLayers.SILVERFISH)), 0.3F);
   }

   protected float getFlipDegrees(Silverfish p_115927_) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(Silverfish p_115929_) {
      return SILVERFISH_LOCATION;
   }
}