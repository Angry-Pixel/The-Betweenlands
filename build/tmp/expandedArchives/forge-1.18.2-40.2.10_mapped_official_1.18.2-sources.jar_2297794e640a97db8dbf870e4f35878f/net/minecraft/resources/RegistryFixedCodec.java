package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;

public final class RegistryFixedCodec<E> implements Codec<Holder<E>> {
   private final ResourceKey<? extends Registry<E>> registryKey;

   public static <E> RegistryFixedCodec<E> create(ResourceKey<? extends Registry<E>> p_206741_) {
      return new RegistryFixedCodec<>(p_206741_);
   }

   private RegistryFixedCodec(ResourceKey<? extends Registry<E>> p_206723_) {
      this.registryKey = p_206723_;
   }

   public <T> DataResult<T> encode(Holder<E> p_206729_, DynamicOps<T> p_206730_, T p_206731_) {
      if (p_206730_ instanceof RegistryOps) {
         RegistryOps<?> registryops = (RegistryOps)p_206730_;
         Optional<? extends Registry<E>> optional = registryops.registry(this.registryKey);
         if (optional.isPresent()) {
            if (!p_206729_.isValidInRegistry(optional.get())) {
               return DataResult.error("Element " + p_206729_ + " is not valid in current registry set");
            }

            return p_206729_.unwrap().map((p_206727_) -> {
               return ResourceLocation.CODEC.encode(p_206727_.location(), p_206730_, p_206731_);
            }, (p_206733_) -> {
               return DataResult.error("Elements from registry " + this.registryKey + " can't be serialized to a value");
            });
         }
      }

      return DataResult.error("Can't access registry " + this.registryKey);
   }

   public <T> DataResult<Pair<Holder<E>, T>> decode(DynamicOps<T> p_206743_, T p_206744_) {
      if (p_206743_ instanceof RegistryOps) {
         RegistryOps<?> registryops = (RegistryOps)p_206743_;
         Optional<? extends Registry<E>> optional = registryops.registry(this.registryKey);
         if (optional.isPresent()) {
            return ResourceLocation.CODEC.decode(p_206743_, p_206744_).map((p_206736_) -> {
               return p_206736_.mapFirst((p_206739_) -> {
                  return optional.get().getOrCreateHolder(ResourceKey.create(this.registryKey, p_206739_));
               });
            });
         }
      }

      return DataResult.error("Can't access registry " + this.registryKey);
   }

   public String toString() {
      return "RegistryFixedCodec[" + this.registryKey + "]";
   }
}