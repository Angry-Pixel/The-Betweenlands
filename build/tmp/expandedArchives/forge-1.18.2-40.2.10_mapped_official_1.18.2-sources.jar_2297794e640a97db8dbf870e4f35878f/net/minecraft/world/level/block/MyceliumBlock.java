package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MyceliumBlock extends SpreadingSnowyDirtBlock {
   public MyceliumBlock(BlockBehaviour.Properties p_54898_) {
      super(p_54898_);
   }

   public void animateTick(BlockState p_54900_, Level p_54901_, BlockPos p_54902_, Random p_54903_) {
      super.animateTick(p_54900_, p_54901_, p_54902_, p_54903_);
      if (p_54903_.nextInt(10) == 0) {
         p_54901_.addParticle(ParticleTypes.MYCELIUM, (double)p_54902_.getX() + p_54903_.nextDouble(), (double)p_54902_.getY() + 1.1D, (double)p_54902_.getZ() + p_54903_.nextDouble(), 0.0D, 0.0D, 0.0D);
      }

   }
}