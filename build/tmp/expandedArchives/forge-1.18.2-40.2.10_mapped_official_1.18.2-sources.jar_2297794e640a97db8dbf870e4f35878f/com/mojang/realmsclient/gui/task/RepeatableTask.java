package com.mojang.realmsclient.gui.task;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RepeatableTask implements Runnable {
   private final BooleanSupplier isActive;
   private final RestartDelayCalculator restartDelayCalculator;
   private final Duration interval;
   private final Runnable runnable;

   private RepeatableTask(Runnable p_167577_, Duration p_167578_, BooleanSupplier p_167579_, RestartDelayCalculator p_167580_) {
      this.runnable = p_167577_;
      this.interval = p_167578_;
      this.isActive = p_167579_;
      this.restartDelayCalculator = p_167580_;
   }

   public void run() {
      if (this.isActive.getAsBoolean()) {
         this.restartDelayCalculator.markExecutionStart();
         this.runnable.run();
      }

   }

   public ScheduledFuture<?> schedule(ScheduledExecutorService p_167586_) {
      return p_167586_.scheduleAtFixedRate(this, this.restartDelayCalculator.getNextDelayMs(), this.interval.toMillis(), TimeUnit.MILLISECONDS);
   }

   public static RepeatableTask withRestartDelayAccountingForInterval(Runnable p_167582_, Duration p_167583_, BooleanSupplier p_167584_) {
      return new RepeatableTask(p_167582_, p_167583_, p_167584_, new IntervalBasedStartupDelay(p_167583_));
   }

   public static RepeatableTask withImmediateRestart(Runnable p_167588_, Duration p_167589_, BooleanSupplier p_167590_) {
      return new RepeatableTask(p_167588_, p_167589_, p_167590_, new NoStartupDelay());
   }
}