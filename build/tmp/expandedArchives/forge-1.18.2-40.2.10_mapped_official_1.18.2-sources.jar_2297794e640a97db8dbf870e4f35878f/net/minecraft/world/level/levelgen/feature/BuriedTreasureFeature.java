package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class BuriedTreasureFeature extends StructureFeature<ProbabilityFeatureConfiguration> {
   private static final int RANDOM_SALT = 10387320;

   public BuriedTreasureFeature(Codec<ProbabilityFeatureConfiguration> p_65313_) {
      super(p_65313_, PieceGeneratorSupplier.simple(BuriedTreasureFeature::checkLocation, BuriedTreasureFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<ProbabilityFeatureConfiguration> p_197073_) {
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
      worldgenrandom.setLargeFeatureWithSalt(p_197073_.seed(), p_197073_.chunkPos().x, p_197073_.chunkPos().z, 10387320);
      return worldgenrandom.nextFloat() < (p_197073_.config()).probability && p_197073_.validBiomeOnTop(Heightmap.Types.OCEAN_FLOOR_WG);
   }

   private static void generatePieces(StructurePiecesBuilder p_197075_, PieceGenerator.Context<ProbabilityFeatureConfiguration> p_197076_) {
      BlockPos blockpos = new BlockPos(p_197076_.chunkPos().getBlockX(9), 90, p_197076_.chunkPos().getBlockZ(9));
      p_197075_.addPiece(new BuriedTreasurePieces.BuriedTreasurePiece(blockpos));
   }
}