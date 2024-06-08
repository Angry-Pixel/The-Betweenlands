package net.minecraft.client.sounds;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface Weighted<T> {
   int getWeight();

   T getSound();

   void preloadIfRequired(SoundEngine p_120456_);
}