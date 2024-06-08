package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableInt;

public class SkyLightEngine extends LayerLightEngine<SkyLightSectionStorage.SkyDataLayerStorageMap, SkyLightSectionStorage> {
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

   public SkyLightEngine(LightChunkGetter p_75843_) {
      super(p_75843_, LightLayer.SKY, new SkyLightSectionStorage(p_75843_));
   }

   public int computeLevelFromNeighbor(long p_75855_, long p_75856_, int p_75857_) {
      if (p_75856_ != Long.MAX_VALUE && p_75855_ != Long.MAX_VALUE) {
         if (p_75857_ >= 15) {
            return p_75857_;
         } else {
            MutableInt mutableint = new MutableInt();
            BlockState blockstate = this.getStateAndOpacity(p_75856_, mutableint);
            if (mutableint.getValue() >= 15) {
               return 15;
            } else {
               int i = BlockPos.getX(p_75855_);
               int j = BlockPos.getY(p_75855_);
               int k = BlockPos.getZ(p_75855_);
               int l = BlockPos.getX(p_75856_);
               int i1 = BlockPos.getY(p_75856_);
               int j1 = BlockPos.getZ(p_75856_);
               int k1 = Integer.signum(l - i);
               int l1 = Integer.signum(i1 - j);
               int i2 = Integer.signum(j1 - k);
               Direction direction = Direction.fromNormal(k1, l1, i2);
               if (direction == null) {
                  throw new IllegalStateException(String.format("Light was spread in illegal direction %d, %d, %d", k1, l1, i2));
               } else {
                  BlockState blockstate1 = this.getStateAndOpacity(p_75855_, (MutableInt)null);
                  VoxelShape voxelshape = this.getShape(blockstate1, p_75855_, direction);
                  VoxelShape voxelshape1 = this.getShape(blockstate, p_75856_, direction.getOpposite());
                  if (Shapes.faceShapeOccludes(voxelshape, voxelshape1)) {
                     return 15;
                  } else {
                     boolean flag = i == l && k == j1;
                     boolean flag1 = flag && j > i1;
                     return flag1 && p_75857_ == 0 && mutableint.getValue() == 0 ? 0 : p_75857_ + Math.max(1, mutableint.getValue());
                  }
               }
            }
         }
      } else {
         return 15;
      }
   }

   protected void checkNeighborsAfterUpdate(long p_75845_, int p_75846_, boolean p_75847_) {
      long i = SectionPos.blockToSection(p_75845_);
      int j = BlockPos.getY(p_75845_);
      int k = SectionPos.sectionRelative(j);
      int l = SectionPos.blockToSectionCoord(j);
      int i1;
      if (k != 0) {
         i1 = 0;
      } else {
         int j1;
         for(j1 = 0; !this.storage.storingLightForSection(SectionPos.offset(i, 0, -j1 - 1, 0)) && this.storage.hasSectionsBelow(l - j1 - 1); ++j1) {
         }

         i1 = j1;
      }

      long j3 = BlockPos.offset(p_75845_, 0, -1 - i1 * 16, 0);
      long k1 = SectionPos.blockToSection(j3);
      if (i == k1 || this.storage.storingLightForSection(k1)) {
         this.checkNeighbor(p_75845_, j3, p_75846_, p_75847_);
      }

      long l1 = BlockPos.offset(p_75845_, Direction.UP);
      long i2 = SectionPos.blockToSection(l1);
      if (i == i2 || this.storage.storingLightForSection(i2)) {
         this.checkNeighbor(p_75845_, l1, p_75846_, p_75847_);
      }

      for(Direction direction : HORIZONTALS) {
         int j2 = 0;

         while(true) {
            long k2 = BlockPos.offset(p_75845_, direction.getStepX(), -j2, direction.getStepZ());
            long l2 = SectionPos.blockToSection(k2);
            if (i == l2) {
               this.checkNeighbor(p_75845_, k2, p_75846_, p_75847_);
               break;
            }

            if (this.storage.storingLightForSection(l2)) {
               long i3 = BlockPos.offset(p_75845_, 0, -j2, 0);
               this.checkNeighbor(i3, k2, p_75846_, p_75847_);
            }

            ++j2;
            if (j2 > i1 * 16) {
               break;
            }
         }
      }

   }

   protected int getComputedLevel(long p_75849_, long p_75850_, int p_75851_) {
      int i = p_75851_;
      long j = SectionPos.blockToSection(p_75849_);
      DataLayer datalayer = this.storage.getDataLayer(j, true);

      for(Direction direction : DIRECTIONS) {
         long k = BlockPos.offset(p_75849_, direction);
         if (k != p_75850_) {
            long l = SectionPos.blockToSection(k);
            DataLayer datalayer1;
            if (j == l) {
               datalayer1 = datalayer;
            } else {
               datalayer1 = this.storage.getDataLayer(l, true);
            }

            int i1;
            if (datalayer1 != null) {
               i1 = this.getLevel(datalayer1, k);
            } else {
               if (direction == Direction.DOWN) {
                  continue;
               }

               i1 = 15 - this.storage.getLightValue(k, true);
            }

            int j1 = this.computeLevelFromNeighbor(k, p_75849_, i1);
            if (i > j1) {
               i = j1;
            }

            if (i == 0) {
               return i;
            }
         }
      }

      return i;
   }

   protected void checkNode(long p_75859_) {
      this.storage.runAllUpdates();
      long i = SectionPos.blockToSection(p_75859_);
      if (this.storage.storingLightForSection(i)) {
         super.checkNode(p_75859_);
      } else {
         for(p_75859_ = BlockPos.getFlatIndex(p_75859_); !this.storage.storingLightForSection(i) && !this.storage.isAboveData(i); p_75859_ = BlockPos.offset(p_75859_, 0, 16, 0)) {
            i = SectionPos.offset(i, Direction.UP);
         }

         if (this.storage.storingLightForSection(i)) {
            super.checkNode(p_75859_);
         }
      }

   }

   public String getDebugData(long p_75853_) {
      return super.getDebugData(p_75853_) + (this.storage.isAboveData(p_75853_) ? "*" : "");
   }

   @Override
   public int queuedUpdateSize() {
      return 0;
   }
}
