package net.minecraft.world.phys.shapes;

import java.util.BitSet;
import net.minecraft.core.Direction;

public final class BitSetDiscreteVoxelShape extends DiscreteVoxelShape {
   private final BitSet storage;
   private int xMin;
   private int yMin;
   private int zMin;
   private int xMax;
   private int yMax;
   private int zMax;

   public BitSetDiscreteVoxelShape(int p_82588_, int p_82589_, int p_82590_) {
      super(p_82588_, p_82589_, p_82590_);
      this.storage = new BitSet(p_82588_ * p_82589_ * p_82590_);
      this.xMin = p_82588_;
      this.yMin = p_82589_;
      this.zMin = p_82590_;
   }

   public static BitSetDiscreteVoxelShape withFilledBounds(int p_165933_, int p_165934_, int p_165935_, int p_165936_, int p_165937_, int p_165938_, int p_165939_, int p_165940_, int p_165941_) {
      BitSetDiscreteVoxelShape bitsetdiscretevoxelshape = new BitSetDiscreteVoxelShape(p_165933_, p_165934_, p_165935_);
      bitsetdiscretevoxelshape.xMin = p_165936_;
      bitsetdiscretevoxelshape.yMin = p_165937_;
      bitsetdiscretevoxelshape.zMin = p_165938_;
      bitsetdiscretevoxelshape.xMax = p_165939_;
      bitsetdiscretevoxelshape.yMax = p_165940_;
      bitsetdiscretevoxelshape.zMax = p_165941_;

      for(int i = p_165936_; i < p_165939_; ++i) {
         for(int j = p_165937_; j < p_165940_; ++j) {
            for(int k = p_165938_; k < p_165941_; ++k) {
               bitsetdiscretevoxelshape.fillUpdateBounds(i, j, k, false);
            }
         }
      }

      return bitsetdiscretevoxelshape;
   }

   public BitSetDiscreteVoxelShape(DiscreteVoxelShape p_82602_) {
      super(p_82602_.xSize, p_82602_.ySize, p_82602_.zSize);
      if (p_82602_ instanceof BitSetDiscreteVoxelShape) {
         this.storage = (BitSet)((BitSetDiscreteVoxelShape)p_82602_).storage.clone();
      } else {
         this.storage = new BitSet(this.xSize * this.ySize * this.zSize);

         for(int i = 0; i < this.xSize; ++i) {
            for(int j = 0; j < this.ySize; ++j) {
               for(int k = 0; k < this.zSize; ++k) {
                  if (p_82602_.isFull(i, j, k)) {
                     this.storage.set(this.getIndex(i, j, k));
                  }
               }
            }
         }
      }

      this.xMin = p_82602_.firstFull(Direction.Axis.X);
      this.yMin = p_82602_.firstFull(Direction.Axis.Y);
      this.zMin = p_82602_.firstFull(Direction.Axis.Z);
      this.xMax = p_82602_.lastFull(Direction.Axis.X);
      this.yMax = p_82602_.lastFull(Direction.Axis.Y);
      this.zMax = p_82602_.lastFull(Direction.Axis.Z);
   }

   protected int getIndex(int p_82605_, int p_82606_, int p_82607_) {
      return (p_82605_ * this.ySize + p_82606_) * this.zSize + p_82607_;
   }

   public boolean isFull(int p_82676_, int p_82677_, int p_82678_) {
      return this.storage.get(this.getIndex(p_82676_, p_82677_, p_82678_));
   }

   private void fillUpdateBounds(int p_165943_, int p_165944_, int p_165945_, boolean p_165946_) {
      this.storage.set(this.getIndex(p_165943_, p_165944_, p_165945_));
      if (p_165946_) {
         this.xMin = Math.min(this.xMin, p_165943_);
         this.yMin = Math.min(this.yMin, p_165944_);
         this.zMin = Math.min(this.zMin, p_165945_);
         this.xMax = Math.max(this.xMax, p_165943_ + 1);
         this.yMax = Math.max(this.yMax, p_165944_ + 1);
         this.zMax = Math.max(this.zMax, p_165945_ + 1);
      }

   }

   public void fill(int p_165987_, int p_165988_, int p_165989_) {
      this.fillUpdateBounds(p_165987_, p_165988_, p_165989_, true);
   }

   public boolean isEmpty() {
      return this.storage.isEmpty();
   }

   public int firstFull(Direction.Axis p_82674_) {
      return p_82674_.choose(this.xMin, this.yMin, this.zMin);
   }

   public int lastFull(Direction.Axis p_82680_) {
      return p_82680_.choose(this.xMax, this.yMax, this.zMax);
   }

