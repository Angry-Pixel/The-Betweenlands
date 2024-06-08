package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StrongholdPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class StrongholdFeature extends StructureFeature<NoneFeatureConfiguration> {
   public StrongholdFeature(Codec<NoneFeatureConfiguration> p_66928_) {
      super(p_66928_, PieceGeneratorSupplier.simple(StrongholdFeature::checkLocation, StrongholdFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> p_197160_) {
      return true;
   }

   private static void generatePieces(StructurePiecesBuilder p_197162_, PieceGenerator.Context<NoneFeatureConfiguration> p_197163_) {
      int i = 0;

      StrongholdPieces.StartPiece strongholdpieces$startpiece;
      do {
         p_197162_.clear();
         p_197163_.random().setLargeFeatureSeed(p_197163_.seed() + (long)(i++), p_197163_.chunkPos().x, p_197163_.chunkPos().z);
         StrongholdPieces.resetPieces();
         strongholdpieces$startpiece = new StrongholdPieces.StartPiece(p_197163_.random(), p_197163_.chunkPos().getBlockX(2), p_197163_.chunkPos().getBlockZ(2));
         p_197162_.addPiece(strongholdpieces$startpiece);
         strongholdpieces$startpiece.addChildren(strongholdpieces$startpiece, p_197162_, p_197163_.random());
         List<StructurePiece> list = strongholdpieces$startpiece.pendingChildren;

         while(!list.isEmpty()) {
            int j = p_197163_.random().nextInt(list.size());
            StructurePiece structurepiece = list.remove(j);
            structurepiece.addChildren(strongholdpieces$startpiece, p_197162_, p_197163_.random());
         }

         p_197162_.moveBelowSeaLevel(p_197163_.chunkGenerator().getSeaLevel(), p_197163_.chunkGenerator().getMinY(), p_197163_.random(), 10);
      } while(p_197162_.isEmpty() || strongholdpieces$startpiece.portalRoomPiece == null);

   }
}