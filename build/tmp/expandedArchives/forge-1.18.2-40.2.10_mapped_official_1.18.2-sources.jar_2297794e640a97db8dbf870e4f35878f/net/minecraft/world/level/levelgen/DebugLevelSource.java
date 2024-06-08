package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class DebugLevelSource extends ChunkGenerator {
   public static final Codec<DebugLevelSource> CODEC = RecordCodecBuilder.create((p_208215_) -> {
      return commonCodec(p_208215_).and(RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter((p_208210_) -> {
         return p_208210_.biomes;
      })).apply(p_208215_, p_208215_.stable(DebugLevelSource::new));
   });
   private static final int BLOCK_MARGIN = 2;
   private static List<BlockState> ALL_BLOCKS = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap((p_208208_) -> {
      return p_208208_.getStateDefinition().getPossibleStates().stream();
   }).collect(Collectors.toList());
   private static int GRID_WIDTH = Mth.ceil(Mth.sqrt((float)ALL_BLOCKS.size()));
   private static int GRID_HEIGHT = Mth.ceil((float)ALL_BLOCKS.size() / (float)GRID_WIDTH);
   protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
   protected static final BlockState BARRIER = Blocks.BARRIER.defaultBlockState();
   public static final int HEIGHT = 70;
   public static final int BARRIER_HEIGHT = 60;
   private final Registry<Biome> biomes;

   public DebugLevelSource(Registry<StructureSet> p_208205_, Registry<Biome> p_208206_) {
      super(p_208205_, Optional.empty(), new FixedBiomeSource(p_208206_.getOrCreateHolder(Biomes.PLAINS)));
      this.biomes = p_208206_;
   }

   public Registry<Biome> biomes() {
      return this.biomes;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long p_64130_) {
      return this;
   }

   public void buildSurface(WorldGenRegion p_188520_, StructureFeatureManager p_188521_, ChunkAccess p_188522_) {
   }

   public void applyBiomeDecoration(WorldGenLevel p_188524_, ChunkAccess p_188525_, StructureFeatureManager p_188526_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      ChunkPos chunkpos = p_188525_.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            int i1 = SectionPos.sectionToBlockCoord(i, k);
            int j1 = SectionPos.sectionToBlockCoord(j, l);
            p_188524_.setBlock(blockpos$mutableblockpos.set(i1, 60, j1), BARRIER, 2);
            BlockState blockstate = getBlockStateFor(i1, j1);
            p_188524_.setBlock(blockpos$mutableblockpos.set(i1, 70, j1), blockstate, 2);
         }
      }

   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_188528_, Blender p_188529_, StructureFeatureManager p_188530_, ChunkAccess p_188531_) {
      return CompletableFuture.completedFuture(p_188531_);
   }

   public int getBaseHeight(int p_158233_, int p_158234_, Heightmap.Types p_158235_, LevelHeightAccessor p_158236_) {
      return 0;
   }

   public NoiseColumn getBaseColumn(int p_158229_, int p_158230_, LevelHeightAccessor p_158231_) {
      return new NoiseColumn(0, new BlockState[0]);
   }

   public void addDebugScreenInfo(List<String> p_208212_, BlockPos p_208213_) {
   }

   public static BlockState getBlockStateFor(int p_64149_, int p_64150_) {
      BlockState blockstate = AIR;
      if (p_64149_ > 0 && p_64150_ > 0 && p_64149_ % 2 != 0 && p_64150_ % 2 != 0) {
         p_64149_ /= 2;
         p_64150_ /= 2;
         if (p_64149_ <= GRID_WIDTH && p_64150_ <= GRID_HEIGHT) {
            int i = Mth.abs(p_64149_ * GRID_WIDTH + p_64150_);
            if (i < ALL_BLOCKS.size()) {
               blockstate = ALL_BLOCKS.get(i);
            }
         }
      }

      return blockstate;
   }
   
   public static void initValidStates() {
      ALL_BLOCKS = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toList());
      GRID_WIDTH = Mth.ceil(Mth.sqrt(ALL_BLOCKS.size()));
      GRID_HEIGHT = Mth.ceil((float) (ALL_BLOCKS.size() / GRID_WIDTH));
   }

   public Climate.Sampler climateSampler() {
      return Climate.empty();
   }

   public void applyCarvers(WorldGenRegion p_188513_, long p_188514_, BiomeManager p_188515_, StructureFeatureManager p_188516_, ChunkAccess p_188517_, GenerationStep.Carving p_188518_) {
   }

   public void spawnOriginalMobs(WorldGenRegion p_188511_) {
   }

   public int getMinY() {
      return 0;
   }

   public int getGenDepth() {
      return 384;
   }

   public int getSeaLevel() {
      return 63;
   }
}
