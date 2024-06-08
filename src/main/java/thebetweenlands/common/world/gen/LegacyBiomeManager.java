package thebetweenlands.common.world.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

// Adjusted biome manager
public class LegacyBiomeManager extends BiomeManager {
    public LegacyBiomeManager(NoiseBiomeSource p_186677_, long p_186678_) {
        super(p_186677_, p_186678_);
    }

    @Override
    public Holder<Biome> getBiome(BlockPos p_204215_) {
        return super.getBiome(p_204215_);
    }
}
