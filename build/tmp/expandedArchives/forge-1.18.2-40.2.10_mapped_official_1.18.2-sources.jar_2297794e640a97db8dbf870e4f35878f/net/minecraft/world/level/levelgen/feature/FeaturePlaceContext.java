package net.minecraft.world.level.levelgen.feature;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class FeaturePlaceContext<FC extends FeatureConfiguration> {
   private final Optional<ConfiguredFeature<?, ?>> topFeature;
   private final WorldGenLevel level;
   private final ChunkGenerator chunkGenerator;
   private final Random random;
   private final BlockPos origin;
   private final FC config;

   public FeaturePlaceContext(Optional<ConfiguredFeature<?, ?>> p_190929_, WorldGenLevel p_190930_, ChunkGenerator p_190931_, Random p_190932_, BlockPos p_190933_, FC p_190934_) {
      this.topFeature = p_190929_;
      this.level = p_190930_;
      this.chunkGenerator = p_190931_;
      this.random = p_190932_;
      this.origin = p_190933_;
      this.config = p_190934_;
   }

   public Optional<ConfiguredFeature<?, ?>> topFeature() {
      return this.topFeature;
   }

   public WorldGenLevel level() {
      return this.level;
   }

   public ChunkGenerator chunkGenerator() {
      return this.chunkGenerator;
   }

   public Random random() {
      return this.random;
   }

   public BlockPos origin() {
      return this.origin;
   }

   public FC config() {
      return this.config;
   }
}