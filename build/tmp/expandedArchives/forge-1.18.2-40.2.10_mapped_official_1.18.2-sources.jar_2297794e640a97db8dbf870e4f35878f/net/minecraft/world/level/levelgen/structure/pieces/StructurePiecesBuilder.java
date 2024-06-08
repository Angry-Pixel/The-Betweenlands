package net.minecraft.world.level.levelgen.structure.pieces;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;

public class StructurePiecesBuilder implements StructurePieceAccessor {
   private final List<StructurePiece> pieces = Lists.newArrayList();

   public void addPiece(StructurePiece p_192791_) {
      this.pieces.add(p_192791_);
   }

   @Nullable
   public StructurePiece findCollisionPiece(BoundingBox p_192789_) {
      return StructurePiece.findCollisionPiece(this.pieces, p_192789_);
   }

   /** @deprecated */
   @Deprecated
   public void offsetPiecesVertically(int p_192782_) {
      for(StructurePiece structurepiece : this.pieces) {
         structurepiece.move(0, p_192782_, 0);
      }

   }

   /** @deprecated */
   @Deprecated
   public void moveBelowSeaLevel(int p_192784_, int p_192785_, Random p_192786_, int p_192787_) {
      int i = p_192784_ - p_192787_;
      BoundingBox boundingbox = this.getBoundingBox();
      int j = boundingbox.getYSpan() + p_192785_ + 1;
      if (j < i) {
         j += p_192786_.nextInt(i - j);
      }

      int k = j - boundingbox.maxY();
      this.offsetPiecesVertically(k);
   }

   /** @deprecated */
   public void moveInsideHeights(Random p_192793_, int p_192794_, int p_192795_) {
      BoundingBox boundingbox = this.getBoundingBox();
      int i = p_192795_ - p_192794_ + 1 - boundingbox.getYSpan();
      int j;
      if (i > 1) {
         j = p_192794_ + p_192793_.nextInt(i);
      } else {
         j = p_192794_;
      }

      int k = j - boundingbox.minY();
      this.offsetPiecesVertically(k);
   }

   public PiecesContainer build() {
      return new PiecesContainer(this.pieces);
   }

   public void clear() {
      this.pieces.clear();
   }

   public boolean isEmpty() {
      return this.pieces.isEmpty();
   }

   public BoundingBox getBoundingBox() {
      return StructurePiece.createBoundingBox(this.pieces.stream());
   }
}