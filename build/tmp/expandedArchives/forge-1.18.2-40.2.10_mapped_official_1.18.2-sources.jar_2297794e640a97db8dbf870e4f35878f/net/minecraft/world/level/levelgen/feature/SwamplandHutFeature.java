package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.SwamplandHutPiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class SwamplandHutFeature extends StructureFeature<NoneFeatureConfiguration> {
   public SwamplandHutFeature(Codec<NoneFeatureConfiguration> p_67173_) {
      super(p_67173_, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG), SwamplandHutFeature::generatePieces));
   }

   private static void generatePieces(StructurePiecesBuilder p_197182_, PieceGenerator.Context<NoneFeatureConfiguration> p_197183_) {
      p_197182_.addPiece(new SwamplandHutPiece(p_197183_.random(), p_197183_.chunkPos().getMinBlockX(), p_197183_.chunkPos().getMinBlockZ()));
   }
}