package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class BlockPosTracker implements PositionTracker {
   private final BlockPos blockPos;
   private final Vec3 centerPosition;

   public BlockPosTracker(BlockPos p_22676_) {
      this.blockPos = p_22676_;
      this.centerPosition = Vec3.atCenterOf(p_22676_);
   }

   public Vec3 currentPosition() {
      return this.centerPosition;
   }

   public BlockPos currentBlockPosition() {
      return this.blockPos;
   }

   public boolean isVisibleBy(LivingEntity p_22679_) {
      return true;
   }

   public String toString() {
      return "BlockPosTracker{blockPos=" + this.blockPos + ", centerPosition=" + this.centerPosition + "}";
   }
}