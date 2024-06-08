package net.minecraft.core;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.RegistryResourceAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.slf4j.Logger;

public interface RegistryAccess {
   Logger LOGGER = LogUtils.getLogger();
   Map<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> REGISTRIES = Util.make(() -> {
      Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> builder = ImmutableMap.builder();
      put(builder, Registry.DIMENSION_TYPE_REGISTRY, DimensionType.DIRECT_CODEC, DimensionType.DIRECT_CODEC);
      put(builder, Registry.BIOME_REGISTRY, Biome.DIRECT_CODEC, Biome.NETWORK_CODEC);
      put(builder, Registry.CONFIGURED_CARVER_REGISTRY, ConfiguredWorldCarver.DIRECT_CODEC);
      put(builder, Registry.CONFIGURED_FEATURE_REGISTRY, ConfiguredFeature.DIRECT_CODEC);
      put(builder, Registry.PLACED_FEATURE_REGISTRY, PlacedFeature.DIRECT_CODEC);
      put(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, ConfiguredStructureFeature.DIRECT_CODEC);
      put(builder, Registry.STRUCTURE_SET_REGISTRY, StructureSet.DIRECT_CODEC);
      put(builder, Registry.PROCESSOR_LIST_REGISTRY, StructureProcessorType.DIRECT_CODEC);
      put(builder, Registry.TEMPLATE_POOL_REGISTRY, StructureTemplatePool.DIRECT_CODEC);
      put(builder, Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings.DIRECT_CODEC);
      put(builder, Registry.NOISE_REGISTRY, NormalNoise.NoiseParameters.DIRECT_CODEC);
      put(builder, Registry.DENSITY_FUNCTION_REGISTRY, DensityFunction.DIRECT_CODEC);
      return net.minecraftforge.registries.DataPackRegistriesHooks.grabBuiltinRegistries(builder); // FORGE: Keep the map so custom registries can be added later
   });
   Codec<RegistryAccess> NETWORK_CODEC = makeNetworkCodec();
   Supplier<RegistryAccess.Frozen> BUILTIN = Suppliers.memoize(() -> {
      return builtinCopy().freeze();
   });

   <E> Optional<Registry<E>> ownedRegistry(ResourceKey<? extends Registry<? extends E>> p_175507_);

   default <E> Registry<E> ownedRegistryOrThrow(ResourceKey<? extends Registry<? extends E>> p_206192_) {
      return this.ownedRegistry(p_206192_).orElseThrow(() -> {
         return new IllegalStateException("Missing registry: " + p_206192_);
      });
   }

