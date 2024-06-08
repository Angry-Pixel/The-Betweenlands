package net.minecraft.util.profiling.jfr.callback;

@FunctionalInterface
public interface ProfiledDuration {
   void finish();
}