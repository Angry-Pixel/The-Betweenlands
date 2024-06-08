package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.slf4j.Logger;

public class StructureCheck {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int NO_STRUCTURE = -1;
   private final ChunkScanAccess storageAccess;
   private final RegistryAccess registryAccess;
   private final Registry<Biome> biomes;
   private final Registry<ConfiguredStructureFeature<?, ?>> structureConfigs;
   private final StructureManager structureManager;
   private final ResourceKey<Level> dimension;
   private final ChunkGenerator chunkGenerator;
   private final LevelHeightAccessor heightAccessor;
   private final BiomeSource biomeSource;
   private final long seed;
   private final DataFixer fixerUpper;
   private final Long2ObjectMap<Object2IntMap<ConfiguredStructureFeature<?, ?>>> loadedChunks = new Long2ObjectOpenHashMap<>();
   private final Map<ConfiguredStructureFeature<?, ?>, Long2BooleanMap> featureChecks = new HashMap<>();

   public StructureCheck(ChunkScanAccess p_197251_, RegistryAccess p_197252_, StructureManager p_197253_, ResourceKey<Level> p_197254_, ChunkGenerator p_197255_, LevelHeightAccessor p_197256_, BiomeSource p_197257_, long p_197258_, DataFixer p_197259_) {
      this.storageAccess = p_197251_;
      this.registryAccess = p_197252_;
      this.structureManager = p_197253_;
      this.dimension = p_197254_;
      this.chunkGenerator = p_197255_;
      this.heightAccessor = p_197256_;
      this.biomeSource = p_197257_;
      this.seed = p_197258_;
      this.fixerUpper = p_197259_;
      this.biomes = p_197252_.ownedRegistryOrThrow(Registry.BIOME_REGISTRY);
      this.structureConfigs = p_197252_.ownedRegistryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
   }

   public StructureCheckResult checkStart(ChunkPos p_209965_, ConfiguredStructureFeature<?, ?> p_209966_, boolean p_209967_) {
      long i = p_209965_.toLong();
      Object2IntMap<ConfiguredStructureFeature<?, ?>> object2intmap = this.loadedChunks.get(i);
      if (object2intmap != null) {
         return this.checkStructureInfo(object2intmap, p_209966_, p_209967_);
      } else {
         StructureCheckResult structurecheckresult = this.tryLoadFromStorage(p_209965_, p_209966_, p_209967_, i);
         if (structurecheckresult != null) {
            return structurecheckresult;
         } else {
            boolean flag = this.featureChecks.computeIfAbsent(p_209966_, (p_209974_) -> {
               return new Long2BooleanOpenHashMap();
            }).computeIfAbsent(i, (p_209963_) -> {
               return this.canCreateStructure(p_209965_, p_209966_);
            });
            return !flag ? StructureCheckResult.START_NOT_PRESENT : StructureCheckResult.CHUNK_LOAD_NEEDED;
         }
      }
   }

   private <FC extends FeatureConfiguration, F extends StructureFeature<FC>> boolean canCreateStructure(ChunkPos p_209991_, ConfiguredStructureFeature<FC, F> p_209992_) {
      return p_209992_.feature.canGenerate(this.registryAccess, this.chunkGenerator, this.biomeSource, this.structureManager, this.seed, p_209991_, p_209992_.config, this.heightAccessor, p_209992_.biomes()::contains);
   }

