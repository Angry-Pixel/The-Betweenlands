package net.minecraft.world.level.levelgen;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.commons.lang3.mutable.MutableDouble;

public interface Aquifer {
   static Aquifer create(NoiseChunk p_208161_, ChunkPos p_208162_, DensityFunction p_208163_, DensityFunction p_208164_, DensityFunction p_208165_, DensityFunction p_208166_, PositionalRandomFactory p_208167_, int p_208168_, int p_208169_, Aquifer.FluidPicker p_208170_) {
      return new Aquifer.NoiseBasedAquifer(p_208161_, p_208162_, p_208163_, p_208164_, p_208165_, p_208166_, p_208167_, p_208168_, p_208169_, p_208170_);
   }

   static Aquifer createDisabled(final Aquifer.FluidPicker p_188375_) {
      return new Aquifer() {
         @Nullable
         public BlockState computeSubstance(DensityFunction.FunctionContext p_208172_, double p_208173_) {
            return p_208173_ > 0.0D ? null : p_188375_.computeFluid(p_208172_.blockX(), p_208172_.blockY(), p_208172_.blockZ()).at(p_208172_.blockY());
         }

         public boolean shouldScheduleFluidUpdate() {
            return false;
         }
      };
   }

   @Nullable
   BlockState computeSubstance(DensityFunction.FunctionContext p_208158_, double p_208159_);

   boolean shouldScheduleFluidUpdate();

   public interface FluidPicker {
      Aquifer.FluidStatus computeFluid(int p_188397_, int p_188398_, int p_188399_);
   }

   public static final class FluidStatus {
      final int fluidLevel;
      final BlockState fluidType;

      public FluidStatus(int p_188403_, BlockState p_188404_) {
         this.fluidLevel = p_188403_;
         this.fluidType = p_188404_;
      }

      public BlockState at(int p_188406_) {
         return p_188406_ < this.fluidLevel ? this.fluidType : Blocks.AIR.defaultBlockState();
      }
   }

   public static class NoiseBasedAquifer implements Aquifer {
      private static final int X_RANGE = 10;
      private static final int Y_RANGE = 9;
      private static final int Z_RANGE = 10;
      private static final int X_SEPARATION = 6;
      private static final int Y_SEPARATION = 3;
      private static final int Z_SEPARATION = 6;
      private static final int X_SPACING = 16;
      private static final int Y_SPACING = 12;
      private static final int Z_SPACING = 16;
      private static final int MAX_REASONABLE_DISTANCE_TO_AQUIFER_CENTER = 11;
      private static final double FLOWING_UPDATE_SIMULARITY = similarity(Mth.square(10), Mth.square(12));
      private final NoiseChunk noiseChunk;
      protected final DensityFunction barrierNoise;
      private final DensityFunction fluidLevelFloodednessNoise;
      private final DensityFunction fluidLevelSpreadNoise;
      protected final DensityFunction lavaNoise;
      private final PositionalRandomFactory positionalRandomFactory;
      protected final Aquifer.FluidStatus[] aquiferCache;
      protected final long[] aquiferLocationCache;
      private final Aquifer.FluidPicker globalFluidPicker;
      protected boolean shouldScheduleFluidUpdate;
      protected final int minGridX;
      protected final int minGridY;
      protected final int minGridZ;
      protected final int gridSizeX;
      protected final int gridSizeZ;
      private static final int[][] SURFACE_SAMPLING_OFFSETS_IN_CHUNKS = new int[][]{{-2, -1}, {-1, -1}, {0, -1}, {1, -1}, {-3, 0}, {-2, 0}, {-1, 0}, {0, 0}, {1, 0}, {-2, 1}, {-1, 1}, {0, 1}, {1, 1}};

