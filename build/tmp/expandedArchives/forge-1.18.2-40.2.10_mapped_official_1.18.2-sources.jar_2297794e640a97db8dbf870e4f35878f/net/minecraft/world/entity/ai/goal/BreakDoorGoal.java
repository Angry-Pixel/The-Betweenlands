package net.minecraft.world.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;

public class BreakDoorGoal extends DoorInteractGoal {
   private static final int DEFAULT_DOOR_BREAK_TIME = 240;
   private final Predicate<Difficulty> validDifficulties;
   protected int breakTime;
   protected int lastBreakProgress = -1;
   protected int doorBreakTime = -1;

   public BreakDoorGoal(Mob p_25091_, Predicate<Difficulty> p_25092_) {
      super(p_25091_);
      this.validDifficulties = p_25092_;
   }

   public BreakDoorGoal(Mob p_25087_, int p_25088_, Predicate<Difficulty> p_25089_) {
      this(p_25087_, p_25089_);
      this.doorBreakTime = p_25088_;
   }

   protected int getDoorBreakTime() {
      return Math.max(240, this.doorBreakTime);
   }

   public boolean canUse() {
      if (!super.canUse()) {
         return false;
      } else if (!net.minecraftforge.common.ForgeHooks.canEntityDestroy(this.mob.level, this.doorPos, this.mob)) {
         return false;
      } else {
         return this.isValidDifficulty(this.mob.level.getDifficulty()) && !this.isOpen();
      }
   }

   public void start() {
      super.start();
      this.breakTime = 0;
   }

   public boolean canContinueToUse() {
      return this.breakTime <= this.getDoorBreakTime() && !this.isOpen() && this.doorPos.closerToCenterThan(this.mob.position(), 2.0D) && this.isValidDifficulty(this.mob.level.getDifficulty());
   }

   public void stop() {
      super.stop();
      this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, -1);
   }

   public void tick() {
      super.tick();
      if (this.mob.getRandom().nextInt(20) == 0) {
         this.mob.level.levelEvent(1019, this.doorPos, 0);
         if (!this.mob.swinging) {
            this.mob.swing(this.mob.getUsedItemHand());
         }
      }

      ++this.breakTime;
      int i = (int)((float)this.breakTime / (float)this.getDoorBreakTime() * 10.0F);
      if (i != this.lastBreakProgress) {
         this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, i);
         this.lastBreakProgress = i;
      }

      if (this.breakTime == this.getDoorBreakTime() && this.isValidDifficulty(this.mob.level.getDifficulty())) {
         this.mob.level.removeBlock(this.doorPos, false);
         this.mob.level.levelEvent(1021, this.doorPos, 0);
         this.mob.level.levelEvent(2001, this.doorPos, Block.getId(this.mob.level.getBlockState(this.doorPos)));
      }

   }

   private boolean isValidDifficulty(Difficulty p_25095_) {
      return this.validDifficulties.test(p_25095_);
   }
}
