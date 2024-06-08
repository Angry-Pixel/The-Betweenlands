package net.minecraft.world.level.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class DoubleBlockCombiner {
   public static <S extends BlockEntity> DoubleBlockCombiner.NeighborCombineResult<S> combineWithNeigbour(BlockEntityType<S> p_52823_, Function<BlockState, DoubleBlockCombiner.BlockType> p_52824_, Function<BlockState, Direction> p_52825_, DirectionProperty p_52826_, BlockState p_52827_, LevelAccessor p_52828_, BlockPos p_52829_, BiPredicate<LevelAccessor, BlockPos> p_52830_) {
      S s = p_52823_.getBlockEntity(p_52828_, p_52829_);
      if (s == null) {
         return DoubleBlockCombiner.Combiner::acceptNone;
      } else if (p_52830_.test(p_52828_, p_52829_)) {
         return DoubleBlockCombiner.Combiner::acceptNone;
      } else {
         DoubleBlockCombiner.BlockType doubleblockcombiner$blocktype = p_52824_.apply(p_52827_);
         boolean flag = doubleblockcombiner$blocktype == DoubleBlockCombiner.BlockType.SINGLE;
         boolean flag1 = doubleblockcombiner$blocktype == DoubleBlockCombiner.BlockType.FIRST;
         if (flag) {
            return new DoubleBlockCombiner.NeighborCombineResult.Single<>(s);
         } else {
            BlockPos blockpos = p_52829_.relative(p_52825_.apply(p_52827_));
            BlockState blockstate = p_52828_.getBlockState(blockpos);
            if (blockstate.is(p_52827_.getBlock())) {
               DoubleBlockCombiner.BlockType doubleblockcombiner$blocktype1 = p_52824_.apply(blockstate);
               if (doubleblockcombiner$blocktype1 != DoubleBlockCombiner.BlockType.SINGLE && doubleblockcombiner$blocktype != doubleblockcombiner$blocktype1 && blockstate.getValue(p_52826_) == p_52827_.getValue(p_52826_)) {
                  if (p_52830_.test(p_52828_, blockpos)) {
                     return DoubleBlockCombiner.Combiner::acceptNone;
                  }

                  S s1 = p_52823_.getBlockEntity(p_52828_, blockpos);
                  if (s1 != null) {
                     S s2 = flag1 ? s : s1;
                     S s3 = flag1 ? s1 : s;
                     return new DoubleBlockCombiner.NeighborCombineResult.Double<>(s2, s3);
                  }
               }
            }

            return new DoubleBlockCombiner.NeighborCombineResult.Single<>(s);
         }
      }
   }

   public static enum BlockType {
      SINGLE,
      FIRST,
      SECOND;
   }

   public interface Combiner<S, T> {
      T acceptDouble(S p_52843_, S p_52844_);

      T acceptSingle(S p_52842_);

      T acceptNone();
   }

   public interface NeighborCombineResult<S> {
      <T> T apply(DoubleBlockCombiner.Combiner<? super S, T> p_52845_);

      public static final class Double<S> implements DoubleBlockCombiner.NeighborCombineResult<S> {
         private final S first;
         private final S second;

         public Double(S p_52849_, S p_52850_) {
            this.first = p_52849_;
            this.second = p_52850_;
         }

         public <T> T apply(DoubleBlockCombiner.Combiner<? super S, T> p_52852_) {
            return p_52852_.acceptDouble(this.first, this.second);
         }
      }

      public static final class Single<S> implements DoubleBlockCombiner.NeighborCombineResult<S> {
         private final S single;

         public Single(S p_52855_) {
            this.single = p_52855_;
         }

         public <T> T apply(DoubleBlockCombiner.Combiner<? super S, T> p_52857_) {
            return p_52857_.acceptSingle(this.single);
         }
      }
   }
}