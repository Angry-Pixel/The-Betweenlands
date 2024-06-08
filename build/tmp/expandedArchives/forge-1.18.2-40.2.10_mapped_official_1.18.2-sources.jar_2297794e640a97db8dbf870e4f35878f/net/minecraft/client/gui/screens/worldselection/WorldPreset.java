package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class WorldPreset {
   public static final WorldPreset NORMAL = new WorldPreset("default") {
      protected ChunkGenerator generator(RegistryAccess p_194096_, long p_194097_) {
         return WorldGenSettings.makeDefaultOverworld(p_194096_, p_194097_);
      }
   };
   private static final WorldPreset FLAT = new WorldPreset("flat") {
      protected ChunkGenerator generator(RegistryAccess p_194099_, long p_194100_) {
         Registry<Biome> registry = p_194099_.registryOrThrow(Registry.BIOME_REGISTRY);
         Registry<StructureSet> registry1 = p_194099_.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
         return new FlatLevelSource(registry1, FlatLevelGeneratorSettings.getDefault(registry, registry1));
      }
   };
   public static final WorldPreset LARGE_BIOMES = new WorldPreset("large_biomes") {
      protected ChunkGenerator generator(RegistryAccess p_194102_, long p_194103_) {
         return WorldGenSettings.makeOverworld(p_194102_, p_194103_, NoiseGeneratorSettings.LARGE_BIOMES);
      }
   };
   public static final WorldPreset AMPLIFIED = new WorldPreset("amplified") {
      protected ChunkGenerator generator(RegistryAccess p_194105_, long p_194106_) {
         return WorldGenSettings.makeOverworld(p_194105_, p_194106_, NoiseGeneratorSettings.AMPLIFIED);
      }
   };
   private static final WorldPreset SINGLE_BIOME_SURFACE = new WorldPreset("single_biome_surface") {
      protected ChunkGenerator generator(RegistryAccess p_194108_, long p_194109_) {
         return WorldPreset.fixedBiomeGenerator(p_194108_, p_194109_, NoiseGeneratorSettings.OVERWORLD);
      }
   };
   private static final WorldPreset DEBUG = new WorldPreset("debug_all_block_states") {
      protected ChunkGenerator generator(RegistryAccess p_194111_, long p_194112_) {
         return new DebugLevelSource(p_194111_.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY), p_194111_.registryOrThrow(Registry.BIOME_REGISTRY));
      }
   };
   protected static final List<WorldPreset> PRESETS = Lists.newArrayList(NORMAL, FLAT, LARGE_BIOMES, AMPLIFIED, SINGLE_BIOME_SURFACE, DEBUG);
   protected static final Map<Optional<WorldPreset>, WorldPreset.PresetEditor> EDITORS = ImmutableMap.of(Optional.of(FLAT), (p_205498_, p_205499_) -> {
      ChunkGenerator chunkgenerator = p_205499_.overworld();
      RegistryAccess registryaccess = p_205498_.worldGenSettingsComponent.registryHolder();
      Registry<Biome> registry = registryaccess.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry<StructureSet> registry1 = registryaccess.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
      Registry<DimensionType> registry2 = registryaccess.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      return new CreateFlatWorldScreen(p_205498_, (p_210935_) -> {
         p_205498_.worldGenSettingsComponent.updateSettings(new WorldGenSettings(p_205499_.seed(), p_205499_.generateFeatures(), p_205499_.generateBonusChest(), WorldGenSettings.withOverworld(registry2, p_205499_.dimensions(), new FlatLevelSource(registry1, p_210935_))));
      }, chunkgenerator instanceof FlatLevelSource ? ((FlatLevelSource)chunkgenerator).settings() : FlatLevelGeneratorSettings.getDefault(registry, registry1));
   }, Optional.of(SINGLE_BIOME_SURFACE), (p_205475_, p_205476_) -> {
      return new CreateBuffetWorldScreen(p_205475_, p_205475_.worldGenSettingsComponent.registryHolder(), (p_205484_) -> {
         p_205475_.worldGenSettingsComponent.updateSettings(fromBuffetSettings(p_205475_.worldGenSettingsComponent.registryHolder(), p_205476_, p_205484_));
      }, parseBuffetSettings(p_205475_.worldGenSettingsComponent.registryHolder(), p_205476_));
   });
   private final Component description;

   static NoiseBasedChunkGenerator fixedBiomeGenerator(RegistryAccess p_194086_, long p_194087_, ResourceKey<NoiseGeneratorSettings> p_194088_) {
      Registry<Biome> registry = p_194086_.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry<StructureSet> registry1 = p_194086_.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
      Registry<NormalNoise.NoiseParameters> registry2 = p_194086_.registryOrThrow(Registry.NOISE_REGISTRY);
      Registry<NoiseGeneratorSettings> registry3 = p_194086_.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      return new NoiseBasedChunkGenerator(registry1, registry2, new FixedBiomeSource(registry.getOrCreateHolder(Biomes.PLAINS)), p_194087_, registry3.getOrCreateHolder(p_194088_));
   }

   WorldPreset(String p_101519_) {
      this.description = new TranslatableComponent("generator." + p_101519_);
   }
   public WorldPreset(Component displayName) {
      this.description = displayName;
   }

   private static WorldGenSettings fromBuffetSettings(RegistryAccess p_205494_, WorldGenSettings p_205495_, Holder<Biome> p_205496_) {
      BiomeSource biomesource = new FixedBiomeSource(p_205496_);
      Registry<DimensionType> registry = p_205494_.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry<StructureSet> registry1 = p_205494_.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
      Registry<NoiseGeneratorSettings> registry2 = p_205494_.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      Holder<NoiseGeneratorSettings> holder = registry2.getOrCreateHolder(NoiseGeneratorSettings.OVERWORLD);
      return new WorldGenSettings(p_205495_.seed(), p_205495_.generateFeatures(), p_205495_.generateBonusChest(), WorldGenSettings.withOverworld(registry, p_205495_.dimensions(), new NoiseBasedChunkGenerator(registry1, p_205494_.registryOrThrow(Registry.NOISE_REGISTRY), biomesource, p_205495_.seed(), holder)));
   }

   private static Holder<Biome> parseBuffetSettings(RegistryAccess p_205491_, WorldGenSettings p_205492_) {
      return p_205492_.overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse(p_205491_.registryOrThrow(Registry.BIOME_REGISTRY).getOrCreateHolder(Biomes.PLAINS));
   }

   public static Optional<WorldPreset> of(WorldGenSettings p_101525_) {
      ChunkGenerator chunkgenerator = p_101525_.overworld();
      if (chunkgenerator instanceof FlatLevelSource) {
         return Optional.of(FLAT);
      } else {
         return chunkgenerator instanceof DebugLevelSource ? Optional.of(DEBUG) : Optional.empty();
      }
   }

   public Component description() {
      return this.description;
   }

   public WorldGenSettings create(RegistryAccess p_205486_, long p_205487_, boolean p_205488_, boolean p_205489_) {
      return new WorldGenSettings(p_205487_, p_205488_, p_205489_, WorldGenSettings.withOverworld(p_205486_.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), DimensionType.defaultDimensions(p_205486_, p_205487_), this.generator(p_205486_, p_205487_)));
   }

   protected abstract ChunkGenerator generator(RegistryAccess p_194083_, long p_194084_);

   public static boolean isVisibleByDefault(WorldPreset p_170302_) {
      return p_170302_ != DEBUG;
   }

   @OnlyIn(Dist.CLIENT)
   public interface PresetEditor {
      Screen createEditScreen(CreateWorldScreen p_101643_, WorldGenSettings p_101644_);
   }

   // Forge start
   // For internal use only, automatically called for all ForgeWorldTypes. Register your ForgeWorldType in the forge registry!
   public static void registerGenerator(WorldPreset gen) { PRESETS.add(gen); }
}
