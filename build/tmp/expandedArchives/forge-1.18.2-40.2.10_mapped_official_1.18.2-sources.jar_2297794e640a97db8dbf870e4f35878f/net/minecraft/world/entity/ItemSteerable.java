package net.minecraft.world.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface ItemSteerable {
   boolean boost();

   void travelWithInput(Vec3 p_20858_);

   float getSteeringSpeed();

   default boolean travel(Mob p_20855_, ItemBasedSteering p_20856_, Vec3 p_20857_) {
      if (!p_20855_.isAlive()) {
         return false;
      } else {
         Entity entity = p_20855_.getFirstPassenger();
         if (p_20855_.isVehicle() && p_20855_.canBeControlledByRider() && entity instanceof Player) {
            p_20855_.setYRot(entity.getYRot());
            p_20855_.yRotO = p_20855_.getYRot();
            p_20855_.setXRot(entity.getXRot() * 0.5F);
            p_20855_.setRot(p_20855_.getYRot(), p_20855_.getXRot());
            p_20855_.yBodyRot = p_20855_.getYRot();
            p_20855_.yHeadRot = p_20855_.getYRot();
            p_20855_.maxUpStep = 1.0F;
            p_20855_.flyingSpeed = p_20855_.getSpeed() * 0.1F;
            if (p_20856_.boosting && p_20856_.boostTime++ > p_20856_.boostTimeTotal) {
               p_20856_.boosting = false;
            }

            if (p_20855_.isControlledByLocalInstance()) {
               float f = this.getSteeringSpeed();
               if (p_20856_.boosting) {
                  f += f * 1.15F * Mth.sin((float)p_20856_.boostTime / (float)p_20856_.boostTimeTotal * (float)Math.PI);
               }

               p_20855_.setSpeed(f);
               this.travelWithInput(new Vec3(0.0D, 0.0D, 1.0D));
               p_20855_.lerpSteps = 0;
            } else {
               p_20855_.calculateEntityAnimation(p_20855_, false);
               p_20855_.setDeltaMovement(Vec3.ZERO);
            }

            p_20855_.tryCheckInsideBlocks();
            return true;
         } else {
            p_20855_.maxUpStep = 0.5F;
            p_20855_.flyingSpeed = 0.02F;
            this.travelWithInput(p_20857_);
            return false;
         }
      }
   }
}