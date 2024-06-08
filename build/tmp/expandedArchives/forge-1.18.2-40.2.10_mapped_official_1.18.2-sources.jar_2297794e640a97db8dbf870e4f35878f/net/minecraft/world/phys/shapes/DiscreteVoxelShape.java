package net.minecraft.world.phys.shapes;

import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;

public abstract class DiscreteVoxelShape {
   private static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
   protected final int xSize;
   protected final int ySize;
   protected final int zSize;

   protected DiscreteVoxelShape(int p_82787_, int p_82788_, int p_82789_) {
      if (p_82787_ >= 0 && p_82788_ >= 0 && p_82789_ >= 0) {
         this.xSize = p_82787_;
         this.ySize = p_82788_;
         this.zSize = p_82789_;
      } else {
         throw new IllegalArgumentException("Need all positive sizes: x: " + p_82787_ + ", y: " + p_82788_ + ", z: " + p_82789_);
      }
   }

   public boolean isFullWide(AxisCycle p_82823_, int p_82824_, int p_82825_, int p_82826_) {
      return this.isFullWide(p_82823_.cycle(p_82824_, p_82825_, p_82826_, Direction.Axis.X), p_82823_.cycle(p_82824_, p_82825_, p_82826_, Direction.Axis.Y), p_82823_.cycle(p_82824_, p_82825_, p_82826_, Direction.Axis.Z));
   }

   public boolean isFullWide(int p_82847_, int p_82848_, int p_82849_) {
      if (p_82847_ >= 0 && p_82848_ >= 0 && p_82849_ >= 0) {
         return p_82847_ < this.xSize && p_82848_ < this.ySize && p_82849_ < this.zSize ? this.isFull(p_82847_, p_82848_, p_82849_) : false;
      } else {
         return false;
      }
   }

   public boolean isFull(AxisCycle p_82836_, int p_82837_, int p_82838_, int p_82839_) {
      return this.isFull(p_82836_.cycle(p_82837_, p_82838_, p_82839_, Direction.Axis.X), p_82836_.cycle(p_82837_, p_82838_, p_82839_, Direction.Axis.Y), p_82836_.cycle(p_82837_, p_82838_, p_82839_, Direction.Axis.Z));
   }

   public abstract boolean isFull(int p_82829_, int p_82830_, int p_82831_);

   public abstract void fill(int p_165998_, int p_165999_, int p_166000_);

   public boolean isEmpty() {
      for(Direction.Axis direction$axis : AXIS_VALUES) {
         if (this.firstFull(direction$axis) >= this.lastFull(direction$axis)) {
            return true;
         }
      }

      return false;
   }

   public abstract int firstFull(Direction.Axis p_82827_);

   public abstract int lastFull(Direction.Axis p_82840_);

   public int firstFull(Direction.Axis p_165995_, int p_165996_, int p_165997_) {
      int i = this.getSize(p_165995_);
      if (p_165996_ >= 0 && p_165997_ >= 0) {
         Direction.Axis direction$axis = AxisCycle.FORWARD.cycle(p_165995_);
         Direction.Axis direction$axis1 = AxisCycle.BACKWARD.cycle(p_165995_);
         if (p_165996_ < this.getSize(direction$axis) && p_165997_ < this.getSize(direction$axis1)) {
            AxisCycle axiscycle = AxisCycle.between(Direction.Axis.X, p_165995_);

            for(int j = 0; j < i; ++j) {
               if (this.isFull(axiscycle, j, p_165996_, p_165997_)) {
                  return j;
               }
            }

            return i;
         } else {
            return i;
         }
      } else {
         return i;
      }
   }

