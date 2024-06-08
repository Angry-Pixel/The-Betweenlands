package thebetweenlands.common.dimension;

import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.lighting.LevelLightEngine;


// Definitely considering making a standalone dimension helper mod lib
public class BetweenlandsServerChunkCache extends ClientChunkCache {

    public BetweenlandsLevelLightEngine lightEngine;

    public BetweenlandsServerChunkCache(ClientLevel p_104414_, int p_104415_) {
        super(p_104414_, p_104415_);
        this.lightEngine = new BetweenlandsLevelLightEngine(this, true, p_104414_.dimensionType().hasSkyLight());
    }

    public LevelLightEngine getLightEngine() {
        return this.lightEngine;
    }
}
