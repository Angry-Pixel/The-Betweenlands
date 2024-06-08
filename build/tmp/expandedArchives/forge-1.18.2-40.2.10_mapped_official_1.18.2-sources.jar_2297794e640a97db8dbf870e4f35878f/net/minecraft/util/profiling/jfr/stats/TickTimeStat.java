package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record TickTimeStat(Instant timestamp, Duration currentAverage) {
   public static TickTimeStat from(RecordedEvent p_185826_) {
      return new TickTimeStat(p_185826_.getStartTime(), p_185826_.getDuration("averageTickDuration"));
   }
}