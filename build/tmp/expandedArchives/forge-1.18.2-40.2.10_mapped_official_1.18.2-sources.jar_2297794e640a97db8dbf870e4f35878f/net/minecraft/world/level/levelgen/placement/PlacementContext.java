package net.minecraft.world.level.levelgen.placement;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class PlacementContext extends WorldGenerationContext {
   private final WorldGenLevel level;
   private final ChunkGenerator generator;
   private final Optional<PlacedFeature> topFeature;

   public PlacementContext(WorldGenLevel p_191818_, ChunkGenerator p_191819_, Optional<PlacedFeature> p_191820_) {
      super(p_191819_, p_191818_);
      this.level = p_191818_;
      this.generator = p_191819_;
      this.topFeature = p_191820_;
   }

   public int getHeight(Heightmap.Types p_191825_, int p_191826_, int p_191827_) {
      return this.level.getHeight(p_191825_, p_191826_, p_191827_);
   }

   public CarvingMask getCarvingMask(ChunkPos p_191822_, GenerationStep.Carving p_191823_) {
      return ((ProtoChunk)this.level.getChunk(p_191822_.x, p_191822_.z)).getOrCreateCarvingMask(p_191823_);
   }

   public BlockState getBlockState(BlockPos p_191829_) {
      return this.level.getBlockState(p_191829_);
   }

   public int getMinBuildHeight() {
      return this.level.getMinBuildHeight();
   }

   public WorldGenLevel getLevel() {
      return this.level;
   }

   public Optional<PlacedFeature> topFeature() {
      return this.topFeature;
   }

   public ChunkGenerator generator() {
      return this.generator;
   }
}