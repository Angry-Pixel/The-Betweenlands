package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SoulFireBlock extends BaseFireBlock {
   public SoulFireBlock(BlockBehaviour.Properties p_56653_) {
      super(p_56653_, 2.0F);
   }

   public BlockState updateShape(BlockState p_56659_, Direction p_56660_, BlockState p_56661_, LevelAccessor p_56662_, BlockPos p_56663_, BlockPos p_56664_) {
      return this.canSurvive(p_56659_, p_56662_, p_56663_) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
   }

   public boolean canSurvive(BlockState p_56655_, LevelReader p_56656_, BlockPos p_56657_) {
      return canSurviveOnBlock(p_56656_.getBlockState(p_56657_.below()));
   }

   public static boolean canSurviveOnBlock(BlockState p_154651_) {
      return p_154651_.is(BlockTags.SOUL_FIRE_BASE_BLOCKS);
   }

   protected boolean canBurn(BlockState p_56668_) {
      return true;
   }
}