package net.minecraft.client;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LogaritmicProgressOption extends ProgressOption {
   public LogaritmicProgressOption(String p_90966_, double p_90967_, double p_90968_, float p_90969_, Function<Options, Double> p_90970_, BiConsumer<Options, Double> p_90971_, BiFunction<Options, ProgressOption, Component> p_90972_) {
      super(p_90966_, p_90967_, p_90968_, p_90969_, p_90970_, p_90971_, p_90972_);
   }

   public double toPct(double p_90974_) {
      return Math.log(p_90974_ / this.minValue) / Math.log(this.maxValue / this.minValue);
   }

   public double toValue(double p_90976_) {
      return this.minValue * Math.pow(Math.E, Math.log(this.maxValue / this.minValue) * p_90976_);
   }
}