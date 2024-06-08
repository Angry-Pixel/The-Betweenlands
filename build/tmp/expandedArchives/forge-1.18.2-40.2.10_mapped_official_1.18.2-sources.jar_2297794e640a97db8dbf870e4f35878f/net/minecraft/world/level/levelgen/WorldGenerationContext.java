package net.minecraft.world.level.levelgen;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class WorldGenerationContext {
   private final int minY;
   private final int height;

   public WorldGenerationContext(ChunkGenerator p_182507_, LevelHeightAccessor p_182508_) {
      this.minY = Math.max(p_182508_.getMinBuildHeight(), p_182507_.getMinY());
      this.height = Math.min(p_182508_.getHeight(), p_182507_.getGenDepth());
   }

   public int getMinGenY() {
      return this.minY;
   }

   public int getGenDepth() {
      return this.height;
   }
}