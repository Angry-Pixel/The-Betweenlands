package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

@FunctionalInterface
public interface PostPlacementProcessor {
   PostPlacementProcessor NONE = (p_192430_, p_192431_, p_192432_, p_192433_, p_192434_, p_192435_, p_192436_) -> {
   };

   void afterPlace(WorldGenLevel p_192438_, StructureFeatureManager p_192439_, ChunkGenerator p_192440_, Random p_192441_, BoundingBox p_192442_, ChunkPos p_192443_, PiecesContainer p_192444_);
}