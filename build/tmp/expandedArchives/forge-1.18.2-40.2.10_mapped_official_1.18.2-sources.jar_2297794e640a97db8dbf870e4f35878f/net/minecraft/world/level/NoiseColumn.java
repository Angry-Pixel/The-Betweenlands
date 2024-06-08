package net.minecraft.world.level;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;

public final class NoiseColumn implements BlockColumn {
   private final int minY;
   private final BlockState[] column;

   public NoiseColumn(int p_151623_, BlockState[] p_151624_) {
      this.minY = p_151623_;
      this.column = p_151624_;
   }

   public BlockState getBlock(int p_186552_) {
      int i = p_186552_ - this.minY;
      return i >= 0 && i < this.column.length ? this.column[i] : Blocks.AIR.defaultBlockState();
   }

   public void setBlock(int p_186554_, BlockState p_186555_) {
      int i = p_186554_ - this.minY;
      if (i >= 0 && i < this.column.length) {
         this.column[i] = p_186555_;
      } else {
         throw new IllegalArgumentException("Outside of column height: " + p_186554_);
      }
   }
}