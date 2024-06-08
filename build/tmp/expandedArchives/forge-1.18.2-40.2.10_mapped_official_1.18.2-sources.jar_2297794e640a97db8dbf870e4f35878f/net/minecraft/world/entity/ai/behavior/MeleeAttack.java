package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;

public class MeleeAttack extends Behavior<Mob> {
   private final int cooldownBetweenAttacks;

   public MeleeAttack(int p_23512_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryStatus.VALUE_ABSENT));
      this.cooldownBetweenAttacks = p_23512_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23521_, Mob p_23522_) {
      LivingEntity livingentity = this.getAttackTarget(p_23522_);
      return !this.isHoldingUsableProjectileWeapon(p_23522_) && BehaviorUtils.canSee(p_23522_, livingentity) && BehaviorUtils.isWithinMeleeAttackRange(p_23522_, livingentity);
   }

   private boolean isHoldingUsableProjectileWeapon(Mob p_23528_) {
      return p_23528_.isHolding((p_147697_) -> {
         Item item = p_147697_.getItem();
         return item instanceof ProjectileWeaponItem && p_23528_.canFireProjectileWeapon((ProjectileWeaponItem)item);
      });
   }

   protected void start(ServerLevel p_23524_, Mob p_23525_, long p_23526_) {
      LivingEntity livingentity = this.getAttackTarget(p_23525_);
      BehaviorUtils.lookAtEntity(p_23525_, livingentity);
      p_23525_.swing(InteractionHand.MAIN_HAND);
      p_23525_.doHurtTarget(livingentity);
      p_23525_.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)this.cooldownBetweenAttacks);
   }

   private LivingEntity getAttackTarget(Mob p_23533_) {
      return p_23533_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }
}