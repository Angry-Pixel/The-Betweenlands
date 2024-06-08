package com.mojang.math;

import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.apache.commons.lang3.tuple.Triple;

public final class Transformation implements net.minecraftforge.client.extensions.IForgeTransformation {
   private final Matrix4f matrix;
   private boolean decomposed;
   @Nullable
   private Vector3f translation;
   @Nullable
   private Quaternion leftRotation;
   @Nullable
   private Vector3f scale;
   @Nullable
   private Quaternion rightRotation;
   private static final Transformation IDENTITY = Util.make(() -> {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      Transformation transformation = new Transformation(matrix4f);
      transformation.getLeftRotation();
      return transformation;
   });

   public Transformation(@Nullable Matrix4f p_121087_) {
      if (p_121087_ == null) {
         this.matrix = IDENTITY.matrix;
      } else {
         this.matrix = p_121087_;
      }

   }

   public Transformation(@Nullable Vector3f p_121089_, @Nullable Quaternion p_121090_, @Nullable Vector3f p_121091_, @Nullable Quaternion p_121092_) {
      this.matrix = compose(p_121089_, p_121090_, p_121091_, p_121092_);
      this.translation = p_121089_ != null ? p_121089_ : new Vector3f();
      this.leftRotation = p_121090_ != null ? p_121090_ : Quaternion.ONE.copy();
      this.scale = p_121091_ != null ? p_121091_ : new Vector3f(1.0F, 1.0F, 1.0F);
      this.rightRotation = p_121092_ != null ? p_121092_ : Quaternion.ONE.copy();
      this.decomposed = true;
   }

   public static Transformation identity() {
      return IDENTITY;
   }

   public Transformation compose(Transformation p_121097_) {
      Matrix4f matrix4f = this.getMatrix();
      matrix4f.multiply(p_121097_.getMatrix());
      return new Transformation(matrix4f);
   }

   @Nullable
   public Transformation inverse() {
      if (this == IDENTITY) {
         return this;
      } else {
         Matrix4f matrix4f = this.getMatrix();
         return matrix4f.invert() ? new Transformation(matrix4f) : null;
      }
   }

   private void ensureDecomposed() {
      if (!this.decomposed) {
         Pair<Matrix3f, Vector3f> pair = toAffine(this.matrix);
         Triple<Quaternion, Vector3f, Quaternion> triple = pair.getFirst().svdDecompose();
         this.translation = pair.getSecond();
         this.leftRotation = triple.getLeft();
         this.scale = triple.getMiddle();
         this.rightRotation = triple.getRight();
         this.decomposed = true;
      }

   }

   private static Matrix4f compose(@Nullable Vector3f p_121099_, @Nullable Quaternion p_121100_, @Nullable Vector3f p_121101_, @Nullable Quaternion p_121102_) {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      if (p_121100_ != null) {
         matrix4f.multiply(new Matrix4f(p_121100_));
      }

      if (p_121101_ != null) {
         matrix4f.multiply(Matrix4f.createScaleMatrix(p_121101_.x(), p_121101_.y(), p_121101_.z()));
      }

      if (p_121102_ != null) {
         matrix4f.multiply(new Matrix4f(p_121102_));
      }

      if (p_121099_ != null) {
         matrix4f.m03 = p_121099_.x();
         matrix4f.m13 = p_121099_.y();
         matrix4f.m23 = p_121099_.z();
      }

      return matrix4f;
   }

   public static Pair<Matrix3f, Vector3f> toAffine(Matrix4f p_121095_) {
      p_121095_.multiply(1.0F / p_121095_.m33);
      Vector3f vector3f = new Vector3f(p_121095_.m03, p_121095_.m13, p_121095_.m23);
      Matrix3f matrix3f = new Matrix3f(p_121095_);
      return Pair.of(matrix3f, vector3f);
   }

   public Matrix4f getMatrix() {
      return this.matrix.copy();
   }

   public Vector3f getTranslation() {
      this.ensureDecomposed();
      return this.translation.copy();
   }

   public Quaternion getLeftRotation() {
      this.ensureDecomposed();
      return this.leftRotation.copy();
   }

   public Vector3f getScale() {
      this.ensureDecomposed();
      return this.scale.copy();
   }

   public Quaternion getRightRotation() {
      this.ensureDecomposed();
      return this.rightRotation.copy();
   }

   public boolean equals(Object p_121108_) {
      if (this == p_121108_) {
         return true;
      } else if (p_121108_ != null && this.getClass() == p_121108_.getClass()) {
         Transformation transformation = (Transformation)p_121108_;
         return Objects.equals(this.matrix, transformation.matrix);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.matrix);
   }

    private Matrix3f normalTransform = null;
    public Matrix3f getNormalMatrix() {
        checkNormalTransform();
        return normalTransform;
    }
    private void checkNormalTransform() {
        if (normalTransform == null) {
            normalTransform = new Matrix3f(matrix);
            normalTransform.invert();
            normalTransform.transpose();
        }
    }

   public Transformation slerp(Transformation p_175938_, float p_175939_) {
      Vector3f vector3f = this.getTranslation();
      Quaternion quaternion = this.getLeftRotation();
      Vector3f vector3f1 = this.getScale();
      Quaternion quaternion1 = this.getRightRotation();
      vector3f.lerp(p_175938_.getTranslation(), p_175939_);
      quaternion.slerp(p_175938_.getLeftRotation(), p_175939_);
      vector3f1.lerp(p_175938_.getScale(), p_175939_);
      quaternion1.slerp(p_175938_.getRightRotation(), p_175939_);
      return new Transformation(vector3f, quaternion, vector3f1, quaternion1);
   }
}
