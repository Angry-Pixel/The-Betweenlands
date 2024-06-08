package net.minecraft.world.level.material;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class WaterFluid extends FlowingFluid {
   public Fluid getFlowing() {
      return Fluids.FLOWING_WATER;
   }

   public Fluid getSource() {
      return Fluids.WATER;
   }

   public Item getBucket() {
      return Items.WATER_BUCKET;
   }

   public void animateTick(Level p_76445_, BlockPos p_76446_, FluidState p_76447_, Random p_76448_) {
      if (!p_76447_.isSource() && !p_76447_.getValue(FALLING)) {
         if (p_76448_.nextInt(64) == 0) {
            p_76445_.playLocalSound((double)p_76446_.getX() + 0.5D, (double)p_76446_.getY() + 0.5D, (double)p_76446_.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, p_76448_.nextFloat() * 0.25F + 0.75F, p_76448_.nextFloat() + 0.5F, false);
         }
      } else if (p_76448_.nextInt(10) == 0) {
         p_76445_.addParticle(ParticleTypes.UNDERWATER, (double)p_76446_.getX() + p_76448_.nextDouble(), (double)p_76446_.getY() + p_76448_.nextDouble(), (double)p_76446_.getZ() + p_76448_.nextDouble(), 0.0D, 0.0D, 0.0D);
      }

   }

   @Nullable
   public ParticleOptions getDripParticle() {
      return ParticleTypes.DRIPPING_WATER;
   }

   protected boolean canConvertToSource() {
      return true;
   }

   protected void beforeDestroyingBlock(LevelAccessor p_76450_, BlockPos p_76451_, BlockState p_76452_) {
      BlockEntity blockentity = p_76452_.hasBlockEntity() ? p_76450_.getBlockEntity(p_76451_) : null;
      Block.dropResources(p_76452_, p_76450_, p_76451_, blockentity);
   }

   public int getSlopeFindDistance(LevelReader p_76464_) {
      return 4;
   }

   public BlockState createLegacyBlock(FluidState p_76466_) {
      return Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(p_76466_)));
   }

   public boolean isSame(Fluid p_76456_) {
      return p_76456_ == Fluids.WATER || p_76456_ == Fluids.FLOWING_WATER;
   }

   public int getDropOff(LevelReader p_76469_) {
      return 1;
   }

   public int getTickDelay(LevelReader p_76454_) {
      return 5;
   }

   public boolean canBeReplacedWith(FluidState p_76458_, BlockGetter p_76459_, BlockPos p_76460_, Fluid p_76461_, Direction p_76462_) {
      return p_76462_ == Direction.DOWN && !p_76461_.is(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public Optional<SoundEvent> getPickupSound() {
      return Optional.of(SoundEvents.BUCKET_FILL);
   }

   public static class Flowing extends WaterFluid {
      protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> p_76476_) {
         super.createFluidStateDefinition(p_76476_);
         p_76476_.add(LEVEL);
      }

      public int getAmount(FluidState p_76480_) {
         return p_76480_.getValue(LEVEL);
      }

      public boolean isSource(FluidState p_76478_) {
         return false;
      }
   }

   public static class Source extends WaterFluid {
      public int getAmount(FluidState p_76485_) {
         return 8;
      }

      public boolean isSource(FluidState p_76483_) {
         return true;
      }
   }
}