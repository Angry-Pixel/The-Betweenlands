package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Mob;

public class FloatGoal extends Goal {
   private final Mob mob;

   public FloatGoal(Mob p_25230_) {
      this.mob = p_25230_;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP));
      p_25230_.getNavigation().setCanFloat(true);
   }

   public boolean canUse() {
      return this.mob.isInWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getFluidJumpThreshold() || this.mob.isInLava();
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      if (this.mob.getRandom().nextFloat() < 0.8F) {
         this.mob.getJumpControl().jump();
      }

   }
}