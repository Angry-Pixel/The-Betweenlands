package net.minecraft.world.level.chunk;

public class MissingPaletteEntryException extends RuntimeException {
   public MissingPaletteEntryException(int p_188025_) {
      super("Missing Palette entry for index " + p_188025_ + ".");
   }
}