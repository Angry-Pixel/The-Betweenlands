package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class StopAttackingIfTargetInvalid<E extends Mob> extends Behavior<E> {
   private static final int TIMEOUT_TO_GET_WITHIN_ATTACK_RANGE = 200;
   private final Predicate<LivingEntity> stopAttackingWhen;
   private final Consumer<E> onTargetErased;

   public StopAttackingIfTargetInvalid(Predicate<LivingEntity> p_147983_, Consumer<E> p_147984_) {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED));
      this.stopAttackingWhen = p_147983_;
      this.onTargetErased = p_147984_;
   }

   public StopAttackingIfTargetInvalid(Predicate<LivingEntity> p_24236_) {
      this(p_24236_, (p_147992_) -> {
      });
   }

   public StopAttackingIfTargetInvalid(Consumer<E> p_147981_) {
      this((p_147988_) -> {
         return false;
      }, p_147981_);
   }

   public StopAttackingIfTargetInvalid() {
      this((p_147986_) -> {
         return false;
      }, (p_147990_) -> {
      });
   }

   protected void start(ServerLevel p_24242_, E p_24243_, long p_24244_) {
      LivingEntity livingentity = this.getAttackTarget(p_24243_);
      if (!p_24243_.canAttack(livingentity)) {
         this.clearAttackTarget(p_24243_);
      } else if (isTiredOfTryingToReachTarget(p_24243_)) {
         this.clearAttackTarget(p_24243_);
      } else if (this.isCurrentTargetDeadOrRemoved(p_24243_)) {
         this.clearAttackTarget(p_24243_);
      } else if (this.isCurrentTargetInDifferentLevel(p_24243_)) {
         this.clearAttackTarget(p_24243_);
      } else if (this.stopAttackingWhen.test(this.getAttackTarget(p_24243_))) {
         this.clearAttackTarget(p_24243_);
      }
   }

   private boolean isCurrentTargetInDifferentLevel(E p_24248_) {
      return this.getAttackTarget(p_24248_).level != p_24248_.level;
   }

   private LivingEntity getAttackTarget(E p_24252_) {
      return p_24252_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }

   private static <E extends LivingEntity> boolean isTiredOfTryingToReachTarget(E p_24246_) {
      Optional<Long> optional = p_24246_.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      return optional.isPresent() && p_24246_.level.getGameTime() - optional.get() > 200L;
   }

   private boolean isCurrentTargetDeadOrRemoved(E p_24254_) {
      Optional<LivingEntity> optional = p_24254_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
      return optional.isPresent() && !optional.get().isAlive();
   }

   protected void clearAttackTarget(E p_24256_) {
      this.onTargetErased.accept(p_24256_);
      p_24256_.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
   }
}