   public int lastFull(Direction.Axis p_82842_, int p_82843_, int p_82844_) {
      if (p_82843_ >= 0 && p_82844_ >= 0) {
         Direction.Axis direction$axis = AxisCycle.FORWARD.cycle(p_82842_);
         Direction.Axis direction$axis1 = AxisCycle.BACKWARD.cycle(p_82842_);
         if (p_82843_ < this.getSize(direction$axis) && p_82844_ < this.getSize(direction$axis1)) {
            int i = this.getSize(p_82842_);
            AxisCycle axiscycle = AxisCycle.between(Direction.Axis.X, p_82842_);

            for(int j = i - 1; j >= 0; --j) {
               if (this.isFull(axiscycle, j, p_82843_, p_82844_)) {
                  return j + 1;
               }
            }

            return 0;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public int getSize(Direction.Axis p_82851_) {
      return p_82851_.choose(this.xSize, this.ySize, this.zSize);
   }

   public int getXSize() {
      return this.getSize(Direction.Axis.X);
   }

   public int getYSize() {
      return this.getSize(Direction.Axis.Y);
   }

   public int getZSize() {
      return this.getSize(Direction.Axis.Z);
   }

   public void forAllEdges(DiscreteVoxelShape.IntLineConsumer p_82820_, boolean p_82821_) {
      this.forAllAxisEdges(p_82820_, AxisCycle.NONE, p_82821_);
      this.forAllAxisEdges(p_82820_, AxisCycle.FORWARD, p_82821_);
      this.forAllAxisEdges(p_82820_, AxisCycle.BACKWARD, p_82821_);
   }

   private void forAllAxisEdges(DiscreteVoxelShape.IntLineConsumer p_82816_, AxisCycle p_82817_, boolean p_82818_) {
      AxisCycle axiscycle = p_82817_.inverse();
      int j = this.getSize(axiscycle.cycle(Direction.Axis.X));
      int k = this.getSize(axiscycle.cycle(Direction.Axis.Y));
      int l = this.getSize(axiscycle.cycle(Direction.Axis.Z));

      for(int i1 = 0; i1 <= j; ++i1) {
         for(int j1 = 0; j1 <= k; ++j1) {
            int i = -1;

            for(int k1 = 0; k1 <= l; ++k1) {
               int l1 = 0;
               int i2 = 0;

               for(int j2 = 0; j2 <= 1; ++j2) {
                  for(int k2 = 0; k2 <= 1; ++k2) {
                     if (this.isFullWide(axiscycle, i1 + j2 - 1, j1 + k2 - 1, k1)) {
                        ++l1;
                        i2 ^= j2 ^ k2;
                     }
                  }
               }

               if (l1 == 1 || l1 == 3 || l1 == 2 && (i2 & 1) == 0) {
                  if (p_82818_) {
                     if (i == -1) {
                        i = k1;
                     }
                  } else {
                     p_82816_.consume(axiscycle.cycle(i1, j1, k1, Direction.Axis.X), axiscycle.cycle(i1, j1, k1, Direction.Axis.Y), axiscycle.cycle(i1, j1, k1, Direction.Axis.Z), axiscycle.cycle(i1, j1, k1 + 1, Direction.Axis.X), axiscycle.cycle(i1, j1, k1 + 1, Direction.Axis.Y), axiscycle.cycle(i1, j1, k1 + 1, Direction.Axis.Z));
                  }
               } else if (i != -1) {
                  p_82816_.consume(axiscycle.cycle(i1, j1, i, Direction.Axis.X), axiscycle.cycle(i1, j1, i, Direction.Axis.Y), axiscycle.cycle(i1, j1, i, Direction.Axis.Z), axiscycle.cycle(i1, j1, k1, Direction.Axis.X), axiscycle.cycle(i1, j1, k1, Direction.Axis.Y), axiscycle.cycle(i1, j1, k1, Direction.Axis.Z));
                  i = -1;
               }
            }
         }
      }

   }

   public void forAllBoxes(DiscreteVoxelShape.IntLineConsumer p_82833_, boolean p_82834_) {
      BitSetDiscreteVoxelShape.forAllBoxes(this, p_82833_, p_82834_);
   }

   public void forAllFaces(DiscreteVoxelShape.IntFaceConsumer p_82811_) {
      this.forAllAxisFaces(p_82811_, AxisCycle.NONE);
      this.forAllAxisFaces(p_82811_, AxisCycle.FORWARD);
      this.forAllAxisFaces(p_82811_, AxisCycle.BACKWARD);
   }

   private void forAllAxisFaces(DiscreteVoxelShape.IntFaceConsumer p_82813_, AxisCycle p_82814_) {
      AxisCycle axiscycle = p_82814_.inverse();
      Direction.Axis direction$axis = axiscycle.cycle(Direction.Axis.Z);
      int i = this.getSize(axiscycle.cycle(Direction.Axis.X));
      int j = this.getSize(axiscycle.cycle(Direction.Axis.Y));
      int k = this.getSize(direction$axis);
      Direction direction = Direction.fromAxisAndDirection(direction$axis, Direction.AxisDirection.NEGATIVE);
      Direction direction1 = Direction.fromAxisAndDirection(direction$axis, Direction.AxisDirection.POSITIVE);

      for(int l = 0; l < i; ++l) {
         for(int i1 = 0; i1 < j; ++i1) {
            boolean flag = false;

            for(int j1 = 0; j1 <= k; ++j1) {
               boolean flag1 = j1 != k && this.isFull(axiscycle, l, i1, j1);
               if (!flag && flag1) {
                  p_82813_.consume(direction, axiscycle.cycle(l, i1, j1, Direction.Axis.X), axiscycle.cycle(l, i1, j1, Direction.Axis.Y), axiscycle.cycle(l, i1, j1, Direction.Axis.Z));
               }

               if (flag && !flag1) {
                  p_82813_.consume(direction1, axiscycle.cycle(l, i1, j1 - 1, Direction.Axis.X), axiscycle.cycle(l, i1, j1 - 1, Direction.Axis.Y), axiscycle.cycle(l, i1, j1 - 1, Direction.Axis.Z));
               }

               flag = flag1;
            }
         }
      }

   }

   public interface IntFaceConsumer {
      void consume(Direction p_82854_, int p_82855_, int p_82856_, int p_82857_);
   }

   public interface IntLineConsumer {
      void consume(int p_82859_, int p_82860_, int p_82861_, int p_82862_, int p_82863_, int p_82864_);
   }
}