package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class Mount<E extends LivingEntity> extends Behavior<E> {
   private static final int CLOSE_ENOUGH_TO_START_RIDING_DIST = 1;
   private final float speedModifier;

   public Mount(float p_23536_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RIDE_TARGET, MemoryStatus.VALUE_PRESENT));
      this.speedModifier = p_23536_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23538_, E p_23539_) {
      return !p_23539_.isPassenger();
   }

   protected void start(ServerLevel p_23541_, E p_23542_, long p_23543_) {
      if (this.isCloseEnoughToStartRiding(p_23542_)) {
         p_23542_.startRiding(this.getRidableEntity(p_23542_));
      } else {
         BehaviorUtils.setWalkAndLookTargetMemories(p_23542_, this.getRidableEntity(p_23542_), this.speedModifier, 1);
      }

   }

   private boolean isCloseEnoughToStartRiding(E p_23545_) {
      return this.getRidableEntity(p_23545_).closerThan(p_23545_, 1.0D);
   }

   private Entity getRidableEntity(E p_23547_) {
      return p_23547_.getBrain().getMemory(MemoryModuleType.RIDE_TARGET).get();
   }
}