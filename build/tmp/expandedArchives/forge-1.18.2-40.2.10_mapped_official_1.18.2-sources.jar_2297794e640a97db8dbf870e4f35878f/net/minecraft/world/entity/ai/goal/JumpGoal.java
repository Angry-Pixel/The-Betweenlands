package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;

public abstract class JumpGoal extends Goal {
   public JumpGoal() {
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
   }
}