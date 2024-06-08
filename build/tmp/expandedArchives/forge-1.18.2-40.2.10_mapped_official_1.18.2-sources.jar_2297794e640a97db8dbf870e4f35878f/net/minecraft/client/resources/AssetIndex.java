package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class AssetIndex {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<String, File> rootFiles = Maps.newHashMap();
   private final Map<ResourceLocation, File> namespacedFiles = Maps.newHashMap();

   protected AssetIndex() {
   }

   public AssetIndex(File p_118530_, String p_118531_) {
      File file1 = new File(p_118530_, "objects");
      File file2 = new File(p_118530_, "indexes/" + p_118531_ + ".json");
      BufferedReader bufferedreader = null;

      try {
         bufferedreader = Files.newReader(file2, StandardCharsets.UTF_8);
         JsonObject jsonobject = GsonHelper.parse(bufferedreader);
         JsonObject jsonobject1 = GsonHelper.getAsJsonObject(jsonobject, "objects", (JsonObject)null);
         if (jsonobject1 != null) {
            for(Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
               JsonObject jsonobject2 = (JsonObject)entry.getValue();
               String s = entry.getKey();
               String[] astring = s.split("/", 2);
               String s1 = GsonHelper.getAsString(jsonobject2, "hash");
               File file3 = new File(file1, s1.substring(0, 2) + "/" + s1);
               if (astring.length == 1) {
                  this.rootFiles.put(astring[0], file3);
               } else {
                  this.namespacedFiles.put(new ResourceLocation(astring[0], astring[1]), file3);
               }
            }
         }
      } catch (JsonParseException jsonparseexception) {
         LOGGER.error("Unable to parse resource index file: {}", (Object)file2);
      } catch (FileNotFoundException filenotfoundexception) {
         LOGGER.error("Can't find the resource index file: {}", (Object)file2);
      } finally {
         IOUtils.closeQuietly((Reader)bufferedreader);
      }

   }

   @Nullable
   public File getFile(ResourceLocation p_118542_) {
      return this.namespacedFiles.get(p_118542_);
   }

   @Nullable
   public File getRootFile(String p_118532_) {
      return this.rootFiles.get(p_118532_);
   }

   public Collection<ResourceLocation> getFiles(String p_118533_, String p_118534_, int p_118535_, Predicate<String> p_118536_) {
      return this.namespacedFiles.keySet().stream().filter((p_118541_) -> {
         String s = p_118541_.getPath();
         return p_118541_.getNamespace().equals(p_118534_) && !s.endsWith(".mcmeta") && s.startsWith(p_118533_ + "/") && p_118536_.test(s);
      }).collect(Collectors.toList());
   }
}