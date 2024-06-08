package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;

public interface LightChunkGetter {
   @Nullable
   BlockGetter getChunkForLighting(int p_63023_, int p_63024_);

   default void onLightUpdate(LightLayer p_63021_, SectionPos p_63022_) {
   }

   BlockGetter getLevel();
}