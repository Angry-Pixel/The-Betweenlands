package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class CauldronBlock extends AbstractCauldronBlock {
   private static final float RAIN_FILL_CHANCE = 0.05F;
   private static final float POWDER_SNOW_FILL_CHANCE = 0.1F;

   public CauldronBlock(BlockBehaviour.Properties p_51403_) {
      super(p_51403_, CauldronInteraction.EMPTY);
   }

   public boolean isFull(BlockState p_152947_) {
      return false;
   }

   protected static boolean shouldHandlePrecipitation(Level p_182451_, Biome.Precipitation p_182452_) {
      if (p_182452_ == Biome.Precipitation.RAIN) {
         return p_182451_.getRandom().nextFloat() < 0.05F;
      } else if (p_182452_ == Biome.Precipitation.SNOW) {
         return p_182451_.getRandom().nextFloat() < 0.1F;
      } else {
         return false;
      }
   }

   public void handlePrecipitation(BlockState p_152935_, Level p_152936_, BlockPos p_152937_, Biome.Precipitation p_152938_) {
      if (shouldHandlePrecipitation(p_152936_, p_152938_)) {
         if (p_152938_ == Biome.Precipitation.RAIN) {
            p_152936_.setBlockAndUpdate(p_152937_, Blocks.WATER_CAULDRON.defaultBlockState());
            p_152936_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_152937_);
         } else if (p_152938_ == Biome.Precipitation.SNOW) {
            p_152936_.setBlockAndUpdate(p_152937_, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState());
            p_152936_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_152937_);
         }

      }
   }

   protected boolean canReceiveStalactiteDrip(Fluid p_152945_) {
      return true;
   }

   protected void receiveStalactiteDrip(BlockState p_152940_, Level p_152941_, BlockPos p_152942_, Fluid p_152943_) {
      if (p_152943_ == Fluids.WATER) {
         p_152941_.setBlockAndUpdate(p_152942_, Blocks.WATER_CAULDRON.defaultBlockState());
         p_152941_.levelEvent(1047, p_152942_, 0);
         p_152941_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_152942_);
      } else if (p_152943_ == Fluids.LAVA) {
         p_152941_.setBlockAndUpdate(p_152942_, Blocks.LAVA_CAULDRON.defaultBlockState());
         p_152941_.levelEvent(1046, p_152942_, 0);
         p_152941_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_152942_);
      }

   }
}