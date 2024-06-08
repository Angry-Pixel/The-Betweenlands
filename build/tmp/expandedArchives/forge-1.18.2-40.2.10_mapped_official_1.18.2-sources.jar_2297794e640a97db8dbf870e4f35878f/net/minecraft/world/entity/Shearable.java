package net.minecraft.world.entity;

import net.minecraft.sounds.SoundSource;

@Deprecated // Forge: Use IForgeShearable
public interface Shearable {
   @Deprecated // Forge: Use IForgeShearable
   void shear(SoundSource p_21749_);

   @Deprecated // Forge: Use IForgeShearable
   boolean readyForShearing();
}
