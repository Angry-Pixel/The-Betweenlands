package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FlyingMoveControl extends MoveControl {
   private final int maxTurn;
   private final boolean hoversInPlace;

   public FlyingMoveControl(Mob p_24893_, int p_24894_, boolean p_24895_) {
      super(p_24893_);
      this.maxTurn = p_24894_;
      this.hoversInPlace = p_24895_;
   }

   public void tick() {
      if (this.operation == MoveControl.Operation.MOVE_TO) {
         this.operation = MoveControl.Operation.WAIT;
         this.mob.setNoGravity(true);
         double d0 = this.wantedX - this.mob.getX();
         double d1 = this.wantedY - this.mob.getY();
         double d2 = this.wantedZ - this.mob.getZ();
         double d3 = d0 * d0 + d1 * d1 + d2 * d2;
         if (d3 < (double)2.5000003E-7F) {
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
            return;
         }

         float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
         this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));
         float f1;
         if (this.mob.isOnGround()) {
            f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
         } else {
            f1 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
         }

         this.mob.setSpeed(f1);
         double d4 = Math.sqrt(d0 * d0 + d2 * d2);
         if (Math.abs(d1) > (double)1.0E-5F || Math.abs(d4) > (double)1.0E-5F) {
            float f2 = (float)(-(Mth.atan2(d1, d4) * (double)(180F / (float)Math.PI)));
            this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, (float)this.maxTurn));
            this.mob.setYya(d1 > 0.0D ? f1 : -f1);
         }
      } else {
         if (!this.hoversInPlace) {
            this.mob.setNoGravity(false);
         }

         this.mob.setYya(0.0F);
         this.mob.setZza(0.0F);
      }

   }
}