package net.minecraft.client.resources.sounds;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface AmbientSoundHandler {
   void tick();
}