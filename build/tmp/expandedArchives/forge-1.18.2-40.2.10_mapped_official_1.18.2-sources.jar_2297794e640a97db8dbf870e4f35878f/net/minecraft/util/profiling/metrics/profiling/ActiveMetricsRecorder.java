package net.minecraft.util.profiling.metrics.profiling;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ContinuousProfiler;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.profiling.metrics.storage.RecordedDeviation;

public class ActiveMetricsRecorder implements MetricsRecorder {
   public static final int PROFILING_MAX_DURATION_SECONDS = 10;
   @Nullable
   private static Consumer<Path> globalOnReportFinished = null;
   private final Map<MetricSampler, List<RecordedDeviation>> deviationsBySampler = new Object2ObjectOpenHashMap<>();
   private final ContinuousProfiler taskProfiler;
   private final Executor ioExecutor;
   private final MetricsPersister metricsPersister;
   private final Consumer<ProfileResults> onProfilingEnd;
   private final Consumer<Path> onReportFinished;
   private final MetricsSamplerProvider metricsSamplerProvider;
   private final LongSupplier wallTimeSource;
   private final long deadlineNano;
   private int currentTick;
   private ProfileCollector singleTickProfiler;
   private volatile boolean killSwitch;
   private Set<MetricSampler> thisTickSamplers = ImmutableSet.of();

   private ActiveMetricsRecorder(MetricsSamplerProvider p_146121_, LongSupplier p_146122_, Executor p_146123_, MetricsPersister p_146124_, Consumer<ProfileResults> p_146125_, Consumer<Path> p_146126_) {
      this.metricsSamplerProvider = p_146121_;
      this.wallTimeSource = p_146122_;
      this.taskProfiler = new ContinuousProfiler(p_146122_, () -> {
         return this.currentTick;
      });
      this.ioExecutor = p_146123_;
      this.metricsPersister = p_146124_;
      this.onProfilingEnd = p_146125_;
      this.onReportFinished = globalOnReportFinished == null ? p_146126_ : p_146126_.andThen(globalOnReportFinished);
      this.deadlineNano = p_146122_.getAsLong() + TimeUnit.NANOSECONDS.convert(10L, TimeUnit.SECONDS);
      this.singleTickProfiler = new ActiveProfiler(this.wallTimeSource, () -> {
         return this.currentTick;
      }, false);
      this.taskProfiler.enable();
   }

   public static ActiveMetricsRecorder createStarted(MetricsSamplerProvider p_146133_, LongSupplier p_146134_, Executor p_146135_, MetricsPersister p_146136_, Consumer<ProfileResults> p_146137_, Consumer<Path> p_146138_) {
      return new ActiveMetricsRecorder(p_146133_, p_146134_, p_146135_, p_146136_, p_146137_, p_146138_);
   }

   public synchronized void end() {
      if (this.isRecording()) {
         this.killSwitch = true;
      }
   }

   public void startTick() {
      this.verifyStarted();
      this.thisTickSamplers = this.metricsSamplerProvider.samplers(() -> {
         return this.singleTickProfiler;
      });

      for(MetricSampler metricsampler : this.thisTickSamplers) {
         metricsampler.onStartTick();
      }

      ++this.currentTick;
   }

   public void endTick() {
      this.verifyStarted();
      if (this.currentTick != 0) {
         for(MetricSampler metricsampler : this.thisTickSamplers) {
            metricsampler.onEndTick(this.currentTick);
            if (metricsampler.triggersThreshold()) {
               RecordedDeviation recordeddeviation = new RecordedDeviation(Instant.now(), this.currentTick, this.singleTickProfiler.getResults());
               this.deviationsBySampler.computeIfAbsent(metricsampler, (p_146131_) -> {
                  return Lists.newArrayList();
               }).add(recordeddeviation);
            }
         }

         if (!this.killSwitch && this.wallTimeSource.getAsLong() <= this.deadlineNano) {
            this.singleTickProfiler = new ActiveProfiler(this.wallTimeSource, () -> {
               return this.currentTick;
            }, false);
         } else {
            this.killSwitch = false;
            this.singleTickProfiler = InactiveProfiler.INSTANCE;
            ProfileResults profileresults = this.taskProfiler.getResults();
            this.onProfilingEnd.accept(profileresults);
            this.scheduleSaveResults(profileresults);
         }
      }
   }

   public boolean isRecording() {
      return this.taskProfiler.isEnabled();
   }

   public ProfilerFiller getProfiler() {
      return ProfilerFiller.tee(this.taskProfiler.getFiller(), this.singleTickProfiler);
   }

   private void verifyStarted() {
      if (!this.isRecording()) {
         throw new IllegalStateException("Not started!");
      }
   }

   private void scheduleSaveResults(ProfileResults p_146129_) {
      HashSet<MetricSampler> hashset = new HashSet<>(this.thisTickSamplers);
      this.ioExecutor.execute(() -> {
         Path path = this.metricsPersister.saveReports(hashset, this.deviationsBySampler, p_146129_);

         for(MetricSampler metricsampler : hashset) {
            metricsampler.onFinished();
         }

         this.deviationsBySampler.clear();
         this.taskProfiler.disable();
         this.onReportFinished.accept(path);
      });
   }

   public static void registerGlobalCompletionCallback(Consumer<Path> p_146143_) {
      globalOnReportFinished = p_146143_;
   }
}