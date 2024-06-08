package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Music {
   public static final Codec<Music> CODEC = RecordCodecBuilder.create((p_11635_) -> {
      return p_11635_.group(SoundEvent.CODEC.fieldOf("sound").forGetter((p_144041_) -> {
         return p_144041_.event;
      }), Codec.INT.fieldOf("min_delay").forGetter((p_144039_) -> {
         return p_144039_.minDelay;
      }), Codec.INT.fieldOf("max_delay").forGetter((p_144037_) -> {
         return p_144037_.maxDelay;
      }), Codec.BOOL.fieldOf("replace_current_music").forGetter((p_144035_) -> {
         return p_144035_.replaceCurrentMusic;
      })).apply(p_11635_, Music::new);
   });
   private final SoundEvent event;
   private final int minDelay;
   private final int maxDelay;
   private final boolean replaceCurrentMusic;

   public Music(SoundEvent p_11627_, int p_11628_, int p_11629_, boolean p_11630_) {
      this.event = p_11627_;
      this.minDelay = p_11628_;
      this.maxDelay = p_11629_;
      this.replaceCurrentMusic = p_11630_;
   }

   public SoundEvent getEvent() {
      return this.event;
   }

   public int getMinDelay() {
      return this.minDelay;
   }

   public int getMaxDelay() {
      return this.maxDelay;
   }

   public boolean replaceCurrentMusic() {
      return this.replaceCurrentMusic;
   }
}