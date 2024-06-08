package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public class VegetationPatchFeature extends Feature<VegetationPatchConfiguration> {
   public VegetationPatchFeature(Codec<VegetationPatchConfiguration> p_160588_) {
      super(p_160588_);
   }

   public boolean place(FeaturePlaceContext<VegetationPatchConfiguration> p_160612_) {
      WorldGenLevel worldgenlevel = p_160612_.level();
      VegetationPatchConfiguration vegetationpatchconfiguration = p_160612_.config();
      Random random = p_160612_.random();
      BlockPos blockpos = p_160612_.origin();
      Predicate<BlockState> predicate = (p_204782_) -> {
         return p_204782_.is(vegetationpatchconfiguration.replaceable);
      };
      int i = vegetationpatchconfiguration.xzRadius.sample(random) + 1;
      int j = vegetationpatchconfiguration.xzRadius.sample(random) + 1;
      Set<BlockPos> set = this.placeGroundPatch(worldgenlevel, vegetationpatchconfiguration, random, blockpos, predicate, i, j);
      this.distributeVegetation(p_160612_, worldgenlevel, vegetationpatchconfiguration, random, set, i, j);
      return !set.isEmpty();
   }

   protected Set<BlockPos> placeGroundPatch(WorldGenLevel p_160597_, VegetationPatchConfiguration p_160598_, Random p_160599_, BlockPos p_160600_, Predicate<BlockState> p_160601_, int p_160602_, int p_160603_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = p_160600_.mutable();
      BlockPos.MutableBlockPos blockpos$mutableblockpos1 = blockpos$mutableblockpos.mutable();
      Direction direction = p_160598_.surface.getDirection();
      Direction direction1 = direction.getOpposite();
      Set<BlockPos> set = new HashSet<>();

      for(int i = -p_160602_; i <= p_160602_; ++i) {
         boolean flag = i == -p_160602_ || i == p_160602_;

         for(int j = -p_160603_; j <= p_160603_; ++j) {
            boolean flag1 = j == -p_160603_ || j == p_160603_;
            boolean flag2 = flag || flag1;
            boolean flag3 = flag && flag1;
            boolean flag4 = flag2 && !flag3;
            if (!flag3 && (!flag4 || p_160598_.extraEdgeColumnChance != 0.0F && !(p_160599_.nextFloat() > p_160598_.extraEdgeColumnChance))) {
               blockpos$mutableblockpos.setWithOffset(p_160600_, i, 0, j);

               for(int k = 0; p_160597_.isStateAtPosition(blockpos$mutableblockpos, BlockBehaviour.BlockStateBase::isAir) && k < p_160598_.verticalRange; ++k) {
                  blockpos$mutableblockpos.move(direction);
               }

               for(int i1 = 0; p_160597_.isStateAtPosition(blockpos$mutableblockpos, (p_204784_) -> {
                  return !p_204784_.isAir();
               }) && i1 < p_160598_.verticalRange; ++i1) {
                  blockpos$mutableblockpos.move(direction1);
               }

               blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, p_160598_.surface.getDirection());
               BlockState blockstate = p_160597_.getBlockState(blockpos$mutableblockpos1);
               if (p_160597_.isEmptyBlock(blockpos$mutableblockpos) && blockstate.isFaceSturdy(p_160597_, blockpos$mutableblockpos1, p_160598_.surface.getDirection().getOpposite())) {
                  int l = p_160598_.depth.sample(p_160599_) + (p_160598_.extraBottomBlockChance > 0.0F && p_160599_.nextFloat() < p_160598_.extraBottomBlockChance ? 1 : 0);
                  BlockPos blockpos = blockpos$mutableblockpos1.immutable();
                  boolean flag5 = this.placeGround(p_160597_, p_160598_, p_160601_, p_160599_, blockpos$mutableblockpos1, l);
                  if (flag5) {
                     set.add(blockpos);
                  }
               }
            }
         }
      }

      return set;
   }

   protected void distributeVegetation(FeaturePlaceContext<VegetationPatchConfiguration> p_160614_, WorldGenLevel p_160615_, VegetationPatchConfiguration p_160616_, Random p_160617_, Set<BlockPos> p_160618_, int p_160619_, int p_160620_) {
      for(BlockPos blockpos : p_160618_) {
         if (p_160616_.vegetationChance > 0.0F && p_160617_.nextFloat() < p_160616_.vegetationChance) {
            this.placeVegetation(p_160615_, p_160616_, p_160614_.chunkGenerator(), p_160617_, blockpos);
         }
      }

   }

   protected boolean placeVegetation(WorldGenLevel p_160592_, VegetationPatchConfiguration p_160593_, ChunkGenerator p_160594_, Random p_160595_, BlockPos p_160596_) {
      return p_160593_.vegetationFeature.value().place(p_160592_, p_160594_, p_160595_, p_160596_.relative(p_160593_.surface.getDirection().getOpposite()));
   }

   protected boolean placeGround(WorldGenLevel p_160605_, VegetationPatchConfiguration p_160606_, Predicate<BlockState> p_160607_, Random p_160608_, BlockPos.MutableBlockPos p_160609_, int p_160610_) {
      for(int i = 0; i < p_160610_; ++i) {
         BlockState blockstate = p_160606_.groundState.getState(p_160608_, p_160609_);
         BlockState blockstate1 = p_160605_.getBlockState(p_160609_);
         if (!blockstate.is(blockstate1.getBlock())) {
            if (!p_160607_.test(blockstate1)) {
               return i != 0;
            }

            p_160605_.setBlock(p_160609_, blockstate, 2);
            p_160609_.move(p_160606_.surface.getDirection());
         }
      }

      return true;
   }
}