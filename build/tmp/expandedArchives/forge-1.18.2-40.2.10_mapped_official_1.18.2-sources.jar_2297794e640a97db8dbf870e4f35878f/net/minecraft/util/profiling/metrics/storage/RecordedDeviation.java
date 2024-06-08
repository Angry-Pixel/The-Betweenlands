package net.minecraft.util.profiling.metrics.storage;

import java.time.Instant;
import net.minecraft.util.profiling.ProfileResults;

public final class RecordedDeviation {
   public final Instant timestamp;
   public final int tick;
   public final ProfileResults profilerResultAtTick;

   public RecordedDeviation(Instant p_146258_, int p_146259_, ProfileResults p_146260_) {
      this.timestamp = p_146258_;
      this.tick = p_146259_;
      this.profilerResultAtTick = p_146260_;
   }
}