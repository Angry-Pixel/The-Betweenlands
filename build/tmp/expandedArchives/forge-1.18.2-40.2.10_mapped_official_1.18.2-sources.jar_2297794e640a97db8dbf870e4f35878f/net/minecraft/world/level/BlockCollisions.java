package net.minecraft.world.level;

import com.google.common.collect.AbstractIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCollisions extends AbstractIterator<VoxelShape> {
   private final AABB box;
   private final CollisionContext context;
   private final Cursor3D cursor;
   private final BlockPos.MutableBlockPos pos;
   private final VoxelShape entityShape;
   private final CollisionGetter collisionGetter;
   private final boolean onlySuffocatingBlocks;
   @Nullable
   private BlockGetter cachedBlockGetter;
   private long cachedBlockGetterPos;

   public BlockCollisions(CollisionGetter p_186402_, @Nullable Entity p_186403_, AABB p_186404_) {
      this(p_186402_, p_186403_, p_186404_, false);
   }

   public BlockCollisions(CollisionGetter p_186406_, @Nullable Entity p_186407_, AABB p_186408_, boolean p_186409_) {
      this.context = p_186407_ == null ? CollisionContext.empty() : CollisionContext.of(p_186407_);
      this.pos = new BlockPos.MutableBlockPos();
      this.entityShape = Shapes.create(p_186408_);
      this.collisionGetter = p_186406_;
      this.box = p_186408_;
      this.onlySuffocatingBlocks = p_186409_;
      int i = Mth.floor(p_186408_.minX - 1.0E-7D) - 1;
      int j = Mth.floor(p_186408_.maxX + 1.0E-7D) + 1;
      int k = Mth.floor(p_186408_.minY - 1.0E-7D) - 1;
      int l = Mth.floor(p_186408_.maxY + 1.0E-7D) + 1;
      int i1 = Mth.floor(p_186408_.minZ - 1.0E-7D) - 1;
      int j1 = Mth.floor(p_186408_.maxZ + 1.0E-7D) + 1;
      this.cursor = new Cursor3D(i, k, i1, j, l, j1);
   }

   @Nullable
   private BlockGetter getChunk(int p_186412_, int p_186413_) {
      int i = SectionPos.blockToSectionCoord(p_186412_);
      int j = SectionPos.blockToSectionCoord(p_186413_);
      long k = ChunkPos.asLong(i, j);
      if (this.cachedBlockGetter != null && this.cachedBlockGetterPos == k) {
         return this.cachedBlockGetter;
      } else {
         BlockGetter blockgetter = this.collisionGetter.getChunkForCollisions(i, j);
         this.cachedBlockGetter = blockgetter;
         this.cachedBlockGetterPos = k;
         return blockgetter;
      }
   }

   protected VoxelShape computeNext() {
      while(true) {
         if (this.cursor.advance()) {
            int i = this.cursor.nextX();
            int j = this.cursor.nextY();
            int k = this.cursor.nextZ();
            int l = this.cursor.getNextType();
            if (l == 3) {
               continue;
            }

            BlockGetter blockgetter = this.getChunk(i, k);
            if (blockgetter == null) {
               continue;
            }

            this.pos.set(i, j, k);
            BlockState blockstate = blockgetter.getBlockState(this.pos);
            if (this.onlySuffocatingBlocks && !blockstate.isSuffocating(blockgetter, this.pos) || l == 1 && !blockstate.hasLargeCollisionShape() || l == 2 && !blockstate.is(Blocks.MOVING_PISTON)) {
               continue;
            }

            VoxelShape voxelshape = blockstate.getCollisionShape(this.collisionGetter, this.pos, this.context);
            if (voxelshape == Shapes.block()) {
               if (!this.box.intersects((double)i, (double)j, (double)k, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D)) {
                  continue;
               }

               return voxelshape.move((double)i, (double)j, (double)k);
            }

            VoxelShape voxelshape1 = voxelshape.move((double)i, (double)j, (double)k);
            if (!Shapes.joinIsNotEmpty(voxelshape1, this.entityShape, BooleanOp.AND)) {
               continue;
            }

            return voxelshape1;
         }

         return this.endOfData();
      }
   }
}