      NoiseBasedAquifer(NoiseChunk p_208175_, ChunkPos p_208176_, DensityFunction p_208177_, DensityFunction p_208178_, DensityFunction p_208179_, DensityFunction p_208180_, PositionalRandomFactory p_208181_, int p_208182_, int p_208183_, Aquifer.FluidPicker p_208184_) {
         this.noiseChunk = p_208175_;
         this.barrierNoise = p_208177_;
         this.fluidLevelFloodednessNoise = p_208178_;
         this.fluidLevelSpreadNoise = p_208179_;
         this.lavaNoise = p_208180_;
         this.positionalRandomFactory = p_208181_;
         this.minGridX = this.gridX(p_208176_.getMinBlockX()) - 1;
         this.globalFluidPicker = p_208184_;
         int i = this.gridX(p_208176_.getMaxBlockX()) + 1;
         this.gridSizeX = i - this.minGridX + 1;
         this.minGridY = this.gridY(p_208182_) - 1;
         int j = this.gridY(p_208182_ + p_208183_) + 1;
         int k = j - this.minGridY + 1;
         this.minGridZ = this.gridZ(p_208176_.getMinBlockZ()) - 1;
         int l = this.gridZ(p_208176_.getMaxBlockZ()) + 1;
         this.gridSizeZ = l - this.minGridZ + 1;
         int i1 = this.gridSizeX * k * this.gridSizeZ;
         this.aquiferCache = new Aquifer.FluidStatus[i1];
         this.aquiferLocationCache = new long[i1];
         Arrays.fill(this.aquiferLocationCache, Long.MAX_VALUE);
      }

      protected int getIndex(int p_158028_, int p_158029_, int p_158030_) {
         int i = p_158028_ - this.minGridX;
         int j = p_158029_ - this.minGridY;
         int k = p_158030_ - this.minGridZ;
         return (j * this.gridSizeZ + k) * this.gridSizeX + i;
      }

      @Nullable
      public BlockState computeSubstance(DensityFunction.FunctionContext p_208186_, double p_208187_) {
         int i = p_208186_.blockX();
         int j = p_208186_.blockY();
         int k = p_208186_.blockZ();
         if (p_208187_ > 0.0D) {
            this.shouldScheduleFluidUpdate = false;
            return null;
         } else {
            Aquifer.FluidStatus aquifer$fluidstatus = this.globalFluidPicker.computeFluid(i, j, k);
            if (aquifer$fluidstatus.at(j).is(Blocks.LAVA)) {
               this.shouldScheduleFluidUpdate = false;
               return Blocks.LAVA.defaultBlockState();
            } else {
               int l = Math.floorDiv(i - 5, 16);
               int i1 = Math.floorDiv(j + 1, 12);
               int j1 = Math.floorDiv(k - 5, 16);
               int k1 = Integer.MAX_VALUE;
               int l1 = Integer.MAX_VALUE;
               int i2 = Integer.MAX_VALUE;
               long j2 = 0L;
               long k2 = 0L;
               long l2 = 0L;

               for(int i3 = 0; i3 <= 1; ++i3) {
                  for(int j3 = -1; j3 <= 1; ++j3) {
                     for(int k3 = 0; k3 <= 1; ++k3) {
                        int l3 = l + i3;
                        int i4 = i1 + j3;
                        int j4 = j1 + k3;
                        int k4 = this.getIndex(l3, i4, j4);
                        long i5 = this.aquiferLocationCache[k4];
                        long l4;
                        if (i5 != Long.MAX_VALUE) {
                           l4 = i5;
                        } else {
                           RandomSource randomsource = this.positionalRandomFactory.at(l3, i4, j4);
                           l4 = BlockPos.asLong(l3 * 16 + randomsource.nextInt(10), i4 * 12 + randomsource.nextInt(9), j4 * 16 + randomsource.nextInt(10));
                           this.aquiferLocationCache[k4] = l4;
                        }

                        int i6 = BlockPos.getX(l4) - i;
                        int j5 = BlockPos.getY(l4) - j;
                        int k5 = BlockPos.getZ(l4) - k;
                        int l5 = i6 * i6 + j5 * j5 + k5 * k5;
                        if (k1 >= l5) {
                           l2 = k2;
                           k2 = j2;
                           j2 = l4;
                           i2 = l1;
                           l1 = k1;
                           k1 = l5;
                        } else if (l1 >= l5) {
                           l2 = k2;
                           k2 = l4;
                           i2 = l1;
                           l1 = l5;
                        } else if (i2 >= l5) {
                           l2 = l4;
                           i2 = l5;
                        }
                     }
                  }
               }

               Aquifer.FluidStatus aquifer$fluidstatus1 = this.getAquiferStatus(j2);
               double d1 = similarity(k1, l1);
               BlockState blockstate = aquifer$fluidstatus1.at(j);
               if (d1 <= 0.0D) {
                  this.shouldScheduleFluidUpdate = d1 >= FLOWING_UPDATE_SIMULARITY;
                  return blockstate;
               } else if (blockstate.is(Blocks.WATER) && this.globalFluidPicker.computeFluid(i, j - 1, k).at(j - 1).is(Blocks.LAVA)) {
                  this.shouldScheduleFluidUpdate = true;
                  return blockstate;
               } else {
                  MutableDouble mutabledouble = new MutableDouble(Double.NaN);
                  Aquifer.FluidStatus aquifer$fluidstatus2 = this.getAquiferStatus(k2);
                  double d2 = d1 * this.calculatePressure(p_208186_, mutabledouble, aquifer$fluidstatus1, aquifer$fluidstatus2);
                  if (p_208187_ + d2 > 0.0D) {
                     this.shouldScheduleFluidUpdate = false;
                     return null;
                  } else {
                     Aquifer.FluidStatus aquifer$fluidstatus3 = this.getAquiferStatus(l2);
                     double d0 = similarity(k1, i2);
                     if (d0 > 0.0D) {
                        double d3 = d1 * d0 * this.calculatePressure(p_208186_, mutabledouble, aquifer$fluidstatus1, aquifer$fluidstatus3);
                        if (p_208187_ + d3 > 0.0D) {
                           this.shouldScheduleFluidUpdate = false;
                           return null;
                        }
                     }

                     double d4 = similarity(l1, i2);
                     if (d4 > 0.0D) {
                        double d5 = d1 * d4 * this.calculatePressure(p_208186_, mutabledouble, aquifer$fluidstatus2, aquifer$fluidstatus3);
                        if (p_208187_ + d5 > 0.0D) {
                           this.shouldScheduleFluidUpdate = false;
                           return null;
                        }
                     }

                     this.shouldScheduleFluidUpdate = true;
                     return blockstate;
                  }
               }
            }
         }
      }

