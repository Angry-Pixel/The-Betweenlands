package net.minecraft.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;

public class RunSometimes<E extends LivingEntity> extends Behavior<E> {
   private boolean resetTicks;
   private boolean wasRunning;
   private final UniformInt interval;
   private final Behavior<? super E> wrappedBehavior;
   private int ticksUntilNextStart;

   public RunSometimes(Behavior<? super E> p_147874_, UniformInt p_147875_) {
      this(p_147874_, false, p_147875_);
   }

   public RunSometimes(Behavior<? super E> p_147877_, boolean p_147878_, UniformInt p_147879_) {
      super(p_147877_.entryCondition);
      this.wrappedBehavior = p_147877_;
      this.resetTicks = !p_147878_;
      this.interval = p_147879_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23853_, E p_23854_) {
      if (!this.wrappedBehavior.checkExtraStartConditions(p_23853_, p_23854_)) {
         return false;
      } else {
         if (this.resetTicks) {
            this.resetTicksUntilNextStart(p_23853_);
            this.resetTicks = false;
         }

         if (this.ticksUntilNextStart > 0) {
            --this.ticksUntilNextStart;
         }

         return !this.wasRunning && this.ticksUntilNextStart == 0;
      }
   }

   protected void start(ServerLevel p_23856_, E p_23857_, long p_23858_) {
      this.wrappedBehavior.start(p_23856_, p_23857_, p_23858_);
   }

   protected boolean canStillUse(ServerLevel p_23860_, E p_23861_, long p_23862_) {
      return this.wrappedBehavior.canStillUse(p_23860_, p_23861_, p_23862_);
   }

   protected void tick(ServerLevel p_23868_, E p_23869_, long p_23870_) {
      this.wrappedBehavior.tick(p_23868_, p_23869_, p_23870_);
      this.wasRunning = this.wrappedBehavior.getStatus() == Behavior.Status.RUNNING;
   }

   protected void stop(ServerLevel p_23864_, E p_23865_, long p_23866_) {
      this.resetTicksUntilNextStart(p_23864_);
      this.wrappedBehavior.stop(p_23864_, p_23865_, p_23866_);
   }

   private void resetTicksUntilNextStart(ServerLevel p_23851_) {
      this.ticksUntilNextStart = this.interval.sample(p_23851_.random);
   }

   protected boolean timedOut(long p_23849_) {
      return false;
   }

   public String toString() {
      return "RunSometimes: " + this.wrappedBehavior;
   }
}