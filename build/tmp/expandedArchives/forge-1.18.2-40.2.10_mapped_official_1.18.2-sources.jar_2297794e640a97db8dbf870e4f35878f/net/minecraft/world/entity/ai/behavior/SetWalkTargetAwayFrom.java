package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetAwayFrom<T> extends Behavior<PathfinderMob> {
   private final MemoryModuleType<T> walkAwayFromMemory;
   private final float speedModifier;
   private final int desiredDistance;
   private final Function<T, Vec3> toPosition;

   public SetWalkTargetAwayFrom(MemoryModuleType<T> p_23987_, float p_23988_, int p_23989_, boolean p_23990_, Function<T, Vec3> p_23991_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, p_23990_ ? MemoryStatus.REGISTERED : MemoryStatus.VALUE_ABSENT, p_23987_, MemoryStatus.VALUE_PRESENT));
      this.walkAwayFromMemory = p_23987_;
      this.speedModifier = p_23988_;
      this.desiredDistance = p_23989_;
      this.toPosition = p_23991_;
   }

   public static SetWalkTargetAwayFrom<BlockPos> pos(MemoryModuleType<BlockPos> p_24013_, float p_24014_, int p_24015_, boolean p_24016_) {
      return new SetWalkTargetAwayFrom<>(p_24013_, p_24014_, p_24015_, p_24016_, Vec3::atBottomCenterOf);
   }

   public static SetWalkTargetAwayFrom<? extends Entity> entity(MemoryModuleType<? extends Entity> p_24020_, float p_24021_, int p_24022_, boolean p_24023_) {
      return new SetWalkTargetAwayFrom<>(p_24020_, p_24021_, p_24022_, p_24023_, Entity::position);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24000_, PathfinderMob p_24001_) {
      return this.alreadyWalkingAwayFromPosWithSameSpeed(p_24001_) ? false : p_24001_.position().closerThan(this.getPosToAvoid(p_24001_), (double)this.desiredDistance);
   }

   private Vec3 getPosToAvoid(PathfinderMob p_24007_) {
      return this.toPosition.apply(p_24007_.getBrain().getMemory(this.walkAwayFromMemory).get());
   }

   private boolean alreadyWalkingAwayFromPosWithSameSpeed(PathfinderMob p_24018_) {
      if (!p_24018_.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)) {
         return false;
      } else {
         WalkTarget walktarget = p_24018_.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get();
         if (walktarget.getSpeedModifier() != this.speedModifier) {
            return false;
         } else {
            Vec3 vec3 = walktarget.getTarget().currentPosition().subtract(p_24018_.position());
            Vec3 vec31 = this.getPosToAvoid(p_24018_).subtract(p_24018_.position());
            return vec3.dot(vec31) < 0.0D;
         }
      }
   }

   protected void start(ServerLevel p_24003_, PathfinderMob p_24004_, long p_24005_) {
      moveAwayFrom(p_24004_, this.getPosToAvoid(p_24004_), this.speedModifier);
   }

   private static void moveAwayFrom(PathfinderMob p_24009_, Vec3 p_24010_, float p_24011_) {
      for(int i = 0; i < 10; ++i) {
         Vec3 vec3 = LandRandomPos.getPosAway(p_24009_, 16, 7, p_24010_);
         if (vec3 != null) {
            p_24009_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, p_24011_, 0));
            return;
         }
      }

   }
}