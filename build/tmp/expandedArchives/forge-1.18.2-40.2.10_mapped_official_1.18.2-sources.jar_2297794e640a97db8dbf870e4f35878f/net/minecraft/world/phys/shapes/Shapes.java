package net.minecraft.world.phys.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public final class Shapes {
   public static final double EPSILON = 1.0E-7D;
   public static final double BIG_EPSILON = 1.0E-6D;
   private static final VoxelShape BLOCK = Util.make(() -> {
      DiscreteVoxelShape discretevoxelshape = new BitSetDiscreteVoxelShape(1, 1, 1);
      discretevoxelshape.fill(0, 0, 0);
      return new CubeVoxelShape(discretevoxelshape);
   });
   public static final VoxelShape INFINITY = box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
   private static final VoxelShape EMPTY = new ArrayVoxelShape(new BitSetDiscreteVoxelShape(0, 0, 0), (DoubleList)(new DoubleArrayList(new double[]{0.0D})), (DoubleList)(new DoubleArrayList(new double[]{0.0D})), (DoubleList)(new DoubleArrayList(new double[]{0.0D})));

   public static VoxelShape empty() {
      return EMPTY;
   }

   public static VoxelShape block() {
      return BLOCK;
   }

   public static VoxelShape box(double p_83049_, double p_83050_, double p_83051_, double p_83052_, double p_83053_, double p_83054_) {
      if (!(p_83049_ > p_83052_) && !(p_83050_ > p_83053_) && !(p_83051_ > p_83054_)) {
         return create(p_83049_, p_83050_, p_83051_, p_83052_, p_83053_, p_83054_);
      } else {
         throw new IllegalArgumentException("The min values need to be smaller or equals to the max values");
      }
   }

   public static VoxelShape create(double p_166050_, double p_166051_, double p_166052_, double p_166053_, double p_166054_, double p_166055_) {
      if (!(p_166053_ - p_166050_ < 1.0E-7D) && !(p_166054_ - p_166051_ < 1.0E-7D) && !(p_166055_ - p_166052_ < 1.0E-7D)) {
         int i = findBits(p_166050_, p_166053_);
         int j = findBits(p_166051_, p_166054_);
         int k = findBits(p_166052_, p_166055_);
         if (i >= 0 && j >= 0 && k >= 0) {
            if (i == 0 && j == 0 && k == 0) {
               return block();
            } else {
               int l = 1 << i;
               int i1 = 1 << j;
               int j1 = 1 << k;
               BitSetDiscreteVoxelShape bitsetdiscretevoxelshape = BitSetDiscreteVoxelShape.withFilledBounds(l, i1, j1, (int)Math.round(p_166050_ * (double)l), (int)Math.round(p_166051_ * (double)i1), (int)Math.round(p_166052_ * (double)j1), (int)Math.round(p_166053_ * (double)l), (int)Math.round(p_166054_ * (double)i1), (int)Math.round(p_166055_ * (double)j1));
               return new CubeVoxelShape(bitsetdiscretevoxelshape);
            }
         } else {
            return new ArrayVoxelShape(BLOCK.shape, (DoubleList)DoubleArrayList.wrap(new double[]{p_166050_, p_166053_}), (DoubleList)DoubleArrayList.wrap(new double[]{p_166051_, p_166054_}), (DoubleList)DoubleArrayList.wrap(new double[]{p_166052_, p_166055_}));
         }
      } else {
         return empty();
      }
   }

   public static VoxelShape create(AABB p_83065_) {
      return create(p_83065_.minX, p_83065_.minY, p_83065_.minZ, p_83065_.maxX, p_83065_.maxY, p_83065_.maxZ);
   }

   @VisibleForTesting
   protected static int findBits(double p_83042_, double p_83043_) {
      if (!(p_83042_ < -1.0E-7D) && !(p_83043_ > 1.0000001D)) {
         for(int i = 0; i <= 3; ++i) {
            int j = 1 << i;
            double d0 = p_83042_ * (double)j;
            double d1 = p_83043_ * (double)j;
            boolean flag = Math.abs(d0 - (double)Math.round(d0)) < 1.0E-7D * (double)j;
            boolean flag1 = Math.abs(d1 - (double)Math.round(d1)) < 1.0E-7D * (double)j;
            if (flag && flag1) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   protected static long lcm(int p_83056_, int p_83057_) {
      return (long)p_83056_ * (long)(p_83057_ / IntMath.gcd(p_83056_, p_83057_));
   }

   public static VoxelShape or(VoxelShape p_83111_, VoxelShape p_83112_) {
      return join(p_83111_, p_83112_, BooleanOp.OR);
   }

   public static VoxelShape or(VoxelShape p_83125_, VoxelShape... p_83126_) {
      return Arrays.stream(p_83126_).reduce(p_83125_, Shapes::or);
   }

   public static VoxelShape join(VoxelShape p_83114_, VoxelShape p_83115_, BooleanOp p_83116_) {
      return joinUnoptimized(p_83114_, p_83115_, p_83116_).optimize();
   }

   public static VoxelShape joinUnoptimized(VoxelShape p_83149_, VoxelShape p_83150_, BooleanOp p_83151_) {
      if (p_83151_.apply(false, false)) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException());
      } else if (p_83149_ == p_83150_) {
         return p_83151_.apply(true, true) ? p_83149_ : empty();
      } else {
         boolean flag = p_83151_.apply(true, false);
         boolean flag1 = p_83151_.apply(false, true);
         if (p_83149_.isEmpty()) {
            return flag1 ? p_83150_ : empty();
         } else if (p_83150_.isEmpty()) {
            return flag ? p_83149_ : empty();
         } else {
            IndexMerger indexmerger = createIndexMerger(1, p_83149_.getCoords(Direction.Axis.X), p_83150_.getCoords(Direction.Axis.X), flag, flag1);
            IndexMerger indexmerger1 = createIndexMerger(indexmerger.size() - 1, p_83149_.getCoords(Direction.Axis.Y), p_83150_.getCoords(Direction.Axis.Y), flag, flag1);
            IndexMerger indexmerger2 = createIndexMerger((indexmerger.size() - 1) * (indexmerger1.size() - 1), p_83149_.getCoords(Direction.Axis.Z), p_83150_.getCoords(Direction.Axis.Z), flag, flag1);
            BitSetDiscreteVoxelShape bitsetdiscretevoxelshape = BitSetDiscreteVoxelShape.join(p_83149_.shape, p_83150_.shape, indexmerger, indexmerger1, indexmerger2, p_83151_);
            return (VoxelShape)(indexmerger instanceof DiscreteCubeMerger && indexmerger1 instanceof DiscreteCubeMerger && indexmerger2 instanceof DiscreteCubeMerger ? new CubeVoxelShape(bitsetdiscretevoxelshape) : new ArrayVoxelShape(bitsetdiscretevoxelshape, indexmerger.getList(), indexmerger1.getList(), indexmerger2.getList()));
         }
      }
   }

   public static boolean joinIsNotEmpty(VoxelShape p_83158_, VoxelShape p_83159_, BooleanOp p_83160_) {
      if (p_83160_.apply(false, false)) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException());
      } else {
         boolean flag = p_83158_.isEmpty();
         boolean flag1 = p_83159_.isEmpty();
         if (!flag && !flag1) {
            if (p_83158_ == p_83159_) {
               return p_83160_.apply(true, true);
            } else {
               boolean flag2 = p_83160_.apply(true, false);
               boolean flag3 = p_83160_.apply(false, true);

               for(Direction.Axis direction$axis : AxisCycle.AXIS_VALUES) {
                  if (p_83158_.max(direction$axis) < p_83159_.min(direction$axis) - 1.0E-7D) {
                     return flag2 || flag3;
                  }

                  if (p_83159_.max(direction$axis) < p_83158_.min(direction$axis) - 1.0E-7D) {
                     return flag2 || flag3;
                  }
               }

               IndexMerger indexmerger = createIndexMerger(1, p_83158_.getCoords(Direction.Axis.X), p_83159_.getCoords(Direction.Axis.X), flag2, flag3);
               IndexMerger indexmerger1 = createIndexMerger(indexmerger.size() - 1, p_83158_.getCoords(Direction.Axis.Y), p_83159_.getCoords(Direction.Axis.Y), flag2, flag3);
               IndexMerger indexmerger2 = createIndexMerger((indexmerger.size() - 1) * (indexmerger1.size() - 1), p_83158_.getCoords(Direction.Axis.Z), p_83159_.getCoords(Direction.Axis.Z), flag2, flag3);
               return joinIsNotEmpty(indexmerger, indexmerger1, indexmerger2, p_83158_.shape, p_83159_.shape, p_83160_);
            }
         } else {
            return p_83160_.apply(!flag, !flag1);
         }
      }
   }

   private static boolean joinIsNotEmpty(IndexMerger p_83104_, IndexMerger p_83105_, IndexMerger p_83106_, DiscreteVoxelShape p_83107_, DiscreteVoxelShape p_83108_, BooleanOp p_83109_) {
      return !p_83104_.forMergedIndexes((p_83100_, p_83101_, p_83102_) -> {
         return p_83105_.forMergedIndexes((p_166046_, p_166047_, p_166048_) -> {
            return p_83106_.forMergedIndexes((p_166036_, p_166037_, p_166038_) -> {
               return !p_83109_.apply(p_83107_.isFullWide(p_83100_, p_166046_, p_166036_), p_83108_.isFullWide(p_83101_, p_166047_, p_166037_));
            });
         });
      });
   }

   public static double collide(Direction.Axis p_193136_, AABB p_193137_, Iterable<VoxelShape> p_193138_, double p_193139_) {
      for(VoxelShape voxelshape : p_193138_) {
         if (Math.abs(p_193139_) < 1.0E-7D) {
            return 0.0D;
         }

         p_193139_ = voxelshape.collide(p_193136_, p_193137_, p_193139_);
      }

      return p_193139_;
   }

   public static boolean blockOccudes(VoxelShape p_83118_, VoxelShape p_83119_, Direction p_83120_) {
      if (p_83118_ == block() && p_83119_ == block()) {
         return true;
      } else if (p_83119_.isEmpty()) {
         return false;
      } else {
         Direction.Axis direction$axis = p_83120_.getAxis();
         Direction.AxisDirection direction$axisdirection = p_83120_.getAxisDirection();
         VoxelShape voxelshape = direction$axisdirection == Direction.AxisDirection.POSITIVE ? p_83118_ : p_83119_;
         VoxelShape voxelshape1 = direction$axisdirection == Direction.AxisDirection.POSITIVE ? p_83119_ : p_83118_;
         BooleanOp booleanop = direction$axisdirection == Direction.AxisDirection.POSITIVE ? BooleanOp.ONLY_FIRST : BooleanOp.ONLY_SECOND;
         return DoubleMath.fuzzyEquals(voxelshape.max(direction$axis), 1.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(voxelshape1.min(direction$axis), 0.0D, 1.0E-7D) && !joinIsNotEmpty(new SliceShape(voxelshape, direction$axis, voxelshape.shape.getSize(direction$axis) - 1), new SliceShape(voxelshape1, direction$axis, 0), booleanop);
      }
   }

   public static VoxelShape getFaceShape(VoxelShape p_83122_, Direction p_83123_) {
      if (p_83122_ == block()) {
         return block();
      } else {
         Direction.Axis direction$axis = p_83123_.getAxis();
         boolean flag;
         int i;
         if (p_83123_.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            flag = DoubleMath.fuzzyEquals(p_83122_.max(direction$axis), 1.0D, 1.0E-7D);
            i = p_83122_.shape.getSize(direction$axis) - 1;
         } else {
            flag = DoubleMath.fuzzyEquals(p_83122_.min(direction$axis), 0.0D, 1.0E-7D);
            i = 0;
         }

         return (VoxelShape)(!flag ? empty() : new SliceShape(p_83122_, direction$axis, i));
      }
   }

   public static boolean mergedFaceOccludes(VoxelShape p_83153_, VoxelShape p_83154_, Direction p_83155_) {
      if (p_83153_ != block() && p_83154_ != block()) {
         Direction.Axis direction$axis = p_83155_.getAxis();
         Direction.AxisDirection direction$axisdirection = p_83155_.getAxisDirection();
         VoxelShape voxelshape = direction$axisdirection == Direction.AxisDirection.POSITIVE ? p_83153_ : p_83154_;
         VoxelShape voxelshape1 = direction$axisdirection == Direction.AxisDirection.POSITIVE ? p_83154_ : p_83153_;
         if (!DoubleMath.fuzzyEquals(voxelshape.max(direction$axis), 1.0D, 1.0E-7D)) {
            voxelshape = empty();
         }

         if (!DoubleMath.fuzzyEquals(voxelshape1.min(direction$axis), 0.0D, 1.0E-7D)) {
            voxelshape1 = empty();
         }

         return !joinIsNotEmpty(block(), joinUnoptimized(new SliceShape(voxelshape, direction$axis, voxelshape.shape.getSize(direction$axis) - 1), new SliceShape(voxelshape1, direction$axis, 0), BooleanOp.OR), BooleanOp.ONLY_FIRST);
      } else {
         return true;
      }
   }

   public static boolean faceShapeOccludes(VoxelShape p_83146_, VoxelShape p_83147_) {
      if (p_83146_ != block() && p_83147_ != block()) {
         if (p_83146_.isEmpty() && p_83147_.isEmpty()) {
            return false;
         } else {
            return !joinIsNotEmpty(block(), joinUnoptimized(p_83146_, p_83147_, BooleanOp.OR), BooleanOp.ONLY_FIRST);
         }
      } else {
         return true;
      }
   }

   @VisibleForTesting
   protected static IndexMerger createIndexMerger(int p_83059_, DoubleList p_83060_, DoubleList p_83061_, boolean p_83062_, boolean p_83063_) {
      int i = p_83060_.size() - 1;
      int j = p_83061_.size() - 1;
      if (p_83060_ instanceof CubePointRange && p_83061_ instanceof CubePointRange) {
         long k = lcm(i, j);
         if ((long)p_83059_ * k <= 256L) {
            return new DiscreteCubeMerger(i, j);
         }
      }

      if (p_83060_.getDouble(i) < p_83061_.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(p_83060_, p_83061_, false);
      } else if (p_83061_.getDouble(j) < p_83060_.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(p_83061_, p_83060_, true);
      } else {
         return (IndexMerger)(i == j && Objects.equals(p_83060_, p_83061_) ? new IdenticalMerger(p_83060_) : new IndirectMerger(p_83060_, p_83061_, p_83062_, p_83063_));
      }
   }

   public interface DoubleLineConsumer {
      void consume(double p_83162_, double p_83163_, double p_83164_, double p_83165_, double p_83166_, double p_83167_);
   }
}