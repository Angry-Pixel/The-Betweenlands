package net.minecraft.world.ticks;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;

public class ProtoChunkTicks<T> implements SerializableTickContainer<T>, TickContainerAccess<T> {
   private final List<SavedTick<T>> ticks = Lists.newArrayList();
   private final Set<SavedTick<?>> ticksPerPosition = new ObjectOpenCustomHashSet<>(SavedTick.UNIQUE_TICK_HASH);

   public void schedule(ScheduledTick<T> p_193298_) {
      SavedTick<T> savedtick = new SavedTick<>(p_193298_.type(), p_193298_.pos(), 0, p_193298_.priority());
      this.schedule(savedtick);
   }

   private void schedule(SavedTick<T> p_193296_) {
      if (this.ticksPerPosition.add(p_193296_)) {
         this.ticks.add(p_193296_);
      }

   }

   public boolean hasScheduledTick(BlockPos p_193300_, T p_193301_) {
      return this.ticksPerPosition.contains(SavedTick.probe(p_193301_, p_193300_));
   }

   public int count() {
      return this.ticks.size();
   }

   public Tag save(long p_193308_, Function<T, String> p_193309_) {
      ListTag listtag = new ListTag();

      for(SavedTick<T> savedtick : this.ticks) {
         listtag.add(savedtick.save(p_193309_));
      }

      return listtag;
   }

   public List<SavedTick<T>> scheduledTicks() {
      return List.copyOf(this.ticks);
   }

   public static <T> ProtoChunkTicks<T> load(ListTag p_193303_, Function<String, Optional<T>> p_193304_, ChunkPos p_193305_) {
      ProtoChunkTicks<T> protochunkticks = new ProtoChunkTicks<>();
      SavedTick.loadTickList(p_193303_, p_193304_, p_193305_, protochunkticks::schedule);
      return protochunkticks;
   }
}