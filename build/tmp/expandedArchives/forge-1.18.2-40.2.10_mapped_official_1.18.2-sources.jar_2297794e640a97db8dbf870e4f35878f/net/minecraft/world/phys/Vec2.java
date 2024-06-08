package net.minecraft.world.phys;

import net.minecraft.util.Mth;

public class Vec2 {
   public static final Vec2 ZERO = new Vec2(0.0F, 0.0F);
   public static final Vec2 ONE = new Vec2(1.0F, 1.0F);
   public static final Vec2 UNIT_X = new Vec2(1.0F, 0.0F);
   public static final Vec2 NEG_UNIT_X = new Vec2(-1.0F, 0.0F);
   public static final Vec2 UNIT_Y = new Vec2(0.0F, 1.0F);
   public static final Vec2 NEG_UNIT_Y = new Vec2(0.0F, -1.0F);
   public static final Vec2 MAX = new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
   public static final Vec2 MIN = new Vec2(Float.MIN_VALUE, Float.MIN_VALUE);
   public final float x;
   public final float y;

   public Vec2(float p_82474_, float p_82475_) {
      this.x = p_82474_;
      this.y = p_82475_;
   }

   public Vec2 scale(float p_165904_) {
      return new Vec2(this.x * p_165904_, this.y * p_165904_);
   }

   public float dot(Vec2 p_165906_) {
      return this.x * p_165906_.x + this.y * p_165906_.y;
   }

   public Vec2 add(Vec2 p_165911_) {
      return new Vec2(this.x + p_165911_.x, this.y + p_165911_.y);
   }

   public Vec2 add(float p_165909_) {
      return new Vec2(this.x + p_165909_, this.y + p_165909_);
   }

   public boolean equals(Vec2 p_82477_) {
      return this.x == p_82477_.x && this.y == p_82477_.y;
   }

   public Vec2 normalized() {
      float f = Mth.sqrt(this.x * this.x + this.y * this.y);
      return f < 1.0E-4F ? ZERO : new Vec2(this.x / f, this.y / f);
   }

   public float length() {
      return Mth.sqrt(this.x * this.x + this.y * this.y);
   }

   public float lengthSquared() {
      return this.x * this.x + this.y * this.y;
   }

   public float distanceToSqr(Vec2 p_165915_) {
      float f = p_165915_.x - this.x;
      float f1 = p_165915_.y - this.y;
      return f * f + f1 * f1;
   }

   public Vec2 negated() {
      return new Vec2(-this.x, -this.y);
   }
}