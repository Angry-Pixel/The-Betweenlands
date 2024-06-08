package net.minecraft.util.profiling.jfr.stats;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public record FileIOStat(Duration duration, @Nullable String path, long bytes) {
   public static FileIOStat.Summary summary(Duration p_185641_, List<FileIOStat> p_185642_) {
      long i = p_185642_.stream().mapToLong((p_185652_) -> {
         return p_185652_.bytes;
      }).sum();
      return new FileIOStat.Summary(i, (double)i / (double)p_185641_.getSeconds(), (long)p_185642_.size(), (double)p_185642_.size() / (double)p_185641_.getSeconds(), p_185642_.stream().map(FileIOStat::duration).reduce(Duration.ZERO, Duration::plus), p_185642_.stream().filter((p_185650_) -> {
         return p_185650_.path != null;
      }).collect(Collectors.groupingBy((p_185647_) -> {
         return p_185647_.path;
      }, Collectors.summingLong((p_185639_) -> {
         return p_185639_.bytes;
      }))).entrySet().stream().sorted(Entry.<String, Long>comparingByValue().reversed()).map((p_185644_) -> {
         return Pair.of(p_185644_.getKey(), p_185644_.getValue());
      }).limit(10L).toList());
   }

   public static record Summary(long totalBytes, double bytesPerSecond, long counts, double countsPerSecond, Duration timeSpentInIO, List<Pair<String, Long>> topTenContributorsByTotalBytes) {
   }
}