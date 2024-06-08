package net.minecraft.world.level;

import net.minecraft.world.level.dimension.DimensionType;

public interface LevelTimeAccess extends LevelReader {
   long dayTime();

   default float getMoonBrightness() {
      return DimensionType.MOON_BRIGHTNESS_PER_PHASE[this.dimensionType().moonPhase(this.dayTime())];
   }

   default float getTimeOfDay(float p_46943_) {
      return this.dimensionType().timeOfDay(this.dayTime());
   }

   default int getMoonPhase() {
      return this.dimensionType().moonPhase(this.dayTime());
   }
}