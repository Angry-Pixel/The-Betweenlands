package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.DesertPyramidPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class DesertPyramidFeature extends StructureFeature<NoneFeatureConfiguration> {
   public DesertPyramidFeature(Codec<NoneFeatureConfiguration> p_65568_) {
      super(p_65568_, PieceGeneratorSupplier.simple(DesertPyramidFeature::checkLocation, DesertPyramidFeature::generatePieces));
   }

   private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> p_197078_) {
      if (!p_197078_.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
         return false;
      } else {
         return p_197078_.getLowestY(21, 21) >= p_197078_.chunkGenerator().getSeaLevel();
      }
   }

   private static void generatePieces(StructurePiecesBuilder p_197080_, PieceGenerator.Context<NoneFeatureConfiguration> p_197081_) {
      p_197080_.addPiece(new DesertPyramidPiece(p_197081_.random(), p_197081_.chunkPos().getMinBlockX(), p_197081_.chunkPos().getMinBlockZ()));
   }
}