package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.gameevent.GameEvent;

public class EatBlockGoal extends Goal {
   private static final int EAT_ANIMATION_TICKS = 40;
   private static final Predicate<BlockState> IS_TALL_GRASS = BlockStatePredicate.forBlock(Blocks.GRASS);
   private final Mob mob;
   private final Level level;
   private int eatAnimationTick;

   public EatBlockGoal(Mob p_25207_) {
      this.mob = p_25207_;
      this.level = p_25207_.level;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
   }

   public boolean canUse() {
      if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
         return false;
      } else {
         BlockPos blockpos = this.mob.blockPosition();
         if (IS_TALL_GRASS.test(this.level.getBlockState(blockpos))) {
            return true;
         } else {
            return this.level.getBlockState(blockpos.below()).is(Blocks.GRASS_BLOCK);
         }
      }
   }

   public void start() {
      this.eatAnimationTick = this.adjustedTickDelay(40);
      this.level.broadcastEntityEvent(this.mob, (byte)10);
      this.mob.getNavigation().stop();
   }

   public void stop() {
      this.eatAnimationTick = 0;
   }

   public boolean canContinueToUse() {
      return this.eatAnimationTick > 0;
   }

   public int getEatAnimationTick() {
      return this.eatAnimationTick;
   }

   public void tick() {
      this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      if (this.eatAnimationTick == this.adjustedTickDelay(4)) {
         BlockPos blockpos = this.mob.blockPosition();
         if (IS_TALL_GRASS.test(this.level.getBlockState(blockpos))) {
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.mob)) {
               this.level.destroyBlock(blockpos, false);
            }

            this.mob.ate();
            this.mob.gameEvent(GameEvent.EAT, this.mob.eyeBlockPosition());
         } else {
            BlockPos blockpos1 = blockpos.below();
            if (this.level.getBlockState(blockpos1).is(Blocks.GRASS_BLOCK)) {
               if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.mob)) {
                  this.level.levelEvent(2001, blockpos1, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                  this.level.setBlock(blockpos1, Blocks.DIRT.defaultBlockState(), 2);
               }

               this.mob.ate();
               this.mob.gameEvent(GameEvent.EAT, this.mob.eyeBlockPosition());
            }
         }

      }
   }
}
