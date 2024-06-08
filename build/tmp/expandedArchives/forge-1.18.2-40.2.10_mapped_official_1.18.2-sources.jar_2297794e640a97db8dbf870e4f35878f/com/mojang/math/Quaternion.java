package com.mojang.math;

import net.minecraft.util.Mth;

public final class Quaternion {
   public static final Quaternion ONE = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private float i;
   private float j;
   private float k;
   private float r;

   public Quaternion(float p_80125_, float p_80126_, float p_80127_, float p_80128_) {
      this.i = p_80125_;
      this.j = p_80126_;
      this.k = p_80127_;
      this.r = p_80128_;
   }

   public Quaternion(Vector3f p_80137_, float p_80138_, boolean p_80139_) {
      if (p_80139_) {
         p_80138_ *= ((float)Math.PI / 180F);
      }

      float f = sin(p_80138_ / 2.0F);
      this.i = p_80137_.x() * f;
      this.j = p_80137_.y() * f;
      this.k = p_80137_.z() * f;
      this.r = cos(p_80138_ / 2.0F);
   }

   public Quaternion(float p_80130_, float p_80131_, float p_80132_, boolean p_80133_) {
      if (p_80133_) {
         p_80130_ *= ((float)Math.PI / 180F);
         p_80131_ *= ((float)Math.PI / 180F);
         p_80132_ *= ((float)Math.PI / 180F);
      }

      float f = sin(0.5F * p_80130_);
      float f1 = cos(0.5F * p_80130_);
      float f2 = sin(0.5F * p_80131_);
      float f3 = cos(0.5F * p_80131_);
      float f4 = sin(0.5F * p_80132_);
      float f5 = cos(0.5F * p_80132_);
      this.i = f * f3 * f5 + f1 * f2 * f4;
      this.j = f1 * f2 * f5 - f * f3 * f4;
      this.k = f * f2 * f5 + f1 * f3 * f4;
      this.r = f1 * f3 * f5 - f * f2 * f4;
   }

   public Quaternion(Quaternion p_80135_) {
      this.i = p_80135_.i;
      this.j = p_80135_.j;
      this.k = p_80135_.k;
      this.r = p_80135_.r;
   }

