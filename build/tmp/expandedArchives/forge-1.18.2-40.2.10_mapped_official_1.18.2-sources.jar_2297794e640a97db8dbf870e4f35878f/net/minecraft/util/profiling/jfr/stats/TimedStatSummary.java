package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.jfr.Percentiles;

public record TimedStatSummary<T extends TimedStat>(T fastest, T slowest, @Nullable T secondSlowest, int count, Map<Integer, Double> percentilesNanos, Duration totalDuration) {
   public static <T extends TimedStat> TimedStatSummary<T> summary(List<T> p_185850_) {
      if (p_185850_.isEmpty()) {
         throw new IllegalArgumentException("No values");
      } else {
         List<T> list = p_185850_.stream().sorted(Comparator.comparing(TimedStat::duration)).toList();
         Duration duration = list.stream().map(TimedStat::duration).reduce(Duration::plus).orElse(Duration.ZERO);
         T t = list.get(0);
         T t1 = list.get(list.size() - 1);
         T t2 = list.size() > 1 ? list.get(list.size() - 2) : null;
         int i = list.size();
         Map<Integer, Double> map = Percentiles.evaluate(list.stream().mapToLong((p_185848_) -> {
            return p_185848_.duration().toNanos();
         }).toArray());
         return new TimedStatSummary<>(t, t1, t2, i, map, duration);
      }
   }
}