package com.mojang.math;

import com.mojang.datafixers.util.Pair;
import java.nio.FloatBuffer;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Triple;

public final class Matrix3f {
   private static final int ORDER = 3;
   private static final float G = 3.0F + 2.0F * (float)Math.sqrt(2.0D);
   private static final float CS = (float)Math.cos((Math.PI / 8D));
   private static final float SS = (float)Math.sin((Math.PI / 8D));
   private static final float SQ2 = 1.0F / (float)Math.sqrt(2.0D);
   protected float m00;
   protected float m01;
   protected float m02;
   protected float m10;
   protected float m11;
   protected float m12;
   protected float m20;
   protected float m21;
   protected float m22;

   public Matrix3f() {
   }

   public Matrix3f(Quaternion p_8154_) {
      float f = p_8154_.i();
      float f1 = p_8154_.j();
      float f2 = p_8154_.k();
      float f3 = p_8154_.r();
      float f4 = 2.0F * f * f;
      float f5 = 2.0F * f1 * f1;
      float f6 = 2.0F * f2 * f2;
      this.m00 = 1.0F - f5 - f6;
      this.m11 = 1.0F - f6 - f4;
      this.m22 = 1.0F - f4 - f5;
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

   public static Matrix3f createScaleMatrix(float p_8175_, float p_8176_, float p_8177_) {
      Matrix3f matrix3f = new Matrix3f();
      matrix3f.m00 = p_8175_;
      matrix3f.m11 = p_8176_;
      matrix3f.m22 = p_8177_;
      return matrix3f;
   }

   public Matrix3f(Matrix4f p_8152_) {
      this.m00 = p_8152_.m00;
      this.m01 = p_8152_.m01;
      this.m02 = p_8152_.m02;
      this.m10 = p_8152_.m10;
      this.m11 = p_8152_.m11;
      this.m12 = p_8152_.m12;
      this.m20 = p_8152_.m20;
      this.m21 = p_8152_.m21;
      this.m22 = p_8152_.m22;
   }

   public Matrix3f(Matrix3f p_8150_) {
      this.m00 = p_8150_.m00;
      this.m01 = p_8150_.m01;
      this.m02 = p_8150_.m02;
      this.m10 = p_8150_.m10;
      this.m11 = p_8150_.m11;
      this.m12 = p_8150_.m12;
      this.m20 = p_8150_.m20;
      this.m21 = p_8150_.m21;
      this.m22 = p_8150_.m22;
   }

   private static Pair<Float, Float> approxGivensQuat(float p_8162_, float p_8163_, float p_8164_) {
      float f = 2.0F * (p_8162_ - p_8164_);
      if (G * p_8163_ * p_8163_ < f * f) {
         float f1 = Mth.fastInvSqrt(p_8163_ * p_8163_ + f * f);
         return Pair.of(f1 * p_8163_, f1 * f);
      } else {
         return Pair.of(SS, CS);
      }
   }

   private static Pair<Float, Float> qrGivensQuat(float p_8159_, float p_8160_) {
      float f = (float)Math.hypot((double)p_8159_, (double)p_8160_);
      float f1 = f > 1.0E-6F ? p_8160_ : 0.0F;
      float f2 = Math.abs(p_8159_) + Math.max(f, 1.0E-6F);
      if (p_8159_ < 0.0F) {
         float f3 = f1;
         f1 = f2;
         f2 = f3;
      }

      float f4 = Mth.fastInvSqrt(f2 * f2 + f1 * f1);
      f2 *= f4;
      f1 *= f4;
      return Pair.of(f1, f2);
   }

   private static Quaternion stepJacobi(Matrix3f p_8182_) {
      Matrix3f matrix3f = new Matrix3f();
      Quaternion quaternion = Quaternion.ONE.copy();
      if (p_8182_.m01 * p_8182_.m01 + p_8182_.m10 * p_8182_.m10 > 1.0E-6F) {
         Pair<Float, Float> pair = approxGivensQuat(p_8182_.m00, 0.5F * (p_8182_.m01 + p_8182_.m10), p_8182_.m11);
         Float f = pair.getFirst();
         Float f1 = pair.getSecond();
         Quaternion quaternion1 = new Quaternion(0.0F, 0.0F, f, f1);
         float f2 = f1 * f1 - f * f;
         float f3 = -2.0F * f * f1;
         float f4 = f1 * f1 + f * f;
         quaternion.mul(quaternion1);
         matrix3f.setIdentity();
         matrix3f.m00 = f2;
         matrix3f.m11 = f2;
         matrix3f.m10 = -f3;
         matrix3f.m01 = f3;
         matrix3f.m22 = f4;
         p_8182_.mul(matrix3f);
         matrix3f.transpose();
         matrix3f.mul(p_8182_);
         p_8182_.load(matrix3f);
      }

      if (p_8182_.m02 * p_8182_.m02 + p_8182_.m20 * p_8182_.m20 > 1.0E-6F) {
         Pair<Float, Float> pair1 = approxGivensQuat(p_8182_.m00, 0.5F * (p_8182_.m02 + p_8182_.m20), p_8182_.m22);
         float f5 = -pair1.getFirst();
         Float f7 = pair1.getSecond();
         Quaternion quaternion2 = new Quaternion(0.0F, f5, 0.0F, f7);
         float f9 = f7 * f7 - f5 * f5;
         float f11 = -2.0F * f5 * f7;
         float f13 = f7 * f7 + f5 * f5;
         quaternion.mul(quaternion2);
         matrix3f.setIdentity();
         matrix3f.m00 = f9;
         matrix3f.m22 = f9;
         matrix3f.m20 = f11;
         matrix3f.m02 = -f11;
         matrix3f.m11 = f13;
         p_8182_.mul(matrix3f);
         matrix3f.transpose();
         matrix3f.mul(p_8182_);
         p_8182_.load(matrix3f);
      }

      if (p_8182_.m12 * p_8182_.m12 + p_8182_.m21 * p_8182_.m21 > 1.0E-6F) {
         Pair<Float, Float> pair2 = approxGivensQuat(p_8182_.m11, 0.5F * (p_8182_.m12 + p_8182_.m21), p_8182_.m22);
         Float f6 = pair2.getFirst();
         Float f8 = pair2.getSecond();
         Quaternion quaternion3 = new Quaternion(f6, 0.0F, 0.0F, f8);
         float f10 = f8 * f8 - f6 * f6;
         float f12 = -2.0F * f6 * f8;
         float f14 = f8 * f8 + f6 * f6;
         quaternion.mul(quaternion3);
         matrix3f.setIdentity();
         matrix3f.m11 = f10;
         matrix3f.m22 = f10;
         matrix3f.m21 = -f12;
         matrix3f.m12 = f12;
         matrix3f.m00 = f14;
         p_8182_.mul(matrix3f);
         matrix3f.transpose();
         matrix3f.mul(p_8182_);
         p_8182_.load(matrix3f);
      }

      return quaternion;
   }

   private static void sortSingularValues(Matrix3f p_152766_, Quaternion p_152767_) {
      float f1 = p_152766_.m00 * p_152766_.m00 + p_152766_.m10 * p_152766_.m10 + p_152766_.m20 * p_152766_.m20;
      float f2 = p_152766_.m01 * p_152766_.m01 + p_152766_.m11 * p_152766_.m11 + p_152766_.m21 * p_152766_.m21;
      float f3 = p_152766_.m02 * p_152766_.m02 + p_152766_.m12 * p_152766_.m12 + p_152766_.m22 * p_152766_.m22;
      if (f1 < f2) {
         float f = p_152766_.m10;
         p_152766_.m10 = -p_152766_.m00;
         p_152766_.m00 = f;
         f = p_152766_.m11;
         p_152766_.m11 = -p_152766_.m01;
         p_152766_.m01 = f;
         f = p_152766_.m12;
         p_152766_.m12 = -p_152766_.m02;
         p_152766_.m02 = f;
         Quaternion quaternion = new Quaternion(0.0F, 0.0F, SQ2, SQ2);
         p_152767_.mul(quaternion);
         f = f1;
         f1 = f2;
         f2 = f;
      }

      if (f1 < f3) {
         float f4 = p_152766_.m20;
         p_152766_.m20 = -p_152766_.m00;
         p_152766_.m00 = f4;
         f4 = p_152766_.m21;
         p_152766_.m21 = -p_152766_.m01;
         p_152766_.m01 = f4;
         f4 = p_152766_.m22;
         p_152766_.m22 = -p_152766_.m02;
         p_152766_.m02 = f4;
         Quaternion quaternion1 = new Quaternion(0.0F, SQ2, 0.0F, SQ2);
         p_152767_.mul(quaternion1);
         f3 = f1;
      }

      if (f2 < f3) {
         float f5 = p_152766_.m20;
         p_152766_.m20 = -p_152766_.m10;
         p_152766_.m10 = f5;
         f5 = p_152766_.m21;
         p_152766_.m21 = -p_152766_.m11;
         p_152766_.m11 = f5;
         f5 = p_152766_.m22;
         p_152766_.m22 = -p_152766_.m12;
         p_152766_.m12 = f5;
         Quaternion quaternion2 = new Quaternion(SQ2, 0.0F, 0.0F, SQ2);
         p_152767_.mul(quaternion2);
      }

   }

   public void transpose() {
      float f = this.m01;
      this.m01 = this.m10;
      this.m10 = f;
      f = this.m02;
      this.m02 = this.m20;
      this.m20 = f;
      f = this.m12;
      this.m12 = this.m21;
      this.m21 = f;
   }

   public Triple<Quaternion, Vector3f, Quaternion> svdDecompose() {
      Quaternion quaternion = Quaternion.ONE.copy();
      Quaternion quaternion1 = Quaternion.ONE.copy();
      Matrix3f matrix3f = this.copy();
      matrix3f.transpose();
      matrix3f.mul(this);

      for(int i = 0; i < 5; ++i) {
         quaternion1.mul(stepJacobi(matrix3f));
      }

      quaternion1.normalize();
      Matrix3f matrix3f4 = new Matrix3f(this);
      matrix3f4.mul(new Matrix3f(quaternion1));
      float f = 1.0F;
      Pair<Float, Float> pair = qrGivensQuat(matrix3f4.m00, matrix3f4.m10);
      Float f1 = pair.getFirst();
      Float f2 = pair.getSecond();
      float f3 = f2 * f2 - f1 * f1;
      float f4 = -2.0F * f1 * f2;
      float f5 = f2 * f2 + f1 * f1;
      Quaternion quaternion2 = new Quaternion(0.0F, 0.0F, f1, f2);
      quaternion.mul(quaternion2);
      Matrix3f matrix3f1 = new Matrix3f();
      matrix3f1.setIdentity();
      matrix3f1.m00 = f3;
      matrix3f1.m11 = f3;
      matrix3f1.m10 = f4;
      matrix3f1.m01 = -f4;
      matrix3f1.m22 = f5;
      f *= f5;
      matrix3f1.mul(matrix3f4);
      pair = qrGivensQuat(matrix3f1.m00, matrix3f1.m20);
      float f6 = -pair.getFirst();
      Float f7 = pair.getSecond();
      float f8 = f7 * f7 - f6 * f6;
      float f9 = -2.0F * f6 * f7;
      float f10 = f7 * f7 + f6 * f6;
      Quaternion quaternion3 = new Quaternion(0.0F, f6, 0.0F, f7);
      quaternion.mul(quaternion3);
      Matrix3f matrix3f2 = new Matrix3f();
      matrix3f2.setIdentity();
      matrix3f2.m00 = f8;
      matrix3f2.m22 = f8;
      matrix3f2.m20 = -f9;
      matrix3f2.m02 = f9;
      matrix3f2.m11 = f10;
      f *= f10;
      matrix3f2.mul(matrix3f1);
      pair = qrGivensQuat(matrix3f2.m11, matrix3f2.m21);
      Float f11 = pair.getFirst();
      Float f12 = pair.getSecond();
      float f13 = f12 * f12 - f11 * f11;
      float f14 = -2.0F * f11 * f12;
      float f15 = f12 * f12 + f11 * f11;
      Quaternion quaternion4 = new Quaternion(f11, 0.0F, 0.0F, f12);
      quaternion.mul(quaternion4);
      Matrix3f matrix3f3 = new Matrix3f();
      matrix3f3.setIdentity();
      matrix3f3.m11 = f13;
      matrix3f3.m22 = f13;
      matrix3f3.m21 = f14;
      matrix3f3.m12 = -f14;
      matrix3f3.m00 = f15;
      f *= f15;
      matrix3f3.mul(matrix3f2);
      f = 1.0F / f;
      quaternion.mul((float)Math.sqrt((double)f));
      Vector3f vector3f = new Vector3f(matrix3f3.m00 * f, matrix3f3.m11 * f, matrix3f3.m22 * f);
      return Triple.of(quaternion, vector3f, quaternion1);
   }

   public boolean equals(Object p_8186_) {
      if (this == p_8186_) {
         return true;
      } else if (p_8186_ != null && this.getClass() == p_8186_.getClass()) {
         Matrix3f matrix3f = (Matrix3f)p_8186_;
         return Float.compare(matrix3f.m00, this.m00) == 0 && Float.compare(matrix3f.m01, this.m01) == 0 && Float.compare(matrix3f.m02, this.m02) == 0 && Float.compare(matrix3f.m10, this.m10) == 0 && Float.compare(matrix3f.m11, this.m11) == 0 && Float.compare(matrix3f.m12, this.m12) == 0 && Float.compare(matrix3f.m20, this.m20) == 0 && Float.compare(matrix3f.m21, this.m21) == 0 && Float.compare(matrix3f.m22, this.m22) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.m00 != 0.0F ? Float.floatToIntBits(this.m00) : 0;
      i = 31 * i + (this.m01 != 0.0F ? Float.floatToIntBits(this.m01) : 0);
      i = 31 * i + (this.m02 != 0.0F ? Float.floatToIntBits(this.m02) : 0);
      i = 31 * i + (this.m10 != 0.0F ? Float.floatToIntBits(this.m10) : 0);
      i = 31 * i + (this.m11 != 0.0F ? Float.floatToIntBits(this.m11) : 0);
      i = 31 * i + (this.m12 != 0.0F ? Float.floatToIntBits(this.m12) : 0);
      i = 31 * i + (this.m20 != 0.0F ? Float.floatToIntBits(this.m20) : 0);
      i = 31 * i + (this.m21 != 0.0F ? Float.floatToIntBits(this.m21) : 0);
      return 31 * i + (this.m22 != 0.0F ? Float.floatToIntBits(this.m22) : 0);
   }

   private static int bufferIndex(int p_152763_, int p_152764_) {
      return p_152764_ * 3 + p_152763_;
   }

   public void load(FloatBuffer p_152769_) {
      this.m00 = p_152769_.get(bufferIndex(0, 0));
      this.m01 = p_152769_.get(bufferIndex(0, 1));
      this.m02 = p_152769_.get(bufferIndex(0, 2));
      this.m10 = p_152769_.get(bufferIndex(1, 0));
      this.m11 = p_152769_.get(bufferIndex(1, 1));
      this.m12 = p_152769_.get(bufferIndex(1, 2));
      this.m20 = p_152769_.get(bufferIndex(2, 0));
      this.m21 = p_152769_.get(bufferIndex(2, 1));
      this.m22 = p_152769_.get(bufferIndex(2, 2));
   }

   public void loadTransposed(FloatBuffer p_152774_) {
      this.m00 = p_152774_.get(bufferIndex(0, 0));
      this.m01 = p_152774_.get(bufferIndex(1, 0));
      this.m02 = p_152774_.get(bufferIndex(2, 0));
      this.m10 = p_152774_.get(bufferIndex(0, 1));
      this.m11 = p_152774_.get(bufferIndex(1, 1));
      this.m12 = p_152774_.get(bufferIndex(2, 1));
      this.m20 = p_152774_.get(bufferIndex(0, 2));
      this.m21 = p_152774_.get(bufferIndex(1, 2));
      this.m22 = p_152774_.get(bufferIndex(2, 2));
   }

   public void load(FloatBuffer p_152771_, boolean p_152772_) {
      if (p_152772_) {
         this.loadTransposed(p_152771_);
      } else {
         this.load(p_152771_);
      }

   }

   public void load(Matrix3f p_8170_) {
      this.m00 = p_8170_.m00;
      this.m01 = p_8170_.m01;
      this.m02 = p_8170_.m02;
      this.m10 = p_8170_.m10;
      this.m11 = p_8170_.m11;
      this.m12 = p_8170_.m12;
      this.m20 = p_8170_.m20;
      this.m21 = p_8170_.m21;
      this.m22 = p_8170_.m22;
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("Matrix3f:\n");
      stringbuilder.append(this.m00);
      stringbuilder.append(" ");
      stringbuilder.append(this.m01);
      stringbuilder.append(" ");
      stringbuilder.append(this.m02);
      stringbuilder.append("\n");
      stringbuilder.append(this.m10);
      stringbuilder.append(" ");
      stringbuilder.append(this.m11);
      stringbuilder.append(" ");
      stringbuilder.append(this.m12);
      stringbuilder.append("\n");
      stringbuilder.append(this.m20);
      stringbuilder.append(" ");
      stringbuilder.append(this.m21);
      stringbuilder.append(" ");
      stringbuilder.append(this.m22);
      stringbuilder.append("\n");
      return stringbuilder.toString();
   }

   public void store(FloatBuffer p_152781_) {
      p_152781_.put(bufferIndex(0, 0), this.m00);
      p_152781_.put(bufferIndex(0, 1), this.m01);
      p_152781_.put(bufferIndex(0, 2), this.m02);
      p_152781_.put(bufferIndex(1, 0), this.m10);
      p_152781_.put(bufferIndex(1, 1), this.m11);
      p_152781_.put(bufferIndex(1, 2), this.m12);
      p_152781_.put(bufferIndex(2, 0), this.m20);
      p_152781_.put(bufferIndex(2, 1), this.m21);
      p_152781_.put(bufferIndex(2, 2), this.m22);
   }

   public void storeTransposed(FloatBuffer p_152785_) {
      p_152785_.put(bufferIndex(0, 0), this.m00);
      p_152785_.put(bufferIndex(1, 0), this.m01);
      p_152785_.put(bufferIndex(2, 0), this.m02);
      p_152785_.put(bufferIndex(0, 1), this.m10);
      p_152785_.put(bufferIndex(1, 1), this.m11);
      p_152785_.put(bufferIndex(2, 1), this.m12);
      p_152785_.put(bufferIndex(0, 2), this.m20);
      p_152785_.put(bufferIndex(1, 2), this.m21);
      p_152785_.put(bufferIndex(2, 2), this.m22);
   }

   public void store(FloatBuffer p_152776_, boolean p_152777_) {
      if (p_152777_) {
         this.storeTransposed(p_152776_);
      } else {
         this.store(p_152776_);
      }

   }

   public void setIdentity() {
      this.m00 = 1.0F;
      this.m01 = 0.0F;
      this.m02 = 0.0F;
      this.m10 = 0.0F;
      this.m11 = 1.0F;
      this.m12 = 0.0F;
      this.m20 = 0.0F;
      this.m21 = 0.0F;
      this.m22 = 1.0F;
   }

   public float adjugateAndDet() {
      float f = this.m11 * this.m22 - this.m12 * this.m21;
      float f1 = -(this.m10 * this.m22 - this.m12 * this.m20);
      float f2 = this.m10 * this.m21 - this.m11 * this.m20;
      float f3 = -(this.m01 * this.m22 - this.m02 * this.m21);
      float f4 = this.m00 * this.m22 - this.m02 * this.m20;
      float f5 = -(this.m00 * this.m21 - this.m01 * this.m20);
      float f6 = this.m01 * this.m12 - this.m02 * this.m11;
      float f7 = -(this.m00 * this.m12 - this.m02 * this.m10);
      float f8 = this.m00 * this.m11 - this.m01 * this.m10;
      float f9 = this.m00 * f + this.m01 * f1 + this.m02 * f2;
      this.m00 = f;
      this.m10 = f1;
      this.m20 = f2;
      this.m01 = f3;
      this.m11 = f4;
      this.m21 = f5;
      this.m02 = f6;
      this.m12 = f7;
      this.m22 = f8;
      return f9;
   }

   public float determinant() {
      float f = this.m11 * this.m22 - this.m12 * this.m21;
      float f1 = -(this.m10 * this.m22 - this.m12 * this.m20);
      float f2 = this.m10 * this.m21 - this.m11 * this.m20;
      return this.m00 * f + this.m01 * f1 + this.m02 * f2;
   }

   public boolean invert() {
      float f = this.adjugateAndDet();
      if (Math.abs(f) > 1.0E-6F) {
         this.mul(f);
         return true;
      } else {
         return false;
      }
   }

   public void set(int p_8166_, int p_8167_, float p_8168_) {
      if (p_8166_ == 0) {
         if (p_8167_ == 0) {
            this.m00 = p_8168_;
         } else if (p_8167_ == 1) {
            this.m01 = p_8168_;
         } else {
            this.m02 = p_8168_;
         }
      } else if (p_8166_ == 1) {
         if (p_8167_ == 0) {
            this.m10 = p_8168_;
         } else if (p_8167_ == 1) {
            this.m11 = p_8168_;
         } else {
            this.m12 = p_8168_;
         }
      } else if (p_8167_ == 0) {
         this.m20 = p_8168_;
      } else if (p_8167_ == 1) {
         this.m21 = p_8168_;
      } else {
         this.m22 = p_8168_;
      }

   }

   public void mul(Matrix3f p_8179_) {
      float f = this.m00 * p_8179_.m00 + this.m01 * p_8179_.m10 + this.m02 * p_8179_.m20;
      float f1 = this.m00 * p_8179_.m01 + this.m01 * p_8179_.m11 + this.m02 * p_8179_.m21;
      float f2 = this.m00 * p_8179_.m02 + this.m01 * p_8179_.m12 + this.m02 * p_8179_.m22;
      float f3 = this.m10 * p_8179_.m00 + this.m11 * p_8179_.m10 + this.m12 * p_8179_.m20;
      float f4 = this.m10 * p_8179_.m01 + this.m11 * p_8179_.m11 + this.m12 * p_8179_.m21;
      float f5 = this.m10 * p_8179_.m02 + this.m11 * p_8179_.m12 + this.m12 * p_8179_.m22;
      float f6 = this.m20 * p_8179_.m00 + this.m21 * p_8179_.m10 + this.m22 * p_8179_.m20;
      float f7 = this.m20 * p_8179_.m01 + this.m21 * p_8179_.m11 + this.m22 * p_8179_.m21;
      float f8 = this.m20 * p_8179_.m02 + this.m21 * p_8179_.m12 + this.m22 * p_8179_.m22;
      this.m00 = f;
      this.m01 = f1;
      this.m02 = f2;
      this.m10 = f3;
      this.m11 = f4;
      this.m12 = f5;
      this.m20 = f6;
      this.m21 = f7;
      this.m22 = f8;
   }

   public void mul(Quaternion p_8172_) {
      this.mul(new Matrix3f(p_8172_));
   }

   public void mul(float p_8157_) {
      this.m00 *= p_8157_;
      this.m01 *= p_8157_;
      this.m02 *= p_8157_;
      this.m10 *= p_8157_;
      this.m11 *= p_8157_;
      this.m12 *= p_8157_;
      this.m20 *= p_8157_;
      this.m21 *= p_8157_;
      this.m22 *= p_8157_;
   }

   public void add(Matrix3f p_152779_) {
      this.m00 += p_152779_.m00;
      this.m01 += p_152779_.m01;
      this.m02 += p_152779_.m02;
      this.m10 += p_152779_.m10;
      this.m11 += p_152779_.m11;
      this.m12 += p_152779_.m12;
      this.m20 += p_152779_.m20;
      this.m21 += p_152779_.m21;
      this.m22 += p_152779_.m22;
   }

   public void sub(Matrix3f p_152783_) {
      this.m00 -= p_152783_.m00;
      this.m01 -= p_152783_.m01;
      this.m02 -= p_152783_.m02;
      this.m10 -= p_152783_.m10;
      this.m11 -= p_152783_.m11;
      this.m12 -= p_152783_.m12;
      this.m20 -= p_152783_.m20;
      this.m21 -= p_152783_.m21;
      this.m22 -= p_152783_.m22;
   }

   public float trace() {
      return this.m00 + this.m11 + this.m22;
   }

   public Matrix3f copy() {
      return new Matrix3f(this);
   }

    public void multiplyBackward(Matrix3f other) {
        Matrix3f copy = other.copy();
        copy.mul(this);
        this.load(copy);
    }
}
