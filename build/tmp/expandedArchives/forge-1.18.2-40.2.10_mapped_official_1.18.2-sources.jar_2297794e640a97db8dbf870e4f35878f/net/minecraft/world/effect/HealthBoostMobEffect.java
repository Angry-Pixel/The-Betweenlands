package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class HealthBoostMobEffect extends MobEffect {
   public HealthBoostMobEffect(MobEffectCategory p_19433_, int p_19434_) {
      super(p_19433_, p_19434_);
   }

   public void removeAttributeModifiers(LivingEntity p_19436_, AttributeMap p_19437_, int p_19438_) {
      super.removeAttributeModifiers(p_19436_, p_19437_, p_19438_);
      if (p_19436_.getHealth() > p_19436_.getMaxHealth()) {
         p_19436_.setHealth(p_19436_.getMaxHealth());
      }

   }
}