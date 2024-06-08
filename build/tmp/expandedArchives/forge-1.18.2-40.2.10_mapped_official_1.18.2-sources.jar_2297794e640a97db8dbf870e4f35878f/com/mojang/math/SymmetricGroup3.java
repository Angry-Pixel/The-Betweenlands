package com.mojang.math;

import java.util.Arrays;
import net.minecraft.Util;

public enum SymmetricGroup3 {
   P123(0, 1, 2),
   P213(1, 0, 2),
   P132(0, 2, 1),
   P231(1, 2, 0),
   P312(2, 0, 1),
   P321(2, 1, 0);

   private final int[] permutation;
   private final Matrix3f transformation;
   private static final int ORDER = 3;
   private static final SymmetricGroup3[][] cayleyTable = Util.make(new SymmetricGroup3[values().length][values().length], (p_109188_) -> {
      for(SymmetricGroup3 symmetricgroup3 : values()) {
         for(SymmetricGroup3 symmetricgroup31 : values()) {
            int[] aint = new int[3];

            for(int i = 0; i < 3; ++i) {
               aint[i] = symmetricgroup3.permutation[symmetricgroup31.permutation[i]];
            }

            SymmetricGroup3 symmetricgroup32 = Arrays.stream(values()).filter((p_175577_) -> {
               return Arrays.equals(p_175577_.permutation, aint);
            }).findFirst().get();
            p_109188_[symmetricgroup3.ordinal()][symmetricgroup31.ordinal()] = symmetricgroup32;
         }
      }

   });

   private SymmetricGroup3(int p_109176_, int p_109177_, int p_109178_) {
      this.permutation = new int[]{p_109176_, p_109177_, p_109178_};
      this.transformation = new Matrix3f();
      this.transformation.set(0, this.permutation(0), 1.0F);
      this.transformation.set(1, this.permutation(1), 1.0F);
      this.transformation.set(2, this.permutation(2), 1.0F);
   }

   public SymmetricGroup3 compose(SymmetricGroup3 p_109183_) {
      return cayleyTable[this.ordinal()][p_109183_.ordinal()];
   }

   public int permutation(int p_109181_) {
      return this.permutation[p_109181_];
   }

   public Matrix3f transformation() {
      return this.transformation;
   }
}