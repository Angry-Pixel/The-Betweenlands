package net.minecraft.world.level;

import net.minecraft.world.level.biome.Biome;

@FunctionalInterface
public interface ColorResolver {
   int getColor(Biome p_130046_, double p_130047_, double p_130048_);
}