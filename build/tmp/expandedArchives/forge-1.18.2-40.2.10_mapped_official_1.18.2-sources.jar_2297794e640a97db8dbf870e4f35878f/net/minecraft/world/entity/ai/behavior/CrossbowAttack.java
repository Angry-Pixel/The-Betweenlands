package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CrossbowAttack<E extends Mob & CrossbowAttackMob, T extends LivingEntity> extends Behavior<E> {
   private static final int TIMEOUT = 1200;
   private int attackDelay;
   private CrossbowAttack.CrossbowState crossbowState = CrossbowAttack.CrossbowState.UNCHARGED;

   public CrossbowAttack() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT), 1200);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_22778_, E p_22779_) {
      LivingEntity livingentity = getAttackTarget(p_22779_);
      return p_22779_.isHolding(is -> is.getItem() instanceof CrossbowItem) && BehaviorUtils.canSee(p_22779_, livingentity) && BehaviorUtils.isWithinAttackRange(p_22779_, livingentity, 0);
   }

   protected boolean canStillUse(ServerLevel p_22781_, E p_22782_, long p_22783_) {
      return p_22782_.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET) && this.checkExtraStartConditions(p_22781_, p_22782_);
   }

   protected void tick(ServerLevel p_22794_, E p_22795_, long p_22796_) {
      LivingEntity livingentity = getAttackTarget(p_22795_);
      this.lookAtTarget(p_22795_, livingentity);
      this.crossbowAttack(p_22795_, livingentity);
   }

   protected void stop(ServerLevel p_22805_, E p_22806_, long p_22807_) {
      if (p_22806_.isUsingItem()) {
         p_22806_.stopUsingItem();
      }

      if (p_22806_.isHolding(is -> is.getItem() instanceof CrossbowItem)) {
         p_22806_.setChargingCrossbow(false);
         CrossbowItem.setCharged(p_22806_.getUseItem(), false);
      }

   }

   private void crossbowAttack(E p_22787_, LivingEntity p_22788_) {
      if (this.crossbowState == CrossbowAttack.CrossbowState.UNCHARGED) {
         p_22787_.startUsingItem(ProjectileUtil.getWeaponHoldingHand(p_22787_, item -> item instanceof CrossbowItem));
         this.crossbowState = CrossbowAttack.CrossbowState.CHARGING;
         p_22787_.setChargingCrossbow(true);
      } else if (this.crossbowState == CrossbowAttack.CrossbowState.CHARGING) {
         if (!p_22787_.isUsingItem()) {
            this.crossbowState = CrossbowAttack.CrossbowState.UNCHARGED;
         }

         int i = p_22787_.getTicksUsingItem();
         ItemStack itemstack = p_22787_.getUseItem();
         if (i >= CrossbowItem.getChargeDuration(itemstack)) {
            p_22787_.releaseUsingItem();
            this.crossbowState = CrossbowAttack.CrossbowState.CHARGED;
            this.attackDelay = 20 + p_22787_.getRandom().nextInt(20);
            p_22787_.setChargingCrossbow(false);
         }
      } else if (this.crossbowState == CrossbowAttack.CrossbowState.CHARGED) {
         --this.attackDelay;
         if (this.attackDelay == 0) {
            this.crossbowState = CrossbowAttack.CrossbowState.READY_TO_ATTACK;
         }
      } else if (this.crossbowState == CrossbowAttack.CrossbowState.READY_TO_ATTACK) {
         p_22787_.performRangedAttack(p_22788_, 1.0F);
         ItemStack itemstack1 = p_22787_.getItemInHand(ProjectileUtil.getWeaponHoldingHand(p_22787_, item -> item instanceof CrossbowItem));
         CrossbowItem.setCharged(itemstack1, false);
         this.crossbowState = CrossbowAttack.CrossbowState.UNCHARGED;
      }

   }

   private void lookAtTarget(Mob p_22798_, LivingEntity p_22799_) {
      p_22798_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(p_22799_, true));
   }

   private static LivingEntity getAttackTarget(LivingEntity p_22785_) {
      return p_22785_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }

   static enum CrossbowState {
      UNCHARGED,
      CHARGING,
      CHARGED,
      READY_TO_ATTACK;
   }
}
