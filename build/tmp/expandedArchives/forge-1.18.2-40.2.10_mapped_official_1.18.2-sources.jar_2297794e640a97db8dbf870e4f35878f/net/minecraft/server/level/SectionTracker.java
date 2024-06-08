package net.minecraft.server.level;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;

public abstract class SectionTracker extends DynamicGraphMinFixedPoint {
   protected SectionTracker(int p_8274_, int p_8275_, int p_8276_) {
      super(p_8274_, p_8275_, p_8276_);
   }

   protected boolean isSource(long p_8278_) {
      return p_8278_ == Long.MAX_VALUE;
   }

   protected void checkNeighborsAfterUpdate(long p_8280_, int p_8281_, boolean p_8282_) {
      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               long l = SectionPos.offset(p_8280_, i, j, k);
               if (l != p_8280_) {
                  this.checkNeighbor(p_8280_, l, p_8281_, p_8282_);
               }
            }
         }
      }

   }

   protected int getComputedLevel(long p_8284_, long p_8285_, int p_8286_) {
      int i = p_8286_;

      for(int j = -1; j <= 1; ++j) {
         for(int k = -1; k <= 1; ++k) {
            for(int l = -1; l <= 1; ++l) {
               long i1 = SectionPos.offset(p_8284_, j, k, l);
               if (i1 == p_8284_) {
                  i1 = Long.MAX_VALUE;
               }

               if (i1 != p_8285_) {
                  int j1 = this.computeLevelFromNeighbor(i1, p_8284_, this.getLevel(i1));
                  if (i > j1) {
                     i = j1;
                  }

                  if (i == 0) {
                     return i;
                  }
               }
            }
         }
      }

      return i;
   }

   public int computeLevelFromNeighbor(long p_8293_, long p_8294_, int p_8295_) {
      return p_8293_ == Long.MAX_VALUE ? this.getLevelFromSource(p_8294_) : p_8295_ + 1;
   }

   protected abstract int getLevelFromSource(long p_8287_);

   public void update(long p_8289_, int p_8290_, boolean p_8291_) {
      this.checkEdge(Long.MAX_VALUE, p_8289_, p_8290_, p_8291_);
   }
}