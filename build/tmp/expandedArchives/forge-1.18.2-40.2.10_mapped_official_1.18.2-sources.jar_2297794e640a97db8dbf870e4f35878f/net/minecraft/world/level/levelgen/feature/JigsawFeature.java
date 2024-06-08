package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;

public class JigsawFeature extends StructureFeature<JigsawConfiguration> {
   public JigsawFeature(Codec<JigsawConfiguration> p_197092_, int p_197093_, boolean p_197094_, boolean p_197095_, Predicate<PieceGeneratorSupplier.Context<JigsawConfiguration>> p_197096_) {
      super(p_197092_, (p_197102_) -> {
         if (!p_197096_.test(p_197102_)) {
            return Optional.empty();
         } else {
            BlockPos blockpos = new BlockPos(p_197102_.chunkPos().getMinBlockX(), p_197093_, p_197102_.chunkPos().getMinBlockZ());
            Pools.bootstrap();
            return JigsawPlacement.addPieces(p_197102_, PoolElementStructurePiece::new, blockpos, p_197094_, p_197095_);
         }
      });
   }
}