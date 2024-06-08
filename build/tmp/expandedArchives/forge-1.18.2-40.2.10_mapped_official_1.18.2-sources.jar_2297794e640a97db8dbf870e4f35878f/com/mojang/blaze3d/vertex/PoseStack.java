package com.mojang.blaze3d.vertex;

import com.google.common.collect.Queues;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import java.util.Deque;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PoseStack {
   private final Deque<PoseStack.Pose> poseStack = Util.make(Queues.newArrayDeque(), (p_85848_) -> {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      Matrix3f matrix3f = new Matrix3f();
      matrix3f.setIdentity();
      p_85848_.add(new PoseStack.Pose(matrix4f, matrix3f));
   });

   public void translate(double p_85838_, double p_85839_, double p_85840_) {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      posestack$pose.pose.multiplyWithTranslation((float)p_85838_, (float)p_85839_, (float)p_85840_);
   }

   public void scale(float p_85842_, float p_85843_, float p_85844_) {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      posestack$pose.pose.multiply(Matrix4f.createScaleMatrix(p_85842_, p_85843_, p_85844_));
      if (p_85842_ == p_85843_ && p_85843_ == p_85844_) {
         if (p_85842_ > 0.0F) {
            return;
         }

         posestack$pose.normal.mul(-1.0F);
      }

      float f = 1.0F / p_85842_;
      float f1 = 1.0F / p_85843_;
      float f2 = 1.0F / p_85844_;
      float f3 = Mth.fastInvCubeRoot(f * f1 * f2);
      posestack$pose.normal.mul(Matrix3f.createScaleMatrix(f3 * f, f3 * f1, f3 * f2));
   }

   public void mulPose(Quaternion p_85846_) {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      posestack$pose.pose.multiply(p_85846_);
      posestack$pose.normal.mul(p_85846_);
   }

   public void pushPose() {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      this.poseStack.addLast(new PoseStack.Pose(posestack$pose.pose.copy(), posestack$pose.normal.copy()));
   }

   public void popPose() {
      this.poseStack.removeLast();
   }

   public PoseStack.Pose last() {
      return this.poseStack.getLast();
   }

   public boolean clear() {
      return this.poseStack.size() == 1;
   }

   public void setIdentity() {
      PoseStack.Pose posestack$pose = this.poseStack.getLast();
      posestack$pose.pose.setIdentity();
      posestack$pose.normal.setIdentity();
   }

   public void mulPoseMatrix(Matrix4f p_166855_) {
      (this.poseStack.getLast()).pose.multiply(p_166855_);
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Pose {
      final Matrix4f pose;
      final Matrix3f normal;

      Pose(Matrix4f p_85855_, Matrix3f p_85856_) {
         this.pose = p_85855_;
         this.normal = p_85856_;
      }

      public Matrix4f pose() {
         return this.pose;
      }

      public Matrix3f normal() {
         return this.normal;
      }
   }
}