package net.minecraft.client.gui.narration;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface NarratableEntry extends NarrationSupplier {
   NarratableEntry.NarrationPriority narrationPriority();

   default boolean isActive() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum NarrationPriority {
      NONE,
      HOVERED,
      FOCUSED;

      public boolean isTerminal() {
         return this == FOCUSED;
      }
   }
}