package com.mojang.math;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class Vector3f {
   public static final Codec<Vector3f> CODEC = Codec.FLOAT.listOf().comapFlatMap((p_176767_) -> {
      return Util.fixedSize(p_176767_, 3).map((p_176774_) -> {
         return new Vector3f(p_176774_.get(0), p_176774_.get(1), p_176774_.get(2));
      });
   }, (p_176776_) -> {
      return ImmutableList.of(p_176776_.x, p_176776_.y, p_176776_.z);
   });
   public static Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
   public static Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
   public static Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
   public static Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
   public static Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);
   public static Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);
   public static Vector3f ZERO = new Vector3f(0.0F, 0.0F, 0.0F);
   private float x;
   private float y;
   private float z;

   public Vector3f() {
   }

   public Vector3f(float p_122234_, float p_122235_, float p_122236_) {
      this.x = p_122234_;
      this.y = p_122235_;
      this.z = p_122236_;
   }

   public Vector3f(Vector4f p_176765_) {
      this(p_176765_.x(), p_176765_.y(), p_176765_.z());
   }

   public Vector3f(Vec3 p_122238_) {
      this((float)p_122238_.x, (float)p_122238_.y, (float)p_122238_.z);
   }

   public boolean equals(Object p_122283_) {
      if (this == p_122283_) {
         return true;
      } else if (p_122283_ != null && this.getClass() == p_122283_.getClass()) {
         Vector3f vector3f = (Vector3f)p_122283_;
         if (Float.compare(vector3f.x, this.x) != 0) {
            return false;
         } else if (Float.compare(vector3f.y, this.y) != 0) {
            return false;
         } else {
            return Float.compare(vector3f.z, this.z) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.x);
      i = 31 * i + Float.floatToIntBits(this.y);
      return 31 * i + Float.floatToIntBits(this.z);
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public float z() {
      return this.z;
   }

   public void mul(float p_122262_) {
      this.x *= p_122262_;
      this.y *= p_122262_;
      this.z *= p_122262_;
   }

   public void mul(float p_122264_, float p_122265_, float p_122266_) {
      this.x *= p_122264_;
      this.y *= p_122265_;
      this.z *= p_122266_;
   }

   public void clamp(Vector3f p_176771_, Vector3f p_176772_) {
      this.x = Mth.clamp(this.x, p_176771_.x(), p_176772_.x());
      this.y = Mth.clamp(this.y, p_176771_.x(), p_176772_.y());
      this.z = Mth.clamp(this.z, p_176771_.z(), p_176772_.z());
   }

   public void clamp(float p_122243_, float p_122244_) {
      this.x = Mth.clamp(this.x, p_122243_, p_122244_);
      this.y = Mth.clamp(this.y, p_122243_, p_122244_);
      this.z = Mth.clamp(this.z, p_122243_, p_122244_);
   }

   public void set(float p_122246_, float p_122247_, float p_122248_) {
      this.x = p_122246_;
      this.y = p_122247_;
      this.z = p_122248_;
   }

   public void load(Vector3f p_176769_) {
      this.x = p_176769_.x;
      this.y = p_176769_.y;
      this.z = p_176769_.z;
   }

   public void add(float p_122273_, float p_122274_, float p_122275_) {
      this.x += p_122273_;
      this.y += p_122274_;
      this.z += p_122275_;
   }

   public void add(Vector3f p_122254_) {
      this.x += p_122254_.x;
      this.y += p_122254_.y;
      this.z += p_122254_.z;
   }

   public void sub(Vector3f p_122268_) {
      this.x -= p_122268_.x;
      this.y -= p_122268_.y;
      this.z -= p_122268_.z;
   }

   public float dot(Vector3f p_122277_) {
      return this.x * p_122277_.x + this.y * p_122277_.y + this.z * p_122277_.z;
   }

   public boolean normalize() {
      float f = this.x * this.x + this.y * this.y + this.z * this.z;
      if (f < Float.MIN_NORMAL) { //Forge: Fix MC-239212
         return false;
      } else {
         float f1 = Mth.fastInvSqrt(f);
         this.x *= f1;
         this.y *= f1;
         this.z *= f1;
         return true;
      }
   }

   public void cross(Vector3f p_122280_) {
      float f = this.x;
      float f1 = this.y;
      float f2 = this.z;
      float f3 = p_122280_.x();
      float f4 = p_122280_.y();
      float f5 = p_122280_.z();
      this.x = f1 * f5 - f2 * f4;
      this.y = f2 * f3 - f * f5;
      this.z = f * f4 - f1 * f3;
   }

   public void transform(Matrix3f p_122250_) {
      float f = this.x;
      float f1 = this.y;
      float f2 = this.z;
      this.x = p_122250_.m00 * f + p_122250_.m01 * f1 + p_122250_.m02 * f2;
      this.y = p_122250_.m10 * f + p_122250_.m11 * f1 + p_122250_.m12 * f2;
      this.z = p_122250_.m20 * f + p_122250_.m21 * f1 + p_122250_.m22 * f2;
   }

   public void transform(Quaternion p_122252_) {
      Quaternion quaternion = new Quaternion(p_122252_);
      quaternion.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
      Quaternion quaternion1 = new Quaternion(p_122252_);
      quaternion1.conj();
      quaternion.mul(quaternion1);
      this.set(quaternion.i(), quaternion.j(), quaternion.k());
   }

   public void lerp(Vector3f p_122256_, float p_122257_) {
      float f = 1.0F - p_122257_;
      this.x = this.x * f + p_122256_.x * p_122257_;
      this.y = this.y * f + p_122256_.y * p_122257_;
      this.z = this.z * f + p_122256_.z * p_122257_;
   }

   public Quaternion rotation(float p_122271_) {
      return new Quaternion(this, p_122271_, false);
   }

   public Quaternion rotationDegrees(float p_122241_) {
      return new Quaternion(this, p_122241_, true);
   }

   public Vector3f copy() {
      return new Vector3f(this.x, this.y, this.z);
   }

   public void map(Float2FloatFunction p_122259_) {
      this.x = p_122259_.get(this.x);
      this.y = p_122259_.get(this.y);
      this.z = p_122259_.get(this.z);
   }

   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + "]";
   }

    public Vector3f(float[] values) {
        set(values);
    }
    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
    }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setZ(float z) { this.z = z; }
}
