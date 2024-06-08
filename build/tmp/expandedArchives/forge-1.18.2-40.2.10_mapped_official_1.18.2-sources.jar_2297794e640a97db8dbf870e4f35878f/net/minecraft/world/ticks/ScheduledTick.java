package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public record ScheduledTick<T>(T type, BlockPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
   public static final Comparator<ScheduledTick<?>> DRAIN_ORDER = (p_193406_, p_193407_) -> {
      int i = Long.compare(p_193406_.triggerTick, p_193407_.triggerTick);
      if (i != 0) {
         return i;
      } else {
         i = p_193406_.priority.compareTo(p_193407_.priority);
         return i != 0 ? i : Long.compare(p_193406_.subTickOrder, p_193407_.subTickOrder);
      }
   };
   public static final Comparator<ScheduledTick<?>> INTRA_TICK_DRAIN_ORDER = (p_193395_, p_193396_) -> {
      int i = p_193395_.priority.compareTo(p_193396_.priority);
      return i != 0 ? i : Long.compare(p_193395_.subTickOrder, p_193396_.subTickOrder);
   };
   public static final Strategy<ScheduledTick<?>> UNIQUE_TICK_HASH = new Strategy<ScheduledTick<?>>() {
      public int hashCode(ScheduledTick<?> p_193417_) {
         return 31 * p_193417_.pos().hashCode() + p_193417_.type().hashCode();
      }

      public boolean equals(@Nullable ScheduledTick<?> p_193419_, @Nullable ScheduledTick<?> p_193420_) {
         if (p_193419_ == p_193420_) {
            return true;
         } else if (p_193419_ != null && p_193420_ != null) {
            return p_193419_.type() == p_193420_.type() && p_193419_.pos().equals(p_193420_.pos());
         } else {
            return false;
         }
      }
   };

   public ScheduledTick(T p_193383_, BlockPos p_193384_, long p_193385_, long p_193386_) {
      this(p_193383_, p_193384_, p_193385_, TickPriority.NORMAL, p_193386_);
   }

   public ScheduledTick {
      pos = pos.immutable();
   }

   public static <T> ScheduledTick<T> probe(T p_193398_, BlockPos p_193399_) {
      return new ScheduledTick<T>(p_193398_, p_193399_, 0L, TickPriority.NORMAL, 0L);
   }
}