package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanMonumentPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class OceanMonumentFeature extends StructureFeature<NoneFeatureConfiguration> {
   public OceanMonumentFeature(Codec<NoneFeatureConfiguration> p_66472_) {
      super(p_66472_, PieceGeneratorSupplier.simple(OceanMonumentFeature::checkLocation, OceanMonumentFeature::generatePieces));
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> p_197132_) {
      int i = p_197132_.chunkPos().getBlockX(9);
      int j = p_197132_.chunkPos().getBlockZ(9);

      for(Holder<Biome> holder : p_197132_.biomeSource().getBiomesWithin(i, p_197132_.chunkGenerator().getSeaLevel(), j, 29, p_197132_.chunkGenerator().climateSampler())) {
         if (Biome.getBiomeCategory(holder) != Biome.BiomeCategory.OCEAN && Biome.getBiomeCategory(holder) != Biome.BiomeCategory.RIVER) {
            return false;
         }
      }

      return p_197132_.validBiomeOnTop(Heightmap.Types.OCEAN_FLOOR_WG);
   }

   private static StructurePiece createTopPiece(ChunkPos p_191025_, WorldgenRandom p_191026_) {
      int i = p_191025_.getMinBlockX() - 29;
      int j = p_191025_.getMinBlockZ() - 29;
      Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(p_191026_);
      return new OceanMonumentPieces.MonumentBuilding(p_191026_, i, j, direction);
   }

   private static void generatePieces(StructurePiecesBuilder p_191046_, PieceGenerator.Context<NoneFeatureConfiguration> p_191047_) {
      p_191046_.addPiece(createTopPiece(p_191047_.chunkPos(), p_191047_.random()));
   }

   public static PiecesContainer regeneratePiecesAfterLoad(ChunkPos p_191021_, long p_191022_, PiecesContainer p_191023_) {
      if (p_191023_.isEmpty()) {
         return p_191023_;
      } else {
         WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));
         worldgenrandom.setLargeFeatureSeed(p_191022_, p_191021_.x, p_191021_.z);
         StructurePiece structurepiece = p_191023_.pieces().get(0);
         BoundingBox boundingbox = structurepiece.getBoundingBox();
         int i = boundingbox.minX();
         int j = boundingbox.minZ();
         Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(worldgenrandom);
         Direction direction1 = Objects.requireNonNullElse(structurepiece.getOrientation(), direction);
         StructurePiece structurepiece1 = new OceanMonumentPieces.MonumentBuilding(worldgenrandom, i, j, direction1);
         StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();
         structurepiecesbuilder.addPiece(structurepiece1);
         return structurepiecesbuilder.build();
      }
   }
}