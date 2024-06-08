package net.minecraft.world.level.levelgen.structure.pieces;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

@FunctionalInterface
public interface PieceGeneratorSupplier<C extends FeatureConfiguration> {
   Optional<PieceGenerator<C>> createGenerator(PieceGeneratorSupplier.Context<C> p_197348_);

   static <C extends FeatureConfiguration> PieceGeneratorSupplier<C> simple(Predicate<PieceGeneratorSupplier.Context<C>> p_197350_, PieceGenerator<C> p_197351_) {
      Optional<PieceGenerator<C>> optional = Optional.of(p_197351_);
      return (p_197344_) -> {
         return p_197350_.test(p_197344_) ? optional : Optional.empty();
      };
   }

   static <C extends FeatureConfiguration> Predicate<PieceGeneratorSupplier.Context<C>> checkForBiomeOnTop(Heightmap.Types p_197346_) {
      return (p_197340_) -> {
         return p_197340_.validBiomeOnTop(p_197346_);
      };
   }

   public static record Context<C extends FeatureConfiguration>(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed, ChunkPos chunkPos, C config, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> validBiome, StructureManager structureManager, RegistryAccess registryAccess) {
      public boolean validBiomeOnTop(Heightmap.Types p_197381_) {
         int i = this.chunkPos.getMiddleBlockX();
         int j = this.chunkPos.getMiddleBlockZ();
         int k = this.chunkGenerator.getFirstOccupiedHeight(i, j, p_197381_, this.heightAccessor);
         Holder<Biome> holder = this.chunkGenerator.getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(k), QuartPos.fromBlock(j));
         return this.validBiome.test(holder);
      }

      public int[] getCornerHeights(int p_197376_, int p_197377_, int p_197378_, int p_197379_) {
         return new int[]{this.chunkGenerator.getFirstOccupiedHeight(p_197376_, p_197378_, Heightmap.Types.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getFirstOccupiedHeight(p_197376_, p_197378_ + p_197379_, Heightmap.Types.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getFirstOccupiedHeight(p_197376_ + p_197377_, p_197378_, Heightmap.Types.WORLD_SURFACE_WG, this.heightAccessor), this.chunkGenerator.getFirstOccupiedHeight(p_197376_ + p_197377_, p_197378_ + p_197379_, Heightmap.Types.WORLD_SURFACE_WG, this.heightAccessor)};
      }

      public int getLowestY(int p_197373_, int p_197374_) {
         int i = this.chunkPos.getMinBlockX();
         int j = this.chunkPos.getMinBlockZ();
         int[] aint = this.getCornerHeights(i, p_197373_, j, p_197374_);
         return Math.min(Math.min(aint[0], aint[1]), Math.min(aint[2], aint[3]));
      }
   }
}