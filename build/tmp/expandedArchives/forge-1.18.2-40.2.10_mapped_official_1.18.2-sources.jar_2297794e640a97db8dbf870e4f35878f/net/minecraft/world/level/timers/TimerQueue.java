package net.minecraft.world.level.timers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

public class TimerQueue<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String CALLBACK_DATA_TAG = "Callback";
   private static final String TIMER_NAME_TAG = "Name";
   private static final String TIMER_TRIGGER_TIME_TAG = "TriggerTime";
   private final TimerCallbacks<T> callbacksRegistry;
   private final Queue<TimerQueue.Event<T>> queue = new PriorityQueue<>(createComparator());
   private UnsignedLong sequentialId = UnsignedLong.ZERO;
   private final Table<String, Long, TimerQueue.Event<T>> events = HashBasedTable.create();

   private static <T> Comparator<TimerQueue.Event<T>> createComparator() {
      return Comparator.<TimerQueue.Event<T>>comparingLong((p_82272_) -> {
         return p_82272_.triggerTime;
      }).thenComparing((p_82269_) -> {
         return p_82269_.sequentialId;
      });
   }

   public TimerQueue(TimerCallbacks<T> p_82249_, Stream<Dynamic<Tag>> p_82250_) {
      this(p_82249_);
      this.queue.clear();
      this.events.clear();
      this.sequentialId = UnsignedLong.ZERO;
      p_82250_.forEach((p_82253_) -> {
         if (!(p_82253_.getValue() instanceof CompoundTag)) {
            LOGGER.warn("Invalid format of events: {}", (Object)p_82253_);
         } else {
            this.loadEvent((CompoundTag)p_82253_.getValue());
         }
      });
   }

   public TimerQueue(TimerCallbacks<T> p_82247_) {
      this.callbacksRegistry = p_82247_;
   }

   public void tick(T p_82257_, long p_82258_) {
      while(true) {
         TimerQueue.Event<T> event = this.queue.peek();
         if (event == null || event.triggerTime > p_82258_) {
            return;
         }

         this.queue.remove();
         this.events.remove(event.id, p_82258_);
         event.callback.handle(p_82257_, this, p_82258_);
      }
   }

   public void schedule(String p_82262_, long p_82263_, TimerCallback<T> p_82264_) {
      if (!this.events.contains(p_82262_, p_82263_)) {
         this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
         TimerQueue.Event<T> event = new TimerQueue.Event<>(p_82263_, this.sequentialId, p_82262_, p_82264_);
         this.events.put(p_82262_, p_82263_, event);
         this.queue.add(event);
      }
   }

   public int remove(String p_82260_) {
      Collection<TimerQueue.Event<T>> collection = this.events.row(p_82260_).values();
      collection.forEach(this.queue::remove);
      int i = collection.size();
      collection.clear();
      return i;
   }

   public Set<String> getEventsIds() {
      return Collections.unmodifiableSet(this.events.rowKeySet());
   }

   private void loadEvent(CompoundTag p_82266_) {
      CompoundTag compoundtag = p_82266_.getCompound("Callback");
      TimerCallback<T> timercallback = this.callbacksRegistry.deserialize(compoundtag);
      if (timercallback != null) {
         String s = p_82266_.getString("Name");
         long i = p_82266_.getLong("TriggerTime");
         this.schedule(s, i, timercallback);
      }

   }

   private CompoundTag storeEvent(TimerQueue.Event<T> p_82255_) {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putString("Name", p_82255_.id);
      compoundtag.putLong("TriggerTime", p_82255_.triggerTime);
      compoundtag.put("Callback", this.callbacksRegistry.serialize(p_82255_.callback));
      return compoundtag;
   }

   public ListTag store() {
      ListTag listtag = new ListTag();
      this.queue.stream().sorted(createComparator()).map(this::storeEvent).forEach(listtag::add);
      return listtag;
   }

   public static class Event<T> {
      public final long triggerTime;
      public final UnsignedLong sequentialId;
      public final String id;
      public final TimerCallback<T> callback;

      Event(long p_82278_, UnsignedLong p_82279_, String p_82280_, TimerCallback<T> p_82281_) {
         this.triggerTime = p_82278_;
         this.sequentialId = p_82279_;
         this.id = p_82280_;
         this.callback = p_82281_;
      }
   }
}