      public boolean shouldScheduleFluidUpdate() {
         return this.shouldScheduleFluidUpdate;
      }

      protected static double similarity(int p_158025_, int p_158026_) {
         double d0 = 25.0D;
         return 1.0D - (double)Math.abs(p_158026_ - p_158025_) / 25.0D;
      }

      private double calculatePressure(DensityFunction.FunctionContext p_208189_, MutableDouble p_208190_, Aquifer.FluidStatus p_208191_, Aquifer.FluidStatus p_208192_) {
         int i = p_208189_.blockY();
         BlockState blockstate = p_208191_.at(i);
         BlockState blockstate1 = p_208192_.at(i);
         if ((!blockstate.is(Blocks.LAVA) || !blockstate1.is(Blocks.WATER)) && (!blockstate.is(Blocks.WATER) || !blockstate1.is(Blocks.LAVA))) {
            int j = Math.abs(p_208191_.fluidLevel - p_208192_.fluidLevel);
            if (j == 0) {
               return 0.0D;
            } else {
               double d0 = 0.5D * (double)(p_208191_.fluidLevel + p_208192_.fluidLevel);
               double d1 = (double)i + 0.5D - d0;
               double d2 = (double)j / 2.0D;
               double d3 = 0.0D;
               double d4 = 2.5D;
               double d5 = 1.5D;
               double d6 = 3.0D;
               double d7 = 10.0D;
               double d8 = 3.0D;
               double d9 = d2 - Math.abs(d1);
               double d10;
               if (d1 > 0.0D) {
                  double d11 = 0.0D + d9;
                  if (d11 > 0.0D) {
                     d10 = d11 / 1.5D;
                  } else {
                     d10 = d11 / 2.5D;
                  }
               } else {
                  double d15 = 3.0D + d9;
                  if (d15 > 0.0D) {
                     d10 = d15 / 3.0D;
                  } else {
                     d10 = d15 / 10.0D;
                  }
               }

               double d16 = 2.0D;
               double d12;
               if (!(d10 < -2.0D) && !(d10 > 2.0D)) {
                  double d13 = p_208190_.getValue();
                  if (Double.isNaN(d13)) {
                     double d14 = this.barrierNoise.compute(p_208189_);
                     p_208190_.setValue(d14);
                     d12 = d14;
                  } else {
                     d12 = d13;
                  }
               } else {
                  d12 = 0.0D;
               }

               return 2.0D * (d12 + d10);
            }
         } else {
            return 2.0D;
         }
      }

      protected int gridX(int p_158040_) {
         return Math.floorDiv(p_158040_, 16);
      }

      protected int gridY(int p_158046_) {
         return Math.floorDiv(p_158046_, 12);
      }

      protected int gridZ(int p_158048_) {
         return Math.floorDiv(p_158048_, 16);
      }

