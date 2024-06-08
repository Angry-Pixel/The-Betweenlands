package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.IglooPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class IglooFeature extends StructureFeature<NoneFeatureConfiguration> {
   public IglooFeature(Codec<NoneFeatureConfiguration> p_66121_) {
      super(p_66121_, PieceGeneratorSupplier.simple(PieceGeneratorSupplier.checkForBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG), IglooFeature::generatePieces));
   }

   private static void generatePieces(StructurePiecesBuilder p_197089_, PieceGenerator.Context<NoneFeatureConfiguration> p_197090_) {
      BlockPos blockpos = new BlockPos(p_197090_.chunkPos().getMinBlockX(), 90, p_197090_.chunkPos().getMinBlockZ());
      Rotation rotation = Rotation.getRandom(p_197090_.random());
      IglooPieces.addPieces(p_197090_.structureManager(), blockpos, rotation, p_197089_, p_197090_.random());
   }
}