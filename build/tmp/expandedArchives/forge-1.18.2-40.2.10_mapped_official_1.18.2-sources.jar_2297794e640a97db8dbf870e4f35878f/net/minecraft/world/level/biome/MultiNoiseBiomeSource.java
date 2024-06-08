package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.VisibleForDebug;

public class MultiNoiseBiomeSource extends BiomeSource {
   public static final MapCodec<MultiNoiseBiomeSource> DIRECT_CODEC = RecordCodecBuilder.mapCodec((p_187070_) -> {
      return p_187070_.group(ExtraCodecs.<Pair<Climate.ParameterPoint, Holder<Biome>>>nonEmptyList(RecordCodecBuilder.<Pair<Climate.ParameterPoint, Holder<Biome>>>create((p_187078_) -> {
         return p_187078_.group(Climate.ParameterPoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.CODEC.fieldOf("biome").forGetter(Pair::getSecond)).apply(p_187078_, Pair::of);
      }).listOf()).xmap(Climate.ParameterList::new, (Function<Climate.ParameterList<Holder<Biome>>, List<Pair<Climate.ParameterPoint, Holder<Biome>>>>) Climate.ParameterList::values).fieldOf("biomes").forGetter((p_187080_) -> {
         return p_187080_.parameters;
      })).apply(p_187070_, MultiNoiseBiomeSource::new);
   });
   public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(MultiNoiseBiomeSource.PresetInstance.CODEC, DIRECT_CODEC).xmap((p_187068_) -> {
      return p_187068_.map(MultiNoiseBiomeSource.PresetInstance::biomeSource, Function.identity());
   }, (p_187066_) -> {
      return p_187066_.preset().map(Either::<MultiNoiseBiomeSource.PresetInstance, MultiNoiseBiomeSource>left).orElseGet(() -> {
         return Either.right(p_187066_);
      });
   }).codec();
   private final Climate.ParameterList<Holder<Biome>> parameters;
   private final Optional<MultiNoiseBiomeSource.PresetInstance> preset;

   private MultiNoiseBiomeSource(Climate.ParameterList<Holder<Biome>> p_187057_) {
      this(p_187057_, Optional.empty());
   }

   MultiNoiseBiomeSource(Climate.ParameterList<Holder<Biome>> p_187059_, Optional<MultiNoiseBiomeSource.PresetInstance> p_187060_) {
      super(p_187059_.values().stream().map(Pair::getSecond));
      this.preset = p_187060_;
      this.parameters = p_187059_;
   }

   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public BiomeSource withSeed(long p_48466_) {
      return this;
   }

   private Optional<MultiNoiseBiomeSource.PresetInstance> preset() {
      return this.preset;
   }

   public boolean stable(MultiNoiseBiomeSource.Preset p_187064_) {
      return this.preset.isPresent() && Objects.equals(this.preset.get().preset(), p_187064_);
   }

   public Holder<Biome> getNoiseBiome(int p_204272_, int p_204273_, int p_204274_, Climate.Sampler p_204275_) {
      return this.getNoiseBiome(p_204275_.sample(p_204272_, p_204273_, p_204274_));
   }

   @VisibleForDebug
   public Holder<Biome> getNoiseBiome(Climate.TargetPoint p_204270_) {
      return this.parameters.findValue(p_204270_);
   }

   public void addDebugInfo(List<String> p_207895_, BlockPos p_207896_, Climate.Sampler p_207897_) {
      int i = QuartPos.fromBlock(p_207896_.getX());
      int j = QuartPos.fromBlock(p_207896_.getY());
      int k = QuartPos.fromBlock(p_207896_.getZ());
      Climate.TargetPoint climate$targetpoint = p_207897_.sample(i, j, k);
      float f = Climate.unquantizeCoord(climate$targetpoint.continentalness());
      float f1 = Climate.unquantizeCoord(climate$targetpoint.erosion());
      float f2 = Climate.unquantizeCoord(climate$targetpoint.temperature());
      float f3 = Climate.unquantizeCoord(climate$targetpoint.humidity());
      float f4 = Climate.unquantizeCoord(climate$targetpoint.weirdness());
      double d0 = (double)TerrainShaper.peaksAndValleys(f4);
      OverworldBiomeBuilder overworldbiomebuilder = new OverworldBiomeBuilder();
      p_207895_.add("Biome builder PV: " + OverworldBiomeBuilder.getDebugStringForPeaksAndValleys(d0) + " C: " + overworldbiomebuilder.getDebugStringForContinentalness((double)f) + " E: " + overworldbiomebuilder.getDebugStringForErosion((double)f1) + " T: " + overworldbiomebuilder.getDebugStringForTemperature((double)f2) + " H: " + overworldbiomebuilder.getDebugStringForHumidity((double)f3));
   }

