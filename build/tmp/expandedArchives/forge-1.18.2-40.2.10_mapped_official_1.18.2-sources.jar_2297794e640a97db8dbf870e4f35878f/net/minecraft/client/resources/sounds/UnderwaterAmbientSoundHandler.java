package net.minecraft.client.resources.sounds;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnderwaterAmbientSoundHandler implements AmbientSoundHandler {
   public static final float CHANCE_PER_TICK = 0.01F;
   public static final float RARE_CHANCE_PER_TICK = 0.001F;
   public static final float ULTRA_RARE_CHANCE_PER_TICK = 1.0E-4F;
   private static final int MINIMUM_TICK_DELAY = 0;
   private final LocalPlayer player;
   private final SoundManager soundManager;
   private int tickDelay = 0;

   public UnderwaterAmbientSoundHandler(LocalPlayer p_119856_, SoundManager p_119857_) {
      this.player = p_119856_;
      this.soundManager = p_119857_;
   }

   public void tick() {
      --this.tickDelay;
      if (this.tickDelay <= 0 && this.player.isUnderWater()) {
         float f = this.player.level.random.nextFloat();
         if (f < 1.0E-4F) {
            this.tickDelay = 0;
            this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE));
         } else if (f < 0.001F) {
            this.tickDelay = 0;
            this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE));
         } else if (f < 0.01F) {
            this.tickDelay = 0;
            this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS));
         }
      }

   }
}