package net.minecraft.util.profiling.metrics;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class MetricsRegistry {
   public static final MetricsRegistry INSTANCE = new MetricsRegistry();
   private final WeakHashMap<ProfilerMeasured, Void> measuredInstances = new WeakHashMap<>();

   private MetricsRegistry() {
   }

   public void add(ProfilerMeasured p_146073_) {
      this.measuredInstances.put(p_146073_, (Void)null);
   }

   public List<MetricSampler> getRegisteredSamplers() {
      Map<String, List<MetricSampler>> map = this.measuredInstances.keySet().stream().flatMap((p_146079_) -> {
         return p_146079_.profiledMetrics().stream();
      }).collect(Collectors.groupingBy(MetricSampler::getName));
      return aggregateDuplicates(map);
   }

   private static List<MetricSampler> aggregateDuplicates(Map<String, List<MetricSampler>> p_146077_) {
      return p_146077_.entrySet().stream().map((p_146075_) -> {
         String s = p_146075_.getKey();
         List<MetricSampler> list = p_146075_.getValue();
         return (MetricSampler)(list.size() > 1 ? new MetricsRegistry.AggregatedMetricSampler(s, list) : list.get(0));
      }).collect(Collectors.toList());
   }

   static class AggregatedMetricSampler extends MetricSampler {
      private final List<MetricSampler> delegates;

      AggregatedMetricSampler(String p_146082_, List<MetricSampler> p_146083_) {
         super(p_146082_, p_146083_.get(0).getCategory(), () -> {
            return averageValueFromDelegates(p_146083_);
         }, () -> {
            beforeTick(p_146083_);
         }, thresholdTest(p_146083_));
         this.delegates = p_146083_;
      }

      private static MetricSampler.ThresholdTest thresholdTest(List<MetricSampler> p_146088_) {
         return (p_146091_) -> {
            return p_146088_.stream().anyMatch((p_146086_) -> {
               return p_146086_.thresholdTest != null ? p_146086_.thresholdTest.test(p_146091_) : false;
            });
         };
      }

      private static void beforeTick(List<MetricSampler> p_146093_) {
         for(MetricSampler metricsampler : p_146093_) {
            metricsampler.onStartTick();
         }

      }

      private static double averageValueFromDelegates(List<MetricSampler> p_146095_) {
         double d0 = 0.0D;

         for(MetricSampler metricsampler : p_146095_) {
            d0 += metricsampler.getSampler().getAsDouble();
         }

         return d0 / (double)p_146095_.size();
      }

      public boolean equals(@Nullable Object p_146101_) {
         if (this == p_146101_) {
            return true;
         } else if (p_146101_ != null && this.getClass() == p_146101_.getClass()) {
            if (!super.equals(p_146101_)) {
               return false;
            } else {
               MetricsRegistry.AggregatedMetricSampler metricsregistry$aggregatedmetricsampler = (MetricsRegistry.AggregatedMetricSampler)p_146101_;
               return this.delegates.equals(metricsregistry$aggregatedmetricsampler.delegates);
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(super.hashCode(), this.delegates);
      }
   }
}