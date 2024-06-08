package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MossBlock extends Block implements BonemealableBlock {
   public MossBlock(BlockBehaviour.Properties p_153790_) {
      super(p_153790_);
   }

   public boolean isValidBonemealTarget(BlockGetter p_153797_, BlockPos p_153798_, BlockState p_153799_, boolean p_153800_) {
      return p_153797_.getBlockState(p_153798_.above()).isAir();
   }

   public boolean isBonemealSuccess(Level p_153802_, Random p_153803_, BlockPos p_153804_, BlockState p_153805_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_153792_, Random p_153793_, BlockPos p_153794_, BlockState p_153795_) {
      CaveFeatures.MOSS_PATCH_BONEMEAL.value().place(p_153792_, p_153792_.getChunkSource().getGenerator(), p_153793_, p_153794_.above());
   }
}