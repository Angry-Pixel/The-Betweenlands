package net.minecraft.server.packs.resources;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import org.slf4j.Logger;

public class ProfiledReloadInstance extends SimpleReloadInstance<ProfiledReloadInstance.State> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Stopwatch total = Stopwatch.createUnstarted();

   public ProfiledReloadInstance(ResourceManager p_10649_, List<PreparableReloadListener> p_10650_, Executor p_10651_, Executor p_10652_, CompletableFuture<Unit> p_10653_) {
      super(p_10651_, p_10652_, p_10649_, p_10650_, (p_10668_, p_10669_, p_10670_, p_10671_, p_10672_) -> {
         AtomicLong atomiclong = new AtomicLong();
         AtomicLong atomiclong1 = new AtomicLong();
         ActiveProfiler activeprofiler = new ActiveProfiler(Util.timeSource, () -> {
            return 0;
         }, false);
         ActiveProfiler activeprofiler1 = new ActiveProfiler(Util.timeSource, () -> {
            return 0;
         }, false);
         CompletableFuture<Void> completablefuture = p_10670_.reload(p_10668_, p_10669_, activeprofiler, activeprofiler1, (p_143927_) -> {
            p_10671_.execute(() -> {
               long i = Util.getNanos();
               p_143927_.run();
               atomiclong.addAndGet(Util.getNanos() - i);
            });
         }, (p_143920_) -> {
            p_10672_.execute(() -> {
               long i = Util.getNanos();
               p_143920_.run();
               atomiclong1.addAndGet(Util.getNanos() - i);
            });
         });
         return completablefuture.thenApplyAsync((p_143913_) -> {
            LOGGER.debug("Finished reloading " + p_10670_.getName());
            return new ProfiledReloadInstance.State(p_10670_.getName(), activeprofiler.getResults(), activeprofiler1.getResults(), atomiclong, atomiclong1);
         }, p_10652_);
      }, p_10653_);
      this.total.start();
      this.allDone.thenAcceptAsync(this::finish, p_10652_);
   }

   private void finish(List<ProfiledReloadInstance.State> p_10665_) {
      this.total.stop();
      int i = 0;
      LOGGER.info("Resource reload finished after {} ms", (long)this.total.elapsed(TimeUnit.MILLISECONDS));

      for(ProfiledReloadInstance.State profiledreloadinstance$state : p_10665_) {
         ProfileResults profileresults = profiledreloadinstance$state.preparationResult;
         ProfileResults profileresults1 = profiledreloadinstance$state.reloadResult;
         int j = (int)((double)profiledreloadinstance$state.preparationNanos.get() / 1000000.0D);
         int k = (int)((double)profiledreloadinstance$state.reloadNanos.get() / 1000000.0D);
         int l = j + k;
         String s = profiledreloadinstance$state.name;
         LOGGER.info("{} took approximately {} ms ({} ms preparing, {} ms applying)", s, l, j, k);
         i += k;
      }

      LOGGER.info("Total blocking time: {} ms", (int)i);
   }

   public static class State {
      final String name;
      final ProfileResults preparationResult;
      final ProfileResults reloadResult;
      final AtomicLong preparationNanos;
      final AtomicLong reloadNanos;

      State(String p_10692_, ProfileResults p_10693_, ProfileResults p_10694_, AtomicLong p_10695_, AtomicLong p_10696_) {
         this.name = p_10692_;
         this.preparationResult = p_10693_;
         this.reloadResult = p_10694_;
         this.preparationNanos = p_10695_;
         this.reloadNanos = p_10696_;
      }
   }
}