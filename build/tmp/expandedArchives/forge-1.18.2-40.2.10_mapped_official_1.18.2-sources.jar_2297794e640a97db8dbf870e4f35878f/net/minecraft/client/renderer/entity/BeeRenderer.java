package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeRenderer extends MobRenderer<Bee, BeeModel<Bee>> {
   private static final ResourceLocation ANGRY_BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee_angry.png");
   private static final ResourceLocation ANGRY_NECTAR_BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee_angry_nectar.png");
   private static final ResourceLocation BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee.png");
   private static final ResourceLocation NECTAR_BEE_TEXTURE = new ResourceLocation("textures/entity/bee/bee_nectar.png");

   public BeeRenderer(EntityRendererProvider.Context p_173931_) {
      super(p_173931_, new BeeModel<>(p_173931_.bakeLayer(ModelLayers.BEE)), 0.4F);
   }

   public ResourceLocation getTextureLocation(Bee p_113897_) {
      if (p_113897_.isAngry()) {
         return p_113897_.hasNectar() ? ANGRY_NECTAR_BEE_TEXTURE : ANGRY_BEE_TEXTURE;
      } else {
         return p_113897_.hasNectar() ? NECTAR_BEE_TEXTURE : BEE_TEXTURE;
      }
   }
}