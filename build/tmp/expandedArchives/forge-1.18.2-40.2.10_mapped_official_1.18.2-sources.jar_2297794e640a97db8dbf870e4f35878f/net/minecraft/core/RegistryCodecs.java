package net.minecraft.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.RegistryLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryCodecs {
   private static <T> MapCodec<RegistryCodecs.RegistryEntry<T>> withNameAndId(ResourceKey<? extends Registry<T>> p_206304_, MapCodec<T> p_206305_) {
      return RecordCodecBuilder.mapCodec((p_206309_) -> {
         return p_206309_.group(ResourceLocation.CODEC.xmap(ResourceKey.elementKey(p_206304_), ResourceKey::location).fieldOf("name").forGetter(RegistryCodecs.RegistryEntry::key), Codec.INT.fieldOf("id").forGetter(RegistryCodecs.RegistryEntry::id), p_206305_.forGetter(RegistryCodecs.RegistryEntry::value)).apply(p_206309_, RegistryCodecs.RegistryEntry::new);
      });
   }

   public static <T> Codec<Registry<T>> networkCodec(ResourceKey<? extends Registry<T>> p_206292_, Lifecycle p_206293_, Codec<T> p_206294_) {
      return withNameAndId(p_206292_, p_206294_.fieldOf("element")).codec().listOf().xmap((p_206298_) -> {
         WritableRegistry<T> writableregistry = new MappedRegistry<>(p_206292_, p_206293_, (Function<T, Holder.Reference<T>>)null);

         for(RegistryCodecs.RegistryEntry<T> registryentry : p_206298_) {
            writableregistry.registerMapping(registryentry.id(), registryentry.key(), registryentry.value(), p_206293_);
         }

         return writableregistry;
      }, (p_206314_) -> {
         Builder<RegistryCodecs.RegistryEntry<T>> builder = ImmutableList.builder();

         for(T t : p_206314_) {
            builder.add(new RegistryCodecs.RegistryEntry<>(p_206314_.getResourceKey(t).get(), p_206314_.getId(t), t));
         }

         return builder.build();
      });
   }

   public static <E> Codec<Registry<E>> dataPackAwareCodec(ResourceKey<? extends Registry<E>> p_206319_, Lifecycle p_206320_, Codec<E> p_206321_) {
      Codec<Map<ResourceKey<E>, E>> codec = directCodec(p_206319_, p_206321_);
      Encoder<Registry<E>> encoder = codec.comap((p_206271_) -> {
         return ImmutableMap.copyOf(p_206271_.entrySet());
      });
      return Codec.of(encoder, dataPackAwareDecoder(p_206319_, p_206321_, codec, p_206320_), "DataPackRegistryCodec for " + p_206319_);
   }

   private static <E> Decoder<Registry<E>> dataPackAwareDecoder(final ResourceKey<? extends Registry<E>> p_206283_, final Codec<E> p_206284_, Decoder<Map<ResourceKey<E>, E>> p_206285_, Lifecycle p_206286_) {
      final Decoder<WritableRegistry<E>> decoder = p_206285_.map((p_206302_) -> {
         WritableRegistry<E> writableregistry = new MappedRegistry<>(p_206283_, p_206286_, (Function<E, Holder.Reference<E>>)null);
         p_206302_.forEach((p_206275_, p_206276_) -> {
            writableregistry.register(p_206275_, p_206276_, p_206286_);
         });
         return writableregistry;
      });
      return new Decoder<Registry<E>>() {
         public <T> DataResult<Pair<Registry<E>, T>> decode(DynamicOps<T> p_206352_, T p_206353_) {
            DataResult<Pair<WritableRegistry<E>, T>> dataresult = decoder.decode(p_206352_, p_206353_);
            if (p_206352_ instanceof RegistryOps) {
               RegistryOps<?> registryops = (RegistryOps)p_206352_;
               return registryops.registryLoader().map((p_206338_) -> {
                  return this.overrideFromResources(dataresult, registryops, p_206338_.loader());
               }).orElseGet(() -> {
                  return DataResult.error("Can't load registry with this ops");
               });
            } else {
               return dataresult.map((p_206331_) -> {
                  return p_206331_.mapFirst((p_206344_) -> {
                     return p_206344_;
                  });
               });
            }
         }

         private <T> DataResult<Pair<Registry<E>, T>> overrideFromResources(DataResult<Pair<WritableRegistry<E>, T>> p_206340_, RegistryOps<?> p_206341_, RegistryLoader p_206342_) {
            return p_206340_.flatMap((p_206350_) -> {
               return p_206342_.overrideRegistryFromResources(p_206350_.getFirst(), p_206283_, p_206284_, p_206341_.getAsJson()).map((p_206334_) -> {
                  return Pair.of(p_206334_, (T)p_206350_.getSecond());
               });
            });
         }
      };
   }

   private static <T> Codec<Map<ResourceKey<T>, T>> directCodec(ResourceKey<? extends Registry<T>> p_206316_, Codec<T> p_206317_) {
      // FORGE: Fix MC-197860
      return new net.minecraftforge.common.LenientUnboundedMapCodec<>(ResourceLocation.CODEC.xmap(ResourceKey.elementKey(p_206316_), ResourceKey::location), p_206317_);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> p_206280_, Codec<E> p_206281_) {
      return homogeneousList(p_206280_, p_206281_, false);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> p_206288_, Codec<E> p_206289_, boolean p_206290_) {
      return HolderSetCodec.create(p_206288_, RegistryFileCodec.create(p_206288_, p_206289_), p_206290_);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> p_206278_) {
      return homogeneousList(p_206278_, false);
   }

   public static <E> Codec<HolderSet<E>> homogeneousList(ResourceKey<? extends Registry<E>> p_206311_, boolean p_206312_) {
      return HolderSetCodec.create(p_206311_, RegistryFixedCodec.create(p_206311_), p_206312_);
   }

   static record RegistryEntry<T>(ResourceKey<T> key, int id, T value) {
   }
}
