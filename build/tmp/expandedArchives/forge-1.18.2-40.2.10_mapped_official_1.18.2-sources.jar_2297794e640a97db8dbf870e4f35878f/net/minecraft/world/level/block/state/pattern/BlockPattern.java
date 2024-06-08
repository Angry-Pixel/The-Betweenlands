package net.minecraft.world.level.block.state.pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;

public class BlockPattern {
   private final Predicate<BlockInWorld>[][][] pattern;
   private final int depth;
   private final int height;
   private final int width;

   public BlockPattern(Predicate<BlockInWorld>[][][] p_61182_) {
      this.pattern = p_61182_;
      this.depth = p_61182_.length;
      if (this.depth > 0) {
         this.height = p_61182_[0].length;
         if (this.height > 0) {
            this.width = p_61182_[0][0].length;
         } else {
            this.width = 0;
         }
      } else {
         this.height = 0;
         this.width = 0;
      }

   }

   public int getDepth() {
      return this.depth;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   @VisibleForTesting
   public Predicate<BlockInWorld>[][][] getPattern() {
      return this.pattern;
   }

   @Nullable
   @VisibleForTesting
   public BlockPattern.BlockPatternMatch matches(LevelReader p_155965_, BlockPos p_155966_, Direction p_155967_, Direction p_155968_) {
      LoadingCache<BlockPos, BlockInWorld> loadingcache = createLevelCache(p_155965_, false);
      return this.matches(p_155966_, p_155967_, p_155968_, loadingcache);
   }

   @Nullable
   private BlockPattern.BlockPatternMatch matches(BlockPos p_61198_, Direction p_61199_, Direction p_61200_, LoadingCache<BlockPos, BlockInWorld> p_61201_) {
      for(int i = 0; i < this.width; ++i) {
         for(int j = 0; j < this.height; ++j) {
            for(int k = 0; k < this.depth; ++k) {
               if (!this.pattern[k][j][i].test(p_61201_.getUnchecked(translateAndRotate(p_61198_, p_61199_, p_61200_, i, j, k)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.BlockPatternMatch(p_61198_, p_61199_, p_61200_, p_61201_, this.width, this.height, this.depth);
   }

   @Nullable
   public BlockPattern.BlockPatternMatch find(LevelReader p_61185_, BlockPos p_61186_) {
      LoadingCache<BlockPos, BlockInWorld> loadingcache = createLevelCache(p_61185_, false);
      int i = Math.max(Math.max(this.width, this.height), this.depth);

      for(BlockPos blockpos : BlockPos.betweenClosed(p_61186_, p_61186_.offset(i - 1, i - 1, i - 1))) {
         for(Direction direction : Direction.values()) {
            for(Direction direction1 : Direction.values()) {
               if (direction1 != direction && direction1 != direction.getOpposite()) {
                  BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch = this.matches(blockpos, direction, direction1, loadingcache);
                  if (blockpattern$blockpatternmatch != null) {
                     return blockpattern$blockpatternmatch;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPos, BlockInWorld> createLevelCache(LevelReader p_61188_, boolean p_61189_) {
      return CacheBuilder.newBuilder().build(new BlockPattern.BlockCacheLoader(p_61188_, p_61189_));
   }

   protected static BlockPos translateAndRotate(BlockPos p_61191_, Direction p_61192_, Direction p_61193_, int p_61194_, int p_61195_, int p_61196_) {
      if (p_61192_ != p_61193_ && p_61192_ != p_61193_.getOpposite()) {
         Vec3i vec3i = new Vec3i(p_61192_.getStepX(), p_61192_.getStepY(), p_61192_.getStepZ());
         Vec3i vec3i1 = new Vec3i(p_61193_.getStepX(), p_61193_.getStepY(), p_61193_.getStepZ());
         Vec3i vec3i2 = vec3i.cross(vec3i1);
         return p_61191_.offset(vec3i1.getX() * -p_61195_ + vec3i2.getX() * p_61194_ + vec3i.getX() * p_61196_, vec3i1.getY() * -p_61195_ + vec3i2.getY() * p_61194_ + vec3i.getY() * p_61196_, vec3i1.getZ() * -p_61195_ + vec3i2.getZ() * p_61194_ + vec3i.getZ() * p_61196_);
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   static class BlockCacheLoader extends CacheLoader<BlockPos, BlockInWorld> {
      private final LevelReader level;
      private final boolean loadChunks;

      public BlockCacheLoader(LevelReader p_61207_, boolean p_61208_) {
         this.level = p_61207_;
         this.loadChunks = p_61208_;
      }

      public BlockInWorld load(BlockPos p_61210_) {
         return new BlockInWorld(this.level, p_61210_, this.loadChunks);
      }
   }

   public static class BlockPatternMatch {
      private final BlockPos frontTopLeft;
      private final Direction forwards;
      private final Direction up;
      private final LoadingCache<BlockPos, BlockInWorld> cache;
      private final int width;
      private final int height;
      private final int depth;

      public BlockPatternMatch(BlockPos p_61221_, Direction p_61222_, Direction p_61223_, LoadingCache<BlockPos, BlockInWorld> p_61224_, int p_61225_, int p_61226_, int p_61227_) {
         this.frontTopLeft = p_61221_;
         this.forwards = p_61222_;
         this.up = p_61223_;
         this.cache = p_61224_;
         this.width = p_61225_;
         this.height = p_61226_;
         this.depth = p_61227_;
      }

      public BlockPos getFrontTopLeft() {
         return this.frontTopLeft;
      }

      public Direction getForwards() {
         return this.forwards;
      }

      public Direction getUp() {
         return this.up;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public int getDepth() {
         return this.depth;
      }

      public BlockInWorld getBlock(int p_61230_, int p_61231_, int p_61232_) {
         return this.cache.getUnchecked(BlockPattern.translateAndRotate(this.frontTopLeft, this.getForwards(), this.getUp(), p_61230_, p_61231_, p_61232_));
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
      }
   }
}