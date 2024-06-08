package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VisGraph {
   private static final int SIZE_IN_BITS = 4;
   private static final int LEN = 16;
   private static final int MASK = 15;
   private static final int SIZE = 4096;
   private static final int X_SHIFT = 0;
   private static final int Z_SHIFT = 4;
   private static final int Y_SHIFT = 8;
   private static final int DX = (int)Math.pow(16.0D, 0.0D);
   private static final int DZ = (int)Math.pow(16.0D, 1.0D);
   private static final int DY = (int)Math.pow(16.0D, 2.0D);
   private static final int INVALID_INDEX = -1;
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BitSet bitSet = new BitSet(4096);
   private static final int[] INDEX_OF_EDGES = Util.make(new int[1352], (p_112974_) -> {
      int i = 0;
      int j = 15;
      int k = 0;

      for(int l = 0; l < 16; ++l) {
         for(int i1 = 0; i1 < 16; ++i1) {
            for(int j1 = 0; j1 < 16; ++j1) {
               if (l == 0 || l == 15 || i1 == 0 || i1 == 15 || j1 == 0 || j1 == 15) {
                  p_112974_[k++] = getIndex(l, i1, j1);
               }
            }
         }
      }

   });
   private int empty = 4096;

   public void setOpaque(BlockPos p_112972_) {
      this.bitSet.set(getIndex(p_112972_), true);
      --this.empty;
   }

   private static int getIndex(BlockPos p_112976_) {
      return getIndex(p_112976_.getX() & 15, p_112976_.getY() & 15, p_112976_.getZ() & 15);
   }

   private static int getIndex(int p_112962_, int p_112963_, int p_112964_) {
      return p_112962_ << 0 | p_112963_ << 8 | p_112964_ << 4;
   }

   public VisibilitySet resolve() {
      VisibilitySet visibilityset = new VisibilitySet();
      if (4096 - this.empty < 256) {
         visibilityset.setAll(true);
      } else if (this.empty == 0) {
         visibilityset.setAll(false);
      } else {
         for(int i : INDEX_OF_EDGES) {
            if (!this.bitSet.get(i)) {
               visibilityset.add(this.floodFill(i));
            }
         }
      }

      return visibilityset;
   }

   private Set<Direction> floodFill(int p_112960_) {
      Set<Direction> set = EnumSet.noneOf(Direction.class);
      IntPriorityQueue intpriorityqueue = new IntArrayFIFOQueue();
      intpriorityqueue.enqueue(p_112960_);
      this.bitSet.set(p_112960_, true);

      while(!intpriorityqueue.isEmpty()) {
         int i = intpriorityqueue.dequeueInt();
         this.addEdges(i, set);

         for(Direction direction : DIRECTIONS) {
            int j = this.getNeighborIndexAtFace(i, direction);
            if (j >= 0 && !this.bitSet.get(j)) {
               this.bitSet.set(j, true);
               intpriorityqueue.enqueue(j);
            }
         }
      }

      return set;
   }

   private void addEdges(int p_112969_, Set<Direction> p_112970_) {
      int i = p_112969_ >> 0 & 15;
      if (i == 0) {
         p_112970_.add(Direction.WEST);
      } else if (i == 15) {
         p_112970_.add(Direction.EAST);
      }

      int j = p_112969_ >> 8 & 15;
      if (j == 0) {
         p_112970_.add(Direction.DOWN);
      } else if (j == 15) {
         p_112970_.add(Direction.UP);
      }

      int k = p_112969_ >> 4 & 15;
      if (k == 0) {
         p_112970_.add(Direction.NORTH);
      } else if (k == 15) {
         p_112970_.add(Direction.SOUTH);
      }

   }

   private int getNeighborIndexAtFace(int p_112966_, Direction p_112967_) {
      switch(p_112967_) {
      case DOWN:
         if ((p_112966_ >> 8 & 15) == 0) {
            return -1;
         }

         return p_112966_ - DY;
      case UP:
         if ((p_112966_ >> 8 & 15) == 15) {
            return -1;
         }

         return p_112966_ + DY;
      case NORTH:
         if ((p_112966_ >> 4 & 15) == 0) {
            return -1;
         }

         return p_112966_ - DZ;
      case SOUTH:
         if ((p_112966_ >> 4 & 15) == 15) {
            return -1;
         }

         return p_112966_ + DZ;
      case WEST:
         if ((p_112966_ >> 0 & 15) == 0) {
            return -1;
         }

         return p_112966_ - DX;
      case EAST:
         if ((p_112966_ >> 0 & 15) == 15) {
            return -1;
         }

         return p_112966_ + DX;
      default:
         return -1;
      }
   }
}