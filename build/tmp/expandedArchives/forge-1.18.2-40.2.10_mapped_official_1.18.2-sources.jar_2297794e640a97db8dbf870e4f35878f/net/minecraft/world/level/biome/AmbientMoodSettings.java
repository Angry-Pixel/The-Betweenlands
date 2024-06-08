package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class AmbientMoodSettings {
   public static final Codec<AmbientMoodSettings> CODEC = RecordCodecBuilder.create((p_47402_) -> {
      return p_47402_.group(SoundEvent.CODEC.fieldOf("sound").forGetter((p_151650_) -> {
         return p_151650_.soundEvent;
      }), Codec.INT.fieldOf("tick_delay").forGetter((p_151648_) -> {
         return p_151648_.tickDelay;
      }), Codec.INT.fieldOf("block_search_extent").forGetter((p_151646_) -> {
         return p_151646_.blockSearchExtent;
      }), Codec.DOUBLE.fieldOf("offset").forGetter((p_151644_) -> {
         return p_151644_.soundPositionOffset;
      })).apply(p_47402_, AmbientMoodSettings::new);
   });
   public static final AmbientMoodSettings LEGACY_CAVE_SETTINGS = new AmbientMoodSettings(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0D);
   private final SoundEvent soundEvent;
   private final int tickDelay;
   private final int blockSearchExtent;
   private final double soundPositionOffset;

   public AmbientMoodSettings(SoundEvent p_47394_, int p_47395_, int p_47396_, double p_47397_) {
      this.soundEvent = p_47394_;
      this.tickDelay = p_47395_;
      this.blockSearchExtent = p_47396_;
      this.soundPositionOffset = p_47397_;
   }

   public SoundEvent getSoundEvent() {
      return this.soundEvent;
   }

   public int getTickDelay() {
      return this.tickDelay;
   }

   public int getBlockSearchExtent() {
      return this.blockSearchExtent;
   }

   public double getSoundPositionOffset() {
      return this.soundPositionOffset;
   }
}