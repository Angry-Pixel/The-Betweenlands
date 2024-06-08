package net.minecraft.world.item;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BedItem extends BlockItem {
   public BedItem(Block p_40558_, Item.Properties p_40559_) {
      super(p_40558_, p_40559_);
   }

   protected boolean placeBlock(BlockPlaceContext p_40561_, BlockState p_40562_) {
      return p_40561_.getLevel().setBlock(p_40561_.getClickedPos(), p_40562_, 26);
   }
}