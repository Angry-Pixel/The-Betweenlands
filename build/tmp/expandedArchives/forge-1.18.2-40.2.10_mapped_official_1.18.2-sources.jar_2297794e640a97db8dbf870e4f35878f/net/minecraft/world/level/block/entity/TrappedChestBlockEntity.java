package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TrappedChestBlockEntity extends ChestBlockEntity {
   public TrappedChestBlockEntity(BlockPos p_155862_, BlockState p_155863_) {
      super(BlockEntityType.TRAPPED_CHEST, p_155862_, p_155863_);
   }

   protected void signalOpenCount(Level p_155865_, BlockPos p_155866_, BlockState p_155867_, int p_155868_, int p_155869_) {
      super.signalOpenCount(p_155865_, p_155866_, p_155867_, p_155868_, p_155869_);
      if (p_155868_ != p_155869_) {
         Block block = p_155867_.getBlock();
         p_155865_.updateNeighborsAt(p_155866_, block);
         p_155865_.updateNeighborsAt(p_155866_.below(), block);
      }

   }
}