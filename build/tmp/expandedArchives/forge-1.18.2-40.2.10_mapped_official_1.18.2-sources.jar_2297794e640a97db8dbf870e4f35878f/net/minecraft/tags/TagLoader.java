package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class TagLoader<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new Gson();
   private static final String PATH_SUFFIX = ".json";
   private static final int PATH_SUFFIX_LENGTH = ".json".length();
   private final Function<ResourceLocation, Optional<T>> idToValue;
   private final String directory;

   public TagLoader(Function<ResourceLocation, Optional<T>> p_144493_, String p_144494_) {
      this.idToValue = p_144493_;
      this.directory = p_144494_;
   }

   public Map<ResourceLocation, Tag.Builder> load(ResourceManager p_144496_) {
      Map<ResourceLocation, Tag.Builder> map = Maps.newHashMap();

      for(ResourceLocation resourcelocation : p_144496_.listResources(this.directory, (p_144506_) -> {
         return p_144506_.endsWith(".json");
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(this.directory.length() + 1, s.length() - PATH_SUFFIX_LENGTH));

         try {
            for(Resource resource : p_144496_.getResources(resourcelocation)) {
               try {
                  InputStream inputstream = resource.getInputStream();

                  try {
                     Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));

                     try {
                        JsonObject jsonobject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                        if (jsonobject == null) {
                           LOGGER.error("Couldn't load tag list {} from {} in data pack {} as it is empty or null", resourcelocation1, resourcelocation, resource.getSourceName());
                        } else {
                           map.computeIfAbsent(resourcelocation1, (p_144555_) -> {
                              return Tag.Builder.tag();
                           }).addFromJson(jsonobject, resource.getSourceName());
                        }
                     } catch (Throwable throwable2) {
                        try {
                           reader.close();
                        } catch (Throwable throwable1) {
                           throwable2.addSuppressed(throwable1);
                        }

                        throw throwable2;
                     }

                     reader.close();
                  } catch (Throwable throwable3) {
                     if (inputstream != null) {
                        try {
                           inputstream.close();
                        } catch (Throwable throwable) {
                           throwable3.addSuppressed(throwable);
                        }
                     }

                     throw throwable3;
                  }

                  if (inputstream != null) {
                     inputstream.close();
                  }
               } catch (RuntimeException | IOException ioexception) {
                  LOGGER.error("Couldn't read tag list {} from {} in data pack {}", resourcelocation1, resourcelocation, resource.getSourceName(), ioexception);
               } finally {
                  IOUtils.closeQuietly((Closeable)resource);
               }
            }
         } catch (IOException ioexception1) {
            LOGGER.error("Couldn't read tag list {} from {}", resourcelocation1, resourcelocation, ioexception1);
         }
      }

      return map;
   }

   private static void visitDependenciesAndElement(Map<ResourceLocation, Tag.Builder> p_144524_, Multimap<ResourceLocation, ResourceLocation> p_144525_, Set<ResourceLocation> p_144526_, ResourceLocation p_144527_, BiConsumer<ResourceLocation, Tag.Builder> p_144528_) {
      if (p_144526_.add(p_144527_)) {
         p_144525_.get(p_144527_).forEach((p_144514_) -> {
            visitDependenciesAndElement(p_144524_, p_144525_, p_144526_, p_144514_, p_144528_);
         });
         Tag.Builder tag$builder = p_144524_.get(p_144527_);
         if (tag$builder != null) {
            p_144528_.accept(p_144527_, tag$builder);
         }

      }
   }

   private static boolean isCyclic(Multimap<ResourceLocation, ResourceLocation> p_144502_, ResourceLocation p_144503_, ResourceLocation p_144504_) {
      Collection<ResourceLocation> collection = p_144502_.get(p_144504_);
      return collection.contains(p_144503_) ? true : collection.stream().anyMatch((p_144567_) -> {
         return isCyclic(p_144502_, p_144503_, p_144567_);
      });
   }

   private static void addDependencyIfNotCyclic(Multimap<ResourceLocation, ResourceLocation> p_144551_, ResourceLocation p_144552_, ResourceLocation p_144553_) {
      if (!isCyclic(p_144551_, p_144552_, p_144553_)) {
         p_144551_.put(p_144552_, p_144553_);
      }

   }

   public Map<ResourceLocation, Tag<T>> build(Map<ResourceLocation, Tag.Builder> p_203899_) {
      Map<ResourceLocation, Tag<T>> map = Maps.newHashMap();
      Function<ResourceLocation, Tag<T>> function = map::get;
      Function<ResourceLocation, T> function1 = (p_144540_) -> {
         return this.idToValue.apply(p_144540_).orElse((T)null);
      };
      Multimap<ResourceLocation, ResourceLocation> multimap = HashMultimap.create();
      p_203899_.forEach((p_144548_, p_144549_) -> {
         p_144549_.visitRequiredDependencies((p_144563_) -> {
            addDependencyIfNotCyclic(multimap, p_144548_, p_144563_);
         });
      });
      p_203899_.forEach((p_144499_, p_144500_) -> {
         p_144500_.visitOptionalDependencies((p_144559_) -> {
            addDependencyIfNotCyclic(multimap, p_144499_, p_144559_);
         });
      });
      Set<ResourceLocation> set = Sets.newHashSet();
      p_203899_.keySet().forEach((p_144522_) -> {
         visitDependenciesAndElement(p_203899_, multimap, set, p_144522_, (p_144537_, p_144538_) -> {
            p_144538_.build(function, function1).ifLeft((p_144543_) -> {
               LOGGER.error("Couldn't load tag {} as it is missing following references: {}", p_144537_, p_144543_.stream().map(Objects::toString).collect(Collectors.joining(", \n\t")));
            }).ifRight((p_144532_) -> {
               map.put(p_144537_, p_144532_);
            });
         });
      });
      return map;
   }

   public Map<ResourceLocation, Tag<T>> loadAndBuild(ResourceManager p_203901_) {
      return this.build(this.load(p_203901_));
   }
}
