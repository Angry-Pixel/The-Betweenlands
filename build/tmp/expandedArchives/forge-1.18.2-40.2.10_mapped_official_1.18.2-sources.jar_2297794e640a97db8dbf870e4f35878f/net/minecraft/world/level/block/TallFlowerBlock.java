package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TallFlowerBlock extends DoublePlantBlock implements BonemealableBlock {
   public TallFlowerBlock(BlockBehaviour.Properties p_57296_) {
      super(p_57296_);
   }

   public boolean canBeReplaced(BlockState p_57313_, BlockPlaceContext p_57314_) {
      return false;
   }

   public boolean isValidBonemealTarget(BlockGetter p_57303_, BlockPos p_57304_, BlockState p_57305_, boolean p_57306_) {
      return true;
   }

   public boolean isBonemealSuccess(Level p_57308_, Random p_57309_, BlockPos p_57310_, BlockState p_57311_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_57298_, Random p_57299_, BlockPos p_57300_, BlockState p_57301_) {
      popResource(p_57298_, p_57300_, new ItemStack(this));
   }
}