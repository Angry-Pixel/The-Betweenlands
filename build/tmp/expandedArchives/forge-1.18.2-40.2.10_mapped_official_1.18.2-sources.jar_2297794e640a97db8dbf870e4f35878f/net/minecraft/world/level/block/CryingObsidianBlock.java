package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CryingObsidianBlock extends Block {
   public CryingObsidianBlock(BlockBehaviour.Properties p_52371_) {
      super(p_52371_);
   }

   public void animateTick(BlockState p_52373_, Level p_52374_, BlockPos p_52375_, Random p_52376_) {
      if (p_52376_.nextInt(5) == 0) {
         Direction direction = Direction.getRandom(p_52376_);
         if (direction != Direction.UP) {
            BlockPos blockpos = p_52375_.relative(direction);
            BlockState blockstate = p_52374_.getBlockState(blockpos);
            if (!p_52373_.canOcclude() || !blockstate.isFaceSturdy(p_52374_, blockpos, direction.getOpposite())) {
               double d0 = direction.getStepX() == 0 ? p_52376_.nextDouble() : 0.5D + (double)direction.getStepX() * 0.6D;
               double d1 = direction.getStepY() == 0 ? p_52376_.nextDouble() : 0.5D + (double)direction.getStepY() * 0.6D;
               double d2 = direction.getStepZ() == 0 ? p_52376_.nextDouble() : 0.5D + (double)direction.getStepZ() * 0.6D;
               p_52374_.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (double)p_52375_.getX() + d0, (double)p_52375_.getY() + d1, (double)p_52375_.getZ() + d2, 0.0D, 0.0D, 0.0D);
            }
         }
      }
   }
}