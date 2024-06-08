package net.minecraft.world.entity.monster.hoglin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

public interface HoglinBase {
   int ATTACK_ANIMATION_DURATION = 10;

   int getAttackAnimationRemainingTicks();

   static boolean hurtAndThrowTarget(LivingEntity p_34643_, LivingEntity p_34644_) {
      float f1 = (float)p_34643_.getAttributeValue(Attributes.ATTACK_DAMAGE);
      float f;
      if (!p_34643_.isBaby() && (int)f1 > 0) {
         f = f1 / 2.0F + (float)p_34643_.level.random.nextInt((int)f1);
      } else {
         f = f1;
      }

      boolean flag = p_34644_.hurt(DamageSource.mobAttack(p_34643_), f);
      if (flag) {
         p_34643_.doEnchantDamageEffects(p_34643_, p_34644_);
         if (!p_34643_.isBaby()) {
            throwTarget(p_34643_, p_34644_);
         }
      }

      return flag;
   }

   static void throwTarget(LivingEntity p_34646_, LivingEntity p_34647_) {
      double d0 = p_34646_.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      double d1 = p_34647_.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
      double d2 = d0 - d1;
      if (!(d2 <= 0.0D)) {
         double d3 = p_34647_.getX() - p_34646_.getX();
         double d4 = p_34647_.getZ() - p_34646_.getZ();
         float f = (float)(p_34646_.level.random.nextInt(21) - 10);
         double d5 = d2 * (double)(p_34646_.level.random.nextFloat() * 0.5F + 0.2F);
         Vec3 vec3 = (new Vec3(d3, 0.0D, d4)).normalize().scale(d5).yRot(f);
         double d6 = d2 * (double)p_34646_.level.random.nextFloat() * 0.5D;
         p_34647_.push(vec3.x, d6, vec3.z);
         p_34647_.hurtMarked = true;
      }
   }
}