package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableInt;

public final class BlockLightEngine extends LayerLightEngine<BlockLightSectionStorage.BlockDataLayerStorageMap, BlockLightSectionStorage> {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

   public BlockLightEngine(LightChunkGetter p_75492_) {
      super(p_75492_, LightLayer.BLOCK, new BlockLightSectionStorage(p_75492_));
   }

   private int getLightEmission(long p_75509_) {
      int i = BlockPos.getX(p_75509_);
      int j = BlockPos.getY(p_75509_);
      int k = BlockPos.getZ(p_75509_);
      BlockGetter blockgetter = this.chunkSource.getChunkForLighting(SectionPos.blockToSectionCoord(i), SectionPos.blockToSectionCoord(k));
      return blockgetter != null ? blockgetter.getLightEmission(this.pos.set(i, j, k)) : 0;
   }

   protected int computeLevelFromNeighbor(long p_75505_, long p_75506_, int p_75507_) {
      if (p_75506_ == Long.MAX_VALUE) {
         return 15;
      } else if (p_75505_ == Long.MAX_VALUE) {
         return p_75507_ + 15 - this.getLightEmission(p_75506_);
      } else if (p_75507_ >= 15) {
         return p_75507_;
      } else {
         int i = Integer.signum(BlockPos.getX(p_75506_) - BlockPos.getX(p_75505_));
         int j = Integer.signum(BlockPos.getY(p_75506_) - BlockPos.getY(p_75505_));
         int k = Integer.signum(BlockPos.getZ(p_75506_) - BlockPos.getZ(p_75505_));
         Direction direction = Direction.fromNormal(i, j, k);
         if (direction == null) {
            return 15;
         } else {
            MutableInt mutableint = new MutableInt();
            BlockState blockstate = this.getStateAndOpacity(p_75506_, mutableint);
            if (mutableint.getValue() >= 15) {
               return 15;
            } else {
               BlockState blockstate1 = this.getStateAndOpacity(p_75505_, (MutableInt)null);
               VoxelShape voxelshape = this.getShape(blockstate1, p_75505_, direction);
               VoxelShape voxelshape1 = this.getShape(blockstate, p_75506_, direction.getOpposite());
               return Shapes.faceShapeOccludes(voxelshape, voxelshape1) ? 15 : p_75507_ + Math.max(1, mutableint.getValue());
            }
         }
      }
   }

   protected void checkNeighborsAfterUpdate(long p_75494_, int p_75495_, boolean p_75496_) {
      long i = SectionPos.blockToSection(p_75494_);

      for(Direction direction : DIRECTIONS) {
         long j = BlockPos.offset(p_75494_, direction);
         long k = SectionPos.blockToSection(j);
         if (i == k || this.storage.storingLightForSection(k)) {
            this.checkNeighbor(p_75494_, j, p_75495_, p_75496_);
         }
      }

   }

   protected int getComputedLevel(long p_75498_, long p_75499_, int p_75500_) {
      int i = p_75500_;
      if (Long.MAX_VALUE != p_75499_) {
         int j = this.computeLevelFromNeighbor(Long.MAX_VALUE, p_75498_, 0);
         if (p_75500_ > j) {
            i = j;
         }

         if (i == 0) {
            return i;
         }
      }

      long j1 = SectionPos.blockToSection(p_75498_);
      DataLayer datalayer = this.storage.getDataLayer(j1, true);

      for(Direction direction : DIRECTIONS) {
         long k = BlockPos.offset(p_75498_, direction);
         if (k != p_75499_) {
            long l = SectionPos.blockToSection(k);
            DataLayer datalayer1;
            if (j1 == l) {
               datalayer1 = datalayer;
            } else {
               datalayer1 = this.storage.getDataLayer(l, true);
            }

            if (datalayer1 != null) {
               int i1 = this.computeLevelFromNeighbor(k, p_75498_, this.getLevel(datalayer1, k));
               if (i > i1) {
                  i = i1;
               }

               if (i == 0) {
                  return i;
               }
            }
         }
      }

      return i;
   }

   public void onBlockEmissionIncrease(BlockPos p_75502_, int p_75503_) {
      this.storage.runAllUpdates();
      this.checkEdge(Long.MAX_VALUE, p_75502_.asLong(), 15 - p_75503_, true);
   }

   @Override
   public int queuedUpdateSize() {
      return storage.queuedUpdateSize();
   }
}
