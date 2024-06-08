package com.mojang.math;

public class Vector3d {
   public double x;
   public double y;
   public double z;

   public Vector3d(double p_86218_, double p_86219_, double p_86220_) {
      this.x = p_86218_;
      this.y = p_86219_;
      this.z = p_86220_;
   }

   public void set(Vector3d p_176290_) {
      this.x = p_176290_.x;
      this.y = p_176290_.y;
      this.z = p_176290_.z;
   }

   public void set(double p_176286_, double p_176287_, double p_176288_) {
      this.x = p_176286_;
      this.y = p_176287_;
      this.z = p_176288_;
   }

   public void scale(double p_176284_) {
      this.x *= p_176284_;
      this.y *= p_176284_;
      this.z *= p_176284_;
   }

   public void add(Vector3d p_176292_) {
      this.x += p_176292_.x;
      this.y += p_176292_.y;
      this.z += p_176292_.z;
   }
}