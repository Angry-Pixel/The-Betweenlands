package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MagmaBlock extends Block {
   private static final int BUBBLE_COLUMN_CHECK_DELAY = 20;

   public MagmaBlock(BlockBehaviour.Properties p_54800_) {
      super(p_54800_);
   }

   public void stepOn(Level p_153777_, BlockPos p_153778_, BlockState p_153779_, Entity p_153780_) {
      if (!p_153780_.fireImmune() && p_153780_ instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)p_153780_)) {
         p_153780_.hurt(DamageSource.HOT_FLOOR, 1.0F);
      }

      super.stepOn(p_153777_, p_153778_, p_153779_, p_153780_);
   }

   public void tick(BlockState p_54806_, ServerLevel p_54807_, BlockPos p_54808_, Random p_54809_) {
      BubbleColumnBlock.updateColumn(p_54807_, p_54808_.above(), p_54806_);
   }

   public BlockState updateShape(BlockState p_54811_, Direction p_54812_, BlockState p_54813_, LevelAccessor p_54814_, BlockPos p_54815_, BlockPos p_54816_) {
      if (p_54812_ == Direction.UP && p_54813_.is(Blocks.WATER)) {
         p_54814_.scheduleTick(p_54815_, this, 20);
      }

      return super.updateShape(p_54811_, p_54812_, p_54813_, p_54814_, p_54815_, p_54816_);
   }

   public void randomTick(BlockState p_54818_, ServerLevel p_54819_, BlockPos p_54820_, Random p_54821_) {
      BlockPos blockpos = p_54820_.above();
      if (p_54819_.getFluidState(p_54820_).is(FluidTags.WATER)) {
         p_54819_.playSound((Player)null, p_54820_, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (p_54819_.random.nextFloat() - p_54819_.random.nextFloat()) * 0.8F);
         p_54819_.sendParticles(ParticleTypes.LARGE_SMOKE, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.25D, (double)blockpos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
      }

   }

   public void onPlace(BlockState p_54823_, Level p_54824_, BlockPos p_54825_, BlockState p_54826_, boolean p_54827_) {
      p_54824_.scheduleTick(p_54825_, this, 20);
   }
}