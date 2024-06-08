package net.minecraft.client.renderer.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RabbitRenderer extends MobRenderer<Rabbit, RabbitModel<Rabbit>> {
   private static final ResourceLocation RABBIT_BROWN_LOCATION = new ResourceLocation("textures/entity/rabbit/brown.png");
   private static final ResourceLocation RABBIT_WHITE_LOCATION = new ResourceLocation("textures/entity/rabbit/white.png");
   private static final ResourceLocation RABBIT_BLACK_LOCATION = new ResourceLocation("textures/entity/rabbit/black.png");
   private static final ResourceLocation RABBIT_GOLD_LOCATION = new ResourceLocation("textures/entity/rabbit/gold.png");
   private static final ResourceLocation RABBIT_SALT_LOCATION = new ResourceLocation("textures/entity/rabbit/salt.png");
   private static final ResourceLocation RABBIT_WHITE_SPLOTCHED_LOCATION = new ResourceLocation("textures/entity/rabbit/white_splotched.png");
   private static final ResourceLocation RABBIT_TOAST_LOCATION = new ResourceLocation("textures/entity/rabbit/toast.png");
   private static final ResourceLocation RABBIT_EVIL_LOCATION = new ResourceLocation("textures/entity/rabbit/caerbannog.png");

   public RabbitRenderer(EntityRendererProvider.Context p_174360_) {
      super(p_174360_, new RabbitModel<>(p_174360_.bakeLayer(ModelLayers.RABBIT)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Rabbit p_115803_) {
      String s = ChatFormatting.stripFormatting(p_115803_.getName().getString());
      if (s != null && "Toast".equals(s)) {
         return RABBIT_TOAST_LOCATION;
      } else {
         switch(p_115803_.getRabbitType()) {
         case 0:
         default:
            return RABBIT_BROWN_LOCATION;
         case 1:
            return RABBIT_WHITE_LOCATION;
         case 2:
            return RABBIT_BLACK_LOCATION;
         case 3:
            return RABBIT_WHITE_SPLOTCHED_LOCATION;
         case 4:
            return RABBIT_GOLD_LOCATION;
         case 5:
            return RABBIT_SALT_LOCATION;
         case 99:
            return RABBIT_EVIL_LOCATION;
         }
      }
   }
}