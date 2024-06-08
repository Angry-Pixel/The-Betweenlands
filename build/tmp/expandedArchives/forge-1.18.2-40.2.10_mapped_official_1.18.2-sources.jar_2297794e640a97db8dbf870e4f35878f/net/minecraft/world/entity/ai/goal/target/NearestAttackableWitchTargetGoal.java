package net.minecraft.world.entity.ai.goal.target;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raider;

public class NearestAttackableWitchTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
   private boolean canAttack = true;

   public NearestAttackableWitchTargetGoal(Raider p_26076_, Class<T> p_26077_, int p_26078_, boolean p_26079_, boolean p_26080_, @Nullable Predicate<LivingEntity> p_26081_) {
      super(p_26076_, p_26077_, p_26078_, p_26079_, p_26080_, p_26081_);
   }

   public void setCanAttack(boolean p_26084_) {
      this.canAttack = p_26084_;
   }

   public boolean canUse() {
      return this.canAttack && super.canUse();
   }
}