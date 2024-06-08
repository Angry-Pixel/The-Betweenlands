package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public class EntityHitResult extends HitResult {
   private final Entity entity;

   public EntityHitResult(Entity p_82439_) {
      this(p_82439_, p_82439_.position());
   }

   public EntityHitResult(Entity p_82441_, Vec3 p_82442_) {
      super(p_82442_);
      this.entity = p_82441_;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public HitResult.Type getType() {
      return HitResult.Type.ENTITY;
   }
}