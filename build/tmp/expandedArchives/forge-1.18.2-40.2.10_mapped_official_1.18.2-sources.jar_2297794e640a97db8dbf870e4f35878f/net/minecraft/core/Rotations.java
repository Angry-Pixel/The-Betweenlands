package net.minecraft.core;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;

public class Rotations {
   protected final float x;
   protected final float y;
   protected final float z;

   public Rotations(float p_123150_, float p_123151_, float p_123152_) {
      this.x = !Float.isInfinite(p_123150_) && !Float.isNaN(p_123150_) ? p_123150_ % 360.0F : 0.0F;
      this.y = !Float.isInfinite(p_123151_) && !Float.isNaN(p_123151_) ? p_123151_ % 360.0F : 0.0F;
      this.z = !Float.isInfinite(p_123152_) && !Float.isNaN(p_123152_) ? p_123152_ % 360.0F : 0.0F;
   }

   public Rotations(ListTag p_123154_) {
      this(p_123154_.getFloat(0), p_123154_.getFloat(1), p_123154_.getFloat(2));
   }

   public ListTag save() {
      ListTag listtag = new ListTag();
      listtag.add(FloatTag.valueOf(this.x));
      listtag.add(FloatTag.valueOf(this.y));
      listtag.add(FloatTag.valueOf(this.z));
      return listtag;
   }

   public boolean equals(Object p_123160_) {
      if (!(p_123160_ instanceof Rotations)) {
         return false;
      } else {
         Rotations rotations = (Rotations)p_123160_;
         return this.x == rotations.x && this.y == rotations.y && this.z == rotations.z;
      }
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   public float getWrappedX() {
      return Mth.wrapDegrees(this.x);
   }

   public float getWrappedY() {
      return Mth.wrapDegrees(this.y);
   }

   public float getWrappedZ() {
      return Mth.wrapDegrees(this.z);
   }
}