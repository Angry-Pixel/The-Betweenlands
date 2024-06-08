package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileZipper;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class PerfCommand {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType(new TranslatableComponent("commands.perf.notRunning"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType(new TranslatableComponent("commands.perf.alreadyRunning"));

   public static void register(CommandDispatcher<CommandSourceStack> p_180438_) {
      p_180438_.register(Commands.literal("perf").requires((p_180462_) -> {
         return p_180462_.hasPermission(4);
      }).then(Commands.literal("start").executes((p_180455_) -> {
         return startProfilingDedicatedServer(p_180455_.getSource());
      })).then(Commands.literal("stop").executes((p_180440_) -> {
         return stopProfilingDedicatedServer(p_180440_.getSource());
      })));
   }

   private static int startProfilingDedicatedServer(CommandSourceStack p_180442_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_180442_.getServer();
      if (minecraftserver.isRecordingMetrics()) {
         throw ERROR_ALREADY_RUNNING.create();
      } else {
         Consumer<ProfileResults> consumer = (p_180460_) -> {
            whenStopped(p_180442_, p_180460_);
         };
         Consumer<Path> consumer1 = (p_180453_) -> {
            saveResults(p_180442_, p_180453_, minecraftserver);
         };
         minecraftserver.startRecordingMetrics(consumer, consumer1);
         p_180442_.sendSuccess(new TranslatableComponent("commands.perf.started"), false);
         return 0;
      }
   }

   private static int stopProfilingDedicatedServer(CommandSourceStack p_180457_) throws CommandSyntaxException {
      MinecraftServer minecraftserver = p_180457_.getServer();
      if (!minecraftserver.isRecordingMetrics()) {
         throw ERROR_NOT_RUNNING.create();
      } else {
         minecraftserver.finishRecordingMetrics();
         return 0;
      }
   }

   private static void saveResults(CommandSourceStack p_180447_, Path p_180448_, MinecraftServer p_180449_) {
      String s = String.format("%s-%s-%s", (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()), p_180449_.getWorldData().getLevelName(), SharedConstants.getCurrentVersion().getId());

      String s1;
      try {
         s1 = FileUtil.findAvailableName(MetricsPersister.PROFILING_RESULTS_DIR, s, ".zip");
      } catch (IOException ioexception1) {
         p_180447_.sendFailure(new TranslatableComponent("commands.perf.reportFailed"));
         LOGGER.error("Failed to create report name", (Throwable)ioexception1);
         return;
      }

      FileZipper filezipper = new FileZipper(MetricsPersister.PROFILING_RESULTS_DIR.resolve(s1));

      try {
         filezipper.add(Paths.get("system.txt"), p_180449_.fillSystemReport(new SystemReport()).toLineSeparatedString());
         filezipper.add(p_180448_);
      } catch (Throwable throwable1) {
         try {
            filezipper.close();
         } catch (Throwable throwable) {
            throwable1.addSuppressed(throwable);
         }

         throw throwable1;
      }

      filezipper.close();

      try {
         FileUtils.forceDelete(p_180448_.toFile());
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to delete temporary profiling file {}", p_180448_, ioexception);
      }

      p_180447_.sendSuccess(new TranslatableComponent("commands.perf.reportSaved", s1), false);
   }

   private static void whenStopped(CommandSourceStack p_180444_, ProfileResults p_180445_) {
      int i = p_180445_.getTickDuration();
      double d0 = (double)p_180445_.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
      p_180444_.sendSuccess(new TranslatableComponent("commands.perf.stopped", String.format(Locale.ROOT, "%.2f", d0), i, String.format(Locale.ROOT, "%.2f", (double)i / d0)), false);
   }
}