package net.minecraft.world.entity.ai.memory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.phys.Vec3;

public class WalkTarget {
   private final PositionTracker target;
   private final float speedModifier;
   private final int closeEnoughDist;

   public WalkTarget(BlockPos p_26417_, float p_26418_, int p_26419_) {
      this(new BlockPosTracker(p_26417_), p_26418_, p_26419_);
   }

   public WalkTarget(Vec3 p_26413_, float p_26414_, int p_26415_) {
      this(new BlockPosTracker(new BlockPos(p_26413_)), p_26414_, p_26415_);
   }

   public WalkTarget(Entity p_148209_, float p_148210_, int p_148211_) {
      this(new EntityTracker(p_148209_, false), p_148210_, p_148211_);
   }

   public WalkTarget(PositionTracker p_26409_, float p_26410_, int p_26411_) {
      this.target = p_26409_;
      this.speedModifier = p_26410_;
      this.closeEnoughDist = p_26411_;
   }

   public PositionTracker getTarget() {
      return this.target;
   }

   public float getSpeedModifier() {
      return this.speedModifier;
   }

   public int getCloseEnoughDist() {
      return this.closeEnoughDist;
   }
}