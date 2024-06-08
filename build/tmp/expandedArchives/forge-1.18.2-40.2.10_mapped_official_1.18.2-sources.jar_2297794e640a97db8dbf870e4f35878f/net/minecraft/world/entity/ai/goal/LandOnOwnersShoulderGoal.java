package net.minecraft.world.entity.ai.goal;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;

public class LandOnOwnersShoulderGoal extends Goal {
   private final ShoulderRidingEntity entity;
   private ServerPlayer owner;
   private boolean isSittingOnShoulder;

   public LandOnOwnersShoulderGoal(ShoulderRidingEntity p_25483_) {
      this.entity = p_25483_;
   }

   public boolean canUse() {
      ServerPlayer serverplayer = (ServerPlayer)this.entity.getOwner();
      boolean flag = serverplayer != null && !serverplayer.isSpectator() && !serverplayer.getAbilities().flying && !serverplayer.isInWater() && !serverplayer.isInPowderSnow;
      return !this.entity.isOrderedToSit() && flag && this.entity.canSitOnShoulder();
   }

   public boolean isInterruptable() {
      return !this.isSittingOnShoulder;
   }

   public void start() {
      this.owner = (ServerPlayer)this.entity.getOwner();
      this.isSittingOnShoulder = false;
   }

   public void tick() {
      if (!this.isSittingOnShoulder && !this.entity.isInSittingPose() && !this.entity.isLeashed()) {
         if (this.entity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
            this.isSittingOnShoulder = this.entity.setEntityOnShoulder(this.owner);
         }

      }
   }
}