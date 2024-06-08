package net.minecraft.core;

public class Cursor3D {
   public static final int TYPE_INSIDE = 0;
   public static final int TYPE_FACE = 1;
   public static final int TYPE_EDGE = 2;
   public static final int TYPE_CORNER = 3;
   private final int originX;
   private final int originY;
   private final int originZ;
   private final int width;
   private final int height;
   private final int depth;
   private final int end;
   private int index;
   private int x;
   private int y;
   private int z;

   public Cursor3D(int p_122298_, int p_122299_, int p_122300_, int p_122301_, int p_122302_, int p_122303_) {
      this.originX = p_122298_;
      this.originY = p_122299_;
      this.originZ = p_122300_;
      this.width = p_122301_ - p_122298_ + 1;
      this.height = p_122302_ - p_122299_ + 1;
      this.depth = p_122303_ - p_122300_ + 1;
      this.end = this.width * this.height * this.depth;
   }

   public boolean advance() {
      if (this.index == this.end) {
         return false;
      } else {
         this.x = this.index % this.width;
         int i = this.index / this.width;
         this.y = i % this.height;
         this.z = i / this.height;
         ++this.index;
         return true;
      }
   }

   public int nextX() {
      return this.originX + this.x;
   }

   public int nextY() {
      return this.originY + this.y;
   }

   public int nextZ() {
      return this.originZ + this.z;
   }

   public int getNextType() {
      int i = 0;
      if (this.x == 0 || this.x == this.width - 1) {
         ++i;
      }

      if (this.y == 0 || this.y == this.height - 1) {
         ++i;
      }

      if (this.z == 0 || this.z == this.depth - 1) {
         ++i;
      }

      return i;
   }
}