package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CarverDebugSettings {
   public static final CarverDebugSettings DEFAULT = new CarverDebugSettings(false, Blocks.ACACIA_BUTTON.defaultBlockState(), Blocks.CANDLE.defaultBlockState(), Blocks.ORANGE_STAINED_GLASS.defaultBlockState(), Blocks.GLASS.defaultBlockState());
   public static final Codec<CarverDebugSettings> CODEC = RecordCodecBuilder.create((p_159135_) -> {
      return p_159135_.group(Codec.BOOL.optionalFieldOf("debug_mode", Boolean.valueOf(false)).forGetter(CarverDebugSettings::isDebugMode), BlockState.CODEC.optionalFieldOf("air_state", DEFAULT.getAirState()).forGetter(CarverDebugSettings::getAirState), BlockState.CODEC.optionalFieldOf("water_state", DEFAULT.getAirState()).forGetter(CarverDebugSettings::getWaterState), BlockState.CODEC.optionalFieldOf("lava_state", DEFAULT.getAirState()).forGetter(CarverDebugSettings::getLavaState), BlockState.CODEC.optionalFieldOf("barrier_state", DEFAULT.getAirState()).forGetter(CarverDebugSettings::getBarrierState)).apply(p_159135_, CarverDebugSettings::new);
   });
   private boolean debugMode;
   private final BlockState airState;
   private final BlockState waterState;
   private final BlockState lavaState;
   private final BlockState barrierState;

   public static CarverDebugSettings of(boolean p_159140_, BlockState p_159141_, BlockState p_159142_, BlockState p_159143_, BlockState p_159144_) {
      return new CarverDebugSettings(p_159140_, p_159141_, p_159142_, p_159143_, p_159144_);
   }

   public static CarverDebugSettings of(BlockState p_159130_, BlockState p_159131_, BlockState p_159132_, BlockState p_159133_) {
      return new CarverDebugSettings(false, p_159130_, p_159131_, p_159132_, p_159133_);
   }

   public static CarverDebugSettings of(boolean p_159137_, BlockState p_159138_) {
      return new CarverDebugSettings(p_159137_, p_159138_, DEFAULT.getWaterState(), DEFAULT.getLavaState(), DEFAULT.getBarrierState());
   }

   private CarverDebugSettings(boolean p_159123_, BlockState p_159124_, BlockState p_159125_, BlockState p_159126_, BlockState p_159127_) {
      this.debugMode = p_159123_;
      this.airState = p_159124_;
      this.waterState = p_159125_;
      this.lavaState = p_159126_;
      this.barrierState = p_159127_;
   }

   public boolean isDebugMode() {
      return this.debugMode;
   }

   public BlockState getAirState() {
      return this.airState;
   }

   public BlockState getWaterState() {
      return this.waterState;
   }

   public BlockState getLavaState() {
      return this.lavaState;
   }

   public BlockState getBarrierState() {
      return this.barrierState;
   }
}