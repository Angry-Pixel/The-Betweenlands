package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SmokerBlock extends AbstractFurnaceBlock {
   public SmokerBlock(BlockBehaviour.Properties p_56439_) {
      super(p_56439_);
   }

   public BlockEntity newBlockEntity(BlockPos p_154644_, BlockState p_154645_) {
      return new SmokerBlockEntity(p_154644_, p_154645_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_154640_, BlockState p_154641_, BlockEntityType<T> p_154642_) {
      return createFurnaceTicker(p_154640_, p_154642_, BlockEntityType.SMOKER);
   }

   protected void openContainer(Level p_56443_, BlockPos p_56444_, Player p_56445_) {
      BlockEntity blockentity = p_56443_.getBlockEntity(p_56444_);
      if (blockentity instanceof SmokerBlockEntity) {
         p_56445_.openMenu((MenuProvider)blockentity);
         p_56445_.awardStat(Stats.INTERACT_WITH_SMOKER);
      }

   }

   public void animateTick(BlockState p_56447_, Level p_56448_, BlockPos p_56449_, Random p_56450_) {
      if (p_56447_.getValue(LIT)) {
         double d0 = (double)p_56449_.getX() + 0.5D;
         double d1 = (double)p_56449_.getY();
         double d2 = (double)p_56449_.getZ() + 0.5D;
         if (p_56450_.nextDouble() < 0.1D) {
            p_56448_.playLocalSound(d0, d1, d2, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         p_56448_.addParticle(ParticleTypes.SMOKE, d0, d1 + 1.1D, d2, 0.0D, 0.0D, 0.0D);
      }
   }
}