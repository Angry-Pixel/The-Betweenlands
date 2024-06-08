package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BulkSectionAccess implements AutoCloseable {
   private final LevelAccessor level;
   private final Long2ObjectMap<LevelChunkSection> acquiredSections = new Long2ObjectOpenHashMap<>();
   @Nullable
   private LevelChunkSection lastSection;
   private long lastSectionKey;

   public BulkSectionAccess(LevelAccessor p_156103_) {
      this.level = p_156103_;
   }

   @Nullable
   public LevelChunkSection getSection(BlockPos p_156105_) {
      int i = this.level.getSectionIndex(p_156105_.getY());
      if (i >= 0 && i < this.level.getSectionsCount()) {
         long j = SectionPos.asLong(p_156105_);
         if (this.lastSection == null || this.lastSectionKey != j) {
            this.lastSection = this.acquiredSections.computeIfAbsent(j, (p_156109_) -> {
               ChunkAccess chunkaccess = this.level.getChunk(SectionPos.blockToSectionCoord(p_156105_.getX()), SectionPos.blockToSectionCoord(p_156105_.getZ()));
               LevelChunkSection levelchunksection = chunkaccess.getSection(i);
               levelchunksection.acquire();
               return levelchunksection;
            });
            this.lastSectionKey = j;
         }

         return this.lastSection;
      } else {
         return null;
      }
   }

   public BlockState getBlockState(BlockPos p_156111_) {
      LevelChunkSection levelchunksection = this.getSection(p_156111_);
      if (levelchunksection == null) {
         return Blocks.AIR.defaultBlockState();
      } else {
         int i = SectionPos.sectionRelative(p_156111_.getX());
         int j = SectionPos.sectionRelative(p_156111_.getY());
         int k = SectionPos.sectionRelative(p_156111_.getZ());
         return levelchunksection.getBlockState(i, j, k);
      }
   }

   public void close() {
      for(LevelChunkSection levelchunksection : this.acquiredSections.values()) {
         levelchunksection.release();
      }

   }
}