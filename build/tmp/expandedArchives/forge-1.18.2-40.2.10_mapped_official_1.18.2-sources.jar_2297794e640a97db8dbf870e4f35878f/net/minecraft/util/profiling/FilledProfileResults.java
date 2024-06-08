package net.minecraft.util.profiling;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class FilledProfileResults implements ProfileResults {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ProfilerPathEntry EMPTY = new ProfilerPathEntry() {
      public long getDuration() {
         return 0L;
      }

      public long getMaxDuration() {
         return 0L;
      }

      public long getCount() {
         return 0L;
      }

      public Object2LongMap<String> getCounters() {
         return Object2LongMaps.emptyMap();
      }
   };
   private static final Splitter SPLITTER = Splitter.on('\u001e');
   private static final Comparator<Entry<String, FilledProfileResults.CounterCollector>> COUNTER_ENTRY_COMPARATOR = Entry.<String, FilledProfileResults.CounterCollector>comparingByValue(Comparator.comparingLong((p_18489_) -> {
      return p_18489_.totalValue;
   })).reversed();
   private final Map<String, ? extends ProfilerPathEntry> entries;
   private final long startTimeNano;
   private final int startTimeTicks;
   private final long endTimeNano;
   private final int endTimeTicks;
   private final int tickDuration;

   public FilledProfileResults(Map<String, ? extends ProfilerPathEntry> p_18464_, long p_18465_, int p_18466_, long p_18467_, int p_18468_) {
      this.entries = p_18464_;
      this.startTimeNano = p_18465_;
      this.startTimeTicks = p_18466_;
      this.endTimeNano = p_18467_;
      this.endTimeTicks = p_18468_;
      this.tickDuration = p_18468_ - p_18466_;
   }

   private ProfilerPathEntry getEntry(String p_18526_) {
      ProfilerPathEntry profilerpathentry = this.entries.get(p_18526_);
      return profilerpathentry != null ? profilerpathentry : EMPTY;
   }

   public List<ResultField> getTimes(String p_18493_) {
      String s = p_18493_;
      ProfilerPathEntry profilerpathentry = this.getEntry("root");
      long i = profilerpathentry.getDuration();
      ProfilerPathEntry profilerpathentry1 = this.getEntry(p_18493_);
      long j = profilerpathentry1.getDuration();
      long k = profilerpathentry1.getCount();
      List<ResultField> list = Lists.newArrayList();
      if (!p_18493_.isEmpty()) {
         p_18493_ = p_18493_ + "\u001e";
      }

      long l = 0L;

      for(String s1 : this.entries.keySet()) {
         if (isDirectChild(p_18493_, s1)) {
            l += this.getEntry(s1).getDuration();
         }
      }

      float f = (float)l;
      if (l < j) {
         l = j;
      }

      if (i < l) {
         i = l;
      }

      for(String s2 : this.entries.keySet()) {
         if (isDirectChild(p_18493_, s2)) {
            ProfilerPathEntry profilerpathentry2 = this.getEntry(s2);
            long i1 = profilerpathentry2.getDuration();
            double d0 = (double)i1 * 100.0D / (double)l;
            double d1 = (double)i1 * 100.0D / (double)i;
            String s3 = s2.substring(p_18493_.length());
            list.add(new ResultField(s3, d0, d1, profilerpathentry2.getCount()));
         }
      }

      if ((float)l > f) {
         list.add(new ResultField("unspecified", (double)((float)l - f) * 100.0D / (double)l, (double)((float)l - f) * 100.0D / (double)i, k));
      }

      Collections.sort(list);
      list.add(0, new ResultField(s, 100.0D, (double)l * 100.0D / (double)i, k));
      return list;
   }

   private static boolean isDirectChild(String p_18495_, String p_18496_) {
      return p_18496_.length() > p_18495_.length() && p_18496_.startsWith(p_18495_) && p_18496_.indexOf(30, p_18495_.length() + 1) < 0;
   }

   private Map<String, FilledProfileResults.CounterCollector> getCounterValues() {
      Map<String, FilledProfileResults.CounterCollector> map = Maps.newTreeMap();
      this.entries.forEach((p_18512_, p_18513_) -> {
         Object2LongMap<String> object2longmap = p_18513_.getCounters();
         if (!object2longmap.isEmpty()) {
            List<String> list = SPLITTER.splitToList(p_18512_);
            object2longmap.forEach((p_145944_, p_145945_) -> {
               map.computeIfAbsent(p_145944_, (p_145947_) -> {
                  return new FilledProfileResults.CounterCollector();
               }).addValue(list.iterator(), p_145945_);
            });
         }

      });
      return map;
   }

   public long getStartTimeNano() {
      return this.startTimeNano;
   }

   public int getStartTimeTicks() {
      return this.startTimeTicks;
   }

   public long getEndTimeNano() {
      return this.endTimeNano;
   }

   public int getEndTimeTicks() {
      return this.endTimeTicks;
   }

   public boolean saveResults(Path p_145940_) {
      Writer writer = null;

      boolean flag;
      try {
         Files.createDirectories(p_145940_.getParent());
         writer = Files.newBufferedWriter(p_145940_, StandardCharsets.UTF_8);
         writer.write(this.getProfilerResults(this.getNanoDuration(), this.getTickDuration()));
         return true;
      } catch (Throwable throwable) {
         LOGGER.error("Could not save profiler results to {}", p_145940_, throwable);
         flag = false;
      } finally {
         IOUtils.closeQuietly(writer);
      }

      return flag;
   }

   protected String getProfilerResults(long p_18486_, int p_18487_) {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("---- Minecraft Profiler Results ----\n");
      stringbuilder.append("// ");
      stringbuilder.append(getComment());
      stringbuilder.append("\n\n");
      stringbuilder.append("Version: ").append(SharedConstants.getCurrentVersion().getId()).append('\n');
      stringbuilder.append("Time span: ").append(p_18486_ / 1000000L).append(" ms\n");
      stringbuilder.append("Tick span: ").append(p_18487_).append(" ticks\n");
      stringbuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float)p_18487_ / ((float)p_18486_ / 1.0E9F))).append(" ticks per second. It should be ").append((int)20).append(" ticks per second\n\n");
      stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.appendProfilerResults(0, "root", stringbuilder);
      stringbuilder.append("--- END PROFILE DUMP ---\n\n");
      Map<String, FilledProfileResults.CounterCollector> map = this.getCounterValues();
      if (!map.isEmpty()) {
         stringbuilder.append("--- BEGIN COUNTER DUMP ---\n\n");
         this.appendCounters(map, stringbuilder, p_18487_);
         stringbuilder.append("--- END COUNTER DUMP ---\n\n");
      }

      return stringbuilder.toString();
   }

   public String getProfilerResults() {
      StringBuilder stringbuilder = new StringBuilder();
      this.appendProfilerResults(0, "root", stringbuilder);
      return stringbuilder.toString();
   }

   private static StringBuilder indentLine(StringBuilder p_18498_, int p_18499_) {
      p_18498_.append(String.format("[%02d] ", p_18499_));

      for(int i = 0; i < p_18499_; ++i) {
         p_18498_.append("|   ");
      }

      return p_18498_;
   }

   private void appendProfilerResults(int p_18482_, String p_18483_, StringBuilder p_18484_) {
      List<ResultField> list = this.getTimes(p_18483_);
      Object2LongMap<String> object2longmap = ObjectUtils.firstNonNull(this.entries.get(p_18483_), EMPTY).getCounters();
      object2longmap.forEach((p_18508_, p_18509_) -> {
         indentLine(p_18484_, p_18482_).append('#').append(p_18508_).append(' ').append((Object)p_18509_).append('/').append(p_18509_ / (long)this.tickDuration).append('\n');
      });
      if (list.size() >= 3) {
         for(int i = 1; i < list.size(); ++i) {
            ResultField resultfield = list.get(i);
            indentLine(p_18484_, p_18482_).append(resultfield.name).append('(').append(resultfield.count).append('/').append(String.format(Locale.ROOT, "%.0f", (float)resultfield.count / (float)this.tickDuration)).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", resultfield.percentage)).append("%/").append(String.format(Locale.ROOT, "%.2f", resultfield.globalPercentage)).append("%\n");
            if (!"unspecified".equals(resultfield.name)) {
               try {
                  this.appendProfilerResults(p_18482_ + 1, p_18483_ + "\u001e" + resultfield.name, p_18484_);
               } catch (Exception exception) {
                  p_18484_.append("[[ EXCEPTION ").append((Object)exception).append(" ]]");
               }
            }
         }

      }
   }

   private void appendCounterResults(int p_18476_, String p_18477_, FilledProfileResults.CounterCollector p_18478_, int p_18479_, StringBuilder p_18480_) {
      indentLine(p_18480_, p_18476_).append(p_18477_).append(" total:").append(p_18478_.selfValue).append('/').append(p_18478_.totalValue).append(" average: ").append(p_18478_.selfValue / (long)p_18479_).append('/').append(p_18478_.totalValue / (long)p_18479_).append('\n');
      p_18478_.children.entrySet().stream().sorted(COUNTER_ENTRY_COMPARATOR).forEach((p_18474_) -> {
         this.appendCounterResults(p_18476_ + 1, p_18474_.getKey(), p_18474_.getValue(), p_18479_, p_18480_);
      });
   }

   private void appendCounters(Map<String, FilledProfileResults.CounterCollector> p_18515_, StringBuilder p_18516_, int p_18517_) {
      p_18515_.forEach((p_18503_, p_18504_) -> {
         p_18516_.append("-- Counter: ").append(p_18503_).append(" --\n");
         this.appendCounterResults(0, "root", p_18504_.children.get("root"), p_18517_, p_18516_);
         p_18516_.append("\n\n");
      });
   }

   private static String getComment() {
      String[] astring = new String[]{"I'd Rather Be Surfing", "Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

      try {
         return astring[(int)(Util.getNanos() % (long)astring.length)];
      } catch (Throwable throwable) {
         return "Witty comment unavailable :(";
      }
   }

   public int getTickDuration() {
      return this.tickDuration;
   }

   static class CounterCollector {
      long selfValue;
      long totalValue;
      final Map<String, FilledProfileResults.CounterCollector> children = Maps.newHashMap();

      public void addValue(Iterator<String> p_18548_, long p_18549_) {
         this.totalValue += p_18549_;
         if (!p_18548_.hasNext()) {
            this.selfValue += p_18549_;
         } else {
            this.children.computeIfAbsent(p_18548_.next(), (p_18546_) -> {
               return new FilledProfileResults.CounterCollector();
            }).addValue(p_18548_, p_18549_);
         }

      }
   }
}