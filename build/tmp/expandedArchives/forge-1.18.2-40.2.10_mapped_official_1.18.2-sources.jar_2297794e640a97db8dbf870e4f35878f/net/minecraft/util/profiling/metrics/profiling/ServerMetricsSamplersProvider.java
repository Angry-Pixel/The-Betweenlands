package net.minecraft.util.profiling.metrics.profiling;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class ServerMetricsSamplersProvider implements MetricsSamplerProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Set<MetricSampler> samplers = new ObjectOpenHashSet<>();
   private final ProfilerSamplerAdapter samplerFactory = new ProfilerSamplerAdapter();

   public ServerMetricsSamplersProvider(LongSupplier p_146180_, boolean p_146181_) {
      this.samplers.add(tickTimeSampler(p_146180_));
      if (p_146181_) {
         this.samplers.addAll(runtimeIndependentSamplers());
      }

   }

   public static Set<MetricSampler> runtimeIndependentSamplers() {
      Builder<MetricSampler> builder = ImmutableSet.builder();

      try {
         ServerMetricsSamplersProvider.CpuStats servermetricssamplersprovider$cpustats = new ServerMetricsSamplersProvider.CpuStats();
         IntStream.range(0, servermetricssamplersprovider$cpustats.nrOfCpus).mapToObj((p_146185_) -> {
            return MetricSampler.create("cpu#" + p_146185_, MetricCategory.CPU, () -> {
               return servermetricssamplersprovider$cpustats.loadForCpu(p_146185_);
            });
         }).forEach(builder::add);
      } catch (Throwable throwable) {
         LOGGER.warn("Failed to query cpu, no cpu stats will be recorded", throwable);
      }

      builder.add(MetricSampler.create("heap MiB", MetricCategory.JVM, () -> {
         return (double)((float)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576.0F);
      }));
      builder.addAll(MetricsRegistry.INSTANCE.getRegisteredSamplers());
      return builder.build();
   }

   public Set<MetricSampler> samplers(Supplier<ProfileCollector> p_146191_) {
      this.samplers.addAll(this.samplerFactory.newSamplersFoundInProfiler(p_146191_));
      return this.samplers;
   }

   public static MetricSampler tickTimeSampler(final LongSupplier p_146189_) {
      Stopwatch stopwatch = Stopwatch.createUnstarted(new Ticker() {
         public long read() {
            return p_146189_.getAsLong();
         }
      });
      ToDoubleFunction<Stopwatch> todoublefunction = (p_146187_) -> {
         if (p_146187_.isRunning()) {
            p_146187_.stop();
         }

         long i = p_146187_.elapsed(TimeUnit.NANOSECONDS);
         p_146187_.reset();
         return (double)i;
      };
      MetricSampler.ValueIncreasedByPercentage metricsampler$valueincreasedbypercentage = new MetricSampler.ValueIncreasedByPercentage(2.0F);
      return MetricSampler.builder("ticktime", MetricCategory.TICK_LOOP, todoublefunction, stopwatch).withBeforeTick(Stopwatch::start).withThresholdAlert(metricsampler$valueincreasedbypercentage).build();
   }

   static class CpuStats {
      private final SystemInfo systemInfo = new SystemInfo();
      private final CentralProcessor processor = this.systemInfo.getHardware().getProcessor();
      public final int nrOfCpus = this.processor.getLogicalProcessorCount();
      private long[][] previousCpuLoadTick = this.processor.getProcessorCpuLoadTicks();
      private double[] currentLoad = this.processor.getProcessorCpuLoadBetweenTicks(this.previousCpuLoadTick);
      private long lastPollMs;

      public double loadForCpu(int p_146208_) {
         long i = System.currentTimeMillis();
         if (this.lastPollMs == 0L || this.lastPollMs + 501L < i) {
            this.currentLoad = this.processor.getProcessorCpuLoadBetweenTicks(this.previousCpuLoadTick);
            this.previousCpuLoadTick = this.processor.getProcessorCpuLoadTicks();
            this.lastPollMs = i;
         }

         return this.currentLoad[p_146208_] * 100.0D;
      }
   }
}