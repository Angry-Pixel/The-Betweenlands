package net.minecraft.client.resources.language;

import java.util.IllegalFormatException;
import net.minecraft.locale.Language;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class I18n {
   private static volatile Language language = Language.getInstance();

   private I18n() {
   }

   static void setLanguage(Language p_118942_) {
      language = p_118942_;
      net.minecraftforge.common.ForgeI18n.loadLanguageData(p_118942_.getLanguageData());
   }

   public static String get(String p_118939_, Object... p_118940_) {
      String s = language.getOrDefault(p_118939_);

      try {
         return String.format(s, p_118940_);
      } catch (IllegalFormatException illegalformatexception) {
         return "Format error: " + s;
      }
   }

   public static boolean exists(String p_118937_) {
      return language.has(p_118937_);
   }
}
