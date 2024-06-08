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
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlastFurnaceBlock extends AbstractFurnaceBlock {
   public BlastFurnaceBlock(BlockBehaviour.Properties p_49773_) {
      super(p_49773_);
   }

   public BlockEntity newBlockEntity(BlockPos p_152386_, BlockState p_152387_) {
      return new BlastFurnaceBlockEntity(p_152386_, p_152387_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152382_, BlockState p_152383_, BlockEntityType<T> p_152384_) {
      return createFurnaceTicker(p_152382_, p_152384_, BlockEntityType.BLAST_FURNACE);
   }

   protected void openContainer(Level p_49777_, BlockPos p_49778_, Player p_49779_) {
      BlockEntity blockentity = p_49777_.getBlockEntity(p_49778_);
      if (blockentity instanceof BlastFurnaceBlockEntity) {
         p_49779_.openMenu((MenuProvider)blockentity);
         p_49779_.awardStat(Stats.INTERACT_WITH_BLAST_FURNACE);
      }

   }

   public void animateTick(BlockState p_49781_, Level p_49782_, BlockPos p_49783_, Random p_49784_) {
      if (p_49781_.getValue(LIT)) {
         double d0 = (double)p_49783_.getX() + 0.5D;
         double d1 = (double)p_49783_.getY();
         double d2 = (double)p_49783_.getZ() + 0.5D;
         if (p_49784_.nextDouble() < 0.1D) {
            p_49782_.playLocalSound(d0, d1, d2, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         Direction direction = p_49781_.getValue(FACING);
         Direction.Axis direction$axis = direction.getAxis();
         double d3 = 0.52D;
         double d4 = p_49784_.nextDouble() * 0.6D - 0.3D;
         double d5 = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52D : d4;
         double d6 = p_49784_.nextDouble() * 9.0D / 16.0D;
         double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52D : d4;
         p_49782_.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
      }
   }
}