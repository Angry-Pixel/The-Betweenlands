package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class SetWalkTargetFromAttackTargetIfTargetOutOfReach extends Behavior<Mob> {
   private static final int PROJECTILE_ATTACK_RANGE_BUFFER = 1;
   private final Function<LivingEntity, Float> speedModifier;

   public SetWalkTargetFromAttackTargetIfTargetOutOfReach(float p_24026_) {
      this((p_147908_) -> {
         return p_24026_;
      });
   }

   public SetWalkTargetFromAttackTargetIfTargetOutOfReach(Function<LivingEntity, Float> p_147905_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.REGISTERED));
      this.speedModifier = p_147905_;
   }

   protected void start(ServerLevel p_24032_, Mob p_24033_, long p_24034_) {
      LivingEntity livingentity = p_24033_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
      if (BehaviorUtils.canSee(p_24033_, livingentity) && BehaviorUtils.isWithinAttackRange(p_24033_, livingentity, 1)) {
         this.clearWalkTarget(p_24033_);
      } else {
         this.setWalkAndLookTarget(p_24033_, livingentity);
      }

   }

   private void setWalkAndLookTarget(LivingEntity p_24038_, LivingEntity p_24039_) {
      Brain<?> brain = p_24038_.getBrain();
      brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(p_24039_, true));
      WalkTarget walktarget = new WalkTarget(new EntityTracker(p_24039_, false), this.speedModifier.apply(p_24038_), 0);
      brain.setMemory(MemoryModuleType.WALK_TARGET, walktarget);
   }

   private void clearWalkTarget(LivingEntity p_24036_) {
      p_24036_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
   }
}