package net.minecraft.client.resources.language;

import com.mojang.bridge.game.Language;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageInfo implements Language, Comparable<LanguageInfo> {
   private final String code;
   private final String region;
   private final String name;
   private final boolean bidirectional;

   public LanguageInfo(String p_118948_, String p_118949_, String p_118950_, boolean p_118951_) {
      this.code = p_118948_;
      this.region = p_118949_;
      this.name = p_118950_;
      this.bidirectional = p_118951_;
      String[] splitLangCode = code.split("_", 2);
      if (splitLangCode.length == 1) { // Vanilla has some languages without underscores
         this.javaLocale = new java.util.Locale(code);
      } else {
         this.javaLocale = new java.util.Locale(splitLangCode[0], splitLangCode[1]);
      }
   }

   public String getCode() {
      return this.code;
   }

   public String getName() {
      return this.name;
   }

   public String getRegion() {
      return this.region;
   }

   public boolean isBidirectional() {
      return this.bidirectional;
   }

   public String toString() {
      return String.format("%s (%s)", this.name, this.region);
   }

   public boolean equals(Object p_118958_) {
      if (this == p_118958_) {
         return true;
      } else {
         return !(p_118958_ instanceof LanguageInfo) ? false : this.code.equals(((LanguageInfo)p_118958_).code);
      }
   }

   public int hashCode() {
      return this.code.hashCode();
   }

   public int compareTo(LanguageInfo p_118954_) {
      return this.code.compareTo(p_118954_.code);
   }

   // Forge: add access to Locale so modders can create correct string and number formatters
   private final java.util.Locale javaLocale;
   public java.util.Locale getJavaLocale() { return javaLocale; }
}
