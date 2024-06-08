package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientLanguage extends Language {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<String, String> storage;
   private final boolean defaultRightToLeft;

   private ClientLanguage(Map<String, String> p_118914_, boolean p_118915_) {
      this.storage = p_118914_;
      this.defaultRightToLeft = p_118915_;
   }

   public static ClientLanguage loadFrom(ResourceManager p_118917_, List<LanguageInfo> p_118918_) {
      Map<String, String> map = Maps.newHashMap();
      boolean flag = false;

      for(LanguageInfo languageinfo : p_118918_) {
         flag |= languageinfo.isBidirectional();
         String s = String.format("lang/%s.json", languageinfo.getCode());

         for(String s1 : p_118917_.getNamespaces()) {
            try {
               ResourceLocation resourcelocation = new ResourceLocation(s1, s);
               appendFrom(p_118917_.getResources(resourcelocation), map);
            } catch (FileNotFoundException filenotfoundexception) {
            } catch (Exception exception) {
               LOGGER.warn("Skipped language file: {}:{} ({})", s1, s, exception.toString());
            }
         }
      }

      return new ClientLanguage(ImmutableMap.copyOf(map), flag);
   }

   private static void appendFrom(List<Resource> p_118922_, Map<String, String> p_118923_) {
      for(Resource resource : p_118922_) {
         try {
            InputStream inputstream = resource.getInputStream();

            try {
               Language.loadFromJson(inputstream, p_118923_::put);
            } catch (Throwable throwable1) {
               if (inputstream != null) {
                  try {
                     inputstream.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (inputstream != null) {
               inputstream.close();
            }
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to load translations from {}", resource, ioexception);
         }
      }

   }

   public String getOrDefault(String p_118920_) {
      return this.storage.getOrDefault(p_118920_, p_118920_);
   }

   public boolean has(String p_118928_) {
      return this.storage.containsKey(p_118928_);
   }

   public boolean isDefaultRightToLeft() {
      return this.defaultRightToLeft;
   }

   public FormattedCharSequence getVisualOrder(FormattedText p_118925_) {
      return FormattedBidiReorder.reorder(p_118925_, this.defaultRightToLeft);
   }

   @Override
   public Map<String, String> getLanguageData() {
      return storage;
   }
}
