package net.minecraft.client.resources.metadata.language;

import java.util.Collection;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageMetadataSection {
   public static final LanguageMetadataSectionSerializer SERIALIZER = new LanguageMetadataSectionSerializer();
   public static final boolean DEFAULT_BIDIRECTIONAL = false;
   private final Collection<LanguageInfo> languages;

   public LanguageMetadataSection(Collection<LanguageInfo> p_119100_) {
      this.languages = p_119100_;
   }

   public Collection<LanguageInfo> getLanguages() {
      return this.languages;
   }
}