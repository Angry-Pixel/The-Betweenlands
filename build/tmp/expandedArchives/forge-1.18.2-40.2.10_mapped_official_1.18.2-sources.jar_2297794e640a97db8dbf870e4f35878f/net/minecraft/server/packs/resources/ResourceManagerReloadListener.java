package net.minecraft.server.packs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ResourceManagerReloadListener extends PreparableReloadListener {
   default CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_10752_, ResourceManager p_10753_, ProfilerFiller p_10754_, ProfilerFiller p_10755_, Executor p_10756_, Executor p_10757_) {
      return p_10752_.wait(Unit.INSTANCE).thenRunAsync(() -> {
         p_10755_.startTick();
         p_10755_.push("listener");
         this.onResourceManagerReload(p_10753_);
         p_10755_.pop();
         p_10755_.endTick();
      }, p_10757_);
   }

   void onResourceManagerReload(ResourceManager p_10758_);
}