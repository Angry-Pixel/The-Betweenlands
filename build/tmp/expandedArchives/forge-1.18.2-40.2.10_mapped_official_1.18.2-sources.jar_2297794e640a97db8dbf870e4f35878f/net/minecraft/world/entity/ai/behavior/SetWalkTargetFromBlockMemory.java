package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class SetWalkTargetFromBlockMemory extends Behavior<Villager> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private final float speedModifier;
   private final int closeEnoughDist;
   private final int tooFarDistance;
   private final int tooLongUnreachableDuration;

   public SetWalkTargetFromBlockMemory(MemoryModuleType<GlobalPos> p_24046_, float p_24047_, int p_24048_, int p_24049_, int p_24050_) {
      super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, p_24046_, MemoryStatus.VALUE_PRESENT));
      this.memoryType = p_24046_;
      this.speedModifier = p_24047_;
      this.closeEnoughDist = p_24048_;
      this.tooFarDistance = p_24049_;
      this.tooLongUnreachableDuration = p_24050_;
   }

   private void dropPOI(Villager p_24076_, long p_24077_) {
      Brain<?> brain = p_24076_.getBrain();
      p_24076_.releasePoi(this.memoryType);
      brain.eraseMemory(this.memoryType);
      brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, p_24077_);
   }

   protected void start(ServerLevel p_24059_, Villager p_24060_, long p_24061_) {
      Brain<?> brain = p_24060_.getBrain();
      brain.getMemory(this.memoryType).ifPresent((p_24067_) -> {
         if (!this.wrongDimension(p_24059_, p_24067_) && !this.tiredOfTryingToFindTarget(p_24059_, p_24060_)) {
            if (this.tooFar(p_24060_, p_24067_)) {
               Vec3 vec3 = null;
               int i = 0;

               for(int j = 1000; i < 1000 && (vec3 == null || this.tooFar(p_24060_, GlobalPos.of(p_24059_.dimension(), new BlockPos(vec3)))); ++i) {
                  vec3 = DefaultRandomPos.getPosTowards(p_24060_, 15, 7, Vec3.atBottomCenterOf(p_24067_.pos()), (double)((float)Math.PI / 2F));
               }

               if (i == 1000) {
                  this.dropPOI(p_24060_, p_24061_);
                  return;
               }

               brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, this.speedModifier, this.closeEnoughDist));
            } else if (!this.closeEnough(p_24059_, p_24060_, p_24067_)) {
               brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_24067_.pos(), this.speedModifier, this.closeEnoughDist));
            }
         } else {
            this.dropPOI(p_24060_, p_24061_);
         }

      });
   }

   private boolean tiredOfTryingToFindTarget(ServerLevel p_24056_, Villager p_24057_) {
      Optional<Long> optional = p_24057_.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      if (optional.isPresent()) {
         return p_24056_.getGameTime() - optional.get() > (long)this.tooLongUnreachableDuration;
      } else {
         return false;
      }
   }

   private boolean tooFar(Villager p_24079_, GlobalPos p_24080_) {
      return p_24080_.pos().distManhattan(p_24079_.blockPosition()) > this.tooFarDistance;
   }

   private boolean wrongDimension(ServerLevel p_24073_, GlobalPos p_24074_) {
      return p_24074_.dimension() != p_24073_.dimension();
   }

   private boolean closeEnough(ServerLevel p_24069_, Villager p_24070_, GlobalPos p_24071_) {
      return p_24071_.dimension() == p_24069_.dimension() && p_24071_.pos().distManhattan(p_24070_.blockPosition()) <= this.closeEnoughDist;
   }
}