   public static Quaternion fromYXZ(float p_175219_, float p_175220_, float p_175221_) {
      Quaternion quaternion = ONE.copy();
      quaternion.mul(new Quaternion(0.0F, (float)Math.sin((double)(p_175219_ / 2.0F)), 0.0F, (float)Math.cos((double)(p_175219_ / 2.0F))));
      quaternion.mul(new Quaternion((float)Math.sin((double)(p_175220_ / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(p_175220_ / 2.0F))));
      quaternion.mul(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(p_175221_ / 2.0F)), (float)Math.cos((double)(p_175221_ / 2.0F))));
      return quaternion;
   }

   public static Quaternion fromXYZDegrees(Vector3f p_175226_) {
      return fromXYZ((float)Math.toRadians((double)p_175226_.x()), (float)Math.toRadians((double)p_175226_.y()), (float)Math.toRadians((double)p_175226_.z()));
   }

   public static Quaternion fromXYZ(Vector3f p_175233_) {
      return fromXYZ(p_175233_.x(), p_175233_.y(), p_175233_.z());
   }

   public static Quaternion fromXYZ(float p_175229_, float p_175230_, float p_175231_) {
      Quaternion quaternion = ONE.copy();
      quaternion.mul(new Quaternion((float)Math.sin((double)(p_175229_ / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(p_175229_ / 2.0F))));
      quaternion.mul(new Quaternion(0.0F, (float)Math.sin((double)(p_175230_ / 2.0F)), 0.0F, (float)Math.cos((double)(p_175230_ / 2.0F))));
      quaternion.mul(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(p_175231_ / 2.0F)), (float)Math.cos((double)(p_175231_ / 2.0F))));
      return quaternion;
   }

   public Vector3f toXYZ() {
      float f = this.r() * this.r();
      float f1 = this.i() * this.i();
      float f2 = this.j() * this.j();
      float f3 = this.k() * this.k();
      float f4 = f + f1 + f2 + f3;
      float f5 = 2.0F * this.r() * this.i() - 2.0F * this.j() * this.k();
      float f6 = (float)Math.asin((double)(f5 / f4));
      return Math.abs(f5) > 0.999F * f4 ? new Vector3f(2.0F * (float)Math.atan2((double)this.i(), (double)this.r()), f6, 0.0F) : new Vector3f((float)Math.atan2((double)(2.0F * this.j() * this.k() + 2.0F * this.i() * this.r()), (double)(f - f1 - f2 + f3)), f6, (float)Math.atan2((double)(2.0F * this.i() * this.j() + 2.0F * this.r() * this.k()), (double)(f + f1 - f2 - f3)));
   }

   public Vector3f toXYZDegrees() {
      Vector3f vector3f = this.toXYZ();
      return new Vector3f((float)Math.toDegrees((double)vector3f.x()), (float)Math.toDegrees((double)vector3f.y()), (float)Math.toDegrees((double)vector3f.z()));
   }

   public Vector3f toYXZ() {
      float f = this.r() * this.r();
      float f1 = this.i() * this.i();
      float f2 = this.j() * this.j();
      float f3 = this.k() * this.k();
      float f4 = f + f1 + f2 + f3;
      float f5 = 2.0F * this.r() * this.i() - 2.0F * this.j() * this.k();
      float f6 = (float)Math.asin((double)(f5 / f4));
      return Math.abs(f5) > 0.999F * f4 ? new Vector3f(f6, 2.0F * (float)Math.atan2((double)this.j(), (double)this.r()), 0.0F) : new Vector3f(f6, (float)Math.atan2((double)(2.0F * this.i() * this.k() + 2.0F * this.j() * this.r()), (double)(f - f1 - f2 + f3)), (float)Math.atan2((double)(2.0F * this.i() * this.j() + 2.0F * this.r() * this.k()), (double)(f - f1 + f2 - f3)));
   }

   public Vector3f toYXZDegrees() {
      Vector3f vector3f = this.toYXZ();
      return new Vector3f((float)Math.toDegrees((double)vector3f.x()), (float)Math.toDegrees((double)vector3f.y()), (float)Math.toDegrees((double)vector3f.z()));
   }

   public boolean equals(Object p_80159_) {
      if (this == p_80159_) {
         return true;
      } else if (p_80159_ != null && this.getClass() == p_80159_.getClass()) {
         Quaternion quaternion = (Quaternion)p_80159_;
         if (Float.compare(quaternion.i, this.i) != 0) {
            return false;
         } else if (Float.compare(quaternion.j, this.j) != 0) {
            return false;
         } else if (Float.compare(quaternion.k, this.k) != 0) {
            return false;
         } else {
            return Float.compare(quaternion.r, this.r) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = Float.floatToIntBits(this.i);
      i = 31 * i + Float.floatToIntBits(this.j);
      i = 31 * i + Float.floatToIntBits(this.k);
      return 31 * i + Float.floatToIntBits(this.r);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("Quaternion[").append(this.r()).append(" + ");
      stringbuilder.append(this.i()).append("i + ");
      stringbuilder.append(this.j()).append("j + ");
      stringbuilder.append(this.k()).append("k]");
      return stringbuilder.toString();
   }

   public float i() {
      return this.i;
   }

   public float j() {
      return this.j;
   }

   public float k() {
      return this.k;
   }

   public float r() {
      return this.r;
   }

   public void mul(Quaternion p_80149_) {
      float f = this.i();
      float f1 = this.j();
      float f2 = this.k();
      float f3 = this.r();
      float f4 = p_80149_.i();
      float f5 = p_80149_.j();
      float f6 = p_80149_.k();
      float f7 = p_80149_.r();
      this.i = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
      this.j = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
      this.k = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
      this.r = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
   }

   public void mul(float p_80142_) {
      this.i *= p_80142_;
      this.j *= p_80142_;
      this.k *= p_80142_;
      this.r *= p_80142_;
   }

   public void conj() {
      this.i = -this.i;
      this.j = -this.j;
      this.k = -this.k;
   }

   public void set(float p_80144_, float p_80145_, float p_80146_, float p_80147_) {
      this.i = p_80144_;
      this.j = p_80145_;
      this.k = p_80146_;
      this.r = p_80147_;
   }

   private static float cos(float p_80152_) {
      return (float)Math.cos((double)p_80152_);
   }

   private static float sin(float p_80155_) {
      return (float)Math.sin((double)p_80155_);
   }

   public void normalize() {
      float f = this.i() * this.i() + this.j() * this.j() + this.k() * this.k() + this.r() * this.r();
      if (f > 1.0E-6F) {
         float f1 = Mth.fastInvSqrt(f);
         this.i *= f1;
         this.j *= f1;
         this.k *= f1;
         this.r *= f1;
      } else {
         this.i = 0.0F;
         this.j = 0.0F;
         this.k = 0.0F;
         this.r = 0.0F;
      }

   }

   public void slerp(Quaternion p_175223_, float p_175224_) {
      throw new UnsupportedOperationException();
   }

   public Quaternion copy() {
      return new Quaternion(this);
   }
}