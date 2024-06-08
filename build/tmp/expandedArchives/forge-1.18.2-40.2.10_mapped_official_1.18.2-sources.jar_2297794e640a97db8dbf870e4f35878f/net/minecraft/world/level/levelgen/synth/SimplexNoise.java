package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.RandomSource;

public class SimplexNoise {
   protected static final int[][] GRADIENT = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
   private static final double SQRT_3 = Math.sqrt(3.0D);
   private static final double F2 = 0.5D * (SQRT_3 - 1.0D);
   private static final double G2 = (3.0D - SQRT_3) / 6.0D;
   private final int[] p = new int[512];
   public final double xo;
   public final double yo;
   public final double zo;

   public SimplexNoise(RandomSource p_164399_) {
      this.xo = p_164399_.nextDouble() * 256.0D;
      this.yo = p_164399_.nextDouble() * 256.0D;
      this.zo = p_164399_.nextDouble() * 256.0D;

      for(int i = 0; i < 256; this.p[i] = i++) {
      }

      for(int l = 0; l < 256; ++l) {
         int j = p_164399_.nextInt(256 - l);
         int k = this.p[l];
         this.p[l] = this.p[j + l];
         this.p[j + l] = k;
      }

   }

   private int p(int p_75472_) {
      return this.p[p_75472_ & 255];
   }

   protected static double dot(int[] p_75480_, double p_75481_, double p_75482_, double p_75483_) {
      return (double)p_75480_[0] * p_75481_ + (double)p_75480_[1] * p_75482_ + (double)p_75480_[2] * p_75483_;
   }

   private double getCornerNoise3D(int p_75474_, double p_75475_, double p_75476_, double p_75477_, double p_75478_) {
      double d1 = p_75478_ - p_75475_ * p_75475_ - p_75476_ * p_75476_ - p_75477_ * p_75477_;
      double d0;
      if (d1 < 0.0D) {
         d0 = 0.0D;
      } else {
         d1 *= d1;
         d0 = d1 * d1 * dot(GRADIENT[p_75474_], p_75475_, p_75476_, p_75477_);
      }

      return d0;
   }

   public double getValue(double p_75465_, double p_75466_) {
      double d0 = (p_75465_ + p_75466_) * F2;
      int i = Mth.floor(p_75465_ + d0);
      int j = Mth.floor(p_75466_ + d0);
      double d1 = (double)(i + j) * G2;
      double d2 = (double)i - d1;
      double d3 = (double)j - d1;
      double d4 = p_75465_ - d2;
      double d5 = p_75466_ - d3;
      int k;
      int l;
      if (d4 > d5) {
         k = 1;
         l = 0;
      } else {
         k = 0;
         l = 1;
      }

      double d6 = d4 - (double)k + G2;
      double d7 = d5 - (double)l + G2;
      double d8 = d4 - 1.0D + 2.0D * G2;
      double d9 = d5 - 1.0D + 2.0D * G2;
      int i1 = i & 255;
      int j1 = j & 255;
      int k1 = this.p(i1 + this.p(j1)) % 12;
      int l1 = this.p(i1 + k + this.p(j1 + l)) % 12;
      int i2 = this.p(i1 + 1 + this.p(j1 + 1)) % 12;
      double d10 = this.getCornerNoise3D(k1, d4, d5, 0.0D, 0.5D);
      double d11 = this.getCornerNoise3D(l1, d6, d7, 0.0D, 0.5D);
      double d12 = this.getCornerNoise3D(i2, d8, d9, 0.0D, 0.5D);
      return 70.0D * (d10 + d11 + d12);
   }

   public double getValue(double p_75468_, double p_75469_, double p_75470_) {
      double d0 = 0.3333333333333333D;
      double d1 = (p_75468_ + p_75469_ + p_75470_) * 0.3333333333333333D;
      int i = Mth.floor(p_75468_ + d1);
      int j = Mth.floor(p_75469_ + d1);
      int k = Mth.floor(p_75470_ + d1);
      double d2 = 0.16666666666666666D;
      double d3 = (double)(i + j + k) * 0.16666666666666666D;
      double d4 = (double)i - d3;
      double d5 = (double)j - d3;
      double d6 = (double)k - d3;
      double d7 = p_75468_ - d4;
      double d8 = p_75469_ - d5;
      double d9 = p_75470_ - d6;
      int l;
      int i1;
      int j1;
      int k1;
      int l1;
      int i2;
      if (d7 >= d8) {
         if (d8 >= d9) {
            l = 1;
            i1 = 0;
            j1 = 0;
            k1 = 1;
            l1 = 1;
            i2 = 0;
         } else if (d7 >= d9) {
            l = 1;
            i1 = 0;
            j1 = 0;
            k1 = 1;
            l1 = 0;
            i2 = 1;
         } else {
            l = 0;
            i1 = 0;
            j1 = 1;
            k1 = 1;
            l1 = 0;
            i2 = 1;
         }
      } else if (d8 < d9) {
         l = 0;
         i1 = 0;
         j1 = 1;
         k1 = 0;
         l1 = 1;
         i2 = 1;
      } else if (d7 < d9) {
         l = 0;
         i1 = 1;
         j1 = 0;
         k1 = 0;
         l1 = 1;
         i2 = 1;
      } else {
         l = 0;
         i1 = 1;
         j1 = 0;
         k1 = 1;
         l1 = 1;
         i2 = 0;
      }

      double d10 = d7 - (double)l + 0.16666666666666666D;
      double d11 = d8 - (double)i1 + 0.16666666666666666D;
      double d12 = d9 - (double)j1 + 0.16666666666666666D;
      double d13 = d7 - (double)k1 + 0.3333333333333333D;
      double d14 = d8 - (double)l1 + 0.3333333333333333D;
      double d15 = d9 - (double)i2 + 0.3333333333333333D;
      double d16 = d7 - 1.0D + 0.5D;
      double d17 = d8 - 1.0D + 0.5D;
      double d18 = d9 - 1.0D + 0.5D;
      int j2 = i & 255;
      int k2 = j & 255;
      int l2 = k & 255;
      int i3 = this.p(j2 + this.p(k2 + this.p(l2))) % 12;
      int j3 = this.p(j2 + l + this.p(k2 + i1 + this.p(l2 + j1))) % 12;
      int k3 = this.p(j2 + k1 + this.p(k2 + l1 + this.p(l2 + i2))) % 12;
      int l3 = this.p(j2 + 1 + this.p(k2 + 1 + this.p(l2 + 1))) % 12;
      double d19 = this.getCornerNoise3D(i3, d7, d8, d9, 0.6D);
      double d20 = this.getCornerNoise3D(j3, d10, d11, d12, 0.6D);
      double d21 = this.getCornerNoise3D(k3, d13, d14, d15, 0.6D);
      double d22 = this.getCornerNoise3D(l3, d16, d17, d18, 0.6D);
      return 32.0D * (d19 + d20 + d21 + d22);
   }
}