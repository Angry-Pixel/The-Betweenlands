package net.minecraft.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class FlyingMob extends Mob {
   protected FlyingMob(EntityType<? extends FlyingMob> p_20806_, Level p_20807_) {
      super(p_20806_, p_20807_);
   }

   public boolean causeFallDamage(float p_147105_, float p_147106_, DamageSource p_147107_) {
      return false;
   }

   protected void checkFallDamage(double p_20809_, boolean p_20810_, BlockState p_20811_, BlockPos p_20812_) {
   }

   public void travel(Vec3 p_20818_) {
      if (this.isInWater()) {
         this.moveRelative(0.02F, p_20818_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale((double)0.8F));
      } else if (this.isInLava()) {
         this.moveRelative(0.02F, p_20818_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
      } else {
         BlockPos ground = new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
         float f = 0.91F;
         if (this.onGround) {
            f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
         }

         float f1 = 0.16277137F / (f * f * f);
         f = 0.91F;
         if (this.onGround) {
            f = this.level.getBlockState(ground).getFriction(this.level, ground, this) * 0.91F;
         }

         this.moveRelative(this.onGround ? 0.1F * f1 : 0.02F, p_20818_);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale((double)f));
      }

      this.calculateEntityAnimation(this, false);
   }

   public boolean onClimbable() {
      return false;
   }
}
