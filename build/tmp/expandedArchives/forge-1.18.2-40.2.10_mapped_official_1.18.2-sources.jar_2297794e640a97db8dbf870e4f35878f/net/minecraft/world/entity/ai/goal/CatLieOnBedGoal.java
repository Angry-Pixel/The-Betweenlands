package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.LevelReader;

public class CatLieOnBedGoal extends MoveToBlockGoal {
   private final Cat cat;

   public CatLieOnBedGoal(Cat p_25135_, double p_25136_, int p_25137_) {
      super(p_25135_, p_25136_, p_25137_, 6);
      this.cat = p_25135_;
      this.verticalSearchStart = -2;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canUse() {
      return this.cat.isTame() && !this.cat.isOrderedToSit() && !this.cat.isLying() && super.canUse();
   }

   public void start() {
      super.start();
      this.cat.setInSittingPose(false);
   }

   protected int nextStartTick(PathfinderMob p_25140_) {
      return 40;
   }

   public void stop() {
      super.stop();
      this.cat.setLying(false);
   }

   public void tick() {
      super.tick();
      this.cat.setInSittingPose(false);
      if (!this.isReachedTarget()) {
         this.cat.setLying(false);
      } else if (!this.cat.isLying()) {
         this.cat.setLying(true);
      }

   }

   protected boolean isValidTarget(LevelReader p_25142_, BlockPos p_25143_) {
      return p_25142_.isEmptyBlock(p_25143_.above()) && p_25142_.getBlockState(p_25143_).is(BlockTags.BEDS);
   }
}