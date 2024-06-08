package net.minecraft.client.resources.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Bee;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BeeSoundInstance extends AbstractTickableSoundInstance {
   private static final float VOLUME_MIN = 0.0F;
   private static final float VOLUME_MAX = 1.2F;
   private static final float PITCH_MIN = 0.0F;
   protected final Bee bee;
   private boolean hasSwitched;

   public BeeSoundInstance(Bee p_119621_, SoundEvent p_119622_, SoundSource p_119623_) {
      super(p_119622_, p_119623_);
      this.bee = p_119621_;
      this.x = (double)((float)p_119621_.getX());
      this.y = (double)((float)p_119621_.getY());
      this.z = (double)((float)p_119621_.getZ());
      this.looping = true;
      this.delay = 0;
      this.volume = 0.0F;
   }

   public void tick() {
      boolean flag = this.shouldSwitchSounds();
      if (flag && !this.isStopped()) {
         Minecraft.getInstance().getSoundManager().queueTickingSound(this.getAlternativeSoundInstance());
         this.hasSwitched = true;
      }

      if (!this.bee.isRemoved() && !this.hasSwitched) {
         this.x = (double)((float)this.bee.getX());
         this.y = (double)((float)this.bee.getY());
         this.z = (double)((float)this.bee.getZ());
         float f = (float)this.bee.getDeltaMovement().horizontalDistance();
         if (f >= 0.01F) {
            this.pitch = Mth.lerp(Mth.clamp(f, this.getMinPitch(), this.getMaxPitch()), this.getMinPitch(), this.getMaxPitch());
            this.volume = Mth.lerp(Mth.clamp(f, 0.0F, 0.5F), 0.0F, 1.2F);
         } else {
            this.pitch = 0.0F;
            this.volume = 0.0F;
         }

      } else {
         this.stop();
      }
   }

   private float getMinPitch() {
      return this.bee.isBaby() ? 1.1F : 0.7F;
   }

   private float getMaxPitch() {
      return this.bee.isBaby() ? 1.5F : 1.1F;
   }

   public boolean canStartSilent() {
      return true;
   }

   public boolean canPlaySound() {
      return !this.bee.isSilent();
   }

   protected abstract AbstractTickableSoundInstance getAlternativeSoundInstance();

   protected abstract boolean shouldSwitchSounds();
}