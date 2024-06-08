package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;

public class BiomeSpecialEffects {
   public static final Codec<BiomeSpecialEffects> CODEC = RecordCodecBuilder.create((p_47971_) -> {
      return p_47971_.group(Codec.INT.fieldOf("fog_color").forGetter((p_151782_) -> {
         return p_151782_.fogColor;
      }), Codec.INT.fieldOf("water_color").forGetter((p_151780_) -> {
         return p_151780_.waterColor;
      }), Codec.INT.fieldOf("water_fog_color").forGetter((p_151778_) -> {
         return p_151778_.waterFogColor;
      }), Codec.INT.fieldOf("sky_color").forGetter((p_151776_) -> {
         return p_151776_.skyColor;
      }), Codec.INT.optionalFieldOf("foliage_color").forGetter((p_151774_) -> {
         return p_151774_.foliageColorOverride;
      }), Codec.INT.optionalFieldOf("grass_color").forGetter((p_151772_) -> {
         return p_151772_.grassColorOverride;
      }), BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", BiomeSpecialEffects.GrassColorModifier.NONE).forGetter((p_151770_) -> {
         return p_151770_.grassColorModifier;
      }), AmbientParticleSettings.CODEC.optionalFieldOf("particle").forGetter((p_151768_) -> {
         return p_151768_.ambientParticleSettings;
      }), SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter((p_151766_) -> {
         return p_151766_.ambientLoopSoundEvent;
      }), AmbientMoodSettings.CODEC.optionalFieldOf("mood_sound").forGetter((p_151764_) -> {
         return p_151764_.ambientMoodSettings;
      }), AmbientAdditionsSettings.CODEC.optionalFieldOf("additions_sound").forGetter((p_151762_) -> {
         return p_151762_.ambientAdditionsSettings;
      }), Music.CODEC.optionalFieldOf("music").forGetter((p_151760_) -> {
         return p_151760_.backgroundMusic;
      })).apply(p_47971_, BiomeSpecialEffects::new);
   });
   private final int fogColor;
   private final int waterColor;
   private final int waterFogColor;
   private final int skyColor;
   private final Optional<Integer> foliageColorOverride;
   private final Optional<Integer> grassColorOverride;
   private final BiomeSpecialEffects.GrassColorModifier grassColorModifier;
   private final Optional<AmbientParticleSettings> ambientParticleSettings;
   private final Optional<SoundEvent> ambientLoopSoundEvent;
   private final Optional<AmbientMoodSettings> ambientMoodSettings;
   private final Optional<AmbientAdditionsSettings> ambientAdditionsSettings;
   private final Optional<Music> backgroundMusic;

   BiomeSpecialEffects(int p_47941_, int p_47942_, int p_47943_, int p_47944_, Optional<Integer> p_47945_, Optional<Integer> p_47946_, BiomeSpecialEffects.GrassColorModifier p_47947_, Optional<AmbientParticleSettings> p_47948_, Optional<SoundEvent> p_47949_, Optional<AmbientMoodSettings> p_47950_, Optional<AmbientAdditionsSettings> p_47951_, Optional<Music> p_47952_) {
      this.fogColor = p_47941_;
      this.waterColor = p_47942_;
      this.waterFogColor = p_47943_;
      this.skyColor = p_47944_;
      this.foliageColorOverride = p_47945_;
      this.grassColorOverride = p_47946_;
      this.grassColorModifier = p_47947_;
      this.ambientParticleSettings = p_47948_;
      this.ambientLoopSoundEvent = p_47949_;
      this.ambientMoodSettings = p_47950_;
      this.ambientAdditionsSettings = p_47951_;
      this.backgroundMusic = p_47952_;
   }

   public int getFogColor() {
      return this.fogColor;
   }

   public int getWaterColor() {
      return this.waterColor;
   }

   public int getWaterFogColor() {
      return this.waterFogColor;
   }

   public int getSkyColor() {
      return this.skyColor;
   }

   public Optional<Integer> getFoliageColorOverride() {
      return this.foliageColorOverride;
   }

   public Optional<Integer> getGrassColorOverride() {
      return this.grassColorOverride;
   }

   public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier() {
      return this.grassColorModifier;
   }

   public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
      return this.ambientParticleSettings;
   }

   public Optional<SoundEvent> getAmbientLoopSoundEvent() {
      return this.ambientLoopSoundEvent;
   }

   public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
      return this.ambientMoodSettings;
   }

   public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
      return this.ambientAdditionsSettings;
   }

   public Optional<Music> getBackgroundMusic() {
      return this.backgroundMusic;
   }

   public static class Builder {
      private OptionalInt fogColor = OptionalInt.empty();
      private OptionalInt waterColor = OptionalInt.empty();
      private OptionalInt waterFogColor = OptionalInt.empty();
      private OptionalInt skyColor = OptionalInt.empty();
      private Optional<Integer> foliageColorOverride = Optional.empty();
      private Optional<Integer> grassColorOverride = Optional.empty();
      private BiomeSpecialEffects.GrassColorModifier grassColorModifier = BiomeSpecialEffects.GrassColorModifier.NONE;
      private Optional<AmbientParticleSettings> ambientParticle = Optional.empty();
      private Optional<SoundEvent> ambientLoopSoundEvent = Optional.empty();
      private Optional<AmbientMoodSettings> ambientMoodSettings = Optional.empty();
      private Optional<AmbientAdditionsSettings> ambientAdditionsSettings = Optional.empty();
      private Optional<Music> backgroundMusic = Optional.empty();

      public BiomeSpecialEffects.Builder fogColor(int p_48020_) {
         this.fogColor = OptionalInt.of(p_48020_);
         return this;
      }

      public BiomeSpecialEffects.Builder waterColor(int p_48035_) {
         this.waterColor = OptionalInt.of(p_48035_);
         return this;
      }

      public BiomeSpecialEffects.Builder waterFogColor(int p_48038_) {
         this.waterFogColor = OptionalInt.of(p_48038_);
         return this;
      }

      public BiomeSpecialEffects.Builder skyColor(int p_48041_) {
         this.skyColor = OptionalInt.of(p_48041_);
         return this;
      }

      public BiomeSpecialEffects.Builder foliageColorOverride(int p_48044_) {
         this.foliageColorOverride = Optional.of(p_48044_);
         return this;
      }

      public BiomeSpecialEffects.Builder grassColorOverride(int p_48046_) {
         this.grassColorOverride = Optional.of(p_48046_);
         return this;
      }

      public BiomeSpecialEffects.Builder grassColorModifier(BiomeSpecialEffects.GrassColorModifier p_48032_) {
         this.grassColorModifier = p_48032_;
         return this;
      }

      public BiomeSpecialEffects.Builder ambientParticle(AmbientParticleSettings p_48030_) {
         this.ambientParticle = Optional.of(p_48030_);
         return this;
      }

      public BiomeSpecialEffects.Builder ambientLoopSound(SoundEvent p_48024_) {
         this.ambientLoopSoundEvent = Optional.of(p_48024_);
         return this;
      }

      public BiomeSpecialEffects.Builder ambientMoodSound(AmbientMoodSettings p_48028_) {
         this.ambientMoodSettings = Optional.of(p_48028_);
         return this;
      }

      public BiomeSpecialEffects.Builder ambientAdditionsSound(AmbientAdditionsSettings p_48026_) {
         this.ambientAdditionsSettings = Optional.of(p_48026_);
         return this;
      }

      public BiomeSpecialEffects.Builder backgroundMusic(@Nullable Music p_48022_) {
         this.backgroundMusic = Optional.ofNullable(p_48022_);
         return this;
      }

      public BiomeSpecialEffects build() {
         return new BiomeSpecialEffects(this.fogColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'fog' color.");
         }), this.waterColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water' color.");
         }), this.waterFogColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water fog' color.");
         }), this.skyColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'sky' color.");
         }), this.foliageColorOverride, this.grassColorOverride, this.grassColorModifier, this.ambientParticle, this.ambientLoopSoundEvent, this.ambientMoodSettings, this.ambientAdditionsSettings, this.backgroundMusic);
      }
   }

   public static enum GrassColorModifier implements StringRepresentable, net.minecraftforge.common.IExtensibleEnum {
      NONE("none") {
         public int modifyColor(double p_48081_, double p_48082_, int p_48083_) {
            return p_48083_;
         }
      },
      DARK_FOREST("dark_forest") {
         public int modifyColor(double p_48089_, double p_48090_, int p_48091_) {
            return (p_48091_ & 16711422) + 2634762 >> 1;
         }
      },
      SWAMP("swamp") {
         public int modifyColor(double p_48097_, double p_48098_, int p_48099_) {
            double d0 = Biome.BIOME_INFO_NOISE.getValue(p_48097_ * 0.0225D, p_48098_ * 0.0225D, false);
            return d0 < -0.1D ? 5011004 : 6975545;
         }
      };

      private final String name;
      public static final Codec<BiomeSpecialEffects.GrassColorModifier> CODEC = net.minecraftforge.common.IExtensibleEnum.createCodecForExtensibleEnum(BiomeSpecialEffects.GrassColorModifier::values, BiomeSpecialEffects.GrassColorModifier::byName);
      private static final Map<String, BiomeSpecialEffects.GrassColorModifier> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(BiomeSpecialEffects.GrassColorModifier::getName, (p_48069_) -> {
         return p_48069_;
      }));

      public int modifyColor(double p_48065_, double p_48066_, int p_48067_) {
         return delegate.modifyGrassColor(p_48065_, p_48066_, p_48067_);
      }

      GrassColorModifier(String p_48058_) {
         this.name = p_48058_;
      }

      private ColorModifier delegate;
      private GrassColorModifier(String name, ColorModifier delegate) {
         this(name);
         this.delegate = delegate;
      }
      public static GrassColorModifier create(String name, String id, ColorModifier delegate) {
         throw new IllegalStateException("Enum not extended");
      }
      @Override
      public void init() {
         BY_NAME.put(this.getName(), this);
      }
      @FunctionalInterface
      public interface ColorModifier {
         int modifyGrassColor(double x, double z, int color);
      }
      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static BiomeSpecialEffects.GrassColorModifier byName(String p_48071_) {
         return BY_NAME.get(p_48071_);
      }
   }
}
