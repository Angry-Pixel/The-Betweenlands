package net.minecraft.world.entity.ai.attributes;

import net.minecraft.util.Mth;

public class RangedAttribute extends Attribute {
   private final double minValue;
   private final double maxValue;

   public RangedAttribute(String p_22310_, double p_22311_, double p_22312_, double p_22313_) {
      super(p_22310_, p_22311_);
      this.minValue = p_22312_;
      this.maxValue = p_22313_;
      if (p_22312_ > p_22313_) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      } else if (p_22311_ < p_22312_) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      } else if (p_22311_ > p_22313_) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public double sanitizeValue(double p_22315_) {
      return Mth.clamp(p_22315_, this.minValue, this.maxValue);
   }
}