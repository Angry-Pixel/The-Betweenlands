package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeeAggressiveSoundInstance extends BeeSoundInstance {
   public BeeAggressiveSoundInstance(Bee p_119611_) {
      super(p_119611_, SoundEvents.BEE_LOOP_AGGRESSIVE, SoundSource.NEUTRAL);
      this.delay = 0;
   }

   protected AbstractTickableSoundInstance getAlternativeSoundInstance() {
      return new BeeFlyingSoundInstance(this.bee);
   }

   protected boolean shouldSwitchSounds() {
      return !this.bee.isAngry();
   }
}