   public static class Preset {
      static final Map<ResourceLocation, MultiNoiseBiomeSource.Preset> BY_NAME = Maps.newHashMap();
      public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(new ResourceLocation("nether"), (p_204283_) -> {
         return new Climate.ParameterList<>(ImmutableList.of(Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), p_204283_.getOrCreateHolder(Biomes.NETHER_WASTES)), Pair.of(Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), p_204283_.getOrCreateHolder(Biomes.SOUL_SAND_VALLEY)), Pair.of(Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), p_204283_.getOrCreateHolder(Biomes.CRIMSON_FOREST)), Pair.of(Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), p_204283_.getOrCreateHolder(Biomes.WARPED_FOREST)), Pair.of(Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), p_204283_.getOrCreateHolder(Biomes.BASALT_DELTAS))));
      });
      public static final MultiNoiseBiomeSource.Preset OVERWORLD = new MultiNoiseBiomeSource.Preset(new ResourceLocation("overworld"), (p_204281_) -> {
         Builder<Pair<Climate.ParameterPoint, Holder<Biome>>> builder = ImmutableList.builder();
         (new OverworldBiomeBuilder()).addBiomes((p_204279_) -> {
            builder.add(p_204279_.mapSecond(p_204281_::getOrCreateHolder));
         });
         return new Climate.ParameterList<>(builder.build());
      });
      final ResourceLocation name;
      private final Function<Registry<Biome>, Climate.ParameterList<Holder<Biome>>> parameterSource;

      public Preset(ResourceLocation p_187090_, Function<Registry<Biome>, Climate.ParameterList<Holder<Biome>>> p_187091_) {
         this.name = p_187090_;
         this.parameterSource = p_187091_;
         BY_NAME.put(p_187090_, this);
      }

      MultiNoiseBiomeSource biomeSource(MultiNoiseBiomeSource.PresetInstance p_187093_, boolean p_187094_) {
         Climate.ParameterList<Holder<Biome>> parameterlist = this.parameterSource.apply(p_187093_.biomes());
         return new MultiNoiseBiomeSource(parameterlist, p_187094_ ? Optional.of(p_187093_) : Optional.empty());
      }

      public MultiNoiseBiomeSource biomeSource(Registry<Biome> p_187105_, boolean p_187106_) {
         return this.biomeSource(new MultiNoiseBiomeSource.PresetInstance(this, p_187105_), p_187106_);
      }

      public MultiNoiseBiomeSource biomeSource(Registry<Biome> p_187100_) {
         return this.biomeSource(p_187100_, true);
      }
   }

   static record PresetInstance(MultiNoiseBiomeSource.Preset preset, Registry<Biome> biomes) {
      public static final MapCodec<MultiNoiseBiomeSource.PresetInstance> CODEC = RecordCodecBuilder.mapCodec((p_48558_) -> {
         return p_48558_.group(ResourceLocation.CODEC.flatXmap((p_151869_) -> {
            return Optional.ofNullable(MultiNoiseBiomeSource.Preset.BY_NAME.get(p_151869_)).map(DataResult::success).orElseGet(() -> {
               return DataResult.error("Unknown preset: " + p_151869_);
            });
         }, (p_151867_) -> {
            return DataResult.success(p_151867_.name);
         }).fieldOf("preset").stable().forGetter(MultiNoiseBiomeSource.PresetInstance::preset), RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(MultiNoiseBiomeSource.PresetInstance::biomes)).apply(p_48558_, p_48558_.stable(MultiNoiseBiomeSource.PresetInstance::new));
      });

      public MultiNoiseBiomeSource biomeSource() {
         return this.preset.biomeSource(this, true);
      }
   }
}