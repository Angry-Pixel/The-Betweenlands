package net.minecraft.world.level.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public interface SimpleWaterloggedBlock extends BucketPickup, LiquidBlockContainer {
   default boolean canPlaceLiquid(BlockGetter p_56301_, BlockPos p_56302_, BlockState p_56303_, Fluid p_56304_) {
      return !p_56303_.getValue(BlockStateProperties.WATERLOGGED) && p_56304_ == Fluids.WATER;
   }

   default boolean placeLiquid(LevelAccessor p_56306_, BlockPos p_56307_, BlockState p_56308_, FluidState p_56309_) {
      if (!p_56308_.getValue(BlockStateProperties.WATERLOGGED) && p_56309_.getType() == Fluids.WATER) {
         if (!p_56306_.isClientSide()) {
            p_56306_.setBlock(p_56307_, p_56308_.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 3);
            p_56306_.scheduleTick(p_56307_, p_56309_.getType(), p_56309_.getType().getTickDelay(p_56306_));
         }

         return true;
      } else {
         return false;
      }
   }

   default ItemStack pickupBlock(LevelAccessor p_154560_, BlockPos p_154561_, BlockState p_154562_) {
      if (p_154562_.getValue(BlockStateProperties.WATERLOGGED)) {
         p_154560_.setBlock(p_154561_, p_154562_.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false)), 3);
         if (!p_154562_.canSurvive(p_154560_, p_154561_)) {
            p_154560_.destroyBlock(p_154561_, true);
         }

         return new ItemStack(Items.WATER_BUCKET);
      } else {
         return ItemStack.EMPTY;
      }
   }

   default Optional<SoundEvent> getPickupSound() {
      return Fluids.WATER.getPickupSound();
   }
}