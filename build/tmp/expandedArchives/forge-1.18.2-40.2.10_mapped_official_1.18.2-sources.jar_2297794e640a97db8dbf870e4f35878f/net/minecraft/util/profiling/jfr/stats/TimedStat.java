package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;

public interface TimedStat {
   Duration duration();
}