   static BitSetDiscreteVoxelShape join(DiscreteVoxelShape p_82642_, DiscreteVoxelShape p_82643_, IndexMerger p_82644_, IndexMerger p_82645_, IndexMerger p_82646_, BooleanOp p_82647_) {
      BitSetDiscreteVoxelShape bitsetdiscretevoxelshape = new BitSetDiscreteVoxelShape(p_82644_.size() - 1, p_82645_.size() - 1, p_82646_.size() - 1);
      int[] aint = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      p_82644_.forMergedIndexes((p_82670_, p_82671_, p_82672_) -> {
         boolean[] aboolean = new boolean[]{false};
         p_82645_.forMergedIndexes((p_165978_, p_165979_, p_165980_) -> {
            boolean[] aboolean1 = new boolean[]{false};
            p_82646_.forMergedIndexes((p_165960_, p_165961_, p_165962_) -> {
               if (p_82647_.apply(p_82642_.isFullWide(p_82670_, p_165978_, p_165960_), p_82643_.isFullWide(p_82671_, p_165979_, p_165961_))) {
                  bitsetdiscretevoxelshape.storage.set(bitsetdiscretevoxelshape.getIndex(p_82672_, p_165980_, p_165962_));
                  aint[2] = Math.min(aint[2], p_165962_);
                  aint[5] = Math.max(aint[5], p_165962_);
                  aboolean1[0] = true;
               }

               return true;
            });
            if (aboolean1[0]) {
               aint[1] = Math.min(aint[1], p_165980_);
               aint[4] = Math.max(aint[4], p_165980_);
               aboolean[0] = true;
            }

            return true;
         });
         if (aboolean[0]) {
            aint[0] = Math.min(aint[0], p_82672_);
            aint[3] = Math.max(aint[3], p_82672_);
         }

         return true;
      });
      bitsetdiscretevoxelshape.xMin = aint[0];
      bitsetdiscretevoxelshape.yMin = aint[1];
      bitsetdiscretevoxelshape.zMin = aint[2];
      bitsetdiscretevoxelshape.xMax = aint[3] + 1;
      bitsetdiscretevoxelshape.yMax = aint[4] + 1;
      bitsetdiscretevoxelshape.zMax = aint[5] + 1;
      return bitsetdiscretevoxelshape;
   }

   protected static void forAllBoxes(DiscreteVoxelShape p_165964_, DiscreteVoxelShape.IntLineConsumer p_165965_, boolean p_165966_) {
      BitSetDiscreteVoxelShape bitsetdiscretevoxelshape = new BitSetDiscreteVoxelShape(p_165964_);

      for(int i = 0; i < bitsetdiscretevoxelshape.ySize; ++i) {
         for(int j = 0; j < bitsetdiscretevoxelshape.xSize; ++j) {
            int k = -1;

            for(int l = 0; l <= bitsetdiscretevoxelshape.zSize; ++l) {
               if (bitsetdiscretevoxelshape.isFullWide(j, i, l)) {
                  if (p_165966_) {
                     if (k == -1) {
                        k = l;
                     }
                  } else {
                     p_165965_.consume(j, i, l, j + 1, i + 1, l + 1);
                  }
               } else if (k != -1) {
                  int i1 = j;
                  int j1 = i;
                  bitsetdiscretevoxelshape.clearZStrip(k, l, j, i);

                  while(bitsetdiscretevoxelshape.isZStripFull(k, l, i1 + 1, i)) {
                     bitsetdiscretevoxelshape.clearZStrip(k, l, i1 + 1, i);
                     ++i1;
                  }

                  while(bitsetdiscretevoxelshape.isXZRectangleFull(j, i1 + 1, k, l, j1 + 1)) {
                     for(int k1 = j; k1 <= i1; ++k1) {
                        bitsetdiscretevoxelshape.clearZStrip(k, l, k1, j1 + 1);
                     }

                     ++j1;
                  }

                  p_165965_.consume(j, i, k, i1 + 1, j1 + 1, l);
                  k = -1;
               }
            }
         }
      }

   }

   private boolean isZStripFull(int p_82609_, int p_82610_, int p_82611_, int p_82612_) {
      if (p_82611_ < this.xSize && p_82612_ < this.ySize) {
         return this.storage.nextClearBit(this.getIndex(p_82611_, p_82612_, p_82609_)) >= this.getIndex(p_82611_, p_82612_, p_82610_);
      } else {
         return false;
      }
   }

   private boolean isXZRectangleFull(int p_165927_, int p_165928_, int p_165929_, int p_165930_, int p_165931_) {
      for(int i = p_165927_; i < p_165928_; ++i) {
         if (!this.isZStripFull(p_165929_, p_165930_, i, p_165931_)) {
            return false;
         }
      }

      return true;
   }

   private void clearZStrip(int p_165982_, int p_165983_, int p_165984_, int p_165985_) {
      this.storage.clear(this.getIndex(p_165984_, p_165985_, p_165982_), this.getIndex(p_165984_, p_165985_, p_165983_));
   }
}