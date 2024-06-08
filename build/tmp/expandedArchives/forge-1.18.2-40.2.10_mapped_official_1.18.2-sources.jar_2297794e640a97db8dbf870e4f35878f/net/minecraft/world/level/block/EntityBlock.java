package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;

public interface EntityBlock {
   @Nullable
   BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_);

   @Nullable
   default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
      return null;
   }

   @Nullable
   default <T extends BlockEntity> GameEventListener getListener(Level p_153210_, T p_153211_) {
      return null;
   }
}