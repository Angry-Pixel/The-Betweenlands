package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperStairBlock extends StairBlock implements WeatheringCopper {
   private final WeatheringCopper.WeatherState weatherState;

   public WeatheringCopperStairBlock(WeatheringCopper.WeatherState p_154951_, BlockState p_154952_, BlockBehaviour.Properties p_154953_) {
      super(p_154952_, p_154953_);
      this.weatherState = p_154951_;
   }

   public void randomTick(BlockState p_154956_, ServerLevel p_154957_, BlockPos p_154958_, Random p_154959_) {
      this.onRandomTick(p_154956_, p_154957_, p_154958_, p_154959_);
   }

   public boolean isRandomlyTicking(BlockState p_154961_) {
      return WeatheringCopper.getNext(p_154961_.getBlock()).isPresent();
   }

   public WeatheringCopper.WeatherState getAge() {
      return this.weatherState;
   }
}