package net.minecraft.world.entity.ai.control;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoveControl implements Control {
   public static final float MIN_SPEED = 5.0E-4F;
   public static final float MIN_SPEED_SQR = 2.5000003E-7F;
   protected static final int MAX_TURN = 90;
   protected final Mob mob;
   protected double wantedX;
   protected double wantedY;
   protected double wantedZ;
   protected double speedModifier;
   protected float strafeForwards;
   protected float strafeRight;
   protected MoveControl.Operation operation = MoveControl.Operation.WAIT;

   public MoveControl(Mob p_24983_) {
      this.mob = p_24983_;
   }

   public boolean hasWanted() {
      return this.operation == MoveControl.Operation.MOVE_TO;
   }

   public double getSpeedModifier() {
      return this.speedModifier;
   }

   public void setWantedPosition(double p_24984_, double p_24985_, double p_24986_, double p_24987_) {
      this.wantedX = p_24984_;
      this.wantedY = p_24985_;
      this.wantedZ = p_24986_;
      this.speedModifier = p_24987_;
      if (this.operation != MoveControl.Operation.JUMPING) {
         this.operation = MoveControl.Operation.MOVE_TO;
      }

   }

   public void strafe(float p_24989_, float p_24990_) {
      this.operation = MoveControl.Operation.STRAFE;
      this.strafeForwards = p_24989_;
      this.strafeRight = p_24990_;
      this.speedModifier = 0.25D;
   }

   public void tick() {
      if (this.operation == MoveControl.Operation.STRAFE) {
         float f = (float)this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
         float f1 = (float)this.speedModifier * f;
         float f2 = this.strafeForwards;
         float f3 = this.strafeRight;
         float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
         if (f4 < 1.0F) {
            f4 = 1.0F;
         }

         f4 = f1 / f4;
         f2 *= f4;
         f3 *= f4;
         float f5 = Mth.sin(this.mob.getYRot() * ((float)Math.PI / 180F));
         float f6 = Mth.cos(this.mob.getYRot() * ((float)Math.PI / 180F));
         float f7 = f2 * f6 - f3 * f5;
         float f8 = f3 * f6 + f2 * f5;
         if (!this.isWalkable(f7, f8)) {
            this.strafeForwards = 1.0F;
            this.strafeRight = 0.0F;
         }

         this.mob.setSpeed(f1);
         this.mob.setZza(this.strafeForwards);
         this.mob.setXxa(this.strafeRight);
         this.operation = MoveControl.Operation.WAIT;
      } else if (this.operation == MoveControl.Operation.MOVE_TO) {
         this.operation = MoveControl.Operation.WAIT;
         double d0 = this.wantedX - this.mob.getX();
         double d1 = this.wantedZ - this.mob.getZ();
         double d2 = this.wantedY - this.mob.getY();
         double d3 = d0 * d0 + d2 * d2 + d1 * d1;
         if (d3 < (double)2.5000003E-7F) {
            this.mob.setZza(0.0F);
            return;
         }

         float f9 = (float)(Mth.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
         this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
         BlockPos blockpos = this.mob.blockPosition();
         BlockState blockstate = this.mob.level.getBlockState(blockpos);
         VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level, blockpos);
         if (d2 > (double)this.mob.getStepHeight() && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.mob.getBbWidth()) || !voxelshape.isEmpty() && this.mob.getY() < voxelshape.max(Direction.Axis.Y) + (double)blockpos.getY() && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
            this.mob.getJumpControl().jump();
            this.operation = MoveControl.Operation.JUMPING;
         }
      } else if (this.operation == MoveControl.Operation.JUMPING) {
         this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
         if (this.mob.isOnGround()) {
            this.operation = MoveControl.Operation.WAIT;
         }
      } else {
         this.mob.setZza(0.0F);
      }

   }

   private boolean isWalkable(float p_24997_, float p_24998_) {
      PathNavigation pathnavigation = this.mob.getNavigation();
      if (pathnavigation != null) {
         NodeEvaluator nodeevaluator = pathnavigation.getNodeEvaluator();
         if (nodeevaluator != null && nodeevaluator.getBlockPathType(this.mob.level, Mth.floor(this.mob.getX() + (double)p_24997_), this.mob.getBlockY(), Mth.floor(this.mob.getZ() + (double)p_24998_)) != BlockPathTypes.WALKABLE) {
            return false;
         }
      }

      return true;
   }

   protected float rotlerp(float p_24992_, float p_24993_, float p_24994_) {
      float f = Mth.wrapDegrees(p_24993_ - p_24992_);
      if (f > p_24994_) {
         f = p_24994_;
      }

      if (f < -p_24994_) {
         f = -p_24994_;
      }

      float f1 = p_24992_ + f;
      if (f1 < 0.0F) {
         f1 += 360.0F;
      } else if (f1 > 360.0F) {
         f1 -= 360.0F;
      }

      return f1;
   }

   public double getWantedX() {
      return this.wantedX;
   }

   public double getWantedY() {
      return this.wantedY;
   }

   public double getWantedZ() {
      return this.wantedZ;
   }

   protected static enum Operation {
      WAIT,
      MOVE_TO,
      STRAFE,
      JUMPING;
   }
}
