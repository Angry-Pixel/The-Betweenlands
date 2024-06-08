package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class RedstoneLampBlock extends Block {
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   public RedstoneLampBlock(BlockBehaviour.Properties p_55657_) {
      super(p_55657_);
      this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_55659_) {
      return this.defaultBlockState().setValue(LIT, Boolean.valueOf(p_55659_.getLevel().hasNeighborSignal(p_55659_.getClickedPos())));
   }

   public void neighborChanged(BlockState p_55666_, Level p_55667_, BlockPos p_55668_, Block p_55669_, BlockPos p_55670_, boolean p_55671_) {
      if (!p_55667_.isClientSide) {
         boolean flag = p_55666_.getValue(LIT);
         if (flag != p_55667_.hasNeighborSignal(p_55668_)) {
            if (flag) {
               p_55667_.scheduleTick(p_55668_, this, 4);
            } else {
               p_55667_.setBlock(p_55668_, p_55666_.cycle(LIT), 2);
            }
         }

      }
   }

   public void tick(BlockState p_55661_, ServerLevel p_55662_, BlockPos p_55663_, Random p_55664_) {
      if (p_55661_.getValue(LIT) && !p_55662_.hasNeighborSignal(p_55663_)) {
         p_55662_.setBlock(p_55663_, p_55661_.cycle(LIT), 2);
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55673_) {
      p_55673_.add(LIT);
   }
}