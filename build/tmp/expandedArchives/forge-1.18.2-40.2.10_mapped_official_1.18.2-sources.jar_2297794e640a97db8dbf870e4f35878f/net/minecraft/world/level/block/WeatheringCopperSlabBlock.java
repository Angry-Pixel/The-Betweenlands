package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperSlabBlock extends SlabBlock implements WeatheringCopper {
   private final WeatheringCopper.WeatherState weatherState;

   public WeatheringCopperSlabBlock(WeatheringCopper.WeatherState p_154938_, BlockBehaviour.Properties p_154939_) {
      super(p_154939_);
      this.weatherState = p_154938_;
   }

   public void randomTick(BlockState p_154942_, ServerLevel p_154943_, BlockPos p_154944_, Random p_154945_) {
      this.onRandomTick(p_154942_, p_154943_, p_154944_, p_154945_);
   }

   public boolean isRandomlyTicking(BlockState p_154947_) {
      return WeatheringCopper.getNext(p_154947_.getBlock()).isPresent();
   }

   public WeatheringCopper.WeatherState getAge() {
      return this.weatherState;
   }
}