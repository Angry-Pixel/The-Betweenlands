package net.minecraft.world.effect;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttackDamageMobEffect extends MobEffect {
   protected final double multiplier;

   protected AttackDamageMobEffect(MobEffectCategory p_19426_, int p_19427_, double p_19428_) {
      super(p_19426_, p_19427_);
      this.multiplier = p_19428_;
   }

   public double getAttributeModifierValue(int p_19430_, AttributeModifier p_19431_) {
      return this.multiplier * (double)(p_19430_ + 1);
   }
}