package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class LayeredCauldronBlock extends AbstractCauldronBlock {
   public static final int MIN_FILL_LEVEL = 1;
   public static final int MAX_FILL_LEVEL = 3;
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;
   private static final int BASE_CONTENT_HEIGHT = 6;
   private static final double HEIGHT_PER_LEVEL = 3.0D;
   public static final Predicate<Biome.Precipitation> RAIN = (p_153553_) -> {
      return p_153553_ == Biome.Precipitation.RAIN;
   };
   public static final Predicate<Biome.Precipitation> SNOW = (p_153526_) -> {
      return p_153526_ == Biome.Precipitation.SNOW;
   };
   private final Predicate<Biome.Precipitation> fillPredicate;

   public LayeredCauldronBlock(BlockBehaviour.Properties p_153522_, Predicate<Biome.Precipitation> p_153523_, Map<Item, CauldronInteraction> p_153524_) {
      super(p_153522_, p_153524_);
      this.fillPredicate = p_153523_;
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(1)));
   }

   public boolean isFull(BlockState p_153555_) {
      return p_153555_.getValue(LEVEL) == 3;
   }

   protected boolean canReceiveStalactiteDrip(Fluid p_153551_) {
      return p_153551_ == Fluids.WATER && this.fillPredicate == RAIN;
   }

   protected double getContentHeight(BlockState p_153528_) {
      return (6.0D + (double)p_153528_.getValue(LEVEL).intValue() * 3.0D) / 16.0D;
   }

   public void entityInside(BlockState p_153534_, Level p_153535_, BlockPos p_153536_, Entity p_153537_) {
      if (!p_153535_.isClientSide && p_153537_.isOnFire() && this.isEntityInsideContent(p_153534_, p_153536_, p_153537_)) {
         p_153537_.clearFire();
         if (p_153537_.mayInteract(p_153535_, p_153536_)) {
            this.handleEntityOnFireInside(p_153534_, p_153535_, p_153536_);
         }
      }

   }

   protected void handleEntityOnFireInside(BlockState p_153556_, Level p_153557_, BlockPos p_153558_) {
      lowerFillLevel(p_153556_, p_153557_, p_153558_);
   }

   public static void lowerFillLevel(BlockState p_153560_, Level p_153561_, BlockPos p_153562_) {
      int i = p_153560_.getValue(LEVEL) - 1;
      p_153561_.setBlockAndUpdate(p_153562_, i == 0 ? Blocks.CAULDRON.defaultBlockState() : p_153560_.setValue(LEVEL, Integer.valueOf(i)));
   }

   public void handlePrecipitation(BlockState p_153539_, Level p_153540_, BlockPos p_153541_, Biome.Precipitation p_153542_) {
      if (CauldronBlock.shouldHandlePrecipitation(p_153540_, p_153542_) && p_153539_.getValue(LEVEL) != 3 && this.fillPredicate.test(p_153542_)) {
         p_153540_.setBlockAndUpdate(p_153541_, p_153539_.cycle(LEVEL));
      }
   }

   public int getAnalogOutputSignal(BlockState p_153530_, Level p_153531_, BlockPos p_153532_) {
      return p_153530_.getValue(LEVEL);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153549_) {
      p_153549_.add(LEVEL);
   }

   protected void receiveStalactiteDrip(BlockState p_153544_, Level p_153545_, BlockPos p_153546_, Fluid p_153547_) {
      if (!this.isFull(p_153544_)) {
         p_153545_.setBlockAndUpdate(p_153546_, p_153544_.setValue(LEVEL, Integer.valueOf(p_153544_.getValue(LEVEL) + 1)));
         p_153545_.levelEvent(1047, p_153546_, 0);
      }
   }
}