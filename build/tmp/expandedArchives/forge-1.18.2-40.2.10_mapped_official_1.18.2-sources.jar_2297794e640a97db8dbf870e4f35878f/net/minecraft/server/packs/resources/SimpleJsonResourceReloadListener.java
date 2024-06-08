package net.minecraft.server.packs.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public abstract class SimpleJsonResourceReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String PATH_SUFFIX = ".json";
   private static final int PATH_SUFFIX_LENGTH = ".json".length();
   private final Gson gson;
   private final String directory;

   public SimpleJsonResourceReloadListener(Gson p_10768_, String p_10769_) {
      this.gson = p_10768_;
      this.directory = p_10769_;
   }

   protected Map<ResourceLocation, JsonElement> prepare(ResourceManager p_10771_, ProfilerFiller p_10772_) {
      Map<ResourceLocation, JsonElement> map = Maps.newHashMap();
      int i = this.directory.length() + 1;

      for(ResourceLocation resourcelocation : p_10771_.listResources(this.directory, (p_10774_) -> {
         return p_10774_.endsWith(".json");
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(i, s.length() - PATH_SUFFIX_LENGTH));

         try {
            Resource resource = p_10771_.getResource(resourcelocation);

            try {
               InputStream inputstream = resource.getInputStream();

               try {
                  Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));

                  try {
                     JsonElement jsonelement = GsonHelper.fromJson(this.gson, reader, JsonElement.class);
                     if (jsonelement != null) {
                        JsonElement jsonelement1 = map.put(resourcelocation1, jsonelement);
                        if (jsonelement1 != null) {
                           throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
                        }
                     } else {
                        LOGGER.error("Couldn't load data file {} from {} as it's null or empty", resourcelocation1, resourcelocation);
                     }
                  } catch (Throwable throwable3) {
                     try {
                        reader.close();
                     } catch (Throwable throwable2) {
                        throwable3.addSuppressed(throwable2);
                     }

                     throw throwable3;
                  }

                  reader.close();
               } catch (Throwable throwable4) {
                  if (inputstream != null) {
                     try {
                        inputstream.close();
                     } catch (Throwable throwable1) {
                        throwable4.addSuppressed(throwable1);
                     }
                  }

                  throw throwable4;
               }

               if (inputstream != null) {
                  inputstream.close();
               }
            } catch (Throwable throwable5) {
               if (resource != null) {
                  try {
                     resource.close();
                  } catch (Throwable throwable) {
                     throwable5.addSuppressed(throwable);
                  }
               }

               throw throwable5;
            }

            if (resource != null) {
               resource.close();
            }
         } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't parse data file {} from {}", resourcelocation1, resourcelocation, jsonparseexception);
         }
      }

      return map;
   }

   protected ResourceLocation getPreparedPath(ResourceLocation rl) {
      return new ResourceLocation(rl.getNamespace(), this.directory + "/" + rl.getPath() + ".json");
   }
}
