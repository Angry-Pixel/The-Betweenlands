package net.minecraft.data;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.NoiseData;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.StructureSets;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.slf4j.Logger;

public class BuiltinRegistries {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceLocation, Supplier<? extends Holder<?>>> LOADERS = Maps.newLinkedHashMap();
   private static final WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey(new ResourceLocation("root")), Lifecycle.experimental(), (Function<WritableRegistry<?>, Holder.Reference<WritableRegistry<?>>>)null);
   public static final Registry<? extends Registry<?>> REGISTRY = WRITABLE_REGISTRY;
   public static final Registry<ConfiguredWorldCarver<?>> CONFIGURED_CARVER = registerSimple(Registry.CONFIGURED_CARVER_REGISTRY, () -> {
      return Carvers.CAVE;
   });
   public static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = registerSimple(Registry.CONFIGURED_FEATURE_REGISTRY, FeatureUtils::bootstrap);
   public static final Registry<PlacedFeature> PLACED_FEATURE = registerSimple(Registry.PLACED_FEATURE_REGISTRY, PlacementUtils::bootstrap);
   public static final Registry<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = registerSimple(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StructureFeatures::bootstrap);
   public static final Registry<StructureSet> STRUCTURE_SETS = registerSimple(Registry.STRUCTURE_SET_REGISTRY, StructureSets::bootstrap);
   public static final Registry<StructureProcessorList> PROCESSOR_LIST = registerSimple(Registry.PROCESSOR_LIST_REGISTRY, () -> {
      return ProcessorLists.ZOMBIE_PLAINS;
   });
   public static final Registry<StructureTemplatePool> TEMPLATE_POOL = registerSimple(Registry.TEMPLATE_POOL_REGISTRY, Pools::bootstrap);
   @Deprecated public static final Registry<Biome> BIOME = forge(Registry.BIOME_REGISTRY, Biomes::bootstrap);
   public static final Registry<NormalNoise.NoiseParameters> NOISE = registerSimple(Registry.NOISE_REGISTRY, NoiseData::bootstrap);
   public static final Registry<DensityFunction> DENSITY_FUNCTION = registerSimple(Registry.DENSITY_FUNCTION_REGISTRY, NoiseRouterData::bootstrap);
   public static final Registry<NoiseGeneratorSettings> NOISE_GENERATOR_SETTINGS = registerSimple(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings::bootstrap);
   public static final RegistryAccess ACCESS;

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> p_123894_, Supplier<? extends Holder<? extends T>> p_123895_) {
      return registerSimple(p_123894_, Lifecycle.stable(), p_123895_);
   }

   private static <T extends net.minecraftforge.registries.IForgeRegistryEntry<T>> Registry<T> forge(ResourceKey<? extends Registry<T>> key, Supplier<? extends Holder<? extends T>> holderSupplier) {
      return internalRegister(key, net.minecraftforge.registries.GameData.getWrapper(key, Lifecycle.stable()), holderSupplier, Lifecycle.stable());
   }

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> p_123885_, Lifecycle p_123886_, Supplier<? extends Holder<? extends T>> p_123887_) {
      return internalRegister(p_123885_, new MappedRegistry<>(p_123885_, p_123886_, (Function<T, Holder.Reference<T>>)null), p_123887_, p_123886_);
   }

   private static <T, R extends WritableRegistry<T>> R internalRegister(ResourceKey<? extends Registry<T>> p_123889_, R p_123890_, Supplier<? extends Holder<? extends T>> p_123891_, Lifecycle p_123892_) {
      ResourceLocation resourcelocation = p_123889_.location();
      LOADERS.put(resourcelocation, p_123891_);
      WRITABLE_REGISTRY.register((ResourceKey)p_123889_, p_123890_, p_123892_);
      return p_123890_;
   }

   public static <V extends T, T> Holder<V> registerExact(Registry<T> p_206381_, String p_206382_, V p_206383_) {
      return register((Registry<V>)p_206381_, new ResourceLocation(p_206382_), p_206383_);
   }

   public static <T> Holder<T> register(Registry<T> p_206397_, String p_206398_, T p_206399_) {
      return register(p_206397_, new ResourceLocation(p_206398_), p_206399_);
   }

   public static <T> Holder<T> register(Registry<T> p_206389_, ResourceLocation p_206390_, T p_206391_) {
      return register(p_206389_, ResourceKey.create(p_206389_.key(), p_206390_), p_206391_);
   }

   public static <T> Holder<T> register(Registry<T> p_206385_, ResourceKey<T> p_206386_, T p_206387_) {
      return ((WritableRegistry)p_206385_).register(p_206386_, p_206387_, Lifecycle.stable());
   }

   public static void bootstrap() {
   }

   static {
      LOADERS.forEach((p_206393_, p_206394_) -> {
         if (!p_206394_.get().isBound()) {
            LOGGER.error("Unable to bootstrap registry '{}'", (Object)p_206393_);
         }

      });
      Registry.checkRegistry(WRITABLE_REGISTRY);
      ACCESS = RegistryAccess.fromRegistryOfRegistries(REGISTRY);
   }
}
