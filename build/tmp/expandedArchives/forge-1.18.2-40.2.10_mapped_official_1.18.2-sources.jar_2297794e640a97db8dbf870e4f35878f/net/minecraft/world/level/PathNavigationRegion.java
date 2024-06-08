package net.minecraft.world.level;

import com.google.common.base.Suppliers;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PathNavigationRegion implements BlockGetter, CollisionGetter {
   protected final int centerX;
   protected final int centerZ;
   protected final ChunkAccess[][] chunks;
   protected boolean allEmpty;
   protected final Level level;
   private final Supplier<Holder<Biome>> plains;

   public PathNavigationRegion(Level p_47164_, BlockPos p_47165_, BlockPos p_47166_) {
      this.level = p_47164_;
      this.plains = Suppliers.memoize(() -> {
         return p_47164_.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getHolderOrThrow(Biomes.PLAINS);
      });
      this.centerX = SectionPos.blockToSectionCoord(p_47165_.getX());
      this.centerZ = SectionPos.blockToSectionCoord(p_47165_.getZ());
      int i = SectionPos.blockToSectionCoord(p_47166_.getX());
      int j = SectionPos.blockToSectionCoord(p_47166_.getZ());
      this.chunks = new ChunkAccess[i - this.centerX + 1][j - this.centerZ + 1];
      ChunkSource chunksource = p_47164_.getChunkSource();
      this.allEmpty = true;

      for(int k = this.centerX; k <= i; ++k) {
         for(int l = this.centerZ; l <= j; ++l) {
            this.chunks[k - this.centerX][l - this.centerZ] = chunksource.getChunkNow(k, l);
         }
      }

      for(int i1 = SectionPos.blockToSectionCoord(p_47165_.getX()); i1 <= SectionPos.blockToSectionCoord(p_47166_.getX()); ++i1) {
         for(int j1 = SectionPos.blockToSectionCoord(p_47165_.getZ()); j1 <= SectionPos.blockToSectionCoord(p_47166_.getZ()); ++j1) {
            ChunkAccess chunkaccess = this.chunks[i1 - this.centerX][j1 - this.centerZ];
            if (chunkaccess != null && !chunkaccess.isYSpaceEmpty(p_47165_.getY(), p_47166_.getY())) {
               this.allEmpty = false;
               return;
            }
         }
      }

   }

   private ChunkAccess getChunk(BlockPos p_47186_) {
      return this.getChunk(SectionPos.blockToSectionCoord(p_47186_.getX()), SectionPos.blockToSectionCoord(p_47186_.getZ()));
   }

   private ChunkAccess getChunk(int p_47168_, int p_47169_) {
      int i = p_47168_ - this.centerX;
      int j = p_47169_ - this.centerZ;
      if (i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length) {
         ChunkAccess chunkaccess = this.chunks[i][j];
         return (ChunkAccess)(chunkaccess != null ? chunkaccess : new EmptyLevelChunk(this.level, new ChunkPos(p_47168_, p_47169_), this.plains.get()));
      } else {
         return new EmptyLevelChunk(this.level, new ChunkPos(p_47168_, p_47169_), this.plains.get());
      }
   }

   public WorldBorder getWorldBorder() {
      return this.level.getWorldBorder();
   }

   public BlockGetter getChunkForCollisions(int p_47173_, int p_47174_) {
      return this.getChunk(p_47173_, p_47174_);
   }

   public List<VoxelShape> getEntityCollisions(@Nullable Entity p_186557_, AABB p_186558_) {
      return List.of();
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos p_47180_) {
      ChunkAccess chunkaccess = this.getChunk(p_47180_);
      return chunkaccess.getBlockEntity(p_47180_);
   }

   public BlockState getBlockState(BlockPos p_47188_) {
      if (this.isOutsideBuildHeight(p_47188_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         ChunkAccess chunkaccess = this.getChunk(p_47188_);
         return chunkaccess.getBlockState(p_47188_);
      }
   }

   public FluidState getFluidState(BlockPos p_47171_) {
      if (this.isOutsideBuildHeight(p_47171_)) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         ChunkAccess chunkaccess = this.getChunk(p_47171_);
         return chunkaccess.getFluidState(p_47171_);
      }
   }

   public int getMinBuildHeight() {
      return this.level.getMinBuildHeight();
   }

   public int getHeight() {
      return this.level.getHeight();
   }

   public ProfilerFiller getProfiler() {
      return this.level.getProfiler();
   }
}