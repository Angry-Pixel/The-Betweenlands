package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;

public record SavedTick<T>(T type, BlockPos pos, int delay, TickPriority priority) {
   private static final String TAG_ID = "i";
   private static final String TAG_X = "x";
   private static final String TAG_Y = "y";
   private static final String TAG_Z = "z";
   private static final String TAG_DELAY = "t";
   private static final String TAG_PRIORITY = "p";
   public static final Strategy<SavedTick<?>> UNIQUE_TICK_HASH = new Strategy<SavedTick<?>>() {
      public int hashCode(SavedTick<?> p_193364_) {
         return 31 * p_193364_.pos().hashCode() + p_193364_.type().hashCode();
      }

      public boolean equals(@Nullable SavedTick<?> p_193366_, @Nullable SavedTick<?> p_193367_) {
         if (p_193366_ == p_193367_) {
            return true;
         } else if (p_193366_ != null && p_193367_ != null) {
            return p_193366_.type() == p_193367_.type() && p_193366_.pos().equals(p_193367_.pos());
         } else {
            return false;
         }
      }
   };

   public static <T> void loadTickList(ListTag p_193351_, Function<String, Optional<T>> p_193352_, ChunkPos p_193353_, Consumer<SavedTick<T>> p_193354_) {
      long i = p_193353_.toLong();

      for(int j = 0; j < p_193351_.size(); ++j) {
         CompoundTag compoundtag = p_193351_.getCompound(j);
         loadTick(compoundtag, p_193352_).ifPresent((p_210665_) -> {
            if (ChunkPos.asLong(p_210665_.pos()) == i) {
               p_193354_.accept(p_210665_);
            }

         });
      }

   }

   public static <T> Optional<SavedTick<T>> loadTick(CompoundTag p_210670_, Function<String, Optional<T>> p_210671_) {
      return p_210671_.apply(p_210670_.getString("i")).map((p_210668_) -> {
         BlockPos blockpos = new BlockPos(p_210670_.getInt("x"), p_210670_.getInt("y"), p_210670_.getInt("z"));
         return new SavedTick<>(p_210668_, blockpos, p_210670_.getInt("t"), TickPriority.byValue(p_210670_.getInt("p")));
      });
   }

   private static CompoundTag saveTick(String p_193339_, BlockPos p_193340_, int p_193341_, TickPriority p_193342_) {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putString("i", p_193339_);
      compoundtag.putInt("x", p_193340_.getX());
      compoundtag.putInt("y", p_193340_.getY());
      compoundtag.putInt("z", p_193340_.getZ());
      compoundtag.putInt("t", p_193341_);
      compoundtag.putInt("p", p_193342_.getValue());
      return compoundtag;
   }

   public static <T> CompoundTag saveTick(ScheduledTick<T> p_193332_, Function<T, String> p_193333_, long p_193334_) {
      return saveTick(p_193333_.apply(p_193332_.type()), p_193332_.pos(), (int)(p_193332_.triggerTick() - p_193334_), p_193332_.priority());
   }

   public CompoundTag save(Function<T, String> p_193344_) {
      return saveTick(p_193344_.apply(this.type), this.pos, this.delay, this.priority);
   }

   public ScheduledTick<T> unpack(long p_193329_, long p_193330_) {
      return new ScheduledTick<T>(this.type, this.pos, p_193329_ + (long)this.delay, this.priority, p_193330_);
   }

   public static <T> SavedTick<T> probe(T p_193336_, BlockPos p_193337_) {
      return new SavedTick<>(p_193336_, p_193337_, 0, TickPriority.NORMAL);
   }
}