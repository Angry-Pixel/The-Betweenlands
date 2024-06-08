package net.minecraft.world.ticks;

import net.minecraft.core.BlockPos;

public class BlackholeTickAccess {
   private static final TickContainerAccess<Object> CONTAINER_BLACKHOLE = new TickContainerAccess<Object>() {
      public void schedule(ScheduledTick<Object> p_193149_) {
      }

      public boolean hasScheduledTick(BlockPos p_193151_, Object p_193152_) {
         return false;
      }

      public int count() {
         return 0;
      }
   };
   private static final LevelTickAccess<Object> LEVEL_BLACKHOLE = new LevelTickAccess<Object>() {
      public void schedule(ScheduledTick<Object> p_193156_) {
      }

      public boolean hasScheduledTick(BlockPos p_193158_, Object p_193159_) {
         return false;
      }

      public boolean willTickThisTick(BlockPos p_193161_, Object p_193162_) {
         return false;
      }

      public int count() {
         return 0;
      }
   };

   public static <T> TickContainerAccess<T> emptyContainer() {
      return (TickContainerAccess<T>)CONTAINER_BLACKHOLE;
   }

   public static <T> LevelTickAccess<T> emptyLevelList() {
      return (LevelTickAccess<T>)LEVEL_BLACKHOLE;
   }
}