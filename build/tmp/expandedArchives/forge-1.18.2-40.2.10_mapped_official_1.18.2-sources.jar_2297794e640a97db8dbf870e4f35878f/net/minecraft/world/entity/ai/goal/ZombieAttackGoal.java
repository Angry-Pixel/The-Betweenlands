package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.monster.Zombie;

public class ZombieAttackGoal extends MeleeAttackGoal {
   private final Zombie zombie;
   private int raiseArmTicks;

   public ZombieAttackGoal(Zombie p_26019_, double p_26020_, boolean p_26021_) {
      super(p_26019_, p_26020_, p_26021_);
      this.zombie = p_26019_;
   }

   public void start() {
      super.start();
      this.raiseArmTicks = 0;
   }

   public void stop() {
      super.stop();
      this.zombie.setAggressive(false);
   }

   public void tick() {
      super.tick();
      ++this.raiseArmTicks;
      if (this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2) {
         this.zombie.setAggressive(true);
      } else {
         this.zombie.setAggressive(false);
      }

   }
}