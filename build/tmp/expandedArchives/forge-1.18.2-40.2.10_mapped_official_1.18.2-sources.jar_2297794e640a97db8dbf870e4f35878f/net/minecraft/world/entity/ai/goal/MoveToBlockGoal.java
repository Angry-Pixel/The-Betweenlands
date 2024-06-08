package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.LevelReader;

public abstract class MoveToBlockGoal extends Goal {
   private static final int GIVE_UP_TICKS = 1200;
   private static final int STAY_TICKS = 1200;
   private static final int INTERVAL_TICKS = 200;
   protected final PathfinderMob mob;
   public final double speedModifier;
   protected int nextStartTick;
   protected int tryTicks;
   private int maxStayTicks;
   protected BlockPos blockPos = BlockPos.ZERO;
   private boolean reachedTarget;
   private final int searchRange;
   private final int verticalSearchRange;
   protected int verticalSearchStart;

   public MoveToBlockGoal(PathfinderMob p_25609_, double p_25610_, int p_25611_) {
      this(p_25609_, p_25610_, p_25611_, 1);
   }

   public MoveToBlockGoal(PathfinderMob p_25613_, double p_25614_, int p_25615_, int p_25616_) {
      this.mob = p_25613_;
      this.speedModifier = p_25614_;
      this.searchRange = p_25615_;
      this.verticalSearchStart = 0;
      this.verticalSearchRange = p_25616_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
   }

   public boolean canUse() {
      if (this.nextStartTick > 0) {
         --this.nextStartTick;
         return false;
      } else {
         this.nextStartTick = this.nextStartTick(this.mob);
         return this.findNearestBlock();
      }
   }

   protected int nextStartTick(PathfinderMob p_25618_) {
      return reducedTickDelay(200 + p_25618_.getRandom().nextInt(200));
   }

   public boolean canContinueToUse() {
      return this.tryTicks >= -this.maxStayTicks && this.tryTicks <= 1200 && this.isValidTarget(this.mob.level, this.blockPos);
   }

   public void start() {
      this.moveMobToBlock();
      this.tryTicks = 0;
      this.maxStayTicks = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
   }

   protected void moveMobToBlock() {
      this.mob.getNavigation().moveTo((double)((float)this.blockPos.getX()) + 0.5D, (double)(this.blockPos.getY() + 1), (double)((float)this.blockPos.getZ()) + 0.5D, this.speedModifier);
   }

   public double acceptedDistance() {
      return 1.0D;
   }

   protected BlockPos getMoveToTarget() {
      return this.blockPos.above();
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      BlockPos blockpos = this.getMoveToTarget();
      if (!blockpos.closerToCenterThan(this.mob.position(), this.acceptedDistance())) {
         this.reachedTarget = false;
         ++this.tryTicks;
         if (this.shouldRecalculatePath()) {
            this.mob.getNavigation().moveTo((double)((float)blockpos.getX()) + 0.5D, (double)blockpos.getY(), (double)((float)blockpos.getZ()) + 0.5D, this.speedModifier);
         }
      } else {
         this.reachedTarget = true;
         --this.tryTicks;
      }

   }

   public boolean shouldRecalculatePath() {
      return this.tryTicks % 40 == 0;
   }

   protected boolean isReachedTarget() {
      return this.reachedTarget;
   }

   protected boolean findNearestBlock() {
      int i = this.searchRange;
      int j = this.verticalSearchRange;
      BlockPos blockpos = this.mob.blockPosition();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int k = this.verticalSearchStart; k <= j; k = k > 0 ? -k : 1 - k) {
         for(int l = 0; l < i; ++l) {
            for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
               for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                  blockpos$mutableblockpos.setWithOffset(blockpos, i1, k - 1, j1);
                  if (this.mob.isWithinRestriction(blockpos$mutableblockpos) && this.isValidTarget(this.mob.level, blockpos$mutableblockpos)) {
                     this.blockPos = blockpos$mutableblockpos;
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected abstract boolean isValidTarget(LevelReader p_25619_, BlockPos p_25620_);
}