package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapStat(Instant timestamp, long heapUsed, GcHeapStat.Timing timing) {
   public static GcHeapStat from(RecordedEvent p_185698_) {
      return new GcHeapStat(p_185698_.getStartTime(), p_185698_.getLong("heapUsed"), p_185698_.getString("when").equalsIgnoreCase("before gc") ? GcHeapStat.Timing.BEFORE_GC : GcHeapStat.Timing.AFTER_GC);
   }

   public static GcHeapStat.Summary summary(Duration p_185691_, List<GcHeapStat> p_185692_, Duration p_185693_, int p_185694_) {
      return new GcHeapStat.Summary(p_185691_, p_185693_, p_185694_, calculateAllocationRatePerSecond(p_185692_));
   }

   private static double calculateAllocationRatePerSecond(List<GcHeapStat> p_185696_) {
      long i = 0L;
      Map<GcHeapStat.Timing, List<GcHeapStat>> map = p_185696_.stream().collect(Collectors.groupingBy((p_185689_) -> {
         return p_185689_.timing;
      }));
      List<GcHeapStat> list = map.get(GcHeapStat.Timing.BEFORE_GC);
      List<GcHeapStat> list1 = map.get(GcHeapStat.Timing.AFTER_GC);

      for(int j = 1; j < list.size(); ++j) {
         GcHeapStat gcheapstat = list.get(j);
         GcHeapStat gcheapstat1 = list1.get(j - 1);
         i += gcheapstat.heapUsed - gcheapstat1.heapUsed;
      }

      Duration duration = Duration.between((p_185696_.get(1)).timestamp, (p_185696_.get(p_185696_.size() - 1)).timestamp);
      return (double)i / (double)duration.getSeconds();
   }

   public static record Summary(Duration duration, Duration gcTotalDuration, int totalGCs, double allocationRateBytesPerSecond) {
      public float gcOverHead() {
         return (float)this.gcTotalDuration.toMillis() / (float)this.duration.toMillis();
      }
   }

   static enum Timing {
      BEFORE_GC,
      AFTER_GC;
   }
}