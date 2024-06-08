package com.mojang.math;

import java.nio.FloatBuffer;

public final class Matrix4f {
   private static final int ORDER = 4;
   protected float m00;
   protected float m01;
   protected float m02;
   protected float m03;
   protected float m10;
   protected float m11;
   protected float m12;
   protected float m13;
   protected float m20;
   protected float m21;
   protected float m22;
   protected float m23;
   protected float m30;
   protected float m31;
   protected float m32;
   protected float m33;

   public Matrix4f() {
   }

   public Matrix4f(Matrix4f p_27621_) {
      this.m00 = p_27621_.m00;
      this.m01 = p_27621_.m01;
      this.m02 = p_27621_.m02;
      this.m03 = p_27621_.m03;
      this.m10 = p_27621_.m10;
      this.m11 = p_27621_.m11;
      this.m12 = p_27621_.m12;
      this.m13 = p_27621_.m13;
      this.m20 = p_27621_.m20;
      this.m21 = p_27621_.m21;
      this.m22 = p_27621_.m22;
      this.m23 = p_27621_.m23;
      this.m30 = p_27621_.m30;
      this.m31 = p_27621_.m31;
      this.m32 = p_27621_.m32;
      this.m33 = p_27621_.m33;
   }

   public Matrix4f(Quaternion p_27623_) {
      float f = p_27623_.i();
      float f1 = p_27623_.j();
      float f2 = p_27623_.k();
      float f3 = p_27623_.r();
      float f4 = 2.0F * f * f;
      float f5 = 2.0F * f1 * f1;
      float f6 = 2.0F * f2 * f2;
      this.m00 = 1.0F - f5 - f6;
      this.m11 = 1.0F - f6 - f4;
      this.m22 = 1.0F - f4 - f5;
      this.m33 = 1.0F;
      float f7 = f * f1;
      float f8 = f1 * f2;
      float f9 = f2 * f;
      float f10 = f * f3;
      float f11 = f1 * f3;
      float f12 = f2 * f3;
      this.m10 = 2.0F * (f7 + f12);
      this.m01 = 2.0F * (f7 - f12);
      this.m20 = 2.0F * (f9 - f11);
      this.m02 = 2.0F * (f9 + f11);
      this.m21 = 2.0F * (f8 + f10);
      this.m12 = 2.0F * (f8 - f10);
   }

   public boolean isInteger() {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.m30 = 1.0F;
      matrix4f.m31 = 1.0F;
      matrix4f.m32 = 1.0F;
      matrix4f.m33 = 0.0F;
      Matrix4f matrix4f1 = this.copy();
      matrix4f1.multiply(matrix4f);
      return isInteger(matrix4f1.m00 / matrix4f1.m03) && isInteger(matrix4f1.m10 / matrix4f1.m13) && isInteger(matrix4f1.m20 / matrix4f1.m23) && isInteger(matrix4f1.m01 / matrix4f1.m03) && isInteger(matrix4f1.m11 / matrix4f1.m13) && isInteger(matrix4f1.m21 / matrix4f1.m23) && isInteger(matrix4f1.m02 / matrix4f1.m03) && isInteger(matrix4f1.m12 / matrix4f1.m13) && isInteger(matrix4f1.m22 / matrix4f1.m23);
   }

   private static boolean isInteger(float p_162218_) {
      return (double)Math.abs(p_162218_ - (float)Math.round(p_162218_)) <= 1.0E-5D;
   }

