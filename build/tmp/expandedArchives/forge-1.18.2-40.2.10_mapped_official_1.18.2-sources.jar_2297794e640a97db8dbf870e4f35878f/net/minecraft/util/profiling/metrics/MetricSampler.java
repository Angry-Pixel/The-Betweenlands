package net.minecraft.util.profiling.metrics;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

public class MetricSampler {
   private final String name;
   private final MetricCategory category;
   private final DoubleSupplier sampler;
   private final ByteBuf ticks;
   private final ByteBuf values;
   private volatile boolean isRunning;
   @Nullable
   private final Runnable beforeTick;
   @Nullable
   final MetricSampler.ThresholdTest thresholdTest;
   private double currentValue;

   protected MetricSampler(String p_145996_, MetricCategory p_145997_, DoubleSupplier p_145998_, @Nullable Runnable p_145999_, @Nullable MetricSampler.ThresholdTest p_146000_) {
      this.name = p_145996_;
      this.category = p_145997_;
      this.beforeTick = p_145999_;
      this.sampler = p_145998_;
      this.thresholdTest = p_146000_;
      this.values = ByteBufAllocator.DEFAULT.buffer();
      this.ticks = ByteBufAllocator.DEFAULT.buffer();
      this.isRunning = true;
   }

   public static MetricSampler create(String p_146010_, MetricCategory p_146011_, DoubleSupplier p_146012_) {
      return new MetricSampler(p_146010_, p_146011_, p_146012_, (Runnable)null, (MetricSampler.ThresholdTest)null);
   }

   public static <T> MetricSampler create(String p_146005_, MetricCategory p_146006_, T p_146007_, ToDoubleFunction<T> p_146008_) {
      return builder(p_146005_, p_146006_, p_146008_, p_146007_).build();
   }

   public static <T> MetricSampler.MetricSamplerBuilder<T> builder(String p_146014_, MetricCategory p_146015_, ToDoubleFunction<T> p_146016_, T p_146017_) {
      return new MetricSampler.MetricSamplerBuilder<>(p_146014_, p_146015_, p_146016_, p_146017_);
   }

   public void onStartTick() {
      if (!this.isRunning) {
         throw new IllegalStateException("Not running");
      } else {
         if (this.beforeTick != null) {
            this.beforeTick.run();
         }

      }
   }

   public void onEndTick(int p_146003_) {
      this.verifyRunning();
      this.currentValue = this.sampler.getAsDouble();
      this.values.writeDouble(this.currentValue);
      this.ticks.writeInt(p_146003_);
   }

   public void onFinished() {
      this.verifyRunning();
      this.values.release();
      this.ticks.release();
      this.isRunning = false;
   }

   private void verifyRunning() {
      if (!this.isRunning) {
         throw new IllegalStateException(String.format("Sampler for metric %s not started!", this.name));
      }
   }

   DoubleSupplier getSampler() {
      return this.sampler;
   }

   public String getName() {
      return this.name;
   }

   public MetricCategory getCategory() {
      return this.category;
   }

   public MetricSampler.SamplerResult result() {
      Int2DoubleMap int2doublemap = new Int2DoubleOpenHashMap();
      int i = Integer.MIN_VALUE;

      int j;
      int k;
      for(j = Integer.MIN_VALUE; this.values.isReadable(8); j = k) {
         k = this.ticks.readInt();
         if (i == Integer.MIN_VALUE) {
            i = k;
         }

         int2doublemap.put(k, this.values.readDouble());
      }

      return new MetricSampler.SamplerResult(i, j, int2doublemap);
   }

   public boolean triggersThreshold() {
      return this.thresholdTest != null && this.thresholdTest.test(this.currentValue);
   }

   public boolean equals(Object p_146023_) {
      if (this == p_146023_) {
         return true;
      } else if (p_146023_ != null && this.getClass() == p_146023_.getClass()) {
         MetricSampler metricsampler = (MetricSampler)p_146023_;
         return this.name.equals(metricsampler.name) && this.category.equals(metricsampler.category);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public static class MetricSamplerBuilder<T> {
      private final String name;
      private final MetricCategory category;
      private final DoubleSupplier sampler;
      private final T context;
      @Nullable
      private Runnable beforeTick;
      @Nullable
      private MetricSampler.ThresholdTest thresholdTest;

      public MetricSamplerBuilder(String p_146035_, MetricCategory p_146036_, ToDoubleFunction<T> p_146037_, T p_146038_) {
         this.name = p_146035_;
         this.category = p_146036_;
         this.sampler = () -> {
            return p_146037_.applyAsDouble(p_146038_);
         };
         this.context = p_146038_;
      }

      public MetricSampler.MetricSamplerBuilder<T> withBeforeTick(Consumer<T> p_146043_) {
         this.beforeTick = () -> {
            p_146043_.accept(this.context);
         };
         return this;
      }

      public MetricSampler.MetricSamplerBuilder<T> withThresholdAlert(MetricSampler.ThresholdTest p_146041_) {
         this.thresholdTest = p_146041_;
         return this;
      }

      public MetricSampler build() {
         return new MetricSampler(this.name, this.category, this.sampler, this.beforeTick, this.thresholdTest);
      }
   }

   public static class SamplerResult {
      private final Int2DoubleMap recording;
      private final int firstTick;
      private final int lastTick;

      public SamplerResult(int p_146053_, int p_146054_, Int2DoubleMap p_146055_) {
         this.firstTick = p_146053_;
         this.lastTick = p_146054_;
         this.recording = p_146055_;
      }

      public double valueAtTick(int p_146058_) {
         return this.recording.get(p_146058_);
      }

      public int getFirstTick() {
         return this.firstTick;
      }

      public int getLastTick() {
         return this.lastTick;
      }
   }

   public interface ThresholdTest {
      boolean test(double p_146060_);
   }

   public static class ValueIncreasedByPercentage implements MetricSampler.ThresholdTest {
      private final float percentageIncreaseThreshold;
      private double previousValue = Double.MIN_VALUE;

      public ValueIncreasedByPercentage(float p_146064_) {
         this.percentageIncreaseThreshold = p_146064_;
      }

      public boolean test(double p_146066_) {
         boolean flag;
         if (this.previousValue != Double.MIN_VALUE && !(p_146066_ <= this.previousValue)) {
            flag = (p_146066_ - this.previousValue) / this.previousValue >= (double)this.percentageIncreaseThreshold;
         } else {
            flag = false;
         }

         this.previousValue = p_146066_;
         return flag;
      }
   }
}