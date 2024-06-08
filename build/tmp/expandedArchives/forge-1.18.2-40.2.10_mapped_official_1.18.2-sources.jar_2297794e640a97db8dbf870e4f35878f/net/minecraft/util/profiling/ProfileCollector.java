package net.minecraft.util.profiling;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.metrics.MetricCategory;
import org.apache.commons.lang3.tuple.Pair;

public interface ProfileCollector extends ProfilerFiller {
   ProfileResults getResults();

   @Nullable
   ActiveProfiler.PathEntry getEntry(String p_145955_);

   Set<Pair<String, MetricCategory>> getChartedPaths();
}