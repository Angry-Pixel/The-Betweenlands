package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

public class ClimbOnTopOfPowderSnowGoal extends Goal {
   private final Mob mob;
   private final Level level;

   public ClimbOnTopOfPowderSnowGoal(Mob p_204055_, Level p_204056_) {
      this.mob = p_204055_;
      this.level = p_204056_;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP));
   }

   public boolean canUse() {
      boolean flag = this.mob.wasInPowderSnow || this.mob.isInPowderSnow;
      if (flag && this.mob.getType().is(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
         BlockPos blockpos = this.mob.blockPosition().above();
         BlockState blockstate = this.level.getBlockState(blockpos);
         return blockstate.is(Blocks.POWDER_SNOW) || blockstate.getCollisionShape(this.level, blockpos) == Shapes.empty();
      } else {
         return false;
      }
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      this.mob.getJumpControl().jump();
   }
}