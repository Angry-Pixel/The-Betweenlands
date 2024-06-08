package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.lighting.LevelLightEngine;

public interface BlockAndTintGetter extends BlockGetter {
   float getShade(Direction p_45522_, boolean p_45523_);

   LevelLightEngine getLightEngine();

   int getBlockTint(BlockPos p_45520_, ColorResolver p_45521_);

   default int getBrightness(LightLayer p_45518_, BlockPos p_45519_) {
      return this.getLightEngine().getLayerListener(p_45518_).getLightValue(p_45519_);
   }

   default int getRawBrightness(BlockPos p_45525_, int p_45526_) {
      return this.getLightEngine().getRawBrightness(p_45525_, p_45526_);
   }

   default boolean canSeeSky(BlockPos p_45528_) {
      return this.getBrightness(LightLayer.SKY, p_45528_) >= this.getMaxLightLevel();
   }
}