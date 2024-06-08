package net.minecraft.util.profiling.metrics.profiling;

import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;

public class InactiveMetricsRecorder implements MetricsRecorder {
   public static final MetricsRecorder INSTANCE = new InactiveMetricsRecorder();

   public void end() {
   }

   public void startTick() {
   }

   public boolean isRecording() {
      return false;
   }

   public ProfilerFiller getProfiler() {
      return InactiveProfiler.INSTANCE;
   }

   public void endTick() {
   }
}