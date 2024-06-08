package net.minecraft.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.DataResult.PartialResult;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public interface RegistryResourceAccess {
   <E> Collection<ResourceKey<E>> listResources(ResourceKey<? extends Registry<E>> p_195887_);

   <E> Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> parseElement(DynamicOps<JsonElement> p_195883_, ResourceKey<? extends Registry<E>> p_195884_, ResourceKey<E> p_195885_, Decoder<E> p_195886_);

   static RegistryResourceAccess forResourceManager(final ResourceManager p_195882_) {
      return new RegistryResourceAccess() {
         private static final String JSON = ".json";

         public <E> Collection<ResourceKey<E>> listResources(ResourceKey<? extends Registry<E>> p_195905_) {
            String s = registryDirPath(p_195905_);
            Set<ResourceKey<E>> set = new HashSet<>();
            p_195882_.listResources(s, (p_195898_) -> {
               return p_195898_.endsWith(".json");
            }).forEach((p_195903_) -> {
               String s1 = p_195903_.getPath();
               String s2 = s1.substring(s.length() + 1, s1.length() - ".json".length());
               set.add(ResourceKey.create(p_195905_, new ResourceLocation(p_195903_.getNamespace(), s2)));
            });
            return set;
         }

         public <E> Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> parseElement(DynamicOps<JsonElement> p_195893_, ResourceKey<? extends Registry<E>> p_195894_, ResourceKey<E> p_195895_, Decoder<E> p_195896_) {
            ResourceLocation resourcelocation = elementPath(p_195894_, p_195895_);
            if (!p_195882_.hasResource(resourcelocation)) {
               return Optional.empty();
            } else {
               try {
                  Resource resource = p_195882_.getResource(resourcelocation);

                  Optional optional;
                  try {
                     Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);

                     try {
                        JsonElement jsonelement = JsonParser.parseReader(reader);
                        if (jsonelement != null) jsonelement.getAsJsonObject().addProperty("forge:registry_name", p_195895_.location().toString());
                        optional = Optional.of(p_195896_.parse(p_195893_, jsonelement).map(RegistryResourceAccess.ParsedEntry::createWithoutId));
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
                     if (resource != null) {
                        try {
                           resource.close();
                        } catch (Throwable throwable) {
                           throwable3.addSuppressed(throwable);
                        }
                     }

                     throw throwable3;
                  }

                  if (resource != null) {
                     resource.close();
                  }

                  return optional;
               } catch (JsonIOException | JsonSyntaxException | IOException ioexception) {
                  return Optional.of(DataResult.error("Failed to parse " + resourcelocation + " file: " + ioexception.getMessage()));
               }
            }
         }

         private static String registryDirPath(ResourceKey<? extends Registry<?>> p_195910_) {
            if (net.minecraftforge.registries.RegistryManager.FROZEN.getRegistry(p_195910_.location()) != null && !(p_195910_.location().getNamespace().equals("minecraft")))
               return p_195910_.location().getNamespace() + "/" + p_195910_.location().getPath(); // FORGE: add non-vanilla registry namespace to loader directory, same format as tag directory (see net.minecraft.tags.TagManager#getTagDir(ResourceKey)) 
            return p_195910_.location().getPath();
         }

         private static <E> ResourceLocation elementPath(ResourceKey<? extends Registry<E>> p_195907_, ResourceKey<E> p_195908_) {
            return new ResourceLocation(p_195908_.location().getNamespace(), registryDirPath(p_195907_) + "/" + p_195908_.location().getPath() + ".json");
         }

         public String toString() {
            return "ResourceAccess[" + p_195882_ + "]";
         }
      };
   }

   public static final class InMemoryStorage implements RegistryResourceAccess {
      private static final Logger LOGGER = LogUtils.getLogger();
      private final Map<ResourceKey<?>, RegistryResourceAccess.InMemoryStorage.Entry> entries = Maps.newIdentityHashMap();

      public <E> void add(RegistryAccess p_206837_, ResourceKey<E> p_206838_, Encoder<E> p_206839_, int p_206840_, E p_206841_, Lifecycle p_206842_) {
         DataResult<JsonElement> dataresult = p_206839_.encodeStart(RegistryOps.create(JsonOps.INSTANCE, p_206837_), p_206841_);
         Optional<PartialResult<JsonElement>> optional = dataresult.error();
         if (optional.isPresent()) {
            LOGGER.error("Error adding element: {}", (Object)optional.get().message());
         } else {
            this.entries.put(p_206838_, new RegistryResourceAccess.InMemoryStorage.Entry(dataresult.result().get(), p_206840_, p_206842_));
         }

      }

      public <E> Collection<ResourceKey<E>> listResources(ResourceKey<? extends Registry<E>> p_195932_) {
         return this.entries.keySet().stream().flatMap((p_195935_) -> {
            return p_195935_.cast(p_195932_).stream();
         }).collect(Collectors.toList());
      }

      public <E> Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> parseElement(DynamicOps<JsonElement> p_195917_, ResourceKey<? extends Registry<E>> p_195918_, ResourceKey<E> p_195919_, Decoder<E> p_195920_) {
         RegistryResourceAccess.InMemoryStorage.Entry registryresourceaccess$inmemorystorage$entry = this.entries.get(p_195919_);
         if (registryresourceaccess$inmemorystorage$entry != null && registryresourceaccess$inmemorystorage$entry.data != null && registryresourceaccess$inmemorystorage$entry.data.isJsonObject())
            registryresourceaccess$inmemorystorage$entry.data.getAsJsonObject().addProperty("forge:registry_name", p_195919_.location().toString());
         return registryresourceaccess$inmemorystorage$entry == null ? Optional.of(DataResult.error("Unknown element: " + p_195919_)) : Optional.of(p_195920_.parse(p_195917_, registryresourceaccess$inmemorystorage$entry.data).setLifecycle(registryresourceaccess$inmemorystorage$entry.lifecycle).map((p_195930_) -> {
            return RegistryResourceAccess.ParsedEntry.createWithId(p_195930_, registryresourceaccess$inmemorystorage$entry.id);
         }));
      }

      static record Entry(JsonElement data, int id, Lifecycle lifecycle) {
      }
   }

   public static record ParsedEntry<E>(E value, OptionalInt fixedId) {
      public static <E> RegistryResourceAccess.ParsedEntry<E> createWithoutId(E p_195957_) {
         return new RegistryResourceAccess.ParsedEntry<>(p_195957_, OptionalInt.empty());
      }

      public static <E> RegistryResourceAccess.ParsedEntry<E> createWithId(E p_195959_, int p_195960_) {
         return new RegistryResourceAccess.ParsedEntry<>(p_195959_, OptionalInt.of(p_195960_));
      }
   }
}
