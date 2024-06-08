package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record CpuLoadStat(double jvm, double userJvm, double system) {
   public static CpuLoadStat from(RecordedEvent p_185623_) {
      return new CpuLoadStat((double)p_185623_.getFloat("jvmSystem"), (double)p_185623_.getFloat("jvmUser"), (double)p_185623_.getFloat("machineTotal"));
   }
}