   public boolean equals(Object p_27661_) {
      if (this == p_27661_) {
         return true;
      } else if (p_27661_ != null && this.getClass() == p_27661_.getClass()) {
         Matrix4f matrix4f = (Matrix4f)p_27661_;
         return Float.compare(matrix4f.m00, this.m00) == 0 && Float.compare(matrix4f.m01, this.m01) == 0 && Float.compare(matrix4f.m02, this.m02) == 0 && Float.compare(matrix4f.m03, this.m03) == 0 && Float.compare(matrix4f.m10, this.m10) == 0 && Float.compare(matrix4f.m11, this.m11) == 0 && Float.compare(matrix4f.m12, this.m12) == 0 && Float.compare(matrix4f.m13, this.m13) == 0 && Float.compare(matrix4f.m20, this.m20) == 0 && Float.compare(matrix4f.m21, this.m21) == 0 && Float.compare(matrix4f.m22, this.m22) == 0 && Float.compare(matrix4f.m23, this.m23) == 0 && Float.compare(matrix4f.m30, this.m30) == 0 && Float.compare(matrix4f.m31, this.m31) == 0 && Float.compare(matrix4f.m32, this.m32) == 0 && Float.compare(matrix4f.m33, this.m33) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.m00 != 0.0F ? Float.floatToIntBits(this.m00) : 0;
      i = 31 * i + (this.m01 != 0.0F ? Float.floatToIntBits(this.m01) : 0);
      i = 31 * i + (this.m02 != 0.0F ? Float.floatToIntBits(this.m02) : 0);
      i = 31 * i + (this.m03 != 0.0F ? Float.floatToIntBits(this.m03) : 0);
      i = 31 * i + (this.m10 != 0.0F ? Float.floatToIntBits(this.m10) : 0);
      i = 31 * i + (this.m11 != 0.0F ? Float.floatToIntBits(this.m11) : 0);
      i = 31 * i + (this.m12 != 0.0F ? Float.floatToIntBits(this.m12) : 0);
      i = 31 * i + (this.m13 != 0.0F ? Float.floatToIntBits(this.m13) : 0);
      i = 31 * i + (this.m20 != 0.0F ? Float.floatToIntBits(this.m20) : 0);
      i = 31 * i + (this.m21 != 0.0F ? Float.floatToIntBits(this.m21) : 0);
      i = 31 * i + (this.m22 != 0.0F ? Float.floatToIntBits(this.m22) : 0);
      i = 31 * i + (this.m23 != 0.0F ? Float.floatToIntBits(this.m23) : 0);
      i = 31 * i + (this.m30 != 0.0F ? Float.floatToIntBits(this.m30) : 0);
      i = 31 * i + (this.m31 != 0.0F ? Float.floatToIntBits(this.m31) : 0);
      i = 31 * i + (this.m32 != 0.0F ? Float.floatToIntBits(this.m32) : 0);
      return 31 * i + (this.m33 != 0.0F ? Float.floatToIntBits(this.m33) : 0);
   }

   private static int bufferIndex(int p_27642_, int p_27643_) {
      return p_27643_ * 4 + p_27642_;
   }

   public void load(FloatBuffer p_162213_) {
      this.m00 = p_162213_.get(bufferIndex(0, 0));
      this.m01 = p_162213_.get(bufferIndex(0, 1));
      this.m02 = p_162213_.get(bufferIndex(0, 2));
      this.m03 = p_162213_.get(bufferIndex(0, 3));
      this.m10 = p_162213_.get(bufferIndex(1, 0));
      this.m11 = p_162213_.get(bufferIndex(1, 1));
      this.m12 = p_162213_.get(bufferIndex(1, 2));
      this.m13 = p_162213_.get(bufferIndex(1, 3));
      this.m20 = p_162213_.get(bufferIndex(2, 0));
      this.m21 = p_162213_.get(bufferIndex(2, 1));
      this.m22 = p_162213_.get(bufferIndex(2, 2));
      this.m23 = p_162213_.get(bufferIndex(2, 3));
      this.m30 = p_162213_.get(bufferIndex(3, 0));
      this.m31 = p_162213_.get(bufferIndex(3, 1));
      this.m32 = p_162213_.get(bufferIndex(3, 2));
      this.m33 = p_162213_.get(bufferIndex(3, 3));
   }

   public void loadTransposed(FloatBuffer p_162220_) {
      this.m00 = p_162220_.get(bufferIndex(0, 0));
      this.m01 = p_162220_.get(bufferIndex(1, 0));
      this.m02 = p_162220_.get(bufferIndex(2, 0));
      this.m03 = p_162220_.get(bufferIndex(3, 0));
      this.m10 = p_162220_.get(bufferIndex(0, 1));
      this.m11 = p_162220_.get(bufferIndex(1, 1));
      this.m12 = p_162220_.get(bufferIndex(2, 1));
      this.m13 = p_162220_.get(bufferIndex(3, 1));
      this.m20 = p_162220_.get(bufferIndex(0, 2));
      this.m21 = p_162220_.get(bufferIndex(1, 2));
      this.m22 = p_162220_.get(bufferIndex(2, 2));
      this.m23 = p_162220_.get(bufferIndex(3, 2));
      this.m30 = p_162220_.get(bufferIndex(0, 3));
      this.m31 = p_162220_.get(bufferIndex(1, 3));
      this.m32 = p_162220_.get(bufferIndex(2, 3));
      this.m33 = p_162220_.get(bufferIndex(3, 3));
   }

   public void load(FloatBuffer p_162215_, boolean p_162216_) {
      if (p_162216_) {
         this.loadTransposed(p_162215_);
      } else {
         this.load(p_162215_);
      }

   }

   public void load(Matrix4f p_162211_) {
      this.m00 = p_162211_.m00;
      this.m01 = p_162211_.m01;
      this.m02 = p_162211_.m02;
      this.m03 = p_162211_.m03;
      this.m10 = p_162211_.m10;
      this.m11 = p_162211_.m11;
      this.m12 = p_162211_.m12;
      this.m13 = p_162211_.m13;
      this.m20 = p_162211_.m20;
      this.m21 = p_162211_.m21;
      this.m22 = p_162211_.m22;
      this.m23 = p_162211_.m23;
      this.m30 = p_162211_.m30;
      this.m31 = p_162211_.m31;
      this.m32 = p_162211_.m32;
      this.m33 = p_162211_.m33;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("Matrix4f:\n");
      stringbuilder.append(this.m00);
      stringbuilder.append(" ");
      stringbuilder.append(this.m01);
      stringbuilder.append(" ");
      stringbuilder.append(this.m02);
      stringbuilder.append(" ");
      stringbuilder.append(this.m03);
      stringbuilder.append("\n");
      stringbuilder.append(this.m10);
      stringbuilder.append(" ");
      stringbuilder.append(this.m11);
      stringbuilder.append(" ");
      stringbuilder.append(this.m12);
      stringbuilder.append(" ");
      stringbuilder.append(this.m13);
      stringbuilder.append("\n");
      stringbuilder.append(this.m20);
      stringbuilder.append(" ");
      stringbuilder.append(this.m21);
      stringbuilder.append(" ");
      stringbuilder.append(this.m22);
      stringbuilder.append(" ");
      stringbuilder.append(this.m23);
      stringbuilder.append("\n");
      stringbuilder.append(this.m30);
      stringbuilder.append(" ");
      stringbuilder.append(this.m31);
      stringbuilder.append(" ");
      stringbuilder.append(this.m32);
      stringbuilder.append(" ");
      stringbuilder.append(this.m33);
      stringbuilder.append("\n");
      return stringbuilder.toString();
   }

   public void store(FloatBuffer p_27651_) {
      p_27651_.put(bufferIndex(0, 0), this.m00);
      p_27651_.put(bufferIndex(0, 1), this.m01);
      p_27651_.put(bufferIndex(0, 2), this.m02);
      p_27651_.put(bufferIndex(0, 3), this.m03);
      p_27651_.put(bufferIndex(1, 0), this.m10);
      p_27651_.put(bufferIndex(1, 1), this.m11);
      p_27651_.put(bufferIndex(1, 2), this.m12);
      p_27651_.put(bufferIndex(1, 3), this.m13);
      p_27651_.put(bufferIndex(2, 0), this.m20);
      p_27651_.put(bufferIndex(2, 1), this.m21);
      p_27651_.put(bufferIndex(2, 2), this.m22);
      p_27651_.put(bufferIndex(2, 3), this.m23);
      p_27651_.put(bufferIndex(3, 0), this.m30);
      p_27651_.put(bufferIndex(3, 1), this.m31);
      p_27651_.put(bufferIndex(3, 2), this.m32);
      p_27651_.put(bufferIndex(3, 3), this.m33);
   }

   public void storeTransposed(FloatBuffer p_162230_) {
      p_162230_.put(bufferIndex(0, 0), this.m00);
      p_162230_.put(bufferIndex(1, 0), this.m01);
      p_162230_.put(bufferIndex(2, 0), this.m02);
      p_162230_.put(bufferIndex(3, 0), this.m03);
      p_162230_.put(bufferIndex(0, 1), this.m10);
      p_162230_.put(bufferIndex(1, 1), this.m11);
      p_162230_.put(bufferIndex(2, 1), this.m12);
      p_162230_.put(bufferIndex(3, 1), this.m13);
      p_162230_.put(bufferIndex(0, 2), this.m20);
      p_162230_.put(bufferIndex(1, 2), this.m21);
      p_162230_.put(bufferIndex(2, 2), this.m22);
      p_162230_.put(bufferIndex(3, 2), this.m23);
      p_162230_.put(bufferIndex(0, 3), this.m30);
      p_162230_.put(bufferIndex(1, 3), this.m31);
      p_162230_.put(bufferIndex(2, 3), this.m32);
      p_162230_.put(bufferIndex(3, 3), this.m33);
   }

   public void store(FloatBuffer p_162222_, boolean p_162223_) {
      if (p_162223_) {
         this.storeTransposed(p_162222_);
      } else {
         this.store(p_162222_);
      }

   }

   public void setIdentity() {
      this.m00 = 1.0F;
      this.m01 = 0.0F;
      this.m02 = 0.0F;
      this.m03 = 0.0F;
      this.m10 = 0.0F;
      this.m11 = 1.0F;
      this.m12 = 0.0F;
      this.m13 = 0.0F;
      this.m20 = 0.0F;
      this.m21 = 0.0F;
      this.m22 = 1.0F;
      this.m23 = 0.0F;
      this.m30 = 0.0F;
      this.m31 = 0.0F;
      this.m32 = 0.0F;
      this.m33 = 1.0F;
   }

   public float adjugateAndDet() {
      float f = this.m00 * this.m11 - this.m01 * this.m10;
      float f1 = this.m00 * this.m12 - this.m02 * this.m10;
      float f2 = this.m00 * this.m13 - this.m03 * this.m10;
      float f3 = this.m01 * this.m12 - this.m02 * this.m11;
      float f4 = this.m01 * this.m13 - this.m03 * this.m11;
      float f5 = this.m02 * this.m13 - this.m03 * this.m12;
      float f6 = this.m20 * this.m31 - this.m21 * this.m30;
      float f7 = this.m20 * this.m32 - this.m22 * this.m30;
      float f8 = this.m20 * this.m33 - this.m23 * this.m30;
      float f9 = this.m21 * this.m32 - this.m22 * this.m31;
      float f10 = this.m21 * this.m33 - this.m23 * this.m31;
      float f11 = this.m22 * this.m33 - this.m23 * this.m32;
      float f12 = this.m11 * f11 - this.m12 * f10 + this.m13 * f9;
      float f13 = -this.m10 * f11 + this.m12 * f8 - this.m13 * f7;
      float f14 = this.m10 * f10 - this.m11 * f8 + this.m13 * f6;
      float f15 = -this.m10 * f9 + this.m11 * f7 - this.m12 * f6;
      float f16 = -this.m01 * f11 + this.m02 * f10 - this.m03 * f9;
      float f17 = this.m00 * f11 - this.m02 * f8 + this.m03 * f7;
      float f18 = -this.m00 * f10 + this.m01 * f8 - this.m03 * f6;
      float f19 = this.m00 * f9 - this.m01 * f7 + this.m02 * f6;
      float f20 = this.m31 * f5 - this.m32 * f4 + this.m33 * f3;
      float f21 = -this.m30 * f5 + this.m32 * f2 - this.m33 * f1;
      float f22 = this.m30 * f4 - this.m31 * f2 + this.m33 * f;
      float f23 = -this.m30 * f3 + this.m31 * f1 - this.m32 * f;
      float f24 = -this.m21 * f5 + this.m22 * f4 - this.m23 * f3;
      float f25 = this.m20 * f5 - this.m22 * f2 + this.m23 * f1;
      float f26 = -this.m20 * f4 + this.m21 * f2 - this.m23 * f;
      float f27 = this.m20 * f3 - this.m21 * f1 + this.m22 * f;
      this.m00 = f12;
      this.m10 = f13;
      this.m20 = f14;
      this.m30 = f15;
      this.m01 = f16;
      this.m11 = f17;
      this.m21 = f18;
      this.m31 = f19;
      this.m02 = f20;
      this.m12 = f21;
      this.m22 = f22;
      this.m32 = f23;
      this.m03 = f24;
      this.m13 = f25;
      this.m23 = f26;
      this.m33 = f27;
      return f * f11 - f1 * f10 + f2 * f9 + f3 * f8 - f4 * f7 + f5 * f6;
   }

   public float determinant() {
      float f = this.m00 * this.m11 - this.m01 * this.m10;
      float f1 = this.m00 * this.m12 - this.m02 * this.m10;
      float f2 = this.m00 * this.m13 - this.m03 * this.m10;
      float f3 = this.m01 * this.m12 - this.m02 * this.m11;
      float f4 = this.m01 * this.m13 - this.m03 * this.m11;
      float f5 = this.m02 * this.m13 - this.m03 * this.m12;
      float f6 = this.m20 * this.m31 - this.m21 * this.m30;
      float f7 = this.m20 * this.m32 - this.m22 * this.m30;
      float f8 = this.m20 * this.m33 - this.m23 * this.m30;
      float f9 = this.m21 * this.m32 - this.m22 * this.m31;
      float f10 = this.m21 * this.m33 - this.m23 * this.m31;
      float f11 = this.m22 * this.m33 - this.m23 * this.m32;
      return f * f11 - f1 * f10 + f2 * f9 + f3 * f8 - f4 * f7 + f5 * f6;
   }

   public void transpose() {
      float f = this.m10;
      this.m10 = this.m01;
      this.m01 = f;
      f = this.m20;
      this.m20 = this.m02;
      this.m02 = f;
      f = this.m21;
      this.m21 = this.m12;
      this.m12 = f;
      f = this.m30;
      this.m30 = this.m03;
      this.m03 = f;
      f = this.m31;
      this.m31 = this.m13;
      this.m13 = f;
      f = this.m32;
      this.m32 = this.m23;
      this.m23 = f;
   }

   public boolean invert() {
      float f = this.adjugateAndDet();
      if (Math.abs(f) > 1.0E-6F) {
         this.multiply(f);
         return true;
      } else {
         return false;
      }
   }

   public void multiply(Matrix4f p_27645_) {
      float f = this.m00 * p_27645_.m00 + this.m01 * p_27645_.m10 + this.m02 * p_27645_.m20 + this.m03 * p_27645_.m30;
      float f1 = this.m00 * p_27645_.m01 + this.m01 * p_27645_.m11 + this.m02 * p_27645_.m21 + this.m03 * p_27645_.m31;
      float f2 = this.m00 * p_27645_.m02 + this.m01 * p_27645_.m12 + this.m02 * p_27645_.m22 + this.m03 * p_27645_.m32;
      float f3 = this.m00 * p_27645_.m03 + this.m01 * p_27645_.m13 + this.m02 * p_27645_.m23 + this.m03 * p_27645_.m33;
      float f4 = this.m10 * p_27645_.m00 + this.m11 * p_27645_.m10 + this.m12 * p_27645_.m20 + this.m13 * p_27645_.m30;
      float f5 = this.m10 * p_27645_.m01 + this.m11 * p_27645_.m11 + this.m12 * p_27645_.m21 + this.m13 * p_27645_.m31;
      float f6 = this.m10 * p_27645_.m02 + this.m11 * p_27645_.m12 + this.m12 * p_27645_.m22 + this.m13 * p_27645_.m32;
      float f7 = this.m10 * p_27645_.m03 + this.m11 * p_27645_.m13 + this.m12 * p_27645_.m23 + this.m13 * p_27645_.m33;
      float f8 = this.m20 * p_27645_.m00 + this.m21 * p_27645_.m10 + this.m22 * p_27645_.m20 + this.m23 * p_27645_.m30;
      float f9 = this.m20 * p_27645_.m01 + this.m21 * p_27645_.m11 + this.m22 * p_27645_.m21 + this.m23 * p_27645_.m31;
      float f10 = this.m20 * p_27645_.m02 + this.m21 * p_27645_.m12 + this.m22 * p_27645_.m22 + this.m23 * p_27645_.m32;
      float f11 = this.m20 * p_27645_.m03 + this.m21 * p_27645_.m13 + this.m22 * p_27645_.m23 + this.m23 * p_27645_.m33;
      float f12 = this.m30 * p_27645_.m00 + this.m31 * p_27645_.m10 + this.m32 * p_27645_.m20 + this.m33 * p_27645_.m30;
      float f13 = this.m30 * p_27645_.m01 + this.m31 * p_27645_.m11 + this.m32 * p_27645_.m21 + this.m33 * p_27645_.m31;
      float f14 = this.m30 * p_27645_.m02 + this.m31 * p_27645_.m12 + this.m32 * p_27645_.m22 + this.m33 * p_27645_.m32;
      float f15 = this.m30 * p_27645_.m03 + this.m31 * p_27645_.m13 + this.m32 * p_27645_.m23 + this.m33 * p_27645_.m33;
      this.m00 = f;
      this.m01 = f1;
      this.m02 = f2;
      this.m03 = f3;
      this.m10 = f4;
      this.m11 = f5;
      this.m12 = f6;
      this.m13 = f7;
      this.m20 = f8;
      this.m21 = f9;
      this.m22 = f10;
      this.m23 = f11;
      this.m30 = f12;
      this.m31 = f13;
      this.m32 = f14;
      this.m33 = f15;
   }

   public void multiply(Quaternion p_27647_) {
      this.multiply(new Matrix4f(p_27647_));
   }

   public void multiply(float p_27631_) {
      this.m00 *= p_27631_;
      this.m01 *= p_27631_;
      this.m02 *= p_27631_;
      this.m03 *= p_27631_;
      this.m10 *= p_27631_;
      this.m11 *= p_27631_;
      this.m12 *= p_27631_;
      this.m13 *= p_27631_;
      this.m20 *= p_27631_;
      this.m21 *= p_27631_;
      this.m22 *= p_27631_;
      this.m23 *= p_27631_;
      this.m30 *= p_27631_;
      this.m31 *= p_27631_;
      this.m32 *= p_27631_;
      this.m33 *= p_27631_;
   }

   public void add(Matrix4f p_162225_) {
      this.m00 += p_162225_.m00;
      this.m01 += p_162225_.m01;
      this.m02 += p_162225_.m02;
      this.m03 += p_162225_.m03;
      this.m10 += p_162225_.m10;
      this.m11 += p_162225_.m11;
      this.m12 += p_162225_.m12;
      this.m13 += p_162225_.m13;
      this.m20 += p_162225_.m20;
      this.m21 += p_162225_.m21;
      this.m22 += p_162225_.m22;
      this.m23 += p_162225_.m23;
      this.m30 += p_162225_.m30;
      this.m31 += p_162225_.m31;
      this.m32 += p_162225_.m32;
      this.m33 += p_162225_.m33;
   }

   public void subtract(Matrix4f p_162228_) {
      this.m00 -= p_162228_.m00;
      this.m01 -= p_162228_.m01;
      this.m02 -= p_162228_.m02;
      this.m03 -= p_162228_.m03;
      this.m10 -= p_162228_.m10;
      this.m11 -= p_162228_.m11;
      this.m12 -= p_162228_.m12;
      this.m13 -= p_162228_.m13;
      this.m20 -= p_162228_.m20;
      this.m21 -= p_162228_.m21;
      this.m22 -= p_162228_.m22;
      this.m23 -= p_162228_.m23;
      this.m30 -= p_162228_.m30;
      this.m31 -= p_162228_.m31;
      this.m32 -= p_162228_.m32;
      this.m33 -= p_162228_.m33;
   }

   public float trace() {
      return this.m00 + this.m11 + this.m22 + this.m33;
   }

   public static Matrix4f perspective(double p_27626_, float p_27627_, float p_27628_, float p_27629_) {
      float f = (float)(1.0D / Math.tan(p_27626_ * (double)((float)Math.PI / 180F) / 2.0D));
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.m00 = f / p_27627_;
      matrix4f.m11 = f;
      matrix4f.m22 = (p_27629_ + p_27628_) / (p_27628_ - p_27629_);
      matrix4f.m32 = -1.0F;
      matrix4f.m23 = 2.0F * p_27629_ * p_27628_ / (p_27628_ - p_27629_);
      return matrix4f;
   }

   public static Matrix4f orthographic(float p_27637_, float p_27638_, float p_27639_, float p_27640_) {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.m00 = 2.0F / p_27637_;
      matrix4f.m11 = 2.0F / p_27638_;
      float f = p_27640_ - p_27639_;
      matrix4f.m22 = -2.0F / f;
      matrix4f.m33 = 1.0F;
      matrix4f.m03 = -1.0F;
      matrix4f.m13 = 1.0F;
      matrix4f.m23 = -(p_27640_ + p_27639_) / f;
      return matrix4f;
   }

   public static Matrix4f orthographic(float p_162204_, float p_162205_, float p_162206_, float p_162207_, float p_162208_, float p_162209_) {
      Matrix4f matrix4f = new Matrix4f();
      float f = p_162205_ - p_162204_;
      float f1 = p_162206_ - p_162207_;
      float f2 = p_162209_ - p_162208_;
      matrix4f.m00 = 2.0F / f;
      matrix4f.m11 = 2.0F / f1;
      matrix4f.m22 = -2.0F / f2;
      matrix4f.m03 = -(p_162205_ + p_162204_) / f;
      matrix4f.m13 = -(p_162206_ + p_162207_) / f1;
      matrix4f.m23 = -(p_162209_ + p_162208_) / f2;
      matrix4f.m33 = 1.0F;
      return matrix4f;
   }

   public void translate(Vector3f p_27649_) {
      this.m03 += p_27649_.x();
      this.m13 += p_27649_.y();
      this.m23 += p_27649_.z();
   }

   public Matrix4f copy() {
      return new Matrix4f(this);
   }

   public void multiplyWithTranslation(float p_162200_, float p_162201_, float p_162202_) {
      this.m03 += this.m00 * p_162200_ + this.m01 * p_162201_ + this.m02 * p_162202_;
      this.m13 += this.m10 * p_162200_ + this.m11 * p_162201_ + this.m12 * p_162202_;
      this.m23 += this.m20 * p_162200_ + this.m21 * p_162201_ + this.m22 * p_162202_;
      this.m33 += this.m30 * p_162200_ + this.m31 * p_162201_ + this.m32 * p_162202_;
   }

   public static Matrix4f createScaleMatrix(float p_27633_, float p_27634_, float p_27635_) {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.m00 = p_27633_;
      matrix4f.m11 = p_27634_;
      matrix4f.m22 = p_27635_;
      matrix4f.m33 = 1.0F;
      return matrix4f;
   }

   public static Matrix4f createTranslateMatrix(float p_27654_, float p_27655_, float p_27656_) {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.m00 = 1.0F;
      matrix4f.m11 = 1.0F;
      matrix4f.m22 = 1.0F;
      matrix4f.m33 = 1.0F;
      matrix4f.m03 = p_27654_;
      matrix4f.m13 = p_27655_;
      matrix4f.m23 = p_27656_;
      return matrix4f;
   }

    // Forge start
    public Matrix4f(float[] values) {
        m00 = values[0];
        m01 = values[1];
        m02 = values[2];
        m03 = values[3];
        m10 = values[4];
        m11 = values[5];
        m12 = values[6];
        m13 = values[7];
        m20 = values[8];
        m21 = values[9];
        m22 = values[10];
        m23 = values[11];
        m30 = values[12];
        m31 = values[13];
        m32 = values[14];
        m33 = values[15];
    }

    public void multiplyBackward(Matrix4f other) {
        Matrix4f copy = other.copy();
        copy.multiply(this);
        this.load(copy);
    }

    public void setTranslation(float x, float y, float z) {
        this.m00 = 1.0F;
        this.m11 = 1.0F;
        this.m22 = 1.0F;
        this.m33 = 1.0F;
        this.m03 = x;
        this.m13 = y;
        this.m23 = z;
    }
}
