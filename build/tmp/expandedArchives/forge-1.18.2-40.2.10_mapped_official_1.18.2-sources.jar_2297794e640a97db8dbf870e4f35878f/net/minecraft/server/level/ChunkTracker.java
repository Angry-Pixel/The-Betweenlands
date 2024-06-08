package net.minecraft.server.level;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;

public abstract class ChunkTracker extends DynamicGraphMinFixedPoint {
   protected ChunkTracker(int p_140701_, int p_140702_, int p_140703_) {
      super(p_140701_, p_140702_, p_140703_);
   }

   protected boolean isSource(long p_140705_) {
      return p_140705_ == ChunkPos.INVALID_CHUNK_POS;
   }

   protected void checkNeighborsAfterUpdate(long p_140707_, int p_140708_, boolean p_140709_) {
      ChunkPos chunkpos = new ChunkPos(p_140707_);
      int i = chunkpos.x;
      int j = chunkpos.z;

      for(int k = -1; k <= 1; ++k) {
         for(int l = -1; l <= 1; ++l) {
            long i1 = ChunkPos.asLong(i + k, j + l);
            if (i1 != p_140707_) {
               this.checkNeighbor(p_140707_, i1, p_140708_, p_140709_);
            }
         }
      }

   }

   protected int getComputedLevel(long p_140711_, long p_140712_, int p_140713_) {
      int i = p_140713_;
      ChunkPos chunkpos = new ChunkPos(p_140711_);
      int j = chunkpos.x;
      int k = chunkpos.z;

      for(int l = -1; l <= 1; ++l) {
         for(int i1 = -1; i1 <= 1; ++i1) {
            long j1 = ChunkPos.asLong(j + l, k + i1);
            if (j1 == p_140711_) {
               j1 = ChunkPos.INVALID_CHUNK_POS;
            }

            if (j1 != p_140712_) {
               int k1 = this.computeLevelFromNeighbor(j1, p_140711_, this.getLevel(j1));
               if (i > k1) {
                  i = k1;
               }

               if (i == 0) {
                  return i;
               }
            }
         }
      }

      return i;
   }

   protected int computeLevelFromNeighbor(long p_140720_, long p_140721_, int p_140722_) {
      return p_140720_ == ChunkPos.INVALID_CHUNK_POS ? this.getLevelFromSource(p_140721_) : p_140722_ + 1;
   }

   protected abstract int getLevelFromSource(long p_140714_);

   public void update(long p_140716_, int p_140717_, boolean p_140718_) {
      this.checkEdge(ChunkPos.INVALID_CHUNK_POS, p_140716_, p_140717_, p_140718_);
   }
}