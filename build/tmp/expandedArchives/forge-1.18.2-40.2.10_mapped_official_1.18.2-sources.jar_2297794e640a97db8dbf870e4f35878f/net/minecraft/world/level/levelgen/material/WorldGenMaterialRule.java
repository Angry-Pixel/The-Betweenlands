package net.minecraft.world.level.levelgen.material;

import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseChunk;

public interface WorldGenMaterialRule {
   @Nullable
   BlockState apply(NoiseChunk p_191553_, int p_191554_, int p_191555_, int p_191556_);
}