package net.minecraft.world.level.biome;

import net.minecraft.core.Holder;

public interface BiomeResolver {
   Holder<Biome> getNoiseBiome(int p_204221_, int p_204222_, int p_204223_, Climate.Sampler p_204224_);
}