package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RavagerRenderer extends MobRenderer<Ravager, RavagerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/ravager.png");

   public RavagerRenderer(EntityRendererProvider.Context p_174362_) {
      super(p_174362_, new RavagerModel(p_174362_.bakeLayer(ModelLayers.RAVAGER)), 1.1F);
   }

   public ResourceLocation getTextureLocation(Ravager p_115811_) {
      return TEXTURE_LOCATION;
   }
}