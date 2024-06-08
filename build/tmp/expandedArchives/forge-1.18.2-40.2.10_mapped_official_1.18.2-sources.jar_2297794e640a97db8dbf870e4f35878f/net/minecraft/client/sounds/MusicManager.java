package net.minecraft.client.sounds;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MusicManager {
   private static final int STARTING_DELAY = 100;
   private final Random random = new Random();
   private final Minecraft minecraft;
   @Nullable
   private SoundInstance currentMusic;
   private int nextSongDelay = 100;

   public MusicManager(Minecraft p_120182_) {
      this.minecraft = p_120182_;
   }

   public void tick() {
      Music music = this.minecraft.getSituationalMusic();
      if (this.currentMusic != null) {
         if (!music.getEvent().getLocation().equals(this.currentMusic.getLocation()) && music.replaceCurrentMusic()) {
            this.minecraft.getSoundManager().stop(this.currentMusic);
            this.nextSongDelay = Mth.nextInt(this.random, 0, music.getMinDelay() / 2);
         }

         if (!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
            this.currentMusic = null;
            this.nextSongDelay = Math.min(this.nextSongDelay, Mth.nextInt(this.random, music.getMinDelay(), music.getMaxDelay()));
         }
      }

      this.nextSongDelay = Math.min(this.nextSongDelay, music.getMaxDelay());
      if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
         this.startPlaying(music);
      }

   }

   public void startPlaying(Music p_120185_) {
      this.currentMusic = SimpleSoundInstance.forMusic(p_120185_.getEvent());
      if (this.currentMusic.getSound() != SoundManager.EMPTY_SOUND) {
         this.minecraft.getSoundManager().play(this.currentMusic);
      }

      this.nextSongDelay = Integer.MAX_VALUE;
   }

   public void stopPlaying() {
      if (this.currentMusic != null) {
         this.minecraft.getSoundManager().stop(this.currentMusic);
         this.currentMusic = null;
      }

      this.nextSongDelay += 100;
   }

   public boolean isPlayingMusic(Music p_120188_) {
      return this.currentMusic == null ? false : p_120188_.getEvent().getLocation().equals(this.currentMusic.getLocation());
   }
}