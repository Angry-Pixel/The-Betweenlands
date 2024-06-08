package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record ConfiguredFeature<FC extends FeatureConfiguration, F extends Feature<FC>>(F feature, FC config) {
   public static final Codec<ConfiguredFeature<?, ?>> DIRECT_CODEC = Registry.FEATURE.byNameCodec().dispatch((p_65391_) -> {
      return p_65391_.feature;
   }, Feature::configuredCodec);
   public static final Codec<Holder<ConfiguredFeature<?, ?>>> CODEC = RegistryFileCodec.create(Registry.CONFIGURED_FEATURE_REGISTRY, DIRECT_CODEC);
   public static final Codec<HolderSet<ConfiguredFeature<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(Registry.CONFIGURED_FEATURE_REGISTRY, DIRECT_CODEC);

   public boolean place(WorldGenLevel p_65386_, ChunkGenerator p_65387_, Random p_65388_, BlockPos p_65389_) {
      return this.feature.place(this.config, p_65386_, p_65387_, p_65388_, p_65389_);
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(Stream.of(this), this.config.getFeatures());
   }

   public String toString() {
      return "Configured: " + this.feature + ": " + this.config;
   }
}