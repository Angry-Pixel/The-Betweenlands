package net.minecraft.world.level.levelgen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public abstract class ScatteredFeaturePiece extends StructurePiece {
   protected final int width;
   protected final int height;
   protected final int depth;
   protected int heightPosition = -1;

   protected ScatteredFeaturePiece(StructurePieceType p_209920_, int p_209921_, int p_209922_, int p_209923_, int p_209924_, int p_209925_, int p_209926_, Direction p_209927_) {
      super(p_209920_, 0, StructurePiece.makeBoundingBox(p_209921_, p_209922_, p_209923_, p_209927_, p_209924_, p_209925_, p_209926_));
      this.width = p_209924_;
      this.height = p_209925_;
      this.depth = p_209926_;
      this.setOrientation(p_209927_);
   }

   protected ScatteredFeaturePiece(StructurePieceType p_209929_, CompoundTag p_209930_) {
      super(p_209929_, p_209930_);
      this.width = p_209930_.getInt("Width");
      this.height = p_209930_.getInt("Height");
      this.depth = p_209930_.getInt("Depth");
      this.heightPosition = p_209930_.getInt("HPos");
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext p_192471_, CompoundTag p_192472_) {
      p_192472_.putInt("Width", this.width);
      p_192472_.putInt("Height", this.height);
      p_192472_.putInt("Depth", this.depth);
      p_192472_.putInt("HPos", this.heightPosition);
   }

   protected boolean updateAverageGroundHeight(LevelAccessor p_72804_, BoundingBox p_72805_, int p_72806_) {
      if (this.heightPosition >= 0) {
         return true;
      } else {
         int i = 0;
         int j = 0;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = this.boundingBox.minZ(); k <= this.boundingBox.maxZ(); ++k) {
            for(int l = this.boundingBox.minX(); l <= this.boundingBox.maxX(); ++l) {
               blockpos$mutableblockpos.set(l, 64, k);
               if (p_72805_.isInside(blockpos$mutableblockpos)) {
                  i += p_72804_.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).getY();
                  ++j;
               }
            }
         }

         if (j == 0) {
            return false;
         } else {
            this.heightPosition = i / j;
            this.boundingBox.move(0, this.heightPosition - this.boundingBox.minY() + p_72806_, 0);
            return true;
         }
      }
   }

   protected boolean updateHeightPositionToLowestGroundHeight(LevelAccessor p_192468_, int p_192469_) {
      if (this.heightPosition >= 0) {
         return true;
      } else {
         int i = p_192468_.getMaxBuildHeight();
         boolean flag = false;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int j = this.boundingBox.minZ(); j <= this.boundingBox.maxZ(); ++j) {
            for(int k = this.boundingBox.minX(); k <= this.boundingBox.maxX(); ++k) {
               blockpos$mutableblockpos.set(k, 0, j);
               i = Math.min(i, p_192468_.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).getY());
               flag = true;
            }
         }

         if (!flag) {
            return false;
         } else {
            this.heightPosition = i;
            this.boundingBox.move(0, this.heightPosition - this.boundingBox.minY() + p_192469_, 0);
            return true;
         }
      }
   }
}