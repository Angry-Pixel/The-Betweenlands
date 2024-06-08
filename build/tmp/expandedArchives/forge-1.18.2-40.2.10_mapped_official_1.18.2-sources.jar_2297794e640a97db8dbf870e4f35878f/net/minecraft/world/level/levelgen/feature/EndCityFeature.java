package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class EndCityFeature extends StructureFeature<NoneFeatureConfiguration> {
   private static final int RANDOM_SALT = 10387313;

   public EndCityFeature(Codec<NoneFeatureConfiguration> p_65627_) {
      super(p_65627_, EndCityFeature::pieceGeneratorSupplier);
   }

   private static int getYPositionForFeature(ChunkPos p_159670_, ChunkGenerator p_159671_, LevelHeightAccessor p_159672_) {
      Random random = new Random((long)(p_159670_.x + p_159670_.z * 10387313));
      Rotation rotation = Rotation.getRandom(random);
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

      int k = p_159670_.getBlockX(7);
      int l = p_159670_.getBlockZ(7);
      int i1 = p_159671_.getFirstOccupiedHeight(k, l, Heightmap.Types.WORLD_SURFACE_WG, p_159672_);
      int j1 = p_159671_.getFirstOccupiedHeight(k, l + j, Heightmap.Types.WORLD_SURFACE_WG, p_159672_);
      int k1 = p_159671_.getFirstOccupiedHeight(k + i, l, Heightmap.Types.WORLD_SURFACE_WG, p_159672_);
      int l1 = p_159671_.getFirstOccupiedHeight(k + i, l + j, Heightmap.Types.WORLD_SURFACE_WG, p_159672_);
      return Math.min(Math.min(i1, j1), Math.min(k1, l1));
   }

   private static Optional<PieceGenerator<NoneFeatureConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> p_197083_) {
      int i = getYPositionForFeature(p_197083_.chunkPos(), p_197083_.chunkGenerator(), p_197083_.heightAccessor());
      if (i < 60) {
         return Optional.empty();
      } else {
         BlockPos blockpos = p_197083_.chunkPos().getMiddleBlockPosition(i);
         return !p_197083_.validBiome().test(p_197083_.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(blockpos.getX()), QuartPos.fromBlock(blockpos.getY()), QuartPos.fromBlock(blockpos.getZ()))) ? Optional.empty() : Optional.of((p_197086_, p_197087_) -> {
            Rotation rotation = Rotation.getRandom(p_197087_.random());
            List<StructurePiece> list = Lists.newArrayList();
            EndCityPieces.startHouseTower(p_197087_.structureManager(), blockpos, rotation, list, p_197087_.random());
            list.forEach(p_197086_::addPiece);
         });
      }
   }
}