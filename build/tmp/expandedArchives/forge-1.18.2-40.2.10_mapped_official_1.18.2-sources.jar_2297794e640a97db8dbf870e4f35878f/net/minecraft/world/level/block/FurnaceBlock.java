package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class FurnaceBlock extends AbstractFurnaceBlock {
   public FurnaceBlock(BlockBehaviour.Properties p_53627_) {
      super(p_53627_);
   }

   public BlockEntity newBlockEntity(BlockPos p_153277_, BlockState p_153278_) {
      return new FurnaceBlockEntity(p_153277_, p_153278_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153273_, BlockState p_153274_, BlockEntityType<T> p_153275_) {
      return createFurnaceTicker(p_153273_, p_153275_, BlockEntityType.FURNACE);
   }

   protected void openContainer(Level p_53631_, BlockPos p_53632_, Player p_53633_) {
      BlockEntity blockentity = p_53631_.getBlockEntity(p_53632_);
      if (blockentity instanceof FurnaceBlockEntity) {
         p_53633_.openMenu((MenuProvider)blockentity);
         p_53633_.awardStat(Stats.INTERACT_WITH_FURNACE);
      }

   }

   public void animateTick(BlockState p_53635_, Level p_53636_, BlockPos p_53637_, Random p_53638_) {
      if (p_53635_.getValue(LIT)) {
         double d0 = (double)p_53637_.getX() + 0.5D;
         double d1 = (double)p_53637_.getY();
         double d2 = (double)p_53637_.getZ() + 0.5D;
         if (p_53638_.nextDouble() < 0.1D) {
            p_53636_.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction direction = p_53635_.getValue(FACING);
         Direction.Axis direction$axis = direction.getAxis();
         double d3 = 0.52D;
         double d4 = p_53638_.nextDouble() * 0.6D - 0.3D;
         double d5 = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52D : d4;
         double d6 = p_53638_.nextDouble() * 6.0D / 16.0D;
         double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52D : d4;
         p_53636_.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
         p_53636_.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
      }
   }
}