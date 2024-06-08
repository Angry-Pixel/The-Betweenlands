package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MoveToTargetSink extends Behavior<Mob> {
   private static final int MAX_COOLDOWN_BEFORE_RETRYING = 40;
   private int remainingCooldown;
   @Nullable
   private Path path;
   @Nullable
   private BlockPos lastTargetPos;
   private float speedModifier;

   public MoveToTargetSink() {
      this(150, 250);
   }

   public MoveToTargetSink(int p_23573_, int p_23574_) {
      super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.PATH, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT), p_23573_, p_23574_);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23583_, Mob p_23584_) {
      if (this.remainingCooldown > 0) {
         --this.remainingCooldown;
         return false;
      } else {
         Brain<?> brain = p_23584_.getBrain();
         WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
         boolean flag = this.reachedTarget(p_23584_, walktarget);
         if (!flag && this.tryComputePath(p_23584_, walktarget, p_23583_.getGameTime())) {
            this.lastTargetPos = walktarget.getTarget().currentBlockPosition();
            return true;
         } else {
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
            if (flag) {
               brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            }

            return false;
         }
      }
   }

   protected boolean canStillUse(ServerLevel p_23586_, Mob p_23587_, long p_23588_) {
      if (this.path != null && this.lastTargetPos != null) {
         Optional<WalkTarget> optional = p_23587_.getBrain().getMemory(MemoryModuleType.WALK_TARGET);
         PathNavigation pathnavigation = p_23587_.getNavigation();
         return !pathnavigation.isDone() && optional.isPresent() && !this.reachedTarget(p_23587_, optional.get());
      } else {
         return false;
      }
   }

   protected void stop(ServerLevel p_23601_, Mob p_23602_, long p_23603_) {
      if (p_23602_.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET) && !this.reachedTarget(p_23602_, p_23602_.getBrain().getMemory(MemoryModuleType.WALK_TARGET).get()) && p_23602_.getNavigation().isStuck()) {
         this.remainingCooldown = p_23601_.getRandom().nextInt(40);
      }

      p_23602_.getNavigation().stop();
      p_23602_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      p_23602_.getBrain().eraseMemory(MemoryModuleType.PATH);
      this.path = null;
   }

   protected void start(ServerLevel p_23609_, Mob p_23610_, long p_23611_) {
      p_23610_.getBrain().setMemory(MemoryModuleType.PATH, this.path);
      p_23610_.getNavigation().moveTo(this.path, (double)this.speedModifier);
   }

   protected void tick(ServerLevel p_23617_, Mob p_23618_, long p_23619_) {
      Path path = p_23618_.getNavigation().getPath();
      Brain<?> brain = p_23618_.getBrain();
      if (this.path != path) {
         this.path = path;
         brain.setMemory(MemoryModuleType.PATH, path);
      }

      if (path != null && this.lastTargetPos != null) {
         WalkTarget walktarget = brain.getMemory(MemoryModuleType.WALK_TARGET).get();
         if (walktarget.getTarget().currentBlockPosition().distSqr(this.lastTargetPos) > 4.0D && this.tryComputePath(p_23618_, walktarget, p_23617_.getGameTime())) {
            this.lastTargetPos = walktarget.getTarget().currentBlockPosition();
            this.start(p_23617_, p_23618_, p_23619_);
         }

      }
   }

   private boolean tryComputePath(Mob p_23593_, WalkTarget p_23594_, long p_23595_) {
      BlockPos blockpos = p_23594_.getTarget().currentBlockPosition();
      this.path = p_23593_.getNavigation().createPath(blockpos, 0);
      this.speedModifier = p_23594_.getSpeedModifier();
      Brain<?> brain = p_23593_.getBrain();
      if (this.reachedTarget(p_23593_, p_23594_)) {
         brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      } else {
         boolean flag = this.path != null && this.path.canReach();
         if (flag) {
            brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
         } else if (!brain.hasMemoryValue(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
            brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, p_23595_);
         }

         if (this.path != null) {
            return true;
         }

         Vec3 vec3 = DefaultRandomPos.getPosTowards((PathfinderMob)p_23593_, 10, 7, Vec3.atBottomCenterOf(blockpos), (double)((float)Math.PI / 2F));
         if (vec3 != null) {
            this.path = p_23593_.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0);
            return this.path != null;
         }
      }

      return false;
   }

   private boolean reachedTarget(Mob p_23590_, WalkTarget p_23591_) {
      return p_23591_.getTarget().currentBlockPosition().distManhattan(p_23590_.blockPosition()) <= p_23591_.getCloseEnoughDist();
   }
}