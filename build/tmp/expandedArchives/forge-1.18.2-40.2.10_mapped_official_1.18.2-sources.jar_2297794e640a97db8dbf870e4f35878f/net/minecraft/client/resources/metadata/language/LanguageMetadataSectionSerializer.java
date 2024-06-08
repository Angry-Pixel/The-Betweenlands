package net.minecraft.client.resources.metadata.language;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LanguageMetadataSectionSerializer implements MetadataSectionSerializer<LanguageMetadataSection> {
   private static final int MAX_LANGUAGE_LENGTH = 16;

   public LanguageMetadataSection fromJson(JsonObject p_119107_) {
      Set<LanguageInfo> set = Sets.newHashSet();

      for(Entry<String, JsonElement> entry : p_119107_.entrySet()) {
         String s = entry.getKey();
         if (s.length() > 16) {
            throw new JsonParseException("Invalid language->'" + s + "': language code must not be more than 16 characters long");
         }

         JsonObject jsonobject = GsonHelper.convertToJsonObject(entry.getValue(), "language");
         String s1 = GsonHelper.getAsString(jsonobject, "region");
         String s2 = GsonHelper.getAsString(jsonobject, "name");
         boolean flag = GsonHelper.getAsBoolean(jsonobject, "bidirectional", false);
         if (s1.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + s + "'->region: empty value");
         }

         if (s2.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + s + "'->name: empty value");
         }

         if (!set.add(new LanguageInfo(s, s1, s2, flag))) {
            throw new JsonParseException("Duplicate language->'" + s + "' defined");
         }
      }

      return new LanguageMetadataSection(set);
   }

   public String getMetadataSectionName() {
      return "language";
   }
}