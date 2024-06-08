package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.BiPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.GameRules;

public class StartCelebratingIfTargetDead extends Behavior<LivingEntity> {
   private final int celebrateDuration;
   private final BiPredicate<LivingEntity, LivingEntity> dancePredicate;

   public StartCelebratingIfTargetDead(int p_24222_, BiPredicate<LivingEntity, LivingEntity> p_24223_) {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ANGRY_AT, MemoryStatus.REGISTERED, MemoryModuleType.CELEBRATE_LOCATION, MemoryStatus.VALUE_ABSENT, MemoryModuleType.DANCING, MemoryStatus.REGISTERED));
      this.celebrateDuration = p_24222_;
      this.dancePredicate = p_24223_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24225_, LivingEntity p_24226_) {
      return this.getAttackTarget(p_24226_).isDeadOrDying();
   }

   protected void start(ServerLevel p_24228_, LivingEntity p_24229_, long p_24230_) {
      LivingEntity livingentity = this.getAttackTarget(p_24229_);
      if (this.dancePredicate.test(p_24229_, livingentity)) {
         p_24229_.getBrain().setMemoryWithExpiry(MemoryModuleType.DANCING, true, (long)this.celebrateDuration);
      }

      p_24229_.getBrain().setMemoryWithExpiry(MemoryModuleType.CELEBRATE_LOCATION, livingentity.blockPosition(), (long)this.celebrateDuration);
      if (livingentity.getType() != EntityType.PLAYER || p_24228_.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
         p_24229_.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
         p_24229_.getBrain().eraseMemory(MemoryModuleType.ANGRY_AT);
      }

   }

   private LivingEntity getAttackTarget(LivingEntity p_24232_) {
      return p_24232_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }
}