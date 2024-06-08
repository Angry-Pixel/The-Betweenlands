package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import org.slf4j.Logger;

public abstract class BlockableEventLoop<R extends Runnable> implements ProfilerMeasured, ProcessorHandle<R>, Executor {
   private final String name;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Queue<R> pendingRunnables = Queues.newConcurrentLinkedQueue();
   private int blockingCount;

   protected BlockableEventLoop(String p_18686_) {
      this.name = p_18686_;
      MetricsRegistry.INSTANCE.add(this);
   }

   protected abstract R wrapRunnable(Runnable p_18704_);

   protected abstract boolean shouldRun(R p_18703_);

   public boolean isSameThread() {
      return Thread.currentThread() == this.getRunningThread();
   }

   protected abstract Thread getRunningThread();

   protected boolean scheduleExecutables() {
      return !this.isSameThread();
   }

   public int getPendingTasksCount() {
      return this.pendingRunnables.size();
   }

   public String name() {
      return this.name;
   }

   public <V> CompletableFuture<V> submit(Supplier<V> p_18692_) {
      return this.scheduleExecutables() ? CompletableFuture.supplyAsync(p_18692_, this) : CompletableFuture.completedFuture(p_18692_.get());
   }

   public CompletableFuture<Void> submitAsync(Runnable p_18690_) {
      return CompletableFuture.supplyAsync(() -> {
         p_18690_.run();
         return null;
      }, this);
   }

   public CompletableFuture<Void> submit(Runnable p_18708_) {
      if (this.scheduleExecutables()) {
         return this.submitAsync(p_18708_);
      } else {
         p_18708_.run();
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   public void executeBlocking(Runnable p_18710_) {
      if (!this.isSameThread()) {
         this.submitAsync(p_18710_).join();
      } else {
         p_18710_.run();
      }

   }

   public void tell(R p_18712_) {
      this.pendingRunnables.add(p_18712_);
      LockSupport.unpark(this.getRunningThread());
   }

   public void execute(Runnable p_18706_) {
      if (this.scheduleExecutables()) {
         this.tell(this.wrapRunnable(p_18706_));
      } else {
         p_18706_.run();
      }

   }

   public void executeIfPossible(Runnable p_201937_) {
      this.execute(p_201937_);
   }

   protected void dropAllTasks() {
      this.pendingRunnables.clear();
   }

   protected void runAllTasks() {
      while(this.pollTask()) {
      }

   }

   public boolean pollTask() {
      R r = this.pendingRunnables.peek();
      if (r == null) {
         return false;
      } else if (this.blockingCount == 0 && !this.shouldRun(r)) {
         return false;
      } else {
         this.doRunTask(this.pendingRunnables.remove());
         return true;
      }
   }

   public void managedBlock(BooleanSupplier p_18702_) {
      ++this.blockingCount;

      try {
         while(!p_18702_.getAsBoolean()) {
            if (!this.pollTask()) {
               this.waitForTasks();
            }
         }
      } finally {
         --this.blockingCount;
      }

   }

   protected void waitForTasks() {
      Thread.yield();
      LockSupport.parkNanos("waiting for tasks", 100000L);
   }

   protected void doRunTask(R p_18700_) {
      try {
         p_18700_.run();
      } catch (Exception exception) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Error executing task on {}", this.name(), exception);
      }

   }

   public List<MetricSampler> profiledMetrics() {
      return ImmutableList.of(MetricSampler.create(this.name + "-pending-tasks", MetricCategory.EVENT_LOOPS, this::getPendingTasksCount));
   }
}