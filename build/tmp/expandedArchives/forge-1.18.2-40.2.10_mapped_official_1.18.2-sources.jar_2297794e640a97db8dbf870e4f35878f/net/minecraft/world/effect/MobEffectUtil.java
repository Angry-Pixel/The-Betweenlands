package net.minecraft.world.effect;

import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;

public final class MobEffectUtil {
   public static String formatDuration(MobEffectInstance p_19582_, float p_19583_) {
      if (p_19582_.isNoCounter()) {
         return "**:**";
      } else {
         int i = Mth.floor((float)p_19582_.getDuration() * p_19583_);
         return StringUtil.formatTickDuration(i);
      }
   }

   public static boolean hasDigSpeed(LivingEntity p_19585_) {
      return p_19585_.hasEffect(MobEffects.DIG_SPEED) || p_19585_.hasEffect(MobEffects.CONDUIT_POWER);
   }

   public static int getDigSpeedAmplification(LivingEntity p_19587_) {
      int i = 0;
      int j = 0;
      if (p_19587_.hasEffect(MobEffects.DIG_SPEED)) {
         i = p_19587_.getEffect(MobEffects.DIG_SPEED).getAmplifier();
      }

      if (p_19587_.hasEffect(MobEffects.CONDUIT_POWER)) {
         j = p_19587_.getEffect(MobEffects.CONDUIT_POWER).getAmplifier();
      }

      return Math.max(i, j);
   }

   public static boolean hasWaterBreathing(LivingEntity p_19589_) {
      return p_19589_.hasEffect(MobEffects.WATER_BREATHING) || p_19589_.hasEffect(MobEffects.CONDUIT_POWER);
   }
}