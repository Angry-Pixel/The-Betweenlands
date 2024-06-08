package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

public class WoodlandMansionFeature extends StructureFeature<NoneFeatureConfiguration> {
   public WoodlandMansionFeature(Codec<NoneFeatureConfiguration> p_67427_) {
      super(p_67427_, WoodlandMansionFeature::pieceGeneratorSupplier, WoodlandMansionFeature::afterPlace);
   }

   private static Optional<PieceGenerator<NoneFeatureConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> p_197187_) {
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
      worldgenrandom.setLargeFeatureSeed(p_197187_.seed(), p_197187_.chunkPos().x, p_197187_.chunkPos().z);
      Rotation rotation = Rotation.getRandom(worldgenrandom);
      int i = 5;
      int j = 5;
      if (rotation == Rotation.CLOCKWISE_90) {
         i = -5;
      } else if (rotation == Rotation.CLOCKWISE_180) {
         i = -5;
         j = -5;
      } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
         j = -5;
      }

      int k = p_197187_.chunkPos().getBlockX(7);
      int l = p_197187_.chunkPos().getBlockZ(7);
      int[] aint = p_197187_.getCornerHeights(k, i, l, j);
      int i1 = Math.min(Math.min(aint[0], aint[1]), Math.min(aint[2], aint[3]));
      if (i1 < 60) {
         return Optional.empty();
      } else if (!p_197187_.validBiome().test(p_197187_.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(k), QuartPos.fromBlock(aint[0]), QuartPos.fromBlock(l)))) {
         return Optional.empty();
      } else {
         BlockPos blockpos = new BlockPos(p_197187_.chunkPos().getMiddleBlockX(), i1 + 1, p_197187_.chunkPos().getMiddleBlockZ());
         return Optional.of((p_197192_, p_197193_) -> {
            List<WoodlandMansionPieces.WoodlandMansionPiece> list = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(p_197193_.structureManager(), blockpos, rotation, list, worldgenrandom);
            list.forEach(p_197192_::addPiece);
         });
      }
   }

   private static void afterPlace(WorldGenLevel p_191195_, StructureFeatureManager p_191196_, ChunkGenerator p_191197_, Random p_191198_, BoundingBox p_191199_, ChunkPos p_191200_, PiecesContainer p_191201_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = p_191195_.getMinBuildHeight();
      BoundingBox boundingbox = p_191201_.calculateBoundingBox();
      int j = boundingbox.minY();

      for(int k = p_191199_.minX(); k <= p_191199_.maxX(); ++k) {
         for(int l = p_191199_.minZ(); l <= p_191199_.maxZ(); ++l) {
            blockpos$mutableblockpos.set(k, j, l);
            if (!p_191195_.isEmptyBlock(blockpos$mutableblockpos) && boundingbox.isInside(blockpos$mutableblockpos) && p_191201_.isInsidePiece(blockpos$mutableblockpos)) {
               for(int i1 = j - 1; i1 > i; --i1) {
                  blockpos$mutableblockpos.setY(i1);
                  if (!p_191195_.isEmptyBlock(blockpos$mutableblockpos) && !p_191195_.getBlockState(blockpos$mutableblockpos).getMaterial().isLiquid()) {
                     break;
                  }

                  p_191195_.setBlock(blockpos$mutableblockpos, Blocks.COBBLESTONE.defaultBlockState(), 2);
               }
            }
         }
      }

   }
}