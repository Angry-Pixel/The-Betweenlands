package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DefaultPlayerSkin {
   private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
   private static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");
   private static final String STEVE_MODEL = "default";
   private static final String ALEX_MODEL = "slim";

   public static ResourceLocation getDefaultSkin() {
      return STEVE_SKIN_LOCATION;
   }

   public static ResourceLocation getDefaultSkin(UUID p_118628_) {
      return isAlexDefault(p_118628_) ? ALEX_SKIN_LOCATION : STEVE_SKIN_LOCATION;
   }

   public static String getSkinModelName(UUID p_118630_) {
      return isAlexDefault(p_118630_) ? "slim" : "default";
   }

   private static boolean isAlexDefault(UUID p_118632_) {
      return (p_118632_.hashCode() & 1) == 1;
   }
}