   @Nullable
   private StructureCheckResult tryLoadFromStorage(ChunkPos p_209969_, ConfiguredStructureFeature<?, ?> p_209970_, boolean p_209971_, long p_209972_) {
      CollectFields collectfields = new CollectFields(new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector("Level", "Structures", CompoundTag.TYPE, "Starts"), new FieldSelector("structures", CompoundTag.TYPE, "starts"));

      try {
         this.storageAccess.scanChunk(p_209969_, collectfields).join();
      } catch (Exception exception1) {
         LOGGER.warn("Failed to read chunk {}", p_209969_, exception1);
         return StructureCheckResult.CHUNK_LOAD_NEEDED;
      }

      Tag tag = collectfields.getResult();
      if (!(tag instanceof CompoundTag)) {
         return null;
      } else {
         CompoundTag compoundtag = (CompoundTag)tag;
         int i = ChunkStorage.getVersion(compoundtag);
         if (i <= 1493) {
            return StructureCheckResult.CHUNK_LOAD_NEEDED;
         } else {
            ChunkStorage.injectDatafixingContext(compoundtag, this.dimension, this.chunkGenerator.getTypeNameForDataFixer());

            CompoundTag compoundtag1;
            try {
               compoundtag1 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, compoundtag, i);
            } catch (Exception exception) {
               LOGGER.warn("Failed to partially datafix chunk {}", p_209969_, exception);
               return StructureCheckResult.CHUNK_LOAD_NEEDED;
            }

            Object2IntMap<ConfiguredStructureFeature<?, ?>> object2intmap = this.loadStructures(compoundtag1);
            if (object2intmap == null) {
               return null;
            } else {
               this.storeFullResults(p_209972_, object2intmap);
               return this.checkStructureInfo(object2intmap, p_209970_, p_209971_);
            }
         }
      }
   }

   @Nullable
   private Object2IntMap<ConfiguredStructureFeature<?, ?>> loadStructures(CompoundTag p_197312_) {
      if (!p_197312_.contains("structures", 10)) {
         return null;
      } else {
         CompoundTag compoundtag = p_197312_.getCompound("structures");
         if (!compoundtag.contains("starts", 10)) {
            return null;
         } else {
            CompoundTag compoundtag1 = compoundtag.getCompound("starts");
            if (compoundtag1.isEmpty()) {
               return Object2IntMaps.emptyMap();
            } else {
               Object2IntMap<ConfiguredStructureFeature<?, ?>> object2intmap = new Object2IntOpenHashMap<>();
               Registry<ConfiguredStructureFeature<?, ?>> registry = this.registryAccess.registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);

               for(String s : compoundtag1.getAllKeys()) {
                  ResourceLocation resourcelocation = ResourceLocation.tryParse(s);
                  if (resourcelocation != null) {
                     ConfiguredStructureFeature<?, ?> configuredstructurefeature = registry.get(resourcelocation);
                     if (configuredstructurefeature != null) {
                        CompoundTag compoundtag2 = compoundtag1.getCompound(s);
                        if (!compoundtag2.isEmpty()) {
                           String s1 = compoundtag2.getString("id");
                           if (!"INVALID".equals(s1)) {
                              int i = compoundtag2.getInt("references");
                              object2intmap.put(configuredstructurefeature, i);
                           }
                        }
                     }
                  }
               }

               return object2intmap;
            }
         }
      }
   }

   private static Object2IntMap<ConfiguredStructureFeature<?, ?>> deduplicateEmptyMap(Object2IntMap<ConfiguredStructureFeature<?, ?>> p_197299_) {
      return p_197299_.isEmpty() ? Object2IntMaps.emptyMap() : p_197299_;
   }

   private StructureCheckResult checkStructureInfo(Object2IntMap<ConfiguredStructureFeature<?, ?>> p_209987_, ConfiguredStructureFeature<?, ?> p_209988_, boolean p_209989_) {
      int i = p_209987_.getOrDefault(p_209988_, -1);
      return i == -1 || p_209989_ && i != 0 ? StructureCheckResult.START_NOT_PRESENT : StructureCheckResult.START_PRESENT;
   }

   public void onStructureLoad(ChunkPos p_197283_, Map<ConfiguredStructureFeature<?, ?>, StructureStart> p_197284_) {
      long i = p_197283_.toLong();
      Object2IntMap<ConfiguredStructureFeature<?, ?>> object2intmap = new Object2IntOpenHashMap<>();
      p_197284_.forEach((p_209984_, p_209985_) -> {
         if (p_209985_.isValid()) {
            object2intmap.put(p_209984_, p_209985_.getReferences());
         }

      });
      this.storeFullResults(i, object2intmap);
   }

   private void storeFullResults(long p_197264_, Object2IntMap<ConfiguredStructureFeature<?, ?>> p_197265_) {
      this.loadedChunks.put(p_197264_, deduplicateEmptyMap(p_197265_));
      this.featureChecks.values().forEach((p_209956_) -> {
         p_209956_.remove(p_197264_);
      });
   }

   public void incrementReference(ChunkPos p_209958_, ConfiguredStructureFeature<?, ?> p_209959_) {
      this.loadedChunks.compute(p_209958_.toLong(), (p_209980_, p_209981_) -> {
         if (p_209981_ == null || p_209981_.isEmpty()) {
            p_209981_ = new Object2IntOpenHashMap<>();
         }

         p_209981_.computeInt(p_209959_, (p_209976_, p_209977_) -> {
            return p_209977_ == null ? 1 : p_209977_ + 1;
         });
         return p_209981_;
      });
   }
}