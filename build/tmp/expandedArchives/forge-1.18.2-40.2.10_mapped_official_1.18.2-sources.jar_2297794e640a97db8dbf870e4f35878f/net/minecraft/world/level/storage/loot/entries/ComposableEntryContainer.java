package net.minecraft.world.level.storage.loot.entries;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;

@FunctionalInterface
interface ComposableEntryContainer {
   ComposableEntryContainer ALWAYS_FALSE = (p_79418_, p_79419_) -> {
      return false;
   };
   ComposableEntryContainer ALWAYS_TRUE = (p_79409_, p_79410_) -> {
      return true;
   };

   boolean expand(LootContext p_79426_, Consumer<LootPoolEntry> p_79427_);

   default ComposableEntryContainer and(ComposableEntryContainer p_79412_) {
      Objects.requireNonNull(p_79412_);
      return (p_79424_, p_79425_) -> {
         return this.expand(p_79424_, p_79425_) && p_79412_.expand(p_79424_, p_79425_);
      };
   }

   default ComposableEntryContainer or(ComposableEntryContainer p_79421_) {
      Objects.requireNonNull(p_79421_);
      return (p_79415_, p_79416_) -> {
         return this.expand(p_79415_, p_79416_) || p_79421_.expand(p_79415_, p_79416_);
      };
   }
}