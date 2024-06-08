package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RangeConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class NetherFossilFeature extends StructureFeature<RangeConfiguration> {
   public NetherFossilFeature(Codec<RangeConfiguration> p_72031_) {
      super(p_72031_, NetherFossilFeature::pieceGeneratorSupplier);
   }

   private static Optional<PieceGenerator<RangeConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<RangeConfiguration> p_197218_) {
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
      worldgenrandom.setLargeFeatureSeed(p_197218_.seed(), p_197218_.chunkPos().x, p_197218_.chunkPos().z);
      int i = p_197218_.chunkPos().getMinBlockX() + worldgenrandom.nextInt(16);
      int j = p_197218_.chunkPos().getMinBlockZ() + worldgenrandom.nextInt(16);
      int k = p_197218_.chunkGenerator().getSeaLevel();
      WorldGenerationContext worldgenerationcontext = new WorldGenerationContext(p_197218_.chunkGenerator(), p_197218_.heightAccessor());
      int l = (p_197218_.config()).height.sample(worldgenrandom, worldgenerationcontext);
      NoiseColumn noisecolumn = p_197218_.chunkGenerator().getBaseColumn(i, j, p_197218_.heightAccessor());
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(i, l, j);

      while(l > k) {
         BlockState blockstate = noisecolumn.getBlock(l);
         --l;
         BlockState blockstate1 = noisecolumn.getBlock(l);
         if (blockstate.isAir() && (blockstate1.is(Blocks.SOUL_SAND) || blockstate1.isFaceSturdy(EmptyBlockGetter.INSTANCE, blockpos$mutableblockpos.setY(l), Direction.UP))) {
            break;
         }
      }

      if (l <= k) {
         return Optional.empty();
      } else if (!p_197218_.validBiome().test(p_197218_.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(l), QuartPos.fromBlock(j)))) {
         return Optional.empty();
      } else {
         BlockPos blockpos = new BlockPos(i, l, j);
         return Optional.of((p_197223_, p_197224_) -> {
            NetherFossilPieces.addPieces(p_197218_.structureManager(), p_197223_, worldgenrandom, blockpos);
         });
      }
   }
}