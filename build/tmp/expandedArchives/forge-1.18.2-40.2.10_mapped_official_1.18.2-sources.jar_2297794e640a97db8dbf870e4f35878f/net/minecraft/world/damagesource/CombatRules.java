package net.minecraft.world.damagesource;

import net.minecraft.util.Mth;

public class CombatRules {
   public static final float MAX_ARMOR = 20.0F;
   public static final float ARMOR_PROTECTION_DIVIDER = 25.0F;
   public static final float BASE_ARMOR_TOUGHNESS = 2.0F;
   public static final float MIN_ARMOR_RATIO = 0.2F;
   private static final int NUM_ARMOR_ITEMS = 4;

   public static float getDamageAfterAbsorb(float p_19273_, float p_19274_, float p_19275_) {
      float f = 2.0F + p_19275_ / 4.0F;
      float f1 = Mth.clamp(p_19274_ - p_19273_ / f, p_19274_ * 0.2F, 20.0F);
      return p_19273_ * (1.0F - f1 / 25.0F);
   }

   public static float getDamageAfterMagicAbsorb(float p_19270_, float p_19271_) {
      float f = Mth.clamp(p_19271_, 0.0F, 20.0F);
      return p_19270_ * (1.0F - f / 25.0F);
   }
}