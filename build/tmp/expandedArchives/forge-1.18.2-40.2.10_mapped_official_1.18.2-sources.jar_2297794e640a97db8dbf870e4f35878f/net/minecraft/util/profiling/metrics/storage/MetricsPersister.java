package net.minecraft.util.profiling.metrics.storage;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class MetricsPersister {
   public static final Path PROFILING_RESULTS_DIR = Paths.get("debug/profiling");
   public static final String METRICS_DIR_NAME = "metrics";
   public static final String DEVIATIONS_DIR_NAME = "deviations";
   public static final String PROFILING_RESULT_FILENAME = "profiling.txt";
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String rootFolderName;

   public MetricsPersister(String p_146217_) {
      this.rootFolderName = p_146217_;
   }

   public Path saveReports(Set<MetricSampler> p_146251_, Map<MetricSampler, List<RecordedDeviation>> p_146252_, ProfileResults p_146253_) {
      try {
         Files.createDirectories(PROFILING_RESULTS_DIR);
      } catch (IOException ioexception1) {
         throw new UncheckedIOException(ioexception1);
      }

      try {
         Path path = Files.createTempDirectory("minecraft-profiling");
         path.toFile().deleteOnExit();
         Files.createDirectories(PROFILING_RESULTS_DIR);
         Path path1 = path.resolve(this.rootFolderName);
         Path path2 = path1.resolve("metrics");
         this.saveMetrics(p_146251_, path2);
         if (!p_146252_.isEmpty()) {
            this.saveDeviations(p_146252_, path1.resolve("deviations"));
         }

         this.saveProfilingTaskExecutionResult(p_146253_, path1);
         return path;
      } catch (IOException ioexception) {
         throw new UncheckedIOException(ioexception);
      }
   }

   private void saveMetrics(Set<MetricSampler> p_146248_, Path p_146249_) {
      if (p_146248_.isEmpty()) {
         throw new IllegalArgumentException("Expected at least one sampler to persist");
      } else {
         Map<MetricCategory, List<MetricSampler>> map = p_146248_.stream().collect(Collectors.groupingBy(MetricSampler::getCategory));
         map.forEach((p_146232_, p_146233_) -> {
            this.saveCategory(p_146232_, p_146233_, p_146249_);
         });
      }
   }

   private void saveCategory(MetricCategory p_146227_, List<MetricSampler> p_146228_, Path p_146229_) {
      Path path = p_146229_.resolve(Util.sanitizeName(p_146227_.getDescription(), ResourceLocation::validPathChar) + ".csv");
      Writer writer = null;

      try {
         Files.createDirectories(path.getParent());
         writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
         CsvOutput.Builder csvoutput$builder = CsvOutput.builder();
         csvoutput$builder.addColumn("@tick");

         for(MetricSampler metricsampler : p_146228_) {
            csvoutput$builder.addColumn(metricsampler.getName());
         }

         CsvOutput csvoutput = csvoutput$builder.build(writer);
         List<MetricSampler.SamplerResult> list = p_146228_.stream().map(MetricSampler::result).collect(Collectors.toList());
         int i = list.stream().mapToInt(MetricSampler.SamplerResult::getFirstTick).summaryStatistics().getMin();
         int j = list.stream().mapToInt(MetricSampler.SamplerResult::getLastTick).summaryStatistics().getMax();

         for(int k = i; k <= j; ++k) {
            int l = k;
            Stream<String> stream = list.stream().map((p_146222_) -> {
               return String.valueOf(p_146222_.valueAtTick(l));
            });
            Object[] aobject = Stream.concat(Stream.of(String.valueOf(k)), stream).toArray((p_146219_) -> {
               return new String[p_146219_];
            });
            csvoutput.writeRow(aobject);
         }

         LOGGER.info("Flushed metrics to {}", (Object)path);
      } catch (Exception exception) {
         LOGGER.error("Could not save profiler results to {}", path, exception);
      } finally {
         IOUtils.closeQuietly(writer);
      }

   }

   private void saveDeviations(Map<MetricSampler, List<RecordedDeviation>> p_146245_, Path p_146246_) {
      DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss.SSS", Locale.UK).withZone(ZoneId.systemDefault());
      p_146245_.forEach((p_146242_, p_146243_) -> {
         p_146243_.forEach((p_146238_) -> {
            String s = datetimeformatter.format(p_146238_.timestamp);
            Path path = p_146246_.resolve(Util.sanitizeName(p_146242_.getName(), ResourceLocation::validPathChar)).resolve(String.format(Locale.ROOT, "%d@%s.txt", p_146238_.tick, s));
            p_146238_.profilerResultAtTick.saveResults(path);
         });
      });
   }

   private void saveProfilingTaskExecutionResult(ProfileResults p_146224_, Path p_146225_) {
      p_146224_.saveResults(p_146225_.resolve("profiling.txt"));
   }
}