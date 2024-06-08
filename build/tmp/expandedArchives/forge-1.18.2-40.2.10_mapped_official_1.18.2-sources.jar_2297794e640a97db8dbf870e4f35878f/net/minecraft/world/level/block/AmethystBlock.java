package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AmethystBlock extends Block {
   public AmethystBlock(BlockBehaviour.Properties p_151999_) {
      super(p_151999_);
   }

   public void onProjectileHit(Level p_152001_, BlockState p_152002_, BlockHitResult p_152003_, Projectile p_152004_) {
      if (!p_152001_.isClientSide) {
         BlockPos blockpos = p_152003_.getBlockPos();
         p_152001_.playSound((Player)null, blockpos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, 0.5F + p_152001_.random.nextFloat() * 1.2F);
         p_152001_.playSound((Player)null, blockpos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 0.5F + p_152001_.random.nextFloat() * 1.2F);
      }

   }
}