   default <E> Optional<? extends Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> p_123085_) {
      Optional<? extends Registry<E>> optional = this.ownedRegistry(p_123085_);
      return optional.isPresent() ? optional : (Optional<? extends Registry<E>>)Registry.REGISTRY.getOptional(p_123085_.location());
   }

   default <E> Registry<E> registryOrThrow(ResourceKey<? extends Registry<? extends E>> p_175516_) {
      return this.registry(p_175516_).orElseThrow(() -> {
         return new IllegalStateException("Missing registry: " + p_175516_);
      });
   }

   private static <E> void put(Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> p_123054_, ResourceKey<? extends Registry<E>> p_123055_, Codec<E> p_123056_) {
      p_123054_.put(p_123055_, new RegistryAccess.RegistryData<>(p_123055_, p_123056_, (Codec<E>)null));
   }

   private static <E> void put(Builder<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> p_123058_, ResourceKey<? extends Registry<E>> p_123059_, Codec<E> p_123060_, Codec<E> p_123061_) {
      p_123058_.put(p_123059_, new RegistryAccess.RegistryData<>(p_123059_, p_123060_, p_123061_));
   }

   static Iterable<RegistryAccess.RegistryData<?>> knownRegistries() {
      return REGISTRIES.values();
   }

   Stream<RegistryAccess.RegistryEntry<?>> ownedRegistries();

   private static Stream<RegistryAccess.RegistryEntry<Object>> globalRegistries() {
      return Registry.REGISTRY.holders().map(RegistryAccess.RegistryEntry::fromHolder);
   }

   default Stream<RegistryAccess.RegistryEntry<?>> registries() {
      return Stream.concat(this.ownedRegistries(), globalRegistries());
   }

   default Stream<RegistryAccess.RegistryEntry<?>> networkSafeRegistries() {
      return Stream.concat(this.ownedNetworkableRegistries(), globalRegistries());
   }

   private static <E> Codec<RegistryAccess> makeNetworkCodec() {
      Codec<ResourceKey<? extends Registry<E>>> codec = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
      Codec<Registry<E>> codec1 = codec.partialDispatch("type", (p_206188_) -> {
         return DataResult.success(p_206188_.key());
      }, (p_206214_) -> {
         return getNetworkCodec(p_206214_).map((p_206183_) -> {
            return RegistryCodecs.networkCodec(p_206214_, Lifecycle.experimental(), p_206183_);
         });
      });
      UnboundedMapCodec<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> unboundedmapcodec = Codec.unboundedMap(codec, codec1);
      return captureMap(unboundedmapcodec);
   }

   private static <K extends ResourceKey<? extends Registry<?>>, V extends Registry<?>> Codec<RegistryAccess> captureMap(UnboundedMapCodec<K, V> p_206164_) {
      return p_206164_.xmap(RegistryAccess.ImmutableRegistryAccess::new, (p_206180_) -> {
         return p_206180_.ownedNetworkableRegistries().collect(ImmutableMap.toImmutableMap((p_206195_) -> {
            return (K)p_206195_.key();
         }, (p_206190_) -> {
            return (V)p_206190_.value();
         }));
      });
   }

   private Stream<RegistryAccess.RegistryEntry<?>> ownedNetworkableRegistries() {
      return this.ownedRegistries().filter((p_206170_) -> {
         return REGISTRIES.get(p_206170_.key).sendToClient();
      });
   }

   private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends Registry<E>> p_206204_) {
      return Optional.ofNullable(REGISTRIES.get(p_206204_)).map((p_206168_) -> {
         return (Codec<E>)p_206168_.networkCodec();
      }).map(DataResult::success).orElseGet(() -> {
         return DataResult.error("Unknown or not serializable registry: " + p_206204_);
      });
   }

   private static Map<ResourceKey<? extends Registry<?>>, ? extends WritableRegistry<?>> createFreshRegistries() {
      return REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), RegistryAccess::createRegistry));
   }

   private static RegistryAccess.Writable blankWriteable() {
      return new RegistryAccess.WritableRegistryAccess(createFreshRegistries());
   }

   static RegistryAccess.Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> p_206166_) {
      return new RegistryAccess.Frozen() {
         public <T> Optional<Registry<T>> ownedRegistry(ResourceKey<? extends Registry<? extends T>> p_206220_) {
            Registry<Registry<T>> registry = (Registry<Registry<T>>)p_206166_;
            return registry.getOptional((ResourceKey<Registry<T>>)p_206220_);
         }

         public Stream<RegistryAccess.RegistryEntry<?>> ownedRegistries() {
            return p_206166_.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
         }
      };
   }

   static RegistryAccess.Writable builtinCopy() {
      RegistryAccess.Writable registryaccess$writable = blankWriteable();
      RegistryResourceAccess.InMemoryStorage registryresourceaccess$inmemorystorage = new RegistryResourceAccess.InMemoryStorage();

      for(Entry<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> entry : REGISTRIES.entrySet()) {
         if (!entry.getKey().equals(Registry.DIMENSION_TYPE_REGISTRY)) {
            addBuiltinElements(registryresourceaccess$inmemorystorage, entry.getValue());
         }
      }

      RegistryOps.createAndLoad(JsonOps.INSTANCE, registryaccess$writable, registryresourceaccess$inmemorystorage);
      return DimensionType.registerBuiltin(registryaccess$writable);
   }

   private static <E> void addBuiltinElements(RegistryResourceAccess.InMemoryStorage p_211082_, RegistryAccess.RegistryData<E> p_211083_) {
      ResourceKey<? extends Registry<E>> resourcekey = p_211083_.key();
      Registry<E> registry = BuiltinRegistries.ACCESS.registryOrThrow(resourcekey);

      for(Entry<ResourceKey<E>, E> entry : registry.entrySet()) {
         ResourceKey<E> resourcekey1 = entry.getKey();
         E e = entry.getValue();
         p_211082_.add(BuiltinRegistries.ACCESS, resourcekey1, p_211083_.codec(), registry.getId(e), e, registry.lifecycle(e));
      }

   }

   static void load(RegistryAccess.Writable p_206172_, DynamicOps<JsonElement> p_206173_, RegistryLoader p_206174_) {
      RegistryLoader.Bound registryloader$bound = p_206174_.bind(p_206172_);

      for(RegistryAccess.RegistryData<?> registrydata : REGISTRIES.values()) {
         readRegistry(p_206173_, registryloader$bound, registrydata);
      }

   }

   private static <E> void readRegistry(DynamicOps<JsonElement> p_206160_, RegistryLoader.Bound p_206161_, RegistryAccess.RegistryData<E> p_206162_) {
      DataResult<? extends Registry<E>> dataresult = p_206161_.overrideRegistryFromResources(p_206162_.key(), p_206162_.codec(), p_206160_);
      dataresult.error().ifPresent((p_206153_) -> {
         throw new JsonParseException("Error loading registry data: " + p_206153_.message());
      });
   }

   static RegistryAccess readFromDisk(Dynamic<?> p_206155_) {
      return new RegistryAccess.ImmutableRegistryAccess(REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), (p_206158_) -> {
         return retrieveRegistry(p_206158_, p_206155_);
      })));
   }

   static <E> Registry<E> retrieveRegistry(ResourceKey<? extends Registry<? extends E>> p_206185_, Dynamic<?> p_206186_) {
      return RegistryOps.retrieveRegistry(p_206185_).codec().parse(p_206186_).resultOrPartial(Util.prefix(p_206185_ + " registry: ", LOGGER::error)).orElseThrow(() -> {
         return new IllegalStateException("Failed to get " + p_206185_ + " registry");
      });
   }

   static <E> WritableRegistry<?> createRegistry(ResourceKey<? extends Registry<?>> p_206201_) {
      return new MappedRegistry<>((ResourceKey<? extends Registry<E>>)p_206201_, Lifecycle.stable(), (Function<E, Holder.Reference<E>>)null);
   }

   default RegistryAccess.Frozen freeze() {
      return new RegistryAccess.ImmutableRegistryAccess(this.ownedRegistries().map(RegistryAccess.RegistryEntry::freeze));
   }

   default Lifecycle allElementsLifecycle() {
      return this.ownedRegistries().map((p_211815_) -> {
         return p_211815_.value.elementsLifecycle();
      }).reduce(Lifecycle.stable(), Lifecycle::add);
   }

   public interface Frozen extends RegistryAccess {
      default RegistryAccess.Frozen freeze() {
         return this;
      }
   }

   public static final class ImmutableRegistryAccess implements RegistryAccess.Frozen {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

      public ImmutableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> p_206225_) {
         this.registries = Map.copyOf(p_206225_);
      }

      ImmutableRegistryAccess(Stream<RegistryAccess.RegistryEntry<?>> p_206227_) {
         this.registries = p_206227_.collect(ImmutableMap.toImmutableMap(RegistryAccess.RegistryEntry::key, RegistryAccess.RegistryEntry::value));
      }

      public <E> Optional<Registry<E>> ownedRegistry(ResourceKey<? extends Registry<? extends E>> p_206229_) {
         return Optional.ofNullable(this.registries.get(p_206229_)).map((p_206232_) -> {
            return (Registry<E>)p_206232_;
         });
      }

      public Stream<RegistryAccess.RegistryEntry<?>> ownedRegistries() {
         return this.registries.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
      }
   }

   public static record RegistryData<E>(ResourceKey<? extends Registry<E>> key, Codec<E> codec, @Nullable Codec<E> networkCodec) {
      public boolean sendToClient() {
         return this.networkCodec != null;
      }
   }

   public static record RegistryEntry<T>(ResourceKey<? extends Registry<T>> key, Registry<T> value) {
      private static <T, R extends Registry<? extends T>> RegistryAccess.RegistryEntry<T> fromMapEntry(Entry<? extends ResourceKey<? extends Registry<?>>, R> p_206242_) {
         return fromUntyped(p_206242_.getKey(), p_206242_.getValue());
      }

      private static <T> RegistryAccess.RegistryEntry<T> fromHolder(Holder.Reference<? extends Registry<? extends T>> p_206240_) {
         return fromUntyped(p_206240_.key(), p_206240_.value());
      }

      private static <T> RegistryAccess.RegistryEntry<T> fromUntyped(ResourceKey<? extends Registry<?>> p_206244_, Registry<?> p_206245_) {
         return new RegistryAccess.RegistryEntry<>((ResourceKey<? extends Registry<T>>)p_206244_, (Registry<T>)p_206245_);
      }

      private RegistryAccess.RegistryEntry<T> freeze() {
         return new RegistryAccess.RegistryEntry<>(this.key, this.value.freeze());
      }
   }

   public interface Writable extends RegistryAccess {
      <E> Optional<WritableRegistry<E>> ownedWritableRegistry(ResourceKey<? extends Registry<? extends E>> p_206252_);

      default <E> WritableRegistry<E> ownedWritableRegistryOrThrow(ResourceKey<? extends Registry<? extends E>> p_206254_) {
         return this.<E>ownedWritableRegistry(p_206254_).orElseThrow(() -> {
            return new IllegalStateException("Missing registry: " + p_206254_);
         });
      }
   }

   public static final class WritableRegistryAccess implements RegistryAccess.Writable {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends WritableRegistry<?>> registries;

      WritableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends WritableRegistry<?>> p_206259_) {
         this.registries = p_206259_;
      }

      public <E> Optional<Registry<E>> ownedRegistry(ResourceKey<? extends Registry<? extends E>> p_206263_) {
         return Optional.ofNullable(this.registries.get(p_206263_)).map((p_206266_) -> {
            return (Registry<E>)p_206266_;
         });
      }

      public <E> Optional<WritableRegistry<E>> ownedWritableRegistry(ResourceKey<? extends Registry<? extends E>> p_206268_) {
         return Optional.ofNullable(this.registries.get(p_206268_)).map((p_206261_) -> {
            return (WritableRegistry<E>)p_206261_;
         });
      }

      public Stream<RegistryAccess.RegistryEntry<?>> ownedRegistries() {
         return this.registries.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
      }
   }
}
