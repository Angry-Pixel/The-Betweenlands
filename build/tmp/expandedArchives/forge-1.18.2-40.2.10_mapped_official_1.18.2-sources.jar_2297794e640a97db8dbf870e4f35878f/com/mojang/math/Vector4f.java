package com.mojang.math;

import net.minecraft.util.Mth;

public class Vector4f {
   private float x;
   private float y;
   private float z;
   private float w;

   public Vector4f() {
   }

   public Vector4f(float p_123595_, float p_123596_, float p_123597_, float p_123598_) {
      this.x = p_123595_;
      this.y = p_123596_;
      this.z = p_123597_;
      this.w = p_123598_;
   }

   public Vector4f(Vector3f p_123600_) {
      this(p_123600_.x(), p_123600_.y(), p_123600_.z(), 1.0F);
   }

   public boolean equals(Object p_123620_) {
      if (this == p_123620_) {
         return true;
      } else if (p_123620_ != null && this.getClass() == p_123620_.getClass()) {
         Vector4f vector4f = (Vector4f)p_123620_;
         if (Float.compare(vector4f.x, this.x) != 0) {
            return false;
         } else if (Float.compare(vector4f.y, this.y) != 0) {
            return false;
         } else if (Float.compare(vector4f.z, this.z) != 0) {
            return false;
         } else {
            return Float.compare(vector4f.w, this.w) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.x);
      i = 31 * i + Float.floatToIntBits(this.y);
      i = 31 * i + Float.floatToIntBits(this.z);
      return 31 * i + Float.floatToIntBits(this.w);
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

   public float w() {
      return this.w;
   }

   public void mul(float p_176871_) {
      this.x *= p_176871_;
      this.y *= p_176871_;
      this.z *= p_176871_;
      this.w *= p_176871_;
   }

   public void mul(Vector3f p_123612_) {
      this.x *= p_123612_.x();
      this.y *= p_123612_.y();
      this.z *= p_123612_.z();
   }

   public void set(float p_123603_, float p_123604_, float p_123605_, float p_123606_) {
      this.x = p_123603_;
      this.y = p_123604_;
      this.z = p_123605_;
      this.w = p_123606_;
   }

   public void add(float p_176876_, float p_176877_, float p_176878_, float p_176879_) {
      this.x += p_176876_;
      this.y += p_176877_;
      this.z += p_176878_;
      this.w += p_176879_;
   }

   public float dot(Vector4f p_123614_) {
      return this.x * p_123614_.x + this.y * p_123614_.y + this.z * p_123614_.z + this.w * p_123614_.w;
   }

   public boolean normalize() {
      float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
      if ((double)f < 1.0E-5D) {
         return false;
      } else {
         float f1 = Mth.fastInvSqrt(f);
         this.x *= f1;
         this.y *= f1;
         this.z *= f1;
         this.w *= f1;
         return true;
      }
   }

   public void transform(Matrix4f p_123608_) {
      float f = this.x;
      float f1 = this.y;
      float f2 = this.z;
      float f3 = this.w;
      this.x = p_123608_.m00 * f + p_123608_.m01 * f1 + p_123608_.m02 * f2 + p_123608_.m03 * f3;
      this.y = p_123608_.m10 * f + p_123608_.m11 * f1 + p_123608_.m12 * f2 + p_123608_.m13 * f3;
      this.z = p_123608_.m20 * f + p_123608_.m21 * f1 + p_123608_.m22 * f2 + p_123608_.m23 * f3;
      this.w = p_123608_.m30 * f + p_123608_.m31 * f1 + p_123608_.m32 * f2 + p_123608_.m33 * f3;
   }

   public void transform(Quaternion p_123610_) {
      Quaternion quaternion = new Quaternion(p_123610_);
      quaternion.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
      Quaternion quaternion1 = new Quaternion(p_123610_);
      quaternion1.conj();
      quaternion.mul(quaternion1);
      this.set(quaternion.i(), quaternion.j(), quaternion.k(), this.w());
   }

   public void perspectiveDivide() {
      this.x /= this.w;
      this.y /= this.w;
      this.z /= this.w;
      this.w = 1.0F;
   }

   public void lerp(Vector4f p_176873_, float p_176874_) {
      float f = 1.0F - p_176874_;
      this.x = this.x * f + p_176873_.x * p_176874_;
      this.y = this.y * f + p_176873_.y * p_176874_;
      this.z = this.z * f + p_176873_.z * p_176874_;
      this.w = this.w * f + p_176873_.w * p_176874_;
   }

   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
   }

    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
        this.w = values[3];
    }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setZ(float z) { this.z = z; }
    public void setW(float w) { this.w = w; }
}
