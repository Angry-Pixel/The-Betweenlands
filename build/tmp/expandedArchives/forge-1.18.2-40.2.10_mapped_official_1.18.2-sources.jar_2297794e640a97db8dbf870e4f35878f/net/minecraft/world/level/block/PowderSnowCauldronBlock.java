package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class PowderSnowCauldronBlock extends LayeredCauldronBlock {
   public PowderSnowCauldronBlock(BlockBehaviour.Properties p_154290_, Predicate<Biome.Precipitation> p_154291_, Map<Item, CauldronInteraction> p_154292_) {
      super(p_154290_, p_154291_, p_154292_);
   }

   protected void handleEntityOnFireInside(BlockState p_154294_, Level p_154295_, BlockPos p_154296_) {
      lowerFillLevel(Blocks.WATER_CAULDRON.defaultBlockState().setValue(LEVEL, p_154294_.getValue(LEVEL)), p_154295_, p_154296_);
   }
}