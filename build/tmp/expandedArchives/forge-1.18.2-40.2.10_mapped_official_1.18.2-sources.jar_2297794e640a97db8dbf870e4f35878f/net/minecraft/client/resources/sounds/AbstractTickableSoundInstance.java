package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTickableSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
   private boolean stopped;

   protected AbstractTickableSoundInstance(SoundEvent p_119606_, SoundSource p_119607_) {
      super(p_119606_, p_119607_);
   }

   public boolean isStopped() {
      return this.stopped;
   }

   protected final void stop() {
      this.stopped = true;
      this.looping = false;
   }
}