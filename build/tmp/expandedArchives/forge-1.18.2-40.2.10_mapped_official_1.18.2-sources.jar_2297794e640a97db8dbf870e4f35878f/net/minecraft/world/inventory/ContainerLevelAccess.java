package net.minecraft.world.inventory;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ContainerLevelAccess {
   ContainerLevelAccess NULL = new ContainerLevelAccess() {
      public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> p_39304_) {
         return Optional.empty();
      }
   };

   static ContainerLevelAccess create(final Level p_39290_, final BlockPos p_39291_) {
      return new ContainerLevelAccess() {
         public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> p_39311_) {
            return Optional.of(p_39311_.apply(p_39290_, p_39291_));
         }
      };
   }

   <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> p_39298_);

   default <T> T evaluate(BiFunction<Level, BlockPos, T> p_39300_, T p_39301_) {
      return this.evaluate(p_39300_).orElse(p_39301_);
   }

   default void execute(BiConsumer<Level, BlockPos> p_39293_) {
      this.evaluate((p_39296_, p_39297_) -> {
         p_39293_.accept(p_39296_, p_39297_);
         return Optional.empty();
      });
   }
}