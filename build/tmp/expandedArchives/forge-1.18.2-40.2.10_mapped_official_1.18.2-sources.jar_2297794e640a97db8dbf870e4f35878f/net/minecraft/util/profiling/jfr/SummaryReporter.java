package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.profiling.jfr.parse.JfrStatsParser;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class SummaryReporter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Runnable onDeregistration;

   protected SummaryReporter(Runnable p_185398_) {
      this.onDeregistration = p_185398_;
   }

   public void recordingStopped(@Nullable Path p_185401_) {
      if (p_185401_ != null) {
         this.onDeregistration.run();
         infoWithFallback(() -> {
            return "Dumped flight recorder profiling to " + p_185401_;
         });

         JfrStatsResult jfrstatsresult;
         try {
            jfrstatsresult = JfrStatsParser.parse(p_185401_);
         } catch (Throwable throwable1) {
            warnWithFallback(() -> {
               return "Failed to parse JFR recording";
            }, throwable1);
            return;
         }

         try {
            infoWithFallback(jfrstatsresult::asJson);
            Path path = p_185401_.resolveSibling("jfr-report-" + StringUtils.substringBefore(p_185401_.getFileName().toString(), ".jfr") + ".json");
            Files.writeString(path, jfrstatsresult.asJson(), StandardOpenOption.CREATE);
            infoWithFallback(() -> {
               return "Dumped recording summary to " + path;
            });
         } catch (Throwable throwable) {
            warnWithFallback(() -> {
               return "Failed to output JFR report";
            }, throwable);
         }

      }
   }

   private static void infoWithFallback(Supplier<String> p_201933_) {
      if (LogUtils.isLoggerActive()) {
         LOGGER.info(p_201933_.get());
      } else {
         Bootstrap.realStdoutPrintln(p_201933_.get());
      }

   }

   private static void warnWithFallback(Supplier<String> p_201935_, Throwable p_201936_) {
      if (LogUtils.isLoggerActive()) {
         LOGGER.warn(p_201935_.get(), p_201936_);
      } else {
         Bootstrap.realStdoutPrintln(p_201935_.get());
         p_201936_.printStackTrace(Bootstrap.STDOUT);
      }

   }
}