      private Aquifer.FluidStatus getAquiferStatus(long p_188446_) {
         int i = BlockPos.getX(p_188446_);
         int j = BlockPos.getY(p_188446_);
         int k = BlockPos.getZ(p_188446_);
         int l = this.gridX(i);
         int i1 = this.gridY(j);
         int j1 = this.gridZ(k);
         int k1 = this.getIndex(l, i1, j1);
         Aquifer.FluidStatus aquifer$fluidstatus = this.aquiferCache[k1];
         if (aquifer$fluidstatus != null) {
            return aquifer$fluidstatus;
         } else {
            Aquifer.FluidStatus aquifer$fluidstatus1 = this.computeFluid(i, j, k);
            this.aquiferCache[k1] = aquifer$fluidstatus1;
            return aquifer$fluidstatus1;
         }
      }

      private Aquifer.FluidStatus computeFluid(int p_188448_, int p_188449_, int p_188450_) {
         Aquifer.FluidStatus aquifer$fluidstatus = this.globalFluidPicker.computeFluid(p_188448_, p_188449_, p_188450_);
         int i = Integer.MAX_VALUE;
         int j = p_188449_ + 12;
         int k = p_188449_ - 12;
         boolean flag = false;

         for(int[] aint : SURFACE_SAMPLING_OFFSETS_IN_CHUNKS) {
            int l = p_188448_ + SectionPos.sectionToBlockCoord(aint[0]);
            int i1 = p_188450_ + SectionPos.sectionToBlockCoord(aint[1]);
            int j1 = this.noiseChunk.preliminarySurfaceLevel(l, i1);
            int k1 = j1 + 8;
            boolean flag1 = aint[0] == 0 && aint[1] == 0;
            if (flag1 && k > k1) {
               return aquifer$fluidstatus;
            }

            boolean flag2 = j > k1;
            if (flag2 || flag1) {
               Aquifer.FluidStatus aquifer$fluidstatus1 = this.globalFluidPicker.computeFluid(l, k1, i1);
               if (!aquifer$fluidstatus1.at(k1).isAir()) {
                  if (flag1) {
                     flag = true;
                  }

                  if (flag2) {
                     return aquifer$fluidstatus1;
                  }
               }
            }

            i = Math.min(i, j1);
         }

         int j5 = i + 8 - p_188449_;
         int k5 = 64;
         double d2 = flag ? Mth.clampedMap((double)j5, 0.0D, 64.0D, 1.0D, 0.0D) : 0.0D;
         double d3 = Mth.clamp(this.fluidLevelFloodednessNoise.compute(new DensityFunction.SinglePointContext(p_188448_, p_188449_, p_188450_)), -1.0D, 1.0D);
         double d4 = Mth.map(d2, 1.0D, 0.0D, -0.3D, 0.8D);
         if (d3 > d4) {
            return aquifer$fluidstatus;
         } else {
            double d5 = Mth.map(d2, 1.0D, 0.0D, -0.8D, 0.4D);
            if (d3 <= d5) {
               return new Aquifer.FluidStatus(DimensionType.WAY_BELOW_MIN_Y, aquifer$fluidstatus.fluidType);
            } else {
               int l5 = 16;
               int l1 = 40;
               int i2 = Math.floorDiv(p_188448_, 16);
               int j2 = Math.floorDiv(p_188449_, 40);
               int k2 = Math.floorDiv(p_188450_, 16);
               int l2 = j2 * 40 + 20;
               int i3 = 10;
               double d0 = this.fluidLevelSpreadNoise.compute(new DensityFunction.SinglePointContext(i2, j2, k2)) * 10.0D;
               int j3 = Mth.quantize(d0, 3);
               int k3 = l2 + j3;
               int l3 = Math.min(i, k3);
               if (k3 <= -10) {
                  int i4 = 64;
                  int j4 = 40;
                  int k4 = Math.floorDiv(p_188448_, 64);
                  int l4 = Math.floorDiv(p_188449_, 40);
                  int i5 = Math.floorDiv(p_188450_, 64);
                  double d1 = this.lavaNoise.compute(new DensityFunction.SinglePointContext(k4, l4, i5));
                  if (Math.abs(d1) > 0.3D) {
                     return new Aquifer.FluidStatus(l3, Blocks.LAVA.defaultBlockState());
                  }
               }

               return new Aquifer.FluidStatus(l3, aquifer$fluidstatus.fluidType);
            }
         }
      }
   }
}