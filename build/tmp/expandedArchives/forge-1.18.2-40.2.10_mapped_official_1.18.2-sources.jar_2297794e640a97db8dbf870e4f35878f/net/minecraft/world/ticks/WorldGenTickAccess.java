package net.minecraft.world.ticks;

import java.util.function.Function;
import net.minecraft.core.BlockPos;

public class WorldGenTickAccess<T> implements LevelTickAccess<T> {
   private final Function<BlockPos, TickContainerAccess<T>> containerGetter;

   public WorldGenTickAccess(Function<BlockPos, TickContainerAccess<T>> p_193454_) {
      this.containerGetter = p_193454_;
   }

   public boolean hasScheduledTick(BlockPos p_193459_, T p_193460_) {
      return this.containerGetter.apply(p_193459_).hasScheduledTick(p_193459_, p_193460_);
   }

   public void schedule(ScheduledTick<T> p_193457_) {
      this.containerGetter.apply(p_193457_.pos()).schedule(p_193457_);
   }

   public boolean willTickThisTick(BlockPos p_193462_, T p_193463_) {
      return false;
   }

   public int count() {
      return 0;
   }
}