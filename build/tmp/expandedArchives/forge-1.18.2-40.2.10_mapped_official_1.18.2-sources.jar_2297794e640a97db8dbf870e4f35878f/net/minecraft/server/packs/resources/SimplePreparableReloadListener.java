package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class SimplePreparableReloadListener<T> implements PreparableReloadListener {
   public final CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_10780_, ResourceManager p_10781_, ProfilerFiller p_10782_, ProfilerFiller p_10783_, Executor p_10784_, Executor p_10785_) {
      return CompletableFuture.supplyAsync(() -> {
         return this.prepare(p_10781_, p_10782_);
      }, p_10784_).thenCompose(p_10780_::wait).thenAcceptAsync((p_10792_) -> {
         this.apply(p_10792_, p_10781_, p_10783_);
      }, p_10785_);
   }

   protected abstract T prepare(ResourceManager p_10796_, ProfilerFiller p_10797_);

   protected abstract void apply(T p_10793_, ResourceManager p_10794_, ProfilerFiller p_10795_);
}