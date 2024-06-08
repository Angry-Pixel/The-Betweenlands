package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class ConfiguredStructureFeature<FC extends FeatureConfiguration, F extends StructureFeature<FC>> {
   public static final Codec<ConfiguredStructureFeature<?, ?>> DIRECT_CODEC = Registry.STRUCTURE_FEATURE.byNameCodec().dispatch((p_65410_) -> {
      return p_65410_.feature;
   }, StructureFeature::configuredStructureCodec);
   public static final Codec<Holder<ConfiguredStructureFeature<?, ?>>> CODEC = RegistryFileCodec.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, DIRECT_CODEC);
   public static final Codec<HolderSet<ConfiguredStructureFeature<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, DIRECT_CODEC);
   public final F feature;
   public final FC config;
   public final HolderSet<Biome> biomes;
   public final Map<MobCategory, StructureSpawnOverride> spawnOverrides;
   public final boolean adaptNoise;

   public ConfiguredStructureFeature(F p_209747_, FC p_209748_, HolderSet<Biome> p_209749_, boolean p_209750_, Map<MobCategory, StructureSpawnOverride> p_209751_) {
      this.feature = p_209747_;
      this.config = p_209748_;
      this.biomes = p_209749_;
      this.adaptNoise = p_209750_;
      this.spawnOverrides = p_209751_;
   }

   public StructureStart generate(RegistryAccess p_204708_, ChunkGenerator p_204709_, BiomeSource p_204710_, StructureManager p_204711_, long p_204712_, ChunkPos p_204713_, int p_204714_, LevelHeightAccessor p_204715_, Predicate<Holder<Biome>> p_204716_) {
      Optional<PieceGenerator<FC>> optional = this.feature.pieceGeneratorSupplier().createGenerator(new PieceGeneratorSupplier.Context<>(p_204709_, p_204710_, p_204712_, p_204713_, this.config, p_204715_, p_204716_, p_204711_, p_204708_));
      if (optional.isPresent()) {
         StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();
         WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
         worldgenrandom.setLargeFeatureSeed(p_204712_, p_204713_.x, p_204713_.z);
         optional.get().generatePieces(structurepiecesbuilder, new PieceGenerator.Context<>(this.config, p_204709_, p_204711_, p_204713_, p_204715_, worldgenrandom, p_204712_));
         StructureStart structurestart = new StructureStart(this, p_204713_, p_204714_, structurepiecesbuilder.build());
         if (structurestart.isValid()) {
            return structurestart;
         }
      }

      return StructureStart.INVALID_START;
   }

   public HolderSet<Biome> biomes() {
      return this.biomes;
   }

   public BoundingBox adjustBoundingBox(BoundingBox p_209754_) {
      return this.adaptNoise ? p_209754_.inflatedBy(12) : p_209754_;
   }
}