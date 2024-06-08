package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sounds.SoundEvent;

public class AmbientAdditionsSettings {
   public static final Codec<AmbientAdditionsSettings> CODEC = RecordCodecBuilder.create((p_47382_) -> {
      return p_47382_.group(SoundEvent.CODEC.fieldOf("sound").forGetter((p_151642_) -> {
         return p_151642_.soundEvent;
      }), Codec.DOUBLE.fieldOf("tick_chance").forGetter((p_151640_) -> {
         return p_151640_.tickChance;
      })).apply(p_47382_, AmbientAdditionsSettings::new);
   });
   private final SoundEvent soundEvent;
   private final double tickChance;

   public AmbientAdditionsSettings(SoundEvent p_47376_, double p_47377_) {
      this.soundEvent = p_47376_;
      this.tickChance = p_47377_;
   }

   public SoundEvent getSoundEvent() {
      return this.soundEvent;
   }

   public double getTickChance() {
      return this.tickChance;
   }
}