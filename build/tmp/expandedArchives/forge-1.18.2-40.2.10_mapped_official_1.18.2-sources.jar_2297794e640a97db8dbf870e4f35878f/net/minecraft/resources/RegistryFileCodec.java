package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

public final class RegistryFileCodec<E> implements Codec<Holder<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;
   private final Codec<E> elementCodec;
   private final boolean allowInline;

   public static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> p_135590_, Codec<E> p_135591_) {
      return create(p_135590_, p_135591_, true);
   }

   private static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> p_135593_, Codec<E> p_135594_, boolean p_135595_) {
      return new RegistryFileCodec<>(p_135593_, p_135594_, p_135595_);
   }

   private RegistryFileCodec(ResourceKey<? extends Registry<E>> p_135574_, Codec<E> p_135575_, boolean p_135576_) {
      this.registryKey = p_135574_;
      this.elementCodec = p_135575_;
      this.allowInline = p_135576_;
   }

   public <T> DataResult<T> encode(Holder<E> p_206716_, DynamicOps<T> p_206717_, T p_206718_) {
      if (p_206717_ instanceof RegistryOps) {
         RegistryOps<?> registryops = (RegistryOps)p_206717_;
         Optional<? extends Registry<E>> optional = registryops.registry(this.registryKey);
         if (optional.isPresent()) {
            if (!p_206716_.isValidInRegistry(optional.get())) {
               return DataResult.error("Element " + p_206716_ + " is not valid in current registry set");
            }

            return p_206716_.unwrap().map((p_206714_) -> {
               return ResourceLocation.CODEC.encode(p_206714_.location(), p_206717_, p_206718_);
            }, (p_206710_) -> {
               return this.elementCodec.encode(p_206710_, p_206717_, p_206718_);
            });
         }
      }

      return this.elementCodec.encode(p_206716_.value(), p_206717_, p_206718_);
   }

   public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> p_135608_, T p_135609_) {
      if (p_135608_ instanceof RegistryOps) {
         RegistryOps<?> registryops = (RegistryOps)p_135608_;
         Optional<? extends Registry<E>> optional = registryops.registry(this.registryKey);
         if (optional.isEmpty()) {
            return DataResult.error("Registry does not exist: " + this.registryKey);
         } else {
            Registry<E> registry = optional.get();
            DataResult<Pair<ResourceLocation, T>> dataresult = ResourceLocation.CODEC.decode(p_135608_, p_135609_);
            if (dataresult.result().isEmpty()) {
               return !this.allowInline ? DataResult.error("Inline definitions not allowed here") : this.elementCodec.decode(p_135608_, p_135609_).map((p_206720_) -> {
                  return p_206720_.mapFirst(Holder::direct);
               });
            } else {
               Pair<ResourceLocation, T> pair = dataresult.result().get();
               ResourceKey<E> resourcekey = ResourceKey.create(this.registryKey, pair.getFirst());
               Optional<RegistryLoader.Bound> optional1 = registryops.registryLoader();
               if (optional1.isPresent()) {
                  return optional1.get().overrideElementFromResources(this.registryKey, this.elementCodec, resourcekey, registryops.getAsJson()).map((p_206706_) -> {
                     return Pair.of(p_206706_, pair.getSecond());
                  });
               } else {
                  Holder<E> holder = registry.getOrCreateHolder(resourcekey);
                  return DataResult.success(Pair.of(holder, pair.getSecond()), Lifecycle.stable());
               }
            }
         }
      } else {
         return this.elementCodec.decode(p_135608_, p_135609_).map((p_206703_) -> {
            return p_206703_.mapFirst(Holder::direct);
         });
      }
   }

   public String toString() {
      return "RegistryFileCodec[" + this.registryKey + " " + this.elementCodec + "]";
   }
}