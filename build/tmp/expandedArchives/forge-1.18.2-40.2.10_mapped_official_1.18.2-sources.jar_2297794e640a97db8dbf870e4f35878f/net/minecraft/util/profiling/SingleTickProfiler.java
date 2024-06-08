package net.minecraft.util.profiling;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class SingleTickProfiler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final LongSupplier realTime;
   private final long saveThreshold;
   private int tick;
   private final File location;
   private ProfileCollector profiler = InactiveProfiler.INSTANCE;

   public SingleTickProfiler(LongSupplier p_145963_, String p_145964_, long p_145965_) {
      this.realTime = p_145963_;
      this.location = new File("debug", p_145964_);
      this.saveThreshold = p_145965_;
   }

   public ProfilerFiller startTick() {
      this.profiler = new ActiveProfiler(this.realTime, () -> {
         return this.tick;
      }, false);
      ++this.tick;
      return this.profiler;
   }

   public void endTick() {
      if (this.profiler != InactiveProfiler.INSTANCE) {
         ProfileResults profileresults = this.profiler.getResults();
         this.profiler = InactiveProfiler.INSTANCE;
         if (profileresults.getNanoDuration() >= this.saveThreshold) {
            File file1 = new File(this.location, "tick-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
            profileresults.saveResults(file1.toPath());
            LOGGER.info("Recorded long tick -- wrote info to: {}", (Object)file1.getAbsolutePath());
         }

      }
   }

   @Nullable
   public static SingleTickProfiler createTickProfiler(String p_18633_) {
      return null;
   }

   public static ProfilerFiller decorateFiller(ProfilerFiller p_18630_, @Nullable SingleTickProfiler p_18631_) {
      return p_18631_ != null ? ProfilerFiller.tee(p_18631_.startTick(), p_18630_) : p_18630_;
   }
}