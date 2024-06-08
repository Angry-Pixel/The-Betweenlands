package net.minecraft.util.profiling.metrics;

import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.util.profiling.ProfileCollector;

public interface MetricsSamplerProvider {
   Set<MetricSampler> samplers(Supplier<ProfileCollector> p_146103_);
}