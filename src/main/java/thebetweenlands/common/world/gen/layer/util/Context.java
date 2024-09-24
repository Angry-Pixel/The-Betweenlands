package thebetweenlands.common.world.gen.layer.util;

import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

public interface Context {
    int nextRandom(int bound);

    ImprovedNoise getBiomeNoise();
}
