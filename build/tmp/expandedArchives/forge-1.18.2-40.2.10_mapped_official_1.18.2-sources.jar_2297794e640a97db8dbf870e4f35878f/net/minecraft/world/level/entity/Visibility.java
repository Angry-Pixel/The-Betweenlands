package net.minecraft.world.level.entity;

import net.minecraft.server.level.ChunkHolder;

public enum Visibility {
   HIDDEN(false, false),
   TRACKED(true, false),
   TICKING(true, true);

   private final boolean accessible;
   private final boolean ticking;

   private Visibility(boolean p_157689_, boolean p_157690_) {
      this.accessible = p_157689_;
      this.ticking = p_157690_;
   }

   public boolean isTicking() {
      return this.ticking;
   }

   public boolean isAccessible() {
      return this.accessible;
   }

   public static Visibility fromFullChunkStatus(ChunkHolder.FullChunkStatus p_157693_) {
      if (p_157693_.isOrAfter(ChunkHolder.FullChunkStatus.ENTITY_TICKING)) {
         return TICKING;
      } else {
         return p_157693_.isOrAfter(ChunkHolder.FullChunkStatus.BORDER) ? TRACKED : HIDDEN;
      }
   }
}