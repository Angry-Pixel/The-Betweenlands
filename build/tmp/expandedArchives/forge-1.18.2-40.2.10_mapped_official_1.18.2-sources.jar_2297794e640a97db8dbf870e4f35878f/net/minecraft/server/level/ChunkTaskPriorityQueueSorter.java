package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class ChunkTaskPriorityQueueSorter implements ChunkHolder.LevelChangeListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<ProcessorHandle<?>, ChunkTaskPriorityQueue<? extends Function<ProcessorHandle<Unit>, ?>>> queues;
   private final Set<ProcessorHandle<?>> sleeping;
   private final ProcessorMailbox<StrictQueue.IntRunnable> mailbox;

   public ChunkTaskPriorityQueueSorter(List<ProcessorHandle<?>> p_140555_, Executor p_140556_, int p_140557_) {
      this.queues = p_140555_.stream().collect(Collectors.toMap(Function.identity(), (p_140561_) -> {
         return new ChunkTaskPriorityQueue<>(p_140561_.name() + "_queue", p_140557_);
      }));
      this.sleeping = Sets.newHashSet(p_140555_);
      this.mailbox = new ProcessorMailbox<>(new StrictQueue.FixedPriorityQueue(4), p_140556_, "sorter");
   }

   public boolean hasWork() {
      return this.mailbox.hasWork() || this.queues.values().stream().anyMatch(ChunkTaskPriorityQueue::hasWork);
   }

   public static <T> ChunkTaskPriorityQueueSorter.Message<T> message(Function<ProcessorHandle<Unit>, T> p_143182_, long p_143183_, IntSupplier p_143184_) {
      return new ChunkTaskPriorityQueueSorter.Message<>(p_143182_, p_143183_, p_143184_);
   }

   public static ChunkTaskPriorityQueueSorter.Message<Runnable> message(Runnable p_140625_, long p_140626_, IntSupplier p_140627_) {
      return new ChunkTaskPriorityQueueSorter.Message<>((p_140634_) -> {
         return () -> {
            p_140625_.run();
            p_140634_.tell(Unit.INSTANCE);
         };
      }, p_140626_, p_140627_);
   }

   public static ChunkTaskPriorityQueueSorter.Message<Runnable> message(ChunkHolder p_140643_, Runnable p_140644_) {
      return message(p_140644_, p_140643_.getPos().toLong(), p_140643_::getQueueLevel);
   }

   public static <T> ChunkTaskPriorityQueueSorter.Message<T> message(ChunkHolder p_143157_, Function<ProcessorHandle<Unit>, T> p_143158_) {
      return message(p_143158_, p_143157_.getPos().toLong(), p_143157_::getQueueLevel);
   }

   public static ChunkTaskPriorityQueueSorter.Release release(Runnable p_140629_, long p_140630_, boolean p_140631_) {
      return new ChunkTaskPriorityQueueSorter.Release(p_140629_, p_140630_, p_140631_);
   }

   public <T> ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<T>> getProcessor(ProcessorHandle<T> p_140605_, boolean p_140606_) {
      return this.mailbox.<ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<T>>>ask((p_140610_) -> {
         return new StrictQueue.IntRunnable(0, () -> {
            this.getQueue(p_140605_);
            p_140610_.tell(ProcessorHandle.of("chunk priority sorter around " + p_140605_.name(), (p_143176_) -> {
               this.submit(p_140605_, ((Message<T>)p_143176_).task, ((Message<T>)p_143176_).pos, ((Message<T>)p_143176_).level, p_140606_);
            }));
         });
      }).join();
   }

   public ProcessorHandle<ChunkTaskPriorityQueueSorter.Release> getReleaseProcessor(ProcessorHandle<Runnable> p_140568_) {
      return this.mailbox.<ProcessorHandle<ChunkTaskPriorityQueueSorter.Release>>ask((p_140581_) -> {
         return new StrictQueue.IntRunnable(0, () -> {
            p_140581_.tell(ProcessorHandle.of("chunk priority sorter around " + p_140568_.name(), (p_143165_) -> {
               this.release(p_140568_, ((Release)p_143165_).pos, ((Release)p_143165_).task, ((Release)p_143165_).clearQueue);
            }));
         });
      }).join();
   }

   public void onLevelChange(ChunkPos p_140616_, IntSupplier p_140617_, int p_140618_, IntConsumer p_140619_) {
      this.mailbox.tell(new StrictQueue.IntRunnable(0, () -> {
         int i = p_140617_.getAsInt();
         this.queues.values().forEach((p_143155_) -> {
            p_143155_.resortChunkTasks(i, p_140616_, p_140618_);
         });
         p_140619_.accept(p_140618_);
      }));
   }

   private <T> void release(ProcessorHandle<T> p_140570_, long p_140571_, Runnable p_140572_, boolean p_140573_) {
      this.mailbox.tell(new StrictQueue.IntRunnable(1, () -> {
         ChunkTaskPriorityQueue<Function<ProcessorHandle<Unit>, T>> chunktaskpriorityqueue = this.getQueue(p_140570_);
         chunktaskpriorityqueue.release(p_140571_, p_140573_);
         if (this.sleeping.remove(p_140570_)) {
            this.pollTask(chunktaskpriorityqueue, p_140570_);
         }

         p_140572_.run();
      }));
   }

   private <T> void submit(ProcessorHandle<T> p_140590_, Function<ProcessorHandle<Unit>, T> p_140591_, long p_140592_, IntSupplier p_140593_, boolean p_140594_) {
      this.mailbox.tell(new StrictQueue.IntRunnable(2, () -> {
         ChunkTaskPriorityQueue<Function<ProcessorHandle<Unit>, T>> chunktaskpriorityqueue = this.getQueue(p_140590_);
         int i = p_140593_.getAsInt();
         chunktaskpriorityqueue.submit(Optional.of(p_140591_), p_140592_, i);
         if (p_140594_) {
            chunktaskpriorityqueue.submit(Optional.empty(), p_140592_, i);
         }

         if (this.sleeping.remove(p_140590_)) {
            this.pollTask(chunktaskpriorityqueue, p_140590_);
         }

      }));
   }

   private <T> void pollTask(ChunkTaskPriorityQueue<Function<ProcessorHandle<Unit>, T>> p_140646_, ProcessorHandle<T> p_140647_) {
      this.mailbox.tell(new StrictQueue.IntRunnable(3, () -> {
         Stream<Either<Function<ProcessorHandle<Unit>, T>, Runnable>> stream = p_140646_.pop();
         if (stream == null) {
            this.sleeping.add(p_140647_);
         } else {
            CompletableFuture.allOf(stream.map((p_143172_) -> {
               return p_143172_.map(p_140647_::ask, (p_143180_) -> {
                  p_143180_.run();
                  return CompletableFuture.completedFuture(Unit.INSTANCE);
               });
            }).toArray((p_212890_) -> {
               return new CompletableFuture[p_212890_];
            })).thenAccept((p_212894_) -> {
               this.pollTask(p_140646_, p_140647_);
            });
         }

      }));
   }

   private <T> ChunkTaskPriorityQueue<Function<ProcessorHandle<Unit>, T>> getQueue(ProcessorHandle<T> p_140653_) {
      ChunkTaskPriorityQueue<? extends Function<ProcessorHandle<Unit>, ?>> chunktaskpriorityqueue = this.queues.get(p_140653_);
      if (chunktaskpriorityqueue == null) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("No queue for: " + p_140653_));
      } else {
         return (ChunkTaskPriorityQueue<Function<ProcessorHandle<Unit>, T>>)chunktaskpriorityqueue;
      }
   }

   @VisibleForTesting
   public String getDebugStatus() {
      return (String)this.queues.entrySet().stream().map((p_212898_) -> {
         return p_212898_.getKey().name() + "=[" + (String)p_212898_.getValue().getAcquired().stream().map((p_212896_) -> {
            return p_212896_ + ":" + new ChunkPos(p_212896_);
         }).collect(Collectors.joining(",")) + "]";
      }).collect(Collectors.joining(",")) + ", s=" + this.sleeping.size();
   }

   public void close() {
      this.queues.keySet().forEach(ProcessorHandle::close);
   }

   public static final class Message<T> {
      final Function<ProcessorHandle<Unit>, T> task;
      final long pos;
      final IntSupplier level;

      Message(Function<ProcessorHandle<Unit>, T> p_140668_, long p_140669_, IntSupplier p_140670_) {
         this.task = p_140668_;
         this.pos = p_140669_;
         this.level = p_140670_;
      }
   }

   public static final class Release {
      final Runnable task;
      final long pos;
      final boolean clearQueue;

      Release(Runnable p_140686_, long p_140687_, boolean p_140688_) {
         this.task = p_140686_;
         this.pos = p_140687_;
         this.clearQueue = p_140688_;
      }
   }
}