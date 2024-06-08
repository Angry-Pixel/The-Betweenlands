package net.minecraft.world.level.levelgen;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseBasedChunkGenerator extends ChunkGenerator {
   public static final Codec<NoiseBasedChunkGenerator> CODEC = RecordCodecBuilder.create((p_188643_) -> {
      return commonCodec(p_188643_).and(p_188643_.group(RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter((p_188716_) -> {
         return p_188716_.noises;
      }), BiomeSource.CODEC.fieldOf("biome_source").forGetter((p_188711_) -> {
         return p_188711_.biomeSource;
      }), Codec.LONG.fieldOf("seed").stable().forGetter((p_188690_) -> {
         return p_188690_.seed;
      }), NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((p_204585_) -> {
         return p_204585_.settings;
      }))).apply(p_188643_, p_188643_.stable(NoiseBasedChunkGenerator::new));
   });
   private static final BlockState AIR = Blocks.AIR.defaultBlockState();
   private static final BlockState[] EMPTY_COLUMN = new BlockState[0];
   protected final BlockState defaultBlock;
   private final Registry<NormalNoise.NoiseParameters> noises;
   protected final long seed;
   protected final Holder<NoiseGeneratorSettings> settings;
   private final NoiseRouter router;
   protected final Climate.Sampler sampler;
   private final SurfaceSystem surfaceSystem;
   private final Aquifer.FluidPicker globalFluidPicker;

   public NoiseBasedChunkGenerator(Registry<StructureSet> p_209106_, Registry<NormalNoise.NoiseParameters> p_209107_, BiomeSource p_209108_, long p_209109_, Holder<NoiseGeneratorSettings> p_209110_) {
      this(p_209106_, p_209107_, p_209108_, p_209108_, p_209109_, p_209110_);
   }

   private NoiseBasedChunkGenerator(Registry<StructureSet> p_209112_, Registry<NormalNoise.NoiseParameters> p_209113_, BiomeSource p_209114_, BiomeSource p_209115_, long p_209116_, Holder<NoiseGeneratorSettings> p_209117_) {
      super(p_209112_, Optional.empty(), p_209114_, p_209115_, p_209116_);
      this.noises = p_209113_;
      this.seed = p_209116_;
      this.settings = p_209117_;
      NoiseGeneratorSettings noisegeneratorsettings = this.settings.value();
      this.defaultBlock = noisegeneratorsettings.defaultBlock();
      NoiseSettings noisesettings = noisegeneratorsettings.noiseSettings();
      this.router = noisegeneratorsettings.createNoiseRouter(p_209113_, p_209116_);
      this.sampler = new Climate.Sampler(this.router.temperature(), this.router.humidity(), this.router.continents(), this.router.erosion(), this.router.depth(), this.router.ridges(), this.router.spawnTarget());
      Aquifer.FluidStatus aquifer$fluidstatus = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
      int i = noisegeneratorsettings.seaLevel();
      Aquifer.FluidStatus aquifer$fluidstatus1 = new Aquifer.FluidStatus(i, noisegeneratorsettings.defaultFluid());
      Aquifer.FluidStatus aquifer$fluidstatus2 = new Aquifer.FluidStatus(noisesettings.minY() - 1, Blocks.AIR.defaultBlockState());
      this.globalFluidPicker = (p_198228_, p_198229_, p_198230_) -> {
         return p_198229_ < Math.min(-54, i) ? aquifer$fluidstatus : aquifer$fluidstatus1;
      };
      this.surfaceSystem = new SurfaceSystem(p_209113_, this.defaultBlock, i, p_209116_, noisegeneratorsettings.getRandomSource());
   }

   public CompletableFuture<ChunkAccess> createBiomes(Registry<Biome> p_197005_, Executor p_197006_, Blender p_197007_, StructureFeatureManager p_197008_, ChunkAccess p_197009_) {
      return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
         this.doCreateBiomes(p_197007_, p_197008_, p_197009_);
         return p_197009_;
      }), Util.backgroundExecutor());
   }

   private void doCreateBiomes(Blender p_204587_, StructureFeatureManager p_204588_, ChunkAccess p_204589_) {
      NoiseChunk noisechunk = p_204589_.getOrCreateNoiseChunk(this.router, () -> {
         return new Beardifier(p_204588_, p_204589_);
      }, this.settings.value(), this.globalFluidPicker, p_204587_);
      BiomeResolver biomeresolver = BelowZeroRetrogen.getBiomeResolver(p_204587_.getBiomeResolver(this.runtimeBiomeSource), p_204589_);
      p_204589_.fillBiomesFromNoise(biomeresolver, noisechunk.cachedClimateSampler(this.router));
   }

   @VisibleForDebug
   public NoiseRouter router() {
      return this.router;
   }

   public Climate.Sampler climateSampler() {
      return this.sampler;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long p_64374_) {
      return new NoiseBasedChunkGenerator(this.structureSets, this.noises, this.biomeSource.withSeed(p_64374_), p_64374_, this.settings);
   }

   public boolean stable(long p_64376_, ResourceKey<NoiseGeneratorSettings> p_64377_) {
      return this.seed == p_64376_ && this.settings.is(p_64377_);
   }

   public int getBaseHeight(int p_158405_, int p_158406_, Heightmap.Types p_158407_, LevelHeightAccessor p_158408_) {
      NoiseSettings noisesettings = this.settings.value().noiseSettings();
      int i = Math.max(noisesettings.minY(), p_158408_.getMinBuildHeight());
      int j = Math.min(noisesettings.minY() + noisesettings.height(), p_158408_.getMaxBuildHeight());
      int k = Mth.intFloorDiv(i, noisesettings.getCellHeight());
      int l = Mth.intFloorDiv(j - i, noisesettings.getCellHeight());
      return l <= 0 ? p_158408_.getMinBuildHeight() : this.iterateNoiseColumn(p_158405_, p_158406_, (BlockState[])null, p_158407_.isOpaque(), k, l).orElse(p_158408_.getMinBuildHeight());
   }

   public NoiseColumn getBaseColumn(int p_158401_, int p_158402_, LevelHeightAccessor p_158403_) {
      NoiseSettings noisesettings = this.settings.value().noiseSettings();
      int i = Math.max(noisesettings.minY(), p_158403_.getMinBuildHeight());
      int j = Math.min(noisesettings.minY() + noisesettings.height(), p_158403_.getMaxBuildHeight());
      int k = Mth.intFloorDiv(i, noisesettings.getCellHeight());
      int l = Mth.intFloorDiv(j - i, noisesettings.getCellHeight());
      if (l <= 0) {
         return new NoiseColumn(i, EMPTY_COLUMN);
      } else {
         BlockState[] ablockstate = new BlockState[l * noisesettings.getCellHeight()];
         this.iterateNoiseColumn(p_158401_, p_158402_, ablockstate, (Predicate<BlockState>)null, k, l);
         return new NoiseColumn(i, ablockstate);
      }
   }

   public void addDebugScreenInfo(List<String> p_209128_, BlockPos p_209129_) {
      DecimalFormat decimalformat = new DecimalFormat("0.000");
      DensityFunction.SinglePointContext densityfunction$singlepointcontext = new DensityFunction.SinglePointContext(p_209129_.getX(), p_209129_.getY(), p_209129_.getZ());
      double d0 = this.router.ridges().compute(densityfunction$singlepointcontext);
      p_209128_.add("NoiseRouter T: " + decimalformat.format(this.router.temperature().compute(densityfunction$singlepointcontext)) + " H: " + decimalformat.format(this.router.humidity().compute(densityfunction$singlepointcontext)) + " C: " + decimalformat.format(this.router.continents().compute(densityfunction$singlepointcontext)) + " E: " + decimalformat.format(this.router.erosion().compute(densityfunction$singlepointcontext)) + " D: " + decimalformat.format(this.router.depth().compute(densityfunction$singlepointcontext)) + " W: " + decimalformat.format(d0) + " PV: " + decimalformat.format((double)TerrainShaper.peaksAndValleys((float)d0)) + " AS: " + decimalformat.format(this.router.initialDensityWithoutJaggedness().compute(densityfunction$singlepointcontext)) + " N: " + decimalformat.format(this.router.finalDensity().compute(densityfunction$singlepointcontext)));
   }

   protected OptionalInt iterateNoiseColumn(int p_158414_, int p_158415_, @Nullable BlockState[] p_158416_, @Nullable Predicate<BlockState> p_158417_, int p_158418_, int p_158419_) {
      NoiseSettings noisesettings = this.settings.value().noiseSettings();
      int i = noisesettings.getCellWidth();
      int j = noisesettings.getCellHeight();
      int k = Math.floorDiv(p_158414_, i);
      int l = Math.floorDiv(p_158415_, i);
      int i1 = Math.floorMod(p_158414_, i);
      int j1 = Math.floorMod(p_158415_, i);
      int k1 = k * i;
      int l1 = l * i;
      double d0 = (double)i1 / (double)i;
      double d1 = (double)j1 / (double)i;
      NoiseChunk noisechunk = NoiseChunk.forColumn(k1, l1, p_158418_, p_158419_, this.router, this.settings.value(), this.globalFluidPicker);
      noisechunk.initializeForFirstCellX();
      noisechunk.advanceCellX(0);

      for(int i2 = p_158419_ - 1; i2 >= 0; --i2) {
         noisechunk.selectCellYZ(i2, 0);

         for(int j2 = j - 1; j2 >= 0; --j2) {
            int k2 = (p_158418_ + i2) * j + j2;
            double d2 = (double)j2 / (double)j;
            noisechunk.updateForY(k2, d2);
            noisechunk.updateForX(p_158414_, d0);
            noisechunk.updateForZ(p_158415_, d1);
            BlockState blockstate = noisechunk.getInterpolatedState();
            BlockState blockstate1 = blockstate == null ? this.defaultBlock : blockstate;
            if (p_158416_ != null) {
               int l2 = i2 * j + j2;
               p_158416_[l2] = blockstate1;
            }

            if (p_158417_ != null && p_158417_.test(blockstate1)) {
               noisechunk.stopInterpolation();
               return OptionalInt.of(k2 + 1);
            }
         }
      }

      noisechunk.stopInterpolation();
      return OptionalInt.empty();
   }

   public void buildSurface(WorldGenRegion p_188636_, StructureFeatureManager p_188637_, ChunkAccess p_188638_) {
      if (!SharedConstants.debugVoidTerrain(p_188638_.getPos())) {
         WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(this, p_188636_);
         NoiseGeneratorSettings noisegeneratorsettings = this.settings.value();
         NoiseChunk noisechunk = p_188638_.getOrCreateNoiseChunk(this.router, () -> {
            return new Beardifier(p_188637_, p_188638_);
         }, noisegeneratorsettings, this.globalFluidPicker, Blender.of(p_188636_));
         this.surfaceSystem.buildSurface(p_188636_.getBiomeManager(), p_188636_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), noisegeneratorsettings.useLegacyRandomSource(), worldgenerationcontext, p_188638_, noisechunk, noisegeneratorsettings.surfaceRule());
      }
   }

   public void applyCarvers(WorldGenRegion p_188629_, long p_188630_, BiomeManager p_188631_, StructureFeatureManager p_188632_, ChunkAccess p_188633_, GenerationStep.Carving p_188634_) {
      BiomeManager biomemanager = p_188631_.withDifferentSource((p_209119_, p_209120_, p_209121_) -> {
         return this.biomeSource.getNoiseBiome(p_209119_, p_209120_, p_209121_, this.climateSampler());
      });
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));
      int i = 8;
      ChunkPos chunkpos = p_188633_.getPos();
      NoiseChunk noisechunk = p_188633_.getOrCreateNoiseChunk(this.router, () -> {
         return new Beardifier(p_188632_, p_188633_);
      }, this.settings.value(), this.globalFluidPicker, Blender.of(p_188629_));
      Aquifer aquifer = noisechunk.aquifer();
      CarvingContext carvingcontext = new CarvingContext(this, p_188629_.registryAccess(), p_188633_.getHeightAccessorForGeneration(), noisechunk);
      CarvingMask carvingmask = ((ProtoChunk)p_188633_).getOrCreateCarvingMask(p_188634_);

      for(int j = -8; j <= 8; ++j) {
         for(int k = -8; k <= 8; ++k) {
            ChunkPos chunkpos1 = new ChunkPos(chunkpos.x + j, chunkpos.z + k);
            ChunkAccess chunkaccess = p_188629_.getChunk(chunkpos1.x, chunkpos1.z);
            BiomeGenerationSettings biomegenerationsettings = chunkaccess.carverBiome(() -> {
               return this.biomeSource.getNoiseBiome(QuartPos.fromBlock(chunkpos1.getMinBlockX()), 0, QuartPos.fromBlock(chunkpos1.getMinBlockZ()), this.climateSampler());
            }).value().getGenerationSettings();
            Iterable<Holder<ConfiguredWorldCarver<?>>> iterable = biomegenerationsettings.getCarvers(p_188634_);
            int l = 0;

            for(Holder<ConfiguredWorldCarver<?>> holder : iterable) {
               ConfiguredWorldCarver<?> configuredworldcarver = holder.value();
               worldgenrandom.setLargeFeatureSeed(p_188630_ + (long)l, chunkpos1.x, chunkpos1.z);
               if (configuredworldcarver.isStartChunk(worldgenrandom)) {
                  configuredworldcarver.carve(carvingcontext, p_188633_, biomemanager::getBiome, worldgenrandom, aquifer, chunkpos1, carvingmask);
               }

               ++l;
            }
         }
      }

   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_188702_, Blender p_188703_, StructureFeatureManager p_188704_, ChunkAccess p_188705_) {
      NoiseSettings noisesettings = this.settings.value().noiseSettings();
      LevelHeightAccessor levelheightaccessor = p_188705_.getHeightAccessorForGeneration();
      int i = Math.max(noisesettings.minY(), levelheightaccessor.getMinBuildHeight());
      int j = Math.min(noisesettings.minY() + noisesettings.height(), levelheightaccessor.getMaxBuildHeight());
      int k = Mth.intFloorDiv(i, noisesettings.getCellHeight());
      int l = Mth.intFloorDiv(j - i, noisesettings.getCellHeight());
      if (l <= 0) {
         return CompletableFuture.completedFuture(p_188705_);
      } else {
         int i1 = p_188705_.getSectionIndex(l * noisesettings.getCellHeight() - 1 + i);
         int j1 = p_188705_.getSectionIndex(i);
         Set<LevelChunkSection> set = Sets.newHashSet();

         for(int k1 = i1; k1 >= j1; --k1) {
            LevelChunkSection levelchunksection = p_188705_.getSection(k1);
            levelchunksection.acquire();
            set.add(levelchunksection);
         }

         return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("wgen_fill_noise", () -> {
            return this.doFill(p_188703_, p_188704_, p_188705_, k, l);
         }), Util.backgroundExecutor()).whenCompleteAsync((p_209132_, p_209133_) -> {
            for(LevelChunkSection levelchunksection1 : set) {
               levelchunksection1.release();
            }

         }, p_188702_);
      }
   }

   private ChunkAccess doFill(Blender p_188663_, StructureFeatureManager p_188664_, ChunkAccess p_188665_, int p_188666_, int p_188667_) {
      NoiseGeneratorSettings noisegeneratorsettings = this.settings.value();
      NoiseChunk noisechunk = p_188665_.getOrCreateNoiseChunk(this.router, () -> {
         return new Beardifier(p_188664_, p_188665_);
      }, noisegeneratorsettings, this.globalFluidPicker, p_188663_);
      Heightmap heightmap = p_188665_.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap heightmap1 = p_188665_.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
      ChunkPos chunkpos = p_188665_.getPos();
      int i = chunkpos.getMinBlockX();
      int j = chunkpos.getMinBlockZ();
      Aquifer aquifer = noisechunk.aquifer();
      noisechunk.initializeForFirstCellX();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      NoiseSettings noisesettings = noisegeneratorsettings.noiseSettings();
      int k = noisesettings.getCellWidth();
      int l = noisesettings.getCellHeight();
      int i1 = 16 / k;
      int j1 = 16 / k;

      for(int k1 = 0; k1 < i1; ++k1) {
         noisechunk.advanceCellX(k1);

         for(int l1 = 0; l1 < j1; ++l1) {
            LevelChunkSection levelchunksection = p_188665_.getSection(p_188665_.getSectionsCount() - 1);

            for(int i2 = p_188667_ - 1; i2 >= 0; --i2) {
               noisechunk.selectCellYZ(i2, l1);

               for(int j2 = l - 1; j2 >= 0; --j2) {
                  int k2 = (p_188666_ + i2) * l + j2;
                  int l2 = k2 & 15;
                  int i3 = p_188665_.getSectionIndex(k2);
                  if (p_188665_.getSectionIndex(levelchunksection.bottomBlockY()) != i3) {
                     levelchunksection = p_188665_.getSection(i3);
                  }

                  double d0 = (double)j2 / (double)l;
                  noisechunk.updateForY(k2, d0);

                  for(int j3 = 0; j3 < k; ++j3) {
                     int k3 = i + k1 * k + j3;
                     int l3 = k3 & 15;
                     double d1 = (double)j3 / (double)k;
                     noisechunk.updateForX(k3, d1);

                     for(int i4 = 0; i4 < k; ++i4) {
                        int j4 = j + l1 * k + i4;
                        int k4 = j4 & 15;
                        double d2 = (double)i4 / (double)k;
                        noisechunk.updateForZ(j4, d2);
                        BlockState blockstate = noisechunk.getInterpolatedState();
                        if (blockstate == null) {
                           blockstate = this.defaultBlock;
                        }

                        blockstate = this.debugPreliminarySurfaceLevel(noisechunk, k3, k2, j4, blockstate);
                        if (blockstate != AIR && !SharedConstants.debugVoidTerrain(p_188665_.getPos())) {
                           if (blockstate.getLightEmission() != 0 && p_188665_ instanceof ProtoChunk) {
                              blockpos$mutableblockpos.set(k3, k2, j4);
                              ((ProtoChunk)p_188665_).addLight(blockpos$mutableblockpos);
                           }

                           levelchunksection.setBlockState(l3, l2, k4, blockstate, false);
                           heightmap.update(l3, k2, k4, blockstate);
                           heightmap1.update(l3, k2, k4, blockstate);
                           if (aquifer.shouldScheduleFluidUpdate() && !blockstate.getFluidState().isEmpty()) {
                              blockpos$mutableblockpos.set(k3, k2, j4);
                              p_188665_.markPosForPostprocessing(blockpos$mutableblockpos);
                           }
                        }
                     }
                  }
               }
            }
         }

         noisechunk.swapSlices();
      }

      noisechunk.stopInterpolation();
      return p_188665_;
   }

   private BlockState debugPreliminarySurfaceLevel(NoiseChunk p_198232_, int p_198233_, int p_198234_, int p_198235_, BlockState p_198236_) {
      return p_198236_;
   }

   public int getGenDepth() {
      return this.settings.value().noiseSettings().height();
   }

   public int getSeaLevel() {
      return this.settings.value().seaLevel();
   }

   public int getMinY() {
      return this.settings.value().noiseSettings().minY();
   }

   public void spawnOriginalMobs(WorldGenRegion p_64379_) {
      if (!this.settings.value().disableMobGeneration()) {
         ChunkPos chunkpos = p_64379_.getCenter();
         Holder<Biome> holder = p_64379_.getBiome(chunkpos.getWorldPosition().atY(p_64379_.getMaxBuildHeight() - 1));
         WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));
         worldgenrandom.setDecorationSeed(p_64379_.getSeed(), chunkpos.getMinBlockX(), chunkpos.getMinBlockZ());
         NaturalSpawner.spawnMobsForChunkGeneration(p_64379_, holder, chunkpos, worldgenrandom);
      }
   }

   /** @deprecated */
   @Deprecated
   public Optional<BlockState> topMaterial(CarvingContext p_188669_, Function<BlockPos, Holder<Biome>> p_188670_, ChunkAccess p_188671_, NoiseChunk p_188672_, BlockPos p_188673_, boolean p_188674_) {
      return this.surfaceSystem.topMaterial(this.settings.value().surfaceRule(), p_188669_, p_188670_, p_188671_, p_188672_, p_188673_, p_188674_);
   }
}