package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperFullBlock extends Block implements WeatheringCopper {
   private final WeatheringCopper.WeatherState weatherState;

   public WeatheringCopperFullBlock(WeatheringCopper.WeatherState p_154925_, BlockBehaviour.Properties p_154926_) {
      super(p_154926_);
      this.weatherState = p_154925_;
   }

   public void randomTick(BlockState p_154929_, ServerLevel p_154930_, BlockPos p_154931_, Random p_154932_) {
      this.onRandomTick(p_154929_, p_154930_, p_154931_, p_154932_);
   }

   public boolean isRandomlyTicking(BlockState p_154935_) {
      return WeatheringCopper.getNext(p_154935_.getBlock()).isPresent();
   }

   public WeatheringCopper.WeatherState getAge() {
      return this.weatherState;
   }
}