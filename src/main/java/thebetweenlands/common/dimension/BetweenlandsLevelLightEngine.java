package thebetweenlands.common.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.SkyLightEngine;

public class BetweenlandsLevelLightEngine extends LevelLightEngine {
    public BetweenlandsLevelLightEngine(LightChunkGetter p_75805_, boolean p_75806_, boolean p_75807_) {
        super(p_75805_, p_75806_, p_75807_);
        this.blockEngine = p_75806_ ? new BlockLightEngine(p_75805_) : null;
        this.skyEngine = p_75807_ ? new BetweenlandsSkyLightEngine(p_75805_) : null;
    }

    public int getRawBrightness(BlockPos p_75832_, int p_75833_) {
        int i = this.skyEngine == null ? 0 : this.skyEngine.getLightValue(p_75832_) - p_75833_;
        int j = this.blockEngine == null ? 0 : this.blockEngine.getLightValue(p_75832_);
        return Math.max(j, i);
    }
}
