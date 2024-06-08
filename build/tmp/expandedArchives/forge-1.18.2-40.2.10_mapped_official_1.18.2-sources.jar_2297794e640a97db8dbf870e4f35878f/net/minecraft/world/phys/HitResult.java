package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public abstract class HitResult {
   protected final Vec3 location;

   protected HitResult(Vec3 p_82447_) {
      this.location = p_82447_;
   }

   public double distanceTo(Entity p_82449_) {
      double d0 = this.location.x - p_82449_.getX();
      double d1 = this.location.y - p_82449_.getY();
      double d2 = this.location.z - p_82449_.getZ();
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public abstract HitResult.Type getType();

   public Vec3 getLocation() {
      return this.location;
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;
   }
}