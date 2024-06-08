package net.minecraft.world.effect;

import net.minecraft.ChatFormatting;

public enum MobEffectCategory {
   BENEFICIAL(ChatFormatting.BLUE),
   HARMFUL(ChatFormatting.RED),
   NEUTRAL(ChatFormatting.BLUE);

   private final ChatFormatting tooltipFormatting;

   private MobEffectCategory(ChatFormatting p_19496_) {
      this.tooltipFormatting = p_19496_;
   }

   public ChatFormatting getTooltipFormatting() {
      return this.tooltipFormatting;
   }
}