package net.minecraft.util.profiling.jfr.stats;

import com.google.common.base.MoreObjects;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

public record ThreadAllocationStat(Instant timestamp, String threadName, long totalBytes) {
   private static final String UNKNOWN_THREAD = "unknown";

   public static ThreadAllocationStat from(RecordedEvent p_185804_) {
      RecordedThread recordedthread = p_185804_.getThread("thread");
      String s = recordedthread == null ? "unknown" : MoreObjects.firstNonNull(recordedthread.getJavaName(), "unknown");
      return new ThreadAllocationStat(p_185804_.getStartTime(), s, p_185804_.getLong("allocated"));
   }

   public static ThreadAllocationStat.Summary summary(List<ThreadAllocationStat> p_185798_) {
      Map<String, Double> map = new TreeMap<>();
      Map<String, List<ThreadAllocationStat>> map1 = p_185798_.stream().collect(Collectors.groupingBy((p_185796_) -> {
         return p_185796_.threadName;
      }));
      map1.forEach((p_185801_, p_185802_) -> {
         if (p_185802_.size() >= 2) {
            ThreadAllocationStat threadallocationstat = p_185802_.get(0);
            ThreadAllocationStat threadallocationstat1 = p_185802_.get(p_185802_.size() - 1);
            long i = Duration.between(threadallocationstat.timestamp, threadallocationstat1.timestamp).getSeconds();
            long j = threadallocationstat1.totalBytes - threadallocationstat.totalBytes;
            map.put(p_185801_, (double)j / (double)i);
         }
      });
      return new ThreadAllocationStat.Summary(map);
   }

   public static record Summary(Map<String, Double> allocationsPerSecondByThread) {
   }
}