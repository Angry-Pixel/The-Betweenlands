package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.Mth;

@Immutable
public class DifficultyInstance {
   private static final float DIFFICULTY_TIME_GLOBAL_OFFSET = -72000.0F;
   private static final float MAX_DIFFICULTY_TIME_GLOBAL = 1440000.0F;
   private static final float MAX_DIFFICULTY_TIME_LOCAL = 3600000.0F;
   private final Difficulty base;
   private final float effectiveDifficulty;

   public DifficultyInstance(Difficulty p_19044_, long p_19045_, long p_19046_, float p_19047_) {
      this.base = p_19044_;
      this.effectiveDifficulty = this.calculateDifficulty(p_19044_, p_19045_, p_19046_, p_19047_);
   }

   public Difficulty getDifficulty() {
      return this.base;
   }

   public float getEffectiveDifficulty() {
      return this.effectiveDifficulty;
   }

   public boolean isHard() {
      return this.effectiveDifficulty >= (float)Difficulty.HARD.ordinal();
   }

   public boolean isHarderThan(float p_19050_) {
      return this.effectiveDifficulty > p_19050_;
   }

   public float getSpecialMultiplier() {
      if (this.effectiveDifficulty < 2.0F) {
         return 0.0F;
      } else {
         return this.effectiveDifficulty > 4.0F ? 1.0F : (this.effectiveDifficulty - 2.0F) / 2.0F;
      }
   }

   private float calculateDifficulty(Difficulty p_19052_, long p_19053_, long p_19054_, float p_19055_) {
      if (p_19052_ == Difficulty.PEACEFUL) {
         return 0.0F;
      } else {
         boolean flag = p_19052_ == Difficulty.HARD;
         float f = 0.75F;
         float f1 = Mth.clamp(((float)p_19053_ + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
         f += f1;
         float f2 = 0.0F;
         f2 += Mth.clamp((float)p_19054_ / 3600000.0F, 0.0F, 1.0F) * (flag ? 1.0F : 0.75F);
         f2 += Mth.clamp(p_19055_ * 0.25F, 0.0F, f1);
         if (p_19052_ == Difficulty.EASY) {
            f2 *= 0.5F;
         }

         f += f2;
         return (float)p_19052_.getId() * f;
      }
   }
}