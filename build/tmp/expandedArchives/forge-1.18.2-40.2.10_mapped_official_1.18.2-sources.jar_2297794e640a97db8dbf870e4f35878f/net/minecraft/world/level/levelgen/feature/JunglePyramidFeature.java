package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.JunglePyramidPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class JunglePyramidFeature extends StructureFeature<NoneFeatureConfiguration> {
   public JunglePyramidFeature(Codec<NoneFeatureConfiguration> p_66193_) {
      super(p_66193_, PieceGeneratorSupplier.simple(JunglePyramidFeature::checkLocation, JunglePyramidFeature::generatePieces));
   }

   private static <C extends FeatureConfiguration> boolean checkLocation(PieceGeneratorSupplier.Context<C> p_197104_) {
      if (!p_197104_.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG)) {
         return false;
      } else {
         return p_197104_.getLowestY(12, 15) >= p_197104_.chunkGenerator().getSeaLevel();
      }
   }

   private static void generatePieces(StructurePiecesBuilder p_197106_, PieceGenerator.Context<NoneFeatureConfiguration> p_197107_) {
      p_197106_.addPiece(new JunglePyramidPiece(p_197107_.random(), p_197107_.chunkPos().getMinBlockX(), p_197107_.chunkPos().getMinBlockZ()));
   }
}