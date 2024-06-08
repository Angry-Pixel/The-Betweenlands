package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public interface StrictQueue<T, F> {
   @Nullable
   F pop();

   boolean push(T p_18770_);

   boolean isEmpty();

   int size();

   public static final class FixedPriorityQueue implements StrictQueue<StrictQueue.IntRunnable, Runnable> {
      private final Queue<Runnable>[] queues;
      private final AtomicInteger size = new AtomicInteger();

      public FixedPriorityQueue(int p_18773_) {
         this.queues = new Queue[p_18773_];

         for(int i = 0; i < p_18773_; ++i) {
            this.queues[i] = Queues.newConcurrentLinkedQueue();
         }

      }

      @Nullable
      public Runnable pop() {
         for(Queue<Runnable> queue : this.queues) {
            Runnable runnable = queue.poll();
            if (runnable != null) {
               this.size.decrementAndGet();
               return runnable;
            }
         }

         return null;
      }

      public boolean push(StrictQueue.IntRunnable p_18778_) {
         int i = p_18778_.priority;
         if (i < this.queues.length && i >= 0) {
            this.queues[i].add(p_18778_);
            this.size.incrementAndGet();
            return true;
         } else {
            throw new IndexOutOfBoundsException("Priority %d not supported. Expected range [0-%d]".formatted(i, this.queues.length - 1));
         }
      }

      public boolean isEmpty() {
         return this.size.get() == 0;
      }

      public int size() {
         return this.size.get();
      }
   }

   public static final class IntRunnable implements Runnable {
      final int priority;
      private final Runnable task;

      public IntRunnable(int p_18786_, Runnable p_18787_) {
         this.priority = p_18786_;
         this.task = p_18787_;
      }

      public void run() {
         this.task.run();
      }

      public int getPriority() {
         return this.priority;
      }
   }

   public static final class QueueStrictQueue<T> implements StrictQueue<T, T> {
      private final Queue<T> queue;

      public QueueStrictQueue(Queue<T> p_18792_) {
         this.queue = p_18792_;
      }

      @Nullable
      public T pop() {
         return this.queue.poll();
      }

      public boolean push(T p_18795_) {
         return this.queue.add(p_18795_);
      }

      public boolean isEmpty() {
         return this.queue.isEmpty();
      }

      public int size() {
         return this.queue.size();
      }
   }
}