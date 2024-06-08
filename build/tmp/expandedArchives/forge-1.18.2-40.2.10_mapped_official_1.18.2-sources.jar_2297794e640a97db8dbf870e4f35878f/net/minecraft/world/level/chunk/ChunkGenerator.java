package net.minecraft.world.level.chunk;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.Products.P1;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

public abstract class ChunkGenerator implements BiomeManager.NoiseBiomeSource {
   private static final Logger LOGGER;
   public static final Codec<ChunkGenerator> CODEC;
   protected final Registry<StructureSet> structureSets;
   protected final BiomeSource biomeSource;
   protected final BiomeSource runtimeBiomeSource;
   protected final Optional<HolderSet<StructureSet>> structureOverrides;
   private final Map<ConfiguredStructureFeature<?, ?>, List<StructurePlacement>> placementsForFeature = new Object2ObjectOpenHashMap<>();
   private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> ringPositions = new Object2ObjectArrayMap<>();
   private boolean hasGeneratedPositions;
   /** @deprecated */
   @Deprecated
   private final long ringPlacementSeed;

   protected static final <T extends ChunkGenerator> P1<Mu<T>, Registry<StructureSet>> commonCodec(Instance<T> p_208006_) {
      return p_208006_.group(RegistryOps.retrieveRegistry(Registry.STRUCTURE_SET_REGISTRY).forGetter((p_208008_) -> {
         return p_208008_.structureSets;
      }));
   }

   public ChunkGenerator(Registry<StructureSet> p_207960_, Optional<HolderSet<StructureSet>> p_207961_, BiomeSource p_207962_) {
      this(p_207960_, p_207961_, p_207962_, p_207962_, 0L);
   }

   public ChunkGenerator(Registry<StructureSet> p_207964_, Optional<HolderSet<StructureSet>> p_207965_, BiomeSource p_207966_, BiomeSource p_207967_, long p_207968_) {
      this.structureSets = p_207964_;
      this.biomeSource = p_207966_;
      this.runtimeBiomeSource = p_207967_;
      this.structureOverrides = p_207965_;
      this.ringPlacementSeed = p_207968_;
   }

   public Stream<Holder<StructureSet>> possibleStructureSets() {
      return this.structureOverrides.isPresent() ? this.structureOverrides.get().stream() : this.structureSets.holders().map(Holder::hackyErase);
   }

   private void generatePositions() {
      Set<Holder<Biome>> set = this.runtimeBiomeSource.possibleBiomes();
      this.possibleStructureSets().forEach((p_208094_) -> {
         StructureSet structureset = p_208094_.value();

         for(StructureSet.StructureSelectionEntry structureset$structureselectionentry : structureset.structures()) {
            this.placementsForFeature.computeIfAbsent(structureset$structureselectionentry.structure().value(), (p_208087_) -> {
               return new ArrayList();
            }).add(structureset.placement());
         }

         StructurePlacement structureplacement = structureset.placement();
         if (structureplacement instanceof ConcentricRingsStructurePlacement) {
            ConcentricRingsStructurePlacement concentricringsstructureplacement = (ConcentricRingsStructurePlacement)structureplacement;
            if (structureset.structures().stream().anyMatch((p_208071_) -> {
               return p_208071_.generatesInMatchingBiome(set::contains);
            })) {
               this.ringPositions.put(concentricringsstructureplacement, this.generateRingPositions(p_208094_, concentricringsstructureplacement));
            }
         }

      });
   }

