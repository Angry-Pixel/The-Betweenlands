package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2BooleanFunction;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.Util;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.ProfilerMeasured;
import org.slf4j.Logger;

public class ProcessorMailbox<T> implements ProfilerMeasured, ProcessorHandle<T>, AutoCloseable, Runnable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int CLOSED_BIT = 1;
   private static final int SCHEDULED_BIT = 2;
   private final AtomicInteger status = new AtomicInteger(0);
   private final StrictQueue<? super T, ? extends Runnable> queue;
   private final Executor dispatcher;
   private final String name;

   public static ProcessorMailbox<Runnable> create(Executor p_18752_, String p_18753_) {
      return new ProcessorMailbox<>(new StrictQueue.QueueStrictQueue<>(new ConcurrentLinkedQueue<>()), p_18752_, p_18753_);
   }

   public ProcessorMailbox(StrictQueue<? super T, ? extends Runnable> p_18741_, Executor p_18742_, String p_18743_) {
      this.dispatcher = p_18742_;
      this.queue = p_18741_;
      this.name = p_18743_;
      MetricsRegistry.INSTANCE.add(this);
   }

   private boolean setAsScheduled() {
      int i;
      do {
         i = this.status.get();
         if ((i & 3) != 0) {
            return false;
         }
      } while(!this.status.compareAndSet(i, i | 2));

      return true;
   }

   private void setAsIdle() {
      int i;
      do {
         i = this.status.get();
      } while(!this.status.compareAndSet(i, i & -3));

   }

   private boolean canBeScheduled() {
      if ((this.status.get() & 1) != 0) {
         return false;
      } else {
         return !this.queue.isEmpty();
      }
   }

   public void close() {
      int i;
      do {
         i = this.status.get();
      } while(!this.status.compareAndSet(i, i | 1));

   }

   private boolean shouldProcess() {
      return (this.status.get() & 2) != 0;
   }

   private boolean pollTask() {
      if (!this.shouldProcess()) {
         return false;
      } else {
         Runnable runnable = this.queue.pop();
         if (runnable == null) {
            return false;
         } else {
            Util.wrapThreadWithTaskName(this.name, runnable).run();
            return true;
         }
      }
   }

   public void run() {
      try {
         this.pollUntil((p_18746_) -> {
            return p_18746_ == 0;
         });
      } finally {
         this.setAsIdle();
         this.registerForExecution();
      }

   }

   public void runAll() {
      try {
         this.pollUntil((p_182331_) -> {
            return true;
         });
      } finally {
         this.setAsIdle();
         this.registerForExecution();
      }

   }

   public void tell(T p_18750_) {
      this.queue.push(p_18750_);
      this.registerForExecution();
   }

   private void registerForExecution() {
      if (this.canBeScheduled() && this.setAsScheduled()) {
         try {
            this.dispatcher.execute(this);
         } catch (RejectedExecutionException rejectedexecutionexception1) {
            try {
               this.dispatcher.execute(this);
            } catch (RejectedExecutionException rejectedexecutionexception) {
               LOGGER.error("Cound not schedule mailbox", (Throwable)rejectedexecutionexception);
            }
         }
      }

   }

   private int pollUntil(Int2BooleanFunction p_18748_) {
      int i;
      for(i = 0; p_18748_.get(i) && this.pollTask(); ++i) {
      }

      return i;
   }

   public int size() {
      return this.queue.size();
   }

   public boolean hasWork() {
      return this.shouldProcess() && !this.queue.isEmpty();
   }

   public String toString() {
      return this.name + " " + this.status.get() + " " + this.queue.isEmpty();
   }

   public String name() {
      return this.name;
   }

   public List<MetricSampler> profiledMetrics() {
      return ImmutableList.of(MetricSampler.create(this.name + "-queue-size", MetricCategory.MAIL_BOXES, this::size));
   }
}