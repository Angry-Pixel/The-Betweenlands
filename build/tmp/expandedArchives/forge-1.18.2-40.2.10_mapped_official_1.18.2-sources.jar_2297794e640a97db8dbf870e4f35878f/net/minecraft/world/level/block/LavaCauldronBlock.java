package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class LavaCauldronBlock extends AbstractCauldronBlock {
   public LavaCauldronBlock(BlockBehaviour.Properties p_153498_) {
      super(p_153498_, CauldronInteraction.LAVA);
   }

   protected double getContentHeight(BlockState p_153500_) {
      return 0.9375D;
   }

   public boolean isFull(BlockState p_153511_) {
      return true;
   }

   public void entityInside(BlockState p_153506_, Level p_153507_, BlockPos p_153508_, Entity p_153509_) {
      if (this.isEntityInsideContent(p_153506_, p_153508_, p_153509_)) {
         p_153509_.lavaHurt();
      }

   }

   public int getAnalogOutputSignal(BlockState p_153502_, Level p_153503_, BlockPos p_153504_) {
      return 3;
   }
}