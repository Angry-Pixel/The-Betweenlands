package net.minecraft.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;

public class RegistryLoader {
   private final RegistryResourceAccess resources;
   private final Map<ResourceKey<? extends Registry<?>>, RegistryLoader.ReadCache<?>> readCache = new IdentityHashMap<>();

   RegistryLoader(RegistryResourceAccess p_206753_) {
      this.resources = p_206753_;
   }

   public <E> DataResult<? extends Registry<E>> overrideRegistryFromResources(WritableRegistry<E> p_206763_, ResourceKey<? extends Registry<E>> p_206764_, Codec<E> p_206765_, DynamicOps<JsonElement> p_206766_) {
      Collection<ResourceKey<E>> collection = this.resources.listResources(p_206764_);
      DataResult<WritableRegistry<E>> dataresult = DataResult.success(p_206763_, Lifecycle.stable());

      for(ResourceKey<E> resourcekey : collection) {
         dataresult = dataresult.flatMap((p_206780_) -> {
            return this.overrideElementFromResources(p_206780_, p_206764_, p_206765_, resourcekey, p_206766_).map((p_206761_) -> {
               return p_206780_;
            });
         });
      }

      return dataresult.setPartial(p_206763_);
   }

   <E> DataResult<Holder<E>> overrideElementFromResources(WritableRegistry<E> p_206768_, ResourceKey<? extends Registry<E>> p_206769_, Codec<E> p_206770_, ResourceKey<E> p_206771_, DynamicOps<JsonElement> p_206772_) {
      RegistryLoader.ReadCache<E> readcache = this.readCache(p_206769_);
      DataResult<Holder<E>> dataresult = readcache.values.get(p_206771_);
      if (dataresult != null) {
         return dataresult;
      } else {
         Holder<E> holder = p_206768_.getOrCreateHolder(p_206771_);
         readcache.values.put(p_206771_, DataResult.success(holder));
         Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> optional = this.resources.parseElement(p_206772_, p_206769_, p_206771_, p_206770_);
         DataResult<Holder<E>> dataresult1;
         if (optional.isEmpty()) {
            if (p_206768_.containsKey(p_206771_)) {
               dataresult1 = DataResult.success(holder, Lifecycle.stable());
            } else {
               dataresult1 = DataResult.error("Missing referenced custom/removed registry entry for registry " + p_206769_ + " named " + p_206771_.location());
            }
         } else {
            DataResult<RegistryResourceAccess.ParsedEntry<E>> dataresult2 = optional.get();
            Optional<RegistryResourceAccess.ParsedEntry<E>> optional1 = dataresult2.result();
            if (optional1.isPresent()) {
               RegistryResourceAccess.ParsedEntry<E> parsedentry = optional1.get();
               p_206768_.registerOrOverride(parsedentry.fixedId(), p_206771_, parsedentry.value(), dataresult2.lifecycle());
            }

            dataresult1 = dataresult2.map((p_206756_) -> {
               return holder;
            });
         }

         readcache.values.put(p_206771_, dataresult1);
         return dataresult1;
      }
   }

   private <E> RegistryLoader.ReadCache<E> readCache(ResourceKey<? extends Registry<E>> p_206774_) {
      return (RegistryLoader.ReadCache<E>)this.readCache.computeIfAbsent(p_206774_, (p_206782_) -> {
         return new RegistryLoader.ReadCache();
      });
   }

   public RegistryLoader.Bound bind(RegistryAccess.Writable p_206758_) {
      return new RegistryLoader.Bound(p_206758_, this);
   }

   public static record Bound(RegistryAccess.Writable access, RegistryLoader loader) {
      public <E> DataResult<? extends Registry<E>> overrideRegistryFromResources(ResourceKey<? extends Registry<E>> p_206790_, Codec<E> p_206791_, DynamicOps<JsonElement> p_206792_) {
         WritableRegistry<E> writableregistry = this.access.ownedWritableRegistryOrThrow(p_206790_);
         return this.loader.overrideRegistryFromResources(writableregistry, p_206790_, p_206791_, p_206792_);
      }

      public <E> DataResult<Holder<E>> overrideElementFromResources(ResourceKey<? extends Registry<E>> p_206794_, Codec<E> p_206795_, ResourceKey<E> p_206796_, DynamicOps<JsonElement> p_206797_) {
         WritableRegistry<E> writableregistry = this.access.ownedWritableRegistryOrThrow(p_206794_);
         return this.loader.overrideElementFromResources(writableregistry, p_206794_, p_206795_, p_206796_, p_206797_);
      }
   }

   static final class ReadCache<E> {
      final Map<ResourceKey<E>, DataResult<Holder<E>>> values = Maps.newIdentityHashMap();
   }
}