   private CompletableFuture<List<ChunkPos>> generateRingPositions(Holder<StructureSet> p_211668_, ConcentricRingsStructurePlacement p_211669_) {
      return p_211669_.count() == 0 ? CompletableFuture.completedFuture(List.of()) : CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("placement calculation", () -> {
         Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
         List<ChunkPos> list = new ArrayList<>();
         Set<Holder<Biome>> set = p_211668_.value().structures().stream().flatMap((p_208015_) -> {
            return p_208015_.structure().value().biomes().stream();
         }).collect(Collectors.toSet());
         int i = p_211669_.distance();
         int j = p_211669_.count();
         int k = p_211669_.spread();
         Random random = new Random();
         random.setSeed(this.ringPlacementSeed);
         double d0 = random.nextDouble() * Math.PI * 2.0D;
         int l = 0;
         int i1 = 0;

         for(int j1 = 0; j1 < j; ++j1) {
            double d1 = (double)(4 * i + i * i1 * 6) + (random.nextDouble() - 0.5D) * (double)i * 2.5D;
            int k1 = (int)Math.round(Math.cos(d0) * d1);
            int l1 = (int)Math.round(Math.sin(d0) * d1);
            Pair<BlockPos, Holder<Biome>> pair = this.biomeSource.findBiomeHorizontal(SectionPos.sectionToBlockCoord(k1, 8), 0, SectionPos.sectionToBlockCoord(l1, 8), 112, set::contains, random, this.climateSampler());
            if (pair != null) {
               BlockPos blockpos = pair.getFirst();
               k1 = SectionPos.blockToSectionCoord(blockpos.getX());
               l1 = SectionPos.blockToSectionCoord(blockpos.getZ());
            }

            list.add(new ChunkPos(k1, l1));
            d0 += (Math.PI * 2D) / (double)k;
            ++l;
            if (l == k) {
               ++i1;
               l = 0;
               k += 2 * k / (i1 + 1);
               k = Math.min(k, j - j1);
               d0 += random.nextDouble() * Math.PI * 2.0D;
            }
         }

         double d2 = (double)stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0D;
         LOGGER.debug("Calculation for {} took {}s", p_211668_, d2);
         return list;
      }), Util.backgroundExecutor());
   }

   protected abstract Codec<? extends ChunkGenerator> codec();

   public Optional<ResourceKey<Codec<? extends ChunkGenerator>>> getTypeNameForDataFixer() {
      return Registry.CHUNK_GENERATOR.getResourceKey(this.codec());
   }

   public abstract ChunkGenerator withSeed(long p_62156_);

   public CompletableFuture<ChunkAccess> createBiomes(Registry<Biome> p_196743_, Executor p_196744_, Blender p_196745_, StructureFeatureManager p_196746_, ChunkAccess p_196747_) {
      return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
         p_196747_.fillBiomesFromNoise(this.runtimeBiomeSource::getNoiseBiome, this.climateSampler());
         return p_196747_;
      }), Util.backgroundExecutor());
   }

   public abstract Climate.Sampler climateSampler();

   public Holder<Biome> getNoiseBiome(int p_204416_, int p_204417_, int p_204418_) {
      return this.getBiomeSource().getNoiseBiome(p_204416_, p_204417_, p_204418_, this.climateSampler());
   }

   public abstract void applyCarvers(WorldGenRegion p_187691_, long p_187692_, BiomeManager p_187693_, StructureFeatureManager p_187694_, ChunkAccess p_187695_, GenerationStep.Carving p_187696_);

   @Nullable
   public Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> findNearestMapFeature(ServerLevel p_207971_, HolderSet<ConfiguredStructureFeature<?, ?>> p_207972_, BlockPos p_207973_, int p_207974_, boolean p_207975_) {
      Set<Holder<Biome>> set = p_207972_.stream().flatMap((p_211699_) -> {
         return p_211699_.value().biomes().stream();
      }).collect(Collectors.toSet());
      if (set.isEmpty()) {
         return null;
      } else {
         Set<Holder<Biome>> set1 = this.runtimeBiomeSource.possibleBiomes();
         if (Collections.disjoint(set1, set)) {
            return null;
         } else {
            Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair = null;
            double d0 = Double.MAX_VALUE;
            Map<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> map = new Object2ObjectArrayMap<>();

            for(Holder<ConfiguredStructureFeature<?, ?>> holder : p_207972_) {
               if (!set1.stream().noneMatch(holder.value().biomes()::contains)) {
                  for(StructurePlacement structureplacement : this.getPlacementsForFeature(holder)) {
                     map.computeIfAbsent(structureplacement, (p_211663_) -> {
                        return new ObjectArraySet();
                     }).add(holder);
                  }
               }
            }

            List<Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>>> list = new ArrayList<>(map.size());

            for(Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> entry : map.entrySet()) {
               StructurePlacement structureplacement1 = entry.getKey();
               if (structureplacement1 instanceof ConcentricRingsStructurePlacement) {
                  ConcentricRingsStructurePlacement concentricringsstructureplacement = (ConcentricRingsStructurePlacement)structureplacement1;
                  BlockPos blockpos = this.getNearestGeneratedStructure(p_207973_, concentricringsstructureplacement);
                  double d1 = p_207973_.distSqr(blockpos);
                  if (d1 < d0) {
                     d0 = d1;
                     pair = Pair.of(blockpos, entry.getValue().iterator().next());
                  }
               } else if (structureplacement1 instanceof RandomSpreadStructurePlacement) {
                  list.add(entry);
               }
            }

            if (!list.isEmpty()) {
               int i = SectionPos.blockToSectionCoord(p_207973_.getX());
               int j = SectionPos.blockToSectionCoord(p_207973_.getZ());

               for(int k = 0; k <= p_207974_; ++k) {
                  boolean flag = false;

                  for(Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> entry1 : list) {
                     RandomSpreadStructurePlacement randomspreadstructureplacement = (RandomSpreadStructurePlacement)entry1.getKey();
                     Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair1 = getNearestGeneratedStructure(entry1.getValue(), p_207971_, p_207971_.structureFeatureManager(), i, j, k, p_207975_, p_207971_.getSeed(), randomspreadstructureplacement);
                     if (pair1 != null) {
                        flag = true;
                        double d2 = p_207973_.distSqr(pair1.getFirst());
                        if (d2 < d0) {
                           d0 = d2;
                           pair = pair1;
                        }
                     }
                  }

                  if (flag) {
                     return pair;
                  }
               }
            }

            return pair;
         }
      }
   }

   @Nullable
   private BlockPos getNearestGeneratedStructure(BlockPos p_204383_, ConcentricRingsStructurePlacement p_204384_) {
      List<ChunkPos> list = this.getRingPositionsFor(p_204384_);
      if (list == null) {
         throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
      } else {
         BlockPos blockpos = null;
         double d0 = Double.MAX_VALUE;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(ChunkPos chunkpos : list) {
            blockpos$mutableblockpos.set(SectionPos.sectionToBlockCoord(chunkpos.x, 8), 32, SectionPos.sectionToBlockCoord(chunkpos.z, 8));
            double d1 = blockpos$mutableblockpos.distSqr(p_204383_);
            if (blockpos == null) {
               blockpos = new BlockPos(blockpos$mutableblockpos);
               d0 = d1;
            } else if (d1 < d0) {
               blockpos = new BlockPos(blockpos$mutableblockpos);
               d0 = d1;
            }
         }

         return blockpos;
      }
   }

   @Nullable
   private static Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> getNearestGeneratedStructure(Set<Holder<ConfiguredStructureFeature<?, ?>>> p_208060_, LevelReader p_208061_, StructureFeatureManager p_208062_, int p_208063_, int p_208064_, int p_208065_, boolean p_208066_, long p_208067_, RandomSpreadStructurePlacement p_208068_) {
      int i = p_208068_.spacing();

      for(int j = -p_208065_; j <= p_208065_; ++j) {
         boolean flag = j == -p_208065_ || j == p_208065_;

         for(int k = -p_208065_; k <= p_208065_; ++k) {
            boolean flag1 = k == -p_208065_ || k == p_208065_;
            if (flag || flag1) {
               int l = p_208063_ + i * j;
               int i1 = p_208064_ + i * k;
               ChunkPos chunkpos = p_208068_.getPotentialFeatureChunk(p_208067_, l, i1);

               for(Holder<ConfiguredStructureFeature<?, ?>> holder : p_208060_) {
                  StructureCheckResult structurecheckresult = p_208062_.checkStructurePresence(chunkpos, holder.value(), p_208066_);
                  if (structurecheckresult != StructureCheckResult.START_NOT_PRESENT) {
                     if (!p_208066_ && structurecheckresult == StructureCheckResult.START_PRESENT) {
                        return Pair.of(StructureFeature.getLocatePos(p_208068_, chunkpos), holder);
                     }

                     ChunkAccess chunkaccess = p_208061_.getChunk(chunkpos.x, chunkpos.z, ChunkStatus.STRUCTURE_STARTS);
                     StructureStart structurestart = p_208062_.getStartForFeature(SectionPos.bottomOf(chunkaccess), holder.value(), chunkaccess);
                     if (structurestart != null && structurestart.isValid()) {
                        if (p_208066_ && structurestart.canBeReferenced()) {
                           p_208062_.addReference(structurestart);
                           return Pair.of(StructureFeature.getLocatePos(p_208068_, structurestart.getChunkPos()), holder);
                        }

                        if (!p_208066_) {
                           return Pair.of(StructureFeature.getLocatePos(p_208068_, structurestart.getChunkPos()), holder);
                        }
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   public void applyBiomeDecoration(WorldGenLevel p_187712_, ChunkAccess p_187713_, StructureFeatureManager p_187714_) {
      ChunkPos chunkpos = p_187713_.getPos();
      if (!SharedConstants.debugVoidTerrain(chunkpos)) {
         SectionPos sectionpos = SectionPos.of(chunkpos, p_187712_.getMinSection());
         BlockPos blockpos = sectionpos.origin();
         Registry<ConfiguredStructureFeature<?, ?>> registry = p_187712_.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
         Map<Integer, List<ConfiguredStructureFeature<?, ?>>> map = registry.stream().collect(Collectors.groupingBy((p_211653_) -> {
            return p_211653_.feature.step().ordinal();
         }));
         List<BiomeSource.StepFeatureData> list = this.biomeSource.featuresPerStep();
         WorldgenRandom worldgenrandom = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.seedUniquifier()));
         long i = worldgenrandom.setDecorationSeed(p_187712_.getSeed(), blockpos.getX(), blockpos.getZ());
         Set<Biome> set = new ObjectArraySet<>();
         if (this instanceof FlatLevelSource) {
            this.biomeSource.possibleBiomes().stream().map(Holder::value).forEach(set::add);
         } else {
            ChunkPos.rangeClosed(sectionpos.chunk(), 1).forEach((p_211651_) -> {
               ChunkAccess chunkaccess = p_187712_.getChunk(p_211651_.x, p_211651_.z);

               for(LevelChunkSection levelchunksection : chunkaccess.getSections()) {
                  levelchunksection.getBiomes().getAll((p_211688_) -> {
                     set.add(p_211688_.value());
                  });
               }

            });
            set.retainAll(this.biomeSource.possibleBiomes().stream().map(Holder::value).collect(Collectors.toSet()));
         }

         int j = list.size();

         try {
            Registry<PlacedFeature> registry1 = p_187712_.registryAccess().registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
            int i1 = Math.max(GenerationStep.Decoration.values().length, j);

            for(int k = 0; k < i1; ++k) {
               int l = 0;
               if (p_187714_.shouldGenerateFeatures()) {
                  for(ConfiguredStructureFeature<?, ?> configuredstructurefeature : map.getOrDefault(k, Collections.emptyList())) {
                     worldgenrandom.setFeatureSeed(i, l, k);
                     Supplier<String> supplier = () -> {
                        return registry.getResourceKey(configuredstructurefeature).map(Object::toString).orElseGet(configuredstructurefeature::toString);
                     };

                     try {
                        p_187712_.setCurrentlyGenerating(supplier);
                        p_187714_.startsForFeature(sectionpos, configuredstructurefeature).forEach((p_211647_) -> {
                           p_211647_.placeInChunk(p_187712_, p_187714_, this, worldgenrandom, getWritableArea(p_187713_), chunkpos);
                        });
                     } catch (Exception exception) {
                        CrashReport crashreport1 = CrashReport.forThrowable(exception, "Feature placement");
                        crashreport1.addCategory("Feature").setDetail("Description", supplier::get);
                        throw new ReportedException(crashreport1);
                     }

                     ++l;
                  }
               }

               if (k < j) {
                  IntSet intset = new IntArraySet();

                  for(Biome biome : set) {
                     List<HolderSet<PlacedFeature>> list1 = biome.getGenerationSettings().features();
                     if (k < list1.size()) {
                        HolderSet<PlacedFeature> holderset = list1.get(k);
                        BiomeSource.StepFeatureData biomesource$stepfeaturedata1 = list.get(k);
                        holderset.stream().map(Holder::value).forEach((p_211682_) -> {
                           intset.add(biomesource$stepfeaturedata1.indexMapping().applyAsInt(p_211682_));
                        });
                     }
                  }

                  int j1 = intset.size();
                  int[] aint = intset.toIntArray();
                  Arrays.sort(aint);
                  BiomeSource.StepFeatureData biomesource$stepfeaturedata = list.get(k);

                  for(int k1 = 0; k1 < j1; ++k1) {
                     int l1 = aint[k1];
                     PlacedFeature placedfeature = biomesource$stepfeaturedata.features().get(l1);
                     Supplier<String> supplier1 = () -> {
                        return registry1.getResourceKey(placedfeature).map(Object::toString).orElseGet(placedfeature::toString);
                     };
                     worldgenrandom.setFeatureSeed(i, l1, k);

                     try {
                        p_187712_.setCurrentlyGenerating(supplier1);
                        placedfeature.placeWithBiomeCheck(p_187712_, this, worldgenrandom, blockpos);
                     } catch (Exception exception1) {
                        CrashReport crashreport2 = CrashReport.forThrowable(exception1, "Feature placement");
                        crashreport2.addCategory("Feature").setDetail("Description", supplier1::get);
                        throw new ReportedException(crashreport2);
                     }
                  }
               }
            }

            p_187712_.setCurrentlyGenerating((Supplier<String>)null);
         } catch (Exception exception2) {
            CrashReport crashreport = CrashReport.forThrowable(exception2, "Biome decoration");
            crashreport.addCategory("Generation").setDetail("CenterX", chunkpos.x).setDetail("CenterZ", chunkpos.z).setDetail("Seed", i);
            throw new ReportedException(crashreport);
         }
      }
   }

   public boolean hasFeatureChunkInRange(ResourceKey<StructureSet> p_212266_, long p_212267_, int p_212268_, int p_212269_, int p_212270_) {
      StructureSet structureset = this.structureSets.get(p_212266_);
      if (structureset == null) {
         return false;
      } else {
         StructurePlacement structureplacement = structureset.placement();

         for(int i = p_212268_ - p_212270_; i <= p_212268_ + p_212270_; ++i) {
            for(int j = p_212269_ - p_212270_; j <= p_212269_ + p_212270_; ++j) {
               if (structureplacement.isFeatureChunk(this, p_212267_, i, j)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static BoundingBox getWritableArea(ChunkAccess p_187718_) {
      ChunkPos chunkpos = p_187718_.getPos();
      int i = chunkpos.getMinBlockX();
      int j = chunkpos.getMinBlockZ();
      LevelHeightAccessor levelheightaccessor = p_187718_.getHeightAccessorForGeneration();
      int k = levelheightaccessor.getMinBuildHeight() + 1;
      int l = levelheightaccessor.getMaxBuildHeight() - 1;
      return new BoundingBox(i, k, j, i + 15, l, j + 15);
   }

   public abstract void buildSurface(WorldGenRegion p_187697_, StructureFeatureManager p_187698_, ChunkAccess p_187699_);

   public abstract void spawnOriginalMobs(WorldGenRegion p_62167_);

   public int getSpawnHeight(LevelHeightAccessor p_156157_) {
      return 64;
   }

   public BiomeSource getBiomeSource() {
      return this.runtimeBiomeSource;
   }

   public abstract int getGenDepth();

   public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> p_204386_, StructureFeatureManager p_204387_, MobCategory p_204388_, BlockPos p_204389_) {
      Map<ConfiguredStructureFeature<?, ?>, LongSet> map = p_204387_.getAllStructuresAt(p_204389_);

      for(Entry<ConfiguredStructureFeature<?, ?>, LongSet> entry : map.entrySet()) {
         ConfiguredStructureFeature<?, ?> configuredstructurefeature = entry.getKey();
         StructureSpawnOverride structurespawnoverride = configuredstructurefeature.spawnOverrides.get(p_204388_);
         if (structurespawnoverride != null) {
            MutableBoolean mutableboolean = new MutableBoolean(false);
            Predicate<StructureStart> predicate = structurespawnoverride.boundingBox() == StructureSpawnOverride.BoundingBoxType.PIECE ? (p_211631_) -> {
               return p_204387_.structureHasPieceAt(p_204389_, p_211631_);
            } : (p_211666_) -> {
               return p_211666_.getBoundingBox().isInside(p_204389_);
            };
            p_204387_.fillStartsForFeature(configuredstructurefeature, entry.getValue(), (p_211692_) -> {
               if (mutableboolean.isFalse() && predicate.test(p_211692_)) {
                  mutableboolean.setTrue();
               }

            });
            if (mutableboolean.isTrue()) {
               return structurespawnoverride.spawns();
            }
         }
      }

      return p_204386_.value().getMobSettings().getMobs(p_204388_);
   }

   public static Stream<ConfiguredStructureFeature<?, ?>> allConfigurations(Registry<ConfiguredStructureFeature<?, ?>> p_208045_, StructureFeature<?> p_208046_) {
      return p_208045_.stream().filter((p_211656_) -> {
         return p_211656_.feature == p_208046_;
      });
   }

   public void createStructures(RegistryAccess p_62200_, StructureFeatureManager p_62201_, ChunkAccess p_62202_, StructureManager p_62203_, long p_62204_) {
      ChunkPos chunkpos = p_62202_.getPos();
      SectionPos sectionpos = SectionPos.bottomOf(p_62202_);
      this.possibleStructureSets().forEach((p_212264_) -> {
         StructurePlacement structureplacement = p_212264_.value().placement();
         List<StructureSet.StructureSelectionEntry> list = p_212264_.value().structures();

         for(StructureSet.StructureSelectionEntry structureset$structureselectionentry : list) {
            StructureStart structurestart = p_62201_.getStartForFeature(sectionpos, structureset$structureselectionentry.structure().value(), p_62202_);
            if (structurestart != null && structurestart.isValid()) {
               return;
            }
         }

         if (structureplacement.isFeatureChunk(this, p_62204_, chunkpos.x, chunkpos.z)) {
            if (list.size() == 1) {
               this.tryGenerateStructure(list.get(0), p_62201_, p_62200_, p_62203_, p_62204_, p_62202_, chunkpos, sectionpos);
            } else {
               ArrayList<StructureSet.StructureSelectionEntry> arraylist = new ArrayList<>(list.size());
               arraylist.addAll(list);
               WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
               worldgenrandom.setLargeFeatureSeed(p_62204_, chunkpos.x, chunkpos.z);
               int i = 0;

               for(StructureSet.StructureSelectionEntry structureset$structureselectionentry1 : arraylist) {
                  i += structureset$structureselectionentry1.weight();
               }

               while(!arraylist.isEmpty()) {
                  int j = worldgenrandom.nextInt(i);
                  int k = 0;

                  for(StructureSet.StructureSelectionEntry structureset$structureselectionentry2 : arraylist) {
                     j -= structureset$structureselectionentry2.weight();
                     if (j < 0) {
                        break;
                     }

                     ++k;
                  }

                  StructureSet.StructureSelectionEntry structureset$structureselectionentry3 = arraylist.get(k);
                  if (this.tryGenerateStructure(structureset$structureselectionentry3, p_62201_, p_62200_, p_62203_, p_62204_, p_62202_, chunkpos, sectionpos)) {
                     return;
                  }

                  arraylist.remove(k);
                  i -= structureset$structureselectionentry3.weight();
               }

            }
         }
      });
   }

   private boolean tryGenerateStructure(StructureSet.StructureSelectionEntry p_208017_, StructureFeatureManager p_208018_, RegistryAccess p_208019_, StructureManager p_208020_, long p_208021_, ChunkAccess p_208022_, ChunkPos p_208023_, SectionPos p_208024_) {
      ConfiguredStructureFeature<?, ?> configuredstructurefeature = p_208017_.structure().value();
      int i = fetchReferences(p_208018_, p_208022_, p_208024_, configuredstructurefeature);
      HolderSet<Biome> holderset = configuredstructurefeature.biomes();
      Predicate<Holder<Biome>> predicate = (p_211672_) -> {
         return holderset.contains(this.adjustBiome(p_211672_));
      };
      StructureStart structurestart = configuredstructurefeature.generate(p_208019_, this, this.biomeSource, p_208020_, p_208021_, p_208023_, i, p_208022_, predicate);
      if (structurestart.isValid()) {
         p_208018_.setStartForFeature(p_208024_, configuredstructurefeature, structurestart, p_208022_);
         return true;
      } else {
         return false;
      }
   }

   private static int fetchReferences(StructureFeatureManager p_207977_, ChunkAccess p_207978_, SectionPos p_207979_, ConfiguredStructureFeature<?, ?> p_207980_) {
      StructureStart structurestart = p_207977_.getStartForFeature(p_207979_, p_207980_, p_207978_);
      return structurestart != null ? structurestart.getReferences() : 0;
   }

   protected Holder<Biome> adjustBiome(Holder<Biome> p_204385_) {
      return p_204385_;
   }

   public void createReferences(WorldGenLevel p_62178_, StructureFeatureManager p_62179_, ChunkAccess p_62180_) {
      int i = 8;
      ChunkPos chunkpos = p_62180_.getPos();
      int j = chunkpos.x;
      int k = chunkpos.z;
      int l = chunkpos.getMinBlockX();
      int i1 = chunkpos.getMinBlockZ();
      SectionPos sectionpos = SectionPos.bottomOf(p_62180_);

      for(int j1 = j - 8; j1 <= j + 8; ++j1) {
         for(int k1 = k - 8; k1 <= k + 8; ++k1) {
            long l1 = ChunkPos.asLong(j1, k1);

            for(StructureStart structurestart : p_62178_.getChunk(j1, k1).getAllStarts().values()) {
               try {
                  if (structurestart.isValid() && structurestart.getBoundingBox().intersects(l, i1, l + 15, i1 + 15)) {
                     p_62179_.addReferenceForFeature(sectionpos, structurestart.getFeature(), l1, p_62180_);
                     DebugPackets.sendStructurePacket(p_62178_, structurestart);
                  }
               } catch (Exception exception) {
                  CrashReport crashreport = CrashReport.forThrowable(exception, "Generating structure reference");
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Structure");
                  Optional<? extends Registry<ConfiguredStructureFeature<?, ?>>> optional = p_62178_.registryAccess().registry(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
                  crashreportcategory.setDetail("Id", () -> {
                     return optional.map((p_211661_) -> {
                        return p_211661_.getKey(structurestart.getFeature()).toString();
                     }).orElse("UNKNOWN");
                  });
                  crashreportcategory.setDetail("Name", () -> {
                     return Registry.STRUCTURE_FEATURE.getKey(structurestart.getFeature().feature).toString();
                  });
                  crashreportcategory.setDetail("Class", () -> {
                     return structurestart.getFeature().getClass().getCanonicalName();
                  });
                  throw new ReportedException(crashreport);
               }
            }
         }
      }

   }

   public abstract CompletableFuture<ChunkAccess> fillFromNoise(Executor p_187748_, Blender p_187749_, StructureFeatureManager p_187750_, ChunkAccess p_187751_);

   public abstract int getSeaLevel();

   public abstract int getMinY();

   public abstract int getBaseHeight(int p_156153_, int p_156154_, Heightmap.Types p_156155_, LevelHeightAccessor p_156156_);

   public abstract NoiseColumn getBaseColumn(int p_156150_, int p_156151_, LevelHeightAccessor p_156152_);

   public int getFirstFreeHeight(int p_156175_, int p_156176_, Heightmap.Types p_156177_, LevelHeightAccessor p_156178_) {
      return this.getBaseHeight(p_156175_, p_156176_, p_156177_, p_156178_);
   }

   public int getFirstOccupiedHeight(int p_156180_, int p_156181_, Heightmap.Types p_156182_, LevelHeightAccessor p_156183_) {
      return this.getBaseHeight(p_156180_, p_156181_, p_156182_, p_156183_) - 1;
   }

   public void ensureStructuresGenerated() {
      if (!this.hasGeneratedPositions) {
         this.generatePositions();
         this.hasGeneratedPositions = true;
      }

   }

   @Nullable
   public List<ChunkPos> getRingPositionsFor(ConcentricRingsStructurePlacement p_204381_) {
      this.ensureStructuresGenerated();
      CompletableFuture<List<ChunkPos>> completablefuture = this.ringPositions.get(p_204381_);
      return completablefuture != null ? completablefuture.join() : null;
   }

   private List<StructurePlacement> getPlacementsForFeature(Holder<ConfiguredStructureFeature<?, ?>> p_208091_) {
      this.ensureStructuresGenerated();
      return this.placementsForFeature.getOrDefault(p_208091_.value(), List.of());
   }

   public abstract void addDebugScreenInfo(List<String> p_208054_, BlockPos p_208055_);

   static {
      Registry.register(Registry.CHUNK_GENERATOR, "noise", NoiseBasedChunkGenerator.CODEC);
      Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatLevelSource.CODEC);
      Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugLevelSource.CODEC);
      LOGGER = LogUtils.getLogger();
      CODEC = Registry.CHUNK_GENERATOR.byNameCodec().dispatchStable(ChunkGenerator::codec, Function.identity());
   }
}