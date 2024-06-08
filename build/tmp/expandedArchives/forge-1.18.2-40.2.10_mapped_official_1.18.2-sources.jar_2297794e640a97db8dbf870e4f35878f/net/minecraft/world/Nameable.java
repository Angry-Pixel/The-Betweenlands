package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public interface Nameable {
   Component getName();

   default boolean hasCustomName() {
      return this.getCustomName() != null;
   }

   default Component getDisplayName() {
      return this.getName();
   }

   @Nullable
   default Component getCustomName() {
      return null;
   }
}