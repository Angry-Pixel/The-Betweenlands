package net.minecraft.client.sounds;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface SoundEventListener {
   void onPlaySound(SoundInstance p_120342_, WeighedSoundEvents p_120343_);
}