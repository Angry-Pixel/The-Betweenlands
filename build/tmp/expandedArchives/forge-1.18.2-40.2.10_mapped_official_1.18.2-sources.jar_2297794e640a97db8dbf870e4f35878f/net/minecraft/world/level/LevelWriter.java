package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public interface LevelWriter {
   boolean setBlock(BlockPos p_46947_, BlockState p_46948_, int p_46949_, int p_46950_);

   default boolean setBlock(BlockPos p_46944_, BlockState p_46945_, int p_46946_) {
      return this.setBlock(p_46944_, p_46945_, p_46946_, 512);
   }

   boolean removeBlock(BlockPos p_46951_, boolean p_46952_);

   default boolean destroyBlock(BlockPos p_46962_, boolean p_46963_) {
      return this.destroyBlock(p_46962_, p_46963_, (Entity)null);
   }

   default boolean destroyBlock(BlockPos p_46954_, boolean p_46955_, @Nullable Entity p_46956_) {
      return this.destroyBlock(p_46954_, p_46955_, p_46956_, 512);
   }

   boolean destroyBlock(BlockPos p_46957_, boolean p_46958_, @Nullable Entity p_46959_, int p_46960_);

   default boolean addFreshEntity(Entity p_46964_) {
      return false;
   }
}