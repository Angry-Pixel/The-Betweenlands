package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.SliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProgressOption extends Option {
   protected final float steps;
   protected final double minValue;
   protected double maxValue;
   private final Function<Options, Double> getter;
   private final BiConsumer<Options, Double> setter;
   private final BiFunction<Options, ProgressOption, Component> toString;
   private final Function<Minecraft, List<FormattedCharSequence>> tooltipSupplier;

   public ProgressOption(String p_168540_, double p_168541_, double p_168542_, float p_168543_, Function<Options, Double> p_168544_, BiConsumer<Options, Double> p_168545_, BiFunction<Options, ProgressOption, Component> p_168546_, Function<Minecraft, List<FormattedCharSequence>> p_168547_) {
      super(p_168540_);
      this.minValue = p_168541_;
      this.maxValue = p_168542_;
      this.steps = p_168543_;
      this.getter = p_168544_;
      this.setter = p_168545_;
      this.toString = p_168546_;
      this.tooltipSupplier = p_168547_;
   }

   public ProgressOption(String p_92211_, double p_92212_, double p_92213_, float p_92214_, Function<Options, Double> p_92215_, BiConsumer<Options, Double> p_92216_, BiFunction<Options, ProgressOption, Component> p_92217_) {
      this(p_92211_, p_92212_, p_92213_, p_92214_, p_92215_, p_92216_, p_92217_, (p_168549_) -> {
         return ImmutableList.of();
      });
   }

   public AbstractWidget createButton(Options p_92227_, int p_92228_, int p_92229_, int p_92230_) {
      List<FormattedCharSequence> list = this.tooltipSupplier.apply(Minecraft.getInstance());
      return new SliderButton(p_92227_, p_92228_, p_92229_, p_92230_, 20, this, list);
   }

   public double toPct(double p_92218_) {
      return Mth.clamp((this.clamp(p_92218_) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
   }

   public double toValue(double p_92231_) {
      return this.clamp(Mth.lerp(Mth.clamp(p_92231_, 0.0D, 1.0D), this.minValue, this.maxValue));
   }

   private double clamp(double p_92237_) {
      if (this.steps > 0.0F) {
         p_92237_ = (double)(this.steps * (float)Math.round(p_92237_ / (double)this.steps));
      }

      return Mth.clamp(p_92237_, this.minValue, this.maxValue);
   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public void setMaxValue(float p_92220_) {
      this.maxValue = (double)p_92220_;
   }

   public void set(Options p_92224_, double p_92225_) {
      this.setter.accept(p_92224_, p_92225_);
   }

   public double get(Options p_92222_) {
      return this.getter.apply(p_92222_);
   }

   public Component getMessage(Options p_92234_) {
      return this.toString.apply(p_92234_, this);
   }
}