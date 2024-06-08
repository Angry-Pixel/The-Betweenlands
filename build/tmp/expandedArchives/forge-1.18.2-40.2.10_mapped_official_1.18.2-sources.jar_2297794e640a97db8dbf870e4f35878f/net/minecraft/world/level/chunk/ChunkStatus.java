package net.minecraft.world.level.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class ChunkStatus extends net.minecraftforge.registries.ForgeRegistryEntry<ChunkStatus> {
   public static final int MAX_STRUCTURE_DISTANCE = 8;
   private static final EnumSet<Heightmap.Types> PRE_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.WORLD_SURFACE_WG);
   public static final EnumSet<Heightmap.Types> POST_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
   private static final ChunkStatus.LoadingTask PASSTHROUGH_LOAD_TASK = (p_62461_, p_62462_, p_62463_, p_62464_, p_62465_, p_62466_) -> {
      if (p_62466_ instanceof ProtoChunk) {
         ProtoChunk protochunk = (ProtoChunk)p_62466_;
         if (!p_62466_.getStatus().isOrAfter(p_62461_)) {
            protochunk.setStatus(p_62461_);
         }
      }

      return CompletableFuture.completedFuture(Either.left(p_62466_));
   };
   public static final ChunkStatus EMPTY = registerSimple("empty", (ChunkStatus)null, -1, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_156307_, p_156308_, p_156309_, p_156310_, p_156311_) -> {
   });
   public static final ChunkStatus STRUCTURE_STARTS = register("structure_starts", EMPTY, 0, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_187861_, p_187862_, p_187863_, p_187864_, p_187865_, p_187866_, p_187867_, p_187868_, p_187869_, p_187870_) -> {
      if (!p_187869_.getStatus().isOrAfter(p_187861_)) {
         if (p_187863_.getServer().getWorldData().worldGenSettings().generateFeatures()) {
            p_187864_.createStructures(p_187863_.registryAccess(), p_187863_.structureFeatureManager(), p_187869_, p_187865_, p_187863_.getSeed());
         }

         if (p_187869_ instanceof ProtoChunk) {
            ProtoChunk protochunk = (ProtoChunk)p_187869_;
            protochunk.setStatus(p_187861_);
         }

         p_187863_.onStructureStartsAvailable(p_187869_);
      }

      return CompletableFuture.completedFuture(Either.left(p_187869_));
   }, (p_196811_, p_196812_, p_196813_, p_196814_, p_196815_, p_196816_) -> {
      if (!p_196816_.getStatus().isOrAfter(p_196811_)) {
         if (p_196816_ instanceof ProtoChunk) {
            ProtoChunk protochunk = (ProtoChunk)p_196816_;
            protochunk.setStatus(p_196811_);
         }

         p_196812_.onStructureStartsAvailable(p_196816_);
      }

      return CompletableFuture.completedFuture(Either.left(p_196816_));
   });
   public static final ChunkStatus STRUCTURE_REFERENCES = registerSimple("structure_references", STRUCTURE_STARTS, 8, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_196843_, p_196844_, p_196845_, p_196846_, p_196847_) -> {
      WorldGenRegion worldgenregion = new WorldGenRegion(p_196844_, p_196846_, p_196843_, -1);
      p_196845_.createReferences(worldgenregion, p_196844_.structureFeatureManager().forWorldGenRegion(worldgenregion), p_196847_);
   });
   public static final ChunkStatus BIOMES = register("biomes", STRUCTURE_REFERENCES, 8, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_196832_, p_196833_, p_196834_, p_196835_, p_196836_, p_196837_, p_196838_, p_196839_, p_196840_, p_196841_) -> {
      if (!p_196841_ && p_196840_.getStatus().isOrAfter(p_196832_)) {
         return CompletableFuture.completedFuture(Either.left(p_196840_));
      } else {
         WorldGenRegion worldgenregion = new WorldGenRegion(p_196834_, p_196839_, p_196832_, -1);
         return p_196835_.createBiomes(p_196834_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), p_196833_, Blender.of(worldgenregion), p_196834_.structureFeatureManager().forWorldGenRegion(worldgenregion), p_196840_).thenApply((p_196819_) -> {
            if (p_196819_ instanceof ProtoChunk) {
               ((ProtoChunk)p_196819_).setStatus(p_196832_);
            }

            return Either.left(p_196819_);
         });
      }
   });
   public static final ChunkStatus NOISE = register("noise", BIOMES, 8, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_196821_, p_196822_, p_196823_, p_196824_, p_196825_, p_196826_, p_196827_, p_196828_, p_196829_, p_196830_) -> {
      if (!p_196830_ && p_196829_.getStatus().isOrAfter(p_196821_)) {
         return CompletableFuture.completedFuture(Either.left(p_196829_));
      } else {
         WorldGenRegion worldgenregion = new WorldGenRegion(p_196823_, p_196828_, p_196821_, 0);
         return p_196824_.fillFromNoise(p_196822_, Blender.of(worldgenregion), p_196823_.structureFeatureManager().forWorldGenRegion(worldgenregion), p_196829_).thenApply((p_196792_) -> {
            if (p_196792_ instanceof ProtoChunk) {
               ProtoChunk protochunk = (ProtoChunk)p_196792_;
               BelowZeroRetrogen belowzeroretrogen = protochunk.getBelowZeroRetrogen();
               if (belowzeroretrogen != null) {
                  BelowZeroRetrogen.replaceOldBedrock(protochunk);
                  if (belowzeroretrogen.hasBedrockHoles()) {
                     belowzeroretrogen.applyBedrockMask(protochunk);
                  }
               }

               protochunk.setStatus(p_196821_);
            }

            return Either.left(p_196792_);
         });
      }
   });
   public static final ChunkStatus SURFACE = registerSimple("surface", NOISE, 8, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_156247_, p_156248_, p_156249_, p_156250_, p_156251_) -> {
      WorldGenRegion worldgenregion = new WorldGenRegion(p_156248_, p_156250_, p_156247_, 0);
      p_156249_.buildSurface(worldgenregion, p_156248_.structureFeatureManager().forWorldGenRegion(worldgenregion), p_156251_);
   });
   public static final ChunkStatus CARVERS = registerSimple("carvers", SURFACE, 8, PRE_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_187819_, p_187820_, p_187821_, p_187822_, p_187823_) -> {
      WorldGenRegion worldgenregion = new WorldGenRegion(p_187820_, p_187822_, p_187819_, 0);
      if (p_187823_ instanceof ProtoChunk) {
         ProtoChunk protochunk = (ProtoChunk)p_187823_;
         Blender.addAroundOldChunksCarvingMaskFilter(worldgenregion, protochunk);
      }

      p_187821_.applyCarvers(worldgenregion, p_187820_.getSeed(), p_187820_.getBiomeManager(), p_187820_.structureFeatureManager().forWorldGenRegion(worldgenregion), p_187823_, GenerationStep.Carving.AIR);
   });
   public static final ChunkStatus LIQUID_CARVERS = registerSimple("liquid_carvers", CARVERS, 8, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_196805_, p_196806_, p_196807_, p_196808_, p_196809_) -> {
   });
   public static final ChunkStatus FEATURES = register("features", LIQUID_CARVERS, 8, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_187808_, p_187809_, p_187810_, p_187811_, p_187812_, p_187813_, p_187814_, p_187815_, p_187816_, p_187817_) -> {
      ProtoChunk protochunk = (ProtoChunk)p_187816_;
      protochunk.setLightEngine(p_187813_);
      if (p_187817_ || !p_187816_.getStatus().isOrAfter(p_187808_)) {
         Heightmap.primeHeightmaps(p_187816_, EnumSet.of(Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE));
         WorldGenRegion worldgenregion = new WorldGenRegion(p_187810_, p_187815_, p_187808_, 1);
         p_187811_.applyBiomeDecoration(worldgenregion, p_187816_, p_187810_.structureFeatureManager().forWorldGenRegion(worldgenregion));
         Blender.generateBorderTicks(worldgenregion, p_187816_);
         protochunk.setStatus(p_187808_);
      }

      return CompletableFuture.completedFuture(Either.left(p_187816_));
   });
   public static final ChunkStatus LIGHT = register("light", FEATURES, 1, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_196794_, p_196795_, p_196796_, p_196797_, p_196798_, p_196799_, p_196800_, p_196801_, p_196802_, p_196803_) -> {
      return lightChunk(p_196794_, p_196799_, p_196802_);
   }, (p_196784_, p_196785_, p_196786_, p_196787_, p_196788_, p_196789_) -> {
      return lightChunk(p_196784_, p_196787_, p_196789_);
   });
   public static final ChunkStatus SPAWN = registerSimple("spawn", LIGHT, 0, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_187763_, p_187764_, p_187765_, p_187766_, p_187767_) -> {
      if (!p_187767_.isUpgrading()) {
         p_187765_.spawnOriginalMobs(new WorldGenRegion(p_187764_, p_187766_, p_187763_, -1));
      }

   });
   public static final ChunkStatus HEIGHTMAPS = registerSimple("heightmaps", SPAWN, 0, POST_FEATURES, ChunkStatus.ChunkType.PROTOCHUNK, (p_196758_, p_196759_, p_196760_, p_196761_, p_196762_) -> {
   });
   public static final ChunkStatus FULL = register("full", HEIGHTMAPS, 0, POST_FEATURES, ChunkStatus.ChunkType.LEVELCHUNK, (p_196771_, p_196772_, p_196773_, p_196774_, p_196775_, p_196776_, p_196777_, p_196778_, p_196779_, p_196780_) -> {
      return p_196777_.apply(p_196779_);
   }, (p_196764_, p_196765_, p_196766_, p_196767_, p_196768_, p_196769_) -> {
      return p_196768_.apply(p_196769_);
   });
   private static final List<ChunkStatus> STATUS_BY_RANGE = ImmutableList.of(FULL, FEATURES, LIQUID_CARVERS, BIOMES, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS);
   private static final IntList RANGE_BY_STATUS = Util.make(new IntArrayList(getStatusList().size()), (p_196782_) -> {
      int i = 0;

      for(int j = getStatusList().size() - 1; j >= 0; --j) {
         while(i + 1 < STATUS_BY_RANGE.size() && j <= STATUS_BY_RANGE.get(i + 1).getIndex()) {
            ++i;
         }

         p_196782_.add(0, i);
      }

   });
   private final String name;
   private final int index;
   private final ChunkStatus parent;
   private final ChunkStatus.GenerationTask generationTask;
   private final ChunkStatus.LoadingTask loadingTask;
   private final int range;
   private final ChunkStatus.ChunkType chunkType;
   private final EnumSet<Heightmap.Types> heightmapsAfter;

   private static CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> lightChunk(ChunkStatus p_62389_, ThreadedLevelLightEngine p_62390_, ChunkAccess p_62391_) {
      boolean flag = isLighted(p_62389_, p_62391_);
      if (!p_62391_.getStatus().isOrAfter(p_62389_)) {
         ((ProtoChunk)p_62391_).setStatus(p_62389_);
      }

      return p_62390_.lightChunk(p_62391_, flag).thenApply(Either::left);
   }

   private static ChunkStatus registerSimple(String p_62415_, @Nullable ChunkStatus p_62416_, int p_62417_, EnumSet<Heightmap.Types> p_62418_, ChunkStatus.ChunkType p_62419_, ChunkStatus.SimpleGenerationTask p_62420_) {
      return register(p_62415_, p_62416_, p_62417_, p_62418_, p_62419_, p_62420_);
   }

   private static ChunkStatus register(String p_62400_, @Nullable ChunkStatus p_62401_, int p_62402_, EnumSet<Heightmap.Types> p_62403_, ChunkStatus.ChunkType p_62404_, ChunkStatus.GenerationTask p_62405_) {
      return register(p_62400_, p_62401_, p_62402_, p_62403_, p_62404_, p_62405_, PASSTHROUGH_LOAD_TASK);
   }

   private static ChunkStatus register(String p_62407_, @Nullable ChunkStatus p_62408_, int p_62409_, EnumSet<Heightmap.Types> p_62410_, ChunkStatus.ChunkType p_62411_, ChunkStatus.GenerationTask p_62412_, ChunkStatus.LoadingTask p_62413_) {
      return Registry.register(Registry.CHUNK_STATUS, p_62407_, new ChunkStatus(p_62407_, p_62408_, p_62409_, p_62410_, p_62411_, p_62412_, p_62413_));
   }

   public static List<ChunkStatus> getStatusList() {
      List<ChunkStatus> list = Lists.newArrayList();

      ChunkStatus chunkstatus;
      for(chunkstatus = FULL; chunkstatus.getParent() != chunkstatus; chunkstatus = chunkstatus.getParent()) {
         list.add(chunkstatus);
      }

      list.add(chunkstatus);
      Collections.reverse(list);
      return list;
   }

   private static boolean isLighted(ChunkStatus p_62393_, ChunkAccess p_62394_) {
      return p_62394_.getStatus().isOrAfter(p_62393_) && p_62394_.isLightCorrect();
   }

   public static ChunkStatus getStatusAroundFullChunk(int p_156186_) {
      if (p_156186_ >= STATUS_BY_RANGE.size()) {
         return EMPTY;
      } else {
         return p_156186_ < 0 ? FULL : STATUS_BY_RANGE.get(p_156186_);
      }
   }

   public static int maxDistance() {
      return STATUS_BY_RANGE.size();
   }

   public static int getDistance(ChunkStatus p_62371_) {
      return RANGE_BY_STATUS.getInt(p_62371_.getIndex());
   }

   public ChunkStatus(String p_62342_, @Nullable ChunkStatus p_62343_, int p_62344_, EnumSet<Heightmap.Types> p_62345_, ChunkStatus.ChunkType p_62346_, ChunkStatus.GenerationTask p_62347_, ChunkStatus.LoadingTask p_62348_) {
      this.name = p_62342_;
      this.parent = p_62343_ == null ? this : p_62343_;
      this.generationTask = p_62347_;
      this.loadingTask = p_62348_;
      this.range = p_62344_;
      this.chunkType = p_62346_;
      this.heightmapsAfter = p_62345_;
      this.index = p_62343_ == null ? 0 : p_62343_.getIndex() + 1;
   }

   public int getIndex() {
      return this.index;
   }

   public String getName() {
      return this.name;
   }

   public ChunkStatus getParent() {
      return this.parent;
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> generate(Executor p_187789_, ServerLevel p_187790_, ChunkGenerator p_187791_, StructureManager p_187792_, ThreadedLevelLightEngine p_187793_, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> p_187794_, List<ChunkAccess> p_187795_, boolean p_187796_) {
      ChunkAccess chunkaccess = p_187795_.get(p_187795_.size() / 2);
      ProfiledDuration profiledduration = JvmProfiler.INSTANCE.onChunkGenerate(chunkaccess.getPos(), p_187790_.dimension(), this.name);
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.generationTask.doWork(this, p_187789_, p_187790_, p_187791_, p_187792_, p_187793_, p_187794_, p_187795_, chunkaccess, p_187796_);
      return profiledduration != null ? completablefuture.thenApply((p_196756_) -> {
         profiledduration.finish();
         return p_196756_;
      }) : completablefuture;
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> load(ServerLevel p_62365_, StructureManager p_62366_, ThreadedLevelLightEngine p_62367_, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> p_62368_, ChunkAccess p_62369_) {
      return this.loadingTask.doWork(this, p_62365_, p_62366_, p_62367_, p_62368_, p_62369_);
   }

   public int getRange() {
      return this.range;
   }

   public ChunkStatus.ChunkType getChunkType() {
      return this.chunkType;
   }

   public static ChunkStatus byName(String p_62398_) {
      return Registry.CHUNK_STATUS.get(ResourceLocation.tryParse(p_62398_));
   }

   public EnumSet<Heightmap.Types> heightmapsAfter() {
      return this.heightmapsAfter;
   }

   public boolean isOrAfter(ChunkStatus p_62428_) {
      return this.getIndex() >= p_62428_.getIndex();
   }

   public String toString() {
      return Registry.CHUNK_STATUS.getKey(this).toString();
   }

   public static enum ChunkType {
      PROTOCHUNK,
      LEVELCHUNK;
   }

   interface GenerationTask {
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus p_187871_, Executor p_187872_, ServerLevel p_187873_, ChunkGenerator p_187874_, StructureManager p_187875_, ThreadedLevelLightEngine p_187876_, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> p_187877_, List<ChunkAccess> p_187878_, ChunkAccess p_187879_, boolean p_187880_);
   }

   interface LoadingTask {
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus p_62531_, ServerLevel p_62532_, StructureManager p_62533_, ThreadedLevelLightEngine p_62534_, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> p_62535_, ChunkAccess p_62536_);
   }

   interface SimpleGenerationTask extends ChunkStatus.GenerationTask {
      default CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> doWork(ChunkStatus p_187882_, Executor p_187883_, ServerLevel p_187884_, ChunkGenerator p_187885_, StructureManager p_187886_, ThreadedLevelLightEngine p_187887_, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> p_187888_, List<ChunkAccess> p_187889_, ChunkAccess p_187890_, boolean p_187891_) {
         if (p_187891_ || !p_187890_.getStatus().isOrAfter(p_187882_)) {
            this.doWork(p_187882_, p_187884_, p_187885_, p_187889_, p_187890_);
            if (p_187890_ instanceof ProtoChunk) {
               ProtoChunk protochunk = (ProtoChunk)p_187890_;
               protochunk.setStatus(p_187882_);
            }
         }

         return CompletableFuture.completedFuture(Either.left(p_187890_));
      }

      void doWork(ChunkStatus p_156323_, ServerLevel p_156324_, ChunkGenerator p_156325_, List<ChunkAccess> p_156326_, ChunkAccess p_156327_);
   }
}
