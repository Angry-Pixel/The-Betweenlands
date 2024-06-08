package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class Biome extends net.minecraftforge.registries.ForgeRegistryEntry.UncheckedRegistryEntry<Biome> {
   public static final Codec<Biome> DIRECT_CODEC = RecordCodecBuilder.create((p_186636_) -> {
      return p_186636_.group(Biome.ClimateSettings.CODEC.forGetter((p_151717_) -> {
         return p_151717_.climateSettings;
      }), Biome.BiomeCategory.CODEC.fieldOf("category").forGetter((p_151715_) -> {
         return p_151715_.biomeCategory;
      }), BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter((p_186644_) -> {
         return p_186644_.specialEffects;
      }), BiomeGenerationSettings.CODEC.forGetter((p_186642_) -> {
         return p_186642_.generationSettings;
      }), MobSpawnSettings.CODEC.forGetter((p_186640_) -> {
         return p_186640_.mobSettings;
      }), net.minecraft.resources.ResourceLocation.CODEC.optionalFieldOf("forge:registry_name").forGetter(b -> Optional.ofNullable(b.getRegistryName())))
      .apply(p_186636_, (climate, category, effects, gen, spawns, name) ->
          net.minecraftforge.common.ForgeHooks.enhanceBiome(name.orElse(null), climate, category, effects, gen, spawns, p_186636_, Biome::new));
   });
   public static final Codec<Biome> NETWORK_CODEC = RecordCodecBuilder.create((p_186632_) -> {
      return p_186632_.group(Biome.ClimateSettings.CODEC.forGetter((p_186638_) -> {
         return p_186638_.climateSettings;
      }), Biome.BiomeCategory.CODEC.fieldOf("category").forGetter((p_186634_) -> {
         return p_186634_.biomeCategory;
      }), BiomeSpecialEffects.CODEC.fieldOf("effects").forGetter((p_186630_) -> {
         return p_186630_.specialEffects;
      })).apply(p_186632_, (p_186626_, p_186627_, p_186628_) -> {
         return new Biome(p_186626_, p_186627_, p_186628_, BiomeGenerationSettings.EMPTY, MobSpawnSettings.EMPTY);
      });
   });
   public static final Codec<Holder<Biome>> CODEC = RegistryFileCodec.create(Registry.BIOME_REGISTRY, DIRECT_CODEC);
   public static final Codec<HolderSet<Biome>> LIST_CODEC = RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY, DIRECT_CODEC);
   private static final PerlinSimplexNoise TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(1234L)), ImmutableList.of(0));
   static final PerlinSimplexNoise FROZEN_TEMPERATURE_NOISE = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(3456L)), ImmutableList.of(-2, -1, 0));
   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public static final PerlinSimplexNoise BIOME_INFO_NOISE = new PerlinSimplexNoise(new WorldgenRandom(new LegacyRandomSource(2345L)), ImmutableList.of(0));
   private static final int TEMPERATURE_CACHE_SIZE = 1024;
   private final Biome.ClimateSettings climateSettings;
   private final BiomeGenerationSettings generationSettings;
   private final MobSpawnSettings mobSettings;
   private final Biome.BiomeCategory biomeCategory;
   private final BiomeSpecialEffects specialEffects;
   private final ThreadLocal<Long2FloatLinkedOpenHashMap> temperatureCache = ThreadLocal.withInitial(() -> {
      return Util.make(() -> {
         Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
            protected void rehash(int p_47580_) {
            }
         };
         long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
         return long2floatlinkedopenhashmap;
      });
   });

   Biome(Biome.ClimateSettings p_186620_, Biome.BiomeCategory p_186621_, BiomeSpecialEffects p_186622_, BiomeGenerationSettings p_186623_, MobSpawnSettings p_186624_) {
      this.climateSettings = p_186620_;
      this.generationSettings = p_186623_;
      this.mobSettings = p_186624_;
      this.biomeCategory = p_186621_;
      this.specialEffects = p_186622_;
   }

   public int getSkyColor() {
      return this.specialEffects.getSkyColor();
   }

   public MobSpawnSettings getMobSettings() {
      return this.mobSettings;
   }

   public Biome.Precipitation getPrecipitation() {
      return this.climateSettings.precipitation;
   }

   public boolean isHumid() {
      return this.getDownfall() > 0.85F;
   }

   private float getHeightAdjustedTemperature(BlockPos p_47529_) {
      float f = this.climateSettings.temperatureModifier.modifyTemperature(p_47529_, this.getBaseTemperature());
      if (p_47529_.getY() > 80) {
         float f1 = (float)(TEMPERATURE_NOISE.getValue((double)((float)p_47529_.getX() / 8.0F), (double)((float)p_47529_.getZ() / 8.0F), false) * 8.0D);
         return f - (f1 + (float)p_47529_.getY() - 80.0F) * 0.05F / 40.0F;
      } else {
         return f;
      }
   }

   /** @deprecated */
   @Deprecated
   private float getTemperature(BlockPos p_47506_) {
      long i = p_47506_.asLong();
      Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = this.temperatureCache.get();
      float f = long2floatlinkedopenhashmap.get(i);
      if (!Float.isNaN(f)) {
         return f;
      } else {
         float f1 = this.getHeightAdjustedTemperature(p_47506_);
         if (long2floatlinkedopenhashmap.size() == 1024) {
            long2floatlinkedopenhashmap.removeFirstFloat();
         }

         long2floatlinkedopenhashmap.put(i, f1);
         return f1;
      }
   }

   public boolean shouldFreeze(LevelReader p_47478_, BlockPos p_47479_) {
      return this.shouldFreeze(p_47478_, p_47479_, true);
   }

   public boolean shouldFreeze(LevelReader p_47481_, BlockPos p_47482_, boolean p_47483_) {
      if (this.warmEnoughToRain(p_47482_)) {
         return false;
      } else {
         if (p_47482_.getY() >= p_47481_.getMinBuildHeight() && p_47482_.getY() < p_47481_.getMaxBuildHeight() && p_47481_.getBrightness(LightLayer.BLOCK, p_47482_) < 10) {
            BlockState blockstate = p_47481_.getBlockState(p_47482_);
            FluidState fluidstate = p_47481_.getFluidState(p_47482_);
            if (fluidstate.getType() == Fluids.WATER && blockstate.getBlock() instanceof LiquidBlock) {
               if (!p_47483_) {
                  return true;
               }

               boolean flag = p_47481_.isWaterAt(p_47482_.west()) && p_47481_.isWaterAt(p_47482_.east()) && p_47481_.isWaterAt(p_47482_.north()) && p_47481_.isWaterAt(p_47482_.south());
               if (!flag) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean coldEnoughToSnow(BlockPos p_198905_) {
      return !this.warmEnoughToRain(p_198905_);
   }

   public boolean warmEnoughToRain(BlockPos p_198907_) {
      return this.getTemperature(p_198907_) >= 0.15F;
   }

   public boolean shouldMeltFrozenOceanIcebergSlightly(BlockPos p_198909_) {
      return this.getTemperature(p_198909_) > 0.1F;
   }

   public boolean shouldSnowGolemBurn(BlockPos p_198911_) {
      return this.getTemperature(p_198911_) > 1.0F;
   }

   public boolean shouldSnow(LevelReader p_47520_, BlockPos p_47521_) {
      if (this.warmEnoughToRain(p_47521_)) {
         return false;
      } else {
         if (p_47521_.getY() >= p_47520_.getMinBuildHeight() && p_47521_.getY() < p_47520_.getMaxBuildHeight() && p_47520_.getBrightness(LightLayer.BLOCK, p_47521_) < 10) {
            BlockState blockstate = p_47520_.getBlockState(p_47521_);
            if (blockstate.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(p_47520_, p_47521_)) {
               return true;
            }
         }

         return false;
      }
   }

   public BiomeGenerationSettings getGenerationSettings() {
      return this.generationSettings;
   }

   public int getFogColor() {
      return this.specialEffects.getFogColor();
   }

   public int getGrassColor(double p_47465_, double p_47466_) {
      int i = this.specialEffects.getGrassColorOverride().orElseGet(this::getGrassColorFromTexture);
      return this.specialEffects.getGrassColorModifier().modifyColor(p_47465_, p_47466_, i);
   }

   private int getGrassColorFromTexture() {
      double d0 = (double)Mth.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double d1 = (double)Mth.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return GrassColor.get(d0, d1);
   }

   public int getFoliageColor() {
      return this.specialEffects.getFoliageColorOverride().orElseGet(this::getFoliageColorFromTexture);
   }

   private int getFoliageColorFromTexture() {
      double d0 = (double)Mth.clamp(this.climateSettings.temperature, 0.0F, 1.0F);
      double d1 = (double)Mth.clamp(this.climateSettings.downfall, 0.0F, 1.0F);
      return FoliageColor.get(d0, d1);
   }

   public final float getDownfall() {
      return this.climateSettings.downfall;
   }

   public final float getBaseTemperature() {
      return this.climateSettings.temperature;
   }

   public BiomeSpecialEffects getSpecialEffects() {
      return this.specialEffects;
   }

   public final int getWaterColor() {
      return this.specialEffects.getWaterColor();
   }

   public final int getWaterFogColor() {
      return this.specialEffects.getWaterFogColor();
   }

   public Optional<AmbientParticleSettings> getAmbientParticle() {
      return this.specialEffects.getAmbientParticleSettings();
   }

   public Optional<SoundEvent> getAmbientLoop() {
      return this.specialEffects.getAmbientLoopSoundEvent();
   }

   public Optional<AmbientMoodSettings> getAmbientMood() {
      return this.specialEffects.getAmbientMoodSettings();
   }

   public Optional<AmbientAdditionsSettings> getAmbientAdditions() {
      return this.specialEffects.getAmbientAdditionsSettings();
   }

   public Optional<Music> getBackgroundMusic() {
      return this.specialEffects.getBackgroundMusic();
   }

   Biome.BiomeCategory getBiomeCategory() {
      return this.biomeCategory;
   }

   /** @deprecated */
   @Deprecated
   public static Biome.BiomeCategory getBiomeCategory(Holder<Biome> p_204184_) {
      return p_204184_.value().getBiomeCategory();
   }

   public static class BiomeBuilder {
      @Nullable
      private Biome.Precipitation precipitation;
      @Nullable
      private Biome.BiomeCategory biomeCategory;
      @Nullable
      private Float temperature;
      private Biome.TemperatureModifier temperatureModifier = Biome.TemperatureModifier.NONE;
      @Nullable
      private Float downfall;
      @Nullable
      private BiomeSpecialEffects specialEffects;
      @Nullable
      private MobSpawnSettings mobSpawnSettings;
      @Nullable
      private BiomeGenerationSettings generationSettings;

      public static Biome.BiomeBuilder from(Biome p_204186_) {
         return (new Biome.BiomeBuilder()).precipitation(p_204186_.getPrecipitation()).biomeCategory(p_204186_.getBiomeCategory()).temperature(p_204186_.getBaseTemperature()).downfall(p_204186_.getDownfall()).specialEffects(p_204186_.getSpecialEffects()).generationSettings(p_204186_.getGenerationSettings()).mobSpawnSettings(p_204186_.getMobSettings());
      }

      public Biome.BiomeBuilder precipitation(Biome.Precipitation p_47598_) {
         this.precipitation = p_47598_;
         return this;
      }

      public Biome.BiomeBuilder biomeCategory(Biome.BiomeCategory p_47596_) {
         this.biomeCategory = p_47596_;
         return this;
      }

      public Biome.BiomeBuilder temperature(float p_47610_) {
         this.temperature = p_47610_;
         return this;
      }

      public Biome.BiomeBuilder downfall(float p_47612_) {
         this.downfall = p_47612_;
         return this;
      }

      public Biome.BiomeBuilder specialEffects(BiomeSpecialEffects p_47604_) {
         this.specialEffects = p_47604_;
         return this;
      }

      public Biome.BiomeBuilder mobSpawnSettings(MobSpawnSettings p_47606_) {
         this.mobSpawnSettings = p_47606_;
         return this;
      }

      public Biome.BiomeBuilder generationSettings(BiomeGenerationSettings p_47602_) {
         this.generationSettings = p_47602_;
         return this;
      }

      public Biome.BiomeBuilder temperatureAdjustment(Biome.TemperatureModifier p_47600_) {
         this.temperatureModifier = p_47600_;
         return this;
      }

      public Biome build() {
         if (this.precipitation != null && this.biomeCategory != null && this.temperature != null && this.downfall != null && this.specialEffects != null && this.mobSpawnSettings != null && this.generationSettings != null) {
            return new Biome(new Biome.ClimateSettings(this.precipitation, this.temperature, this.temperatureModifier, this.downfall), this.biomeCategory, this.specialEffects, this.generationSettings, this.mobSpawnSettings);
         } else {
            throw new IllegalStateException("You are missing parameters to build a proper biome\n" + this);
         }
      }

      public String toString() {
         return "BiomeBuilder{\nprecipitation=" + this.precipitation + ",\nbiomeCategory=" + this.biomeCategory + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + this.temperatureModifier + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + this.specialEffects + ",\nmobSpawnSettings=" + this.mobSpawnSettings + ",\ngenerationSettings=" + this.generationSettings + ",\n}";
      }
   }

   public static enum BiomeCategory implements StringRepresentable {
      NONE("none"),
      TAIGA("taiga"),
      EXTREME_HILLS("extreme_hills"),
      JUNGLE("jungle"),
      MESA("mesa"),
      PLAINS("plains"),
      SAVANNA("savanna"),
      ICY("icy"),
      THEEND("the_end"),
      BEACH("beach"),
      FOREST("forest"),
      OCEAN("ocean"),
      DESERT("desert"),
      RIVER("river"),
      SWAMP("swamp"),
      MUSHROOM("mushroom"),
      NETHER("nether"),
      UNDERGROUND("underground"),
      MOUNTAIN("mountain");

      public static final Codec<Biome.BiomeCategory> CODEC = StringRepresentable.fromEnum(Biome.BiomeCategory::values, Biome.BiomeCategory::byName);
      private static final Map<String, Biome.BiomeCategory> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Biome.BiomeCategory::getName, (p_47642_) -> {
         return p_47642_;
      }));
      private final String name;

      private BiomeCategory(String p_47639_) {
         this.name = p_47639_;
      }

      public String getName() {
         return this.name;
      }

      public static Biome.BiomeCategory byName(String p_47644_) {
         return BY_NAME.get(p_47644_);
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   public static class ClimateSettings {
      public static final MapCodec<Biome.ClimateSettings> CODEC = RecordCodecBuilder.mapCodec((p_47699_) -> {
         return p_47699_.group(Biome.Precipitation.CODEC.fieldOf("precipitation").forGetter((p_151739_) -> {
            return p_151739_.precipitation;
         }), Codec.FLOAT.fieldOf("temperature").forGetter((p_151737_) -> {
            return p_151737_.temperature;
         }), Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", Biome.TemperatureModifier.NONE).forGetter((p_151735_) -> {
            return p_151735_.temperatureModifier;
         }), Codec.FLOAT.fieldOf("downfall").forGetter((p_151733_) -> {
            return p_151733_.downfall;
         })).apply(p_47699_, Biome.ClimateSettings::new);
      });
      public final Biome.Precipitation precipitation;
      public final float temperature;
      public final Biome.TemperatureModifier temperatureModifier;
      public final float downfall;

      public ClimateSettings(Biome.Precipitation p_47686_, float p_47687_, Biome.TemperatureModifier p_47688_, float p_47689_) {
         this.precipitation = p_47686_;
         this.temperature = p_47687_;
         this.temperatureModifier = p_47688_;
         this.downfall = p_47689_;
      }
   }

   public static enum Precipitation implements StringRepresentable {
      NONE("none"),
      RAIN("rain"),
      SNOW("snow");

      public static final Codec<Biome.Precipitation> CODEC = StringRepresentable.fromEnum(Biome.Precipitation::values, Biome.Precipitation::byName);
      private static final Map<String, Biome.Precipitation> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Biome.Precipitation::getName, (p_47728_) -> {
         return p_47728_;
      }));
      private final String name;

      private Precipitation(String p_47725_) {
         this.name = p_47725_;
      }

      public String getName() {
         return this.name;
      }

      public static Biome.Precipitation byName(String p_47730_) {
         return BY_NAME.get(p_47730_);
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   public static enum TemperatureModifier implements StringRepresentable {
      NONE("none") {
         public float modifyTemperature(BlockPos p_47767_, float p_47768_) {
            return p_47768_;
         }
      },
      FROZEN("frozen") {
         public float modifyTemperature(BlockPos p_47774_, float p_47775_) {
            double d0 = Biome.FROZEN_TEMPERATURE_NOISE.getValue((double)p_47774_.getX() * 0.05D, (double)p_47774_.getZ() * 0.05D, false) * 7.0D;
            double d1 = Biome.BIOME_INFO_NOISE.getValue((double)p_47774_.getX() * 0.2D, (double)p_47774_.getZ() * 0.2D, false);
            double d2 = d0 + d1;
            if (d2 < 0.3D) {
               double d3 = Biome.BIOME_INFO_NOISE.getValue((double)p_47774_.getX() * 0.09D, (double)p_47774_.getZ() * 0.09D, false);
               if (d3 < 0.8D) {
                  return 0.2F;
               }
            }

            return p_47775_;
         }
      };

      private final String name;
      public static final Codec<Biome.TemperatureModifier> CODEC = StringRepresentable.fromEnum(Biome.TemperatureModifier::values, Biome.TemperatureModifier::byName);
      private static final Map<String, Biome.TemperatureModifier> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Biome.TemperatureModifier::getName, (p_47753_) -> {
         return p_47753_;
      }));

      public abstract float modifyTemperature(BlockPos p_47754_, float p_47755_);

      TemperatureModifier(String p_47745_) {
         this.name = p_47745_;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Biome.TemperatureModifier byName(String p_47757_) {
         return BY_NAME.get(p_47757_);
      }
   }
}
