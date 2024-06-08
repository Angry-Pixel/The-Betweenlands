package net.minecraft.util.profiling.jfr.stats;

import java.time.Instant;

public interface TimeStamped {
   Instant getTimestamp();
}