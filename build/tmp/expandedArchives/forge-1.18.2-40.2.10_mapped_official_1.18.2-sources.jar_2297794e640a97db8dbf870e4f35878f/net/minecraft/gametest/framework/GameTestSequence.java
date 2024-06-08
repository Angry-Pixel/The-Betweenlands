package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class GameTestSequence {
   final GameTestInfo parent;
   private final List<GameTestEvent> events = Lists.newArrayList();
   private long lastTick;

   GameTestSequence(GameTestInfo p_177542_) {
      this.parent = p_177542_;
      this.lastTick = p_177542_.getTick();
   }

   public GameTestSequence thenWaitUntil(Runnable p_177553_) {
      this.events.add(GameTestEvent.create(p_177553_));
      return this;
   }

   public GameTestSequence thenWaitUntil(long p_177550_, Runnable p_177551_) {
      this.events.add(GameTestEvent.create(p_177550_, p_177551_));
      return this;
   }

   public GameTestSequence thenIdle(int p_177545_) {
      return this.thenExecuteAfter(p_177545_, () -> {
      });
   }

   public GameTestSequence thenExecute(Runnable p_177563_) {
      this.events.add(GameTestEvent.create(() -> {
         this.executeWithoutFail(p_177563_);
      }));
      return this;
   }

   public GameTestSequence thenExecuteAfter(int p_177547_, Runnable p_177548_) {
      this.events.add(GameTestEvent.create(() -> {
         if (this.parent.getTick() < this.lastTick + (long)p_177547_) {
            throw new GameTestAssertException("Waiting");
         } else {
            this.executeWithoutFail(p_177548_);
         }
      }));
      return this;
   }

   public GameTestSequence thenExecuteFor(int p_177560_, Runnable p_177561_) {
      this.events.add(GameTestEvent.create(() -> {
         if (this.parent.getTick() < this.lastTick + (long)p_177560_) {
            this.executeWithoutFail(p_177561_);
            throw new GameTestAssertException("Waiting");
         }
      }));
      return this;
   }

   public void thenSucceed() {
      this.events.add(GameTestEvent.create(this.parent::succeed));
   }

   public void thenFail(Supplier<Exception> p_177555_) {
      this.events.add(GameTestEvent.create(() -> {
         this.parent.fail(p_177555_.get());
      }));
   }

   public GameTestSequence.Condition thenTrigger() {
      GameTestSequence.Condition gametestsequence$condition = new GameTestSequence.Condition();
      this.events.add(GameTestEvent.create(() -> {
         gametestsequence$condition.trigger(this.parent.getTick());
      }));
      return gametestsequence$condition;
   }

   public void tickAndContinue(long p_127778_) {
      try {
         this.tick(p_127778_);
      } catch (GameTestAssertException gametestassertexception) {
      }

   }

   public void tickAndFailIfNotComplete(long p_127780_) {
      try {
         this.tick(p_127780_);
      } catch (GameTestAssertException gametestassertexception) {
         this.parent.fail(gametestassertexception);
      }

   }

   private void executeWithoutFail(Runnable p_177571_) {
      try {
         p_177571_.run();
      } catch (GameTestAssertException gametestassertexception) {
         this.parent.fail(gametestassertexception);
      }

   }

   private void tick(long p_127782_) {
      Iterator<GameTestEvent> iterator = this.events.iterator();

      while(iterator.hasNext()) {
         GameTestEvent gametestevent = iterator.next();
         gametestevent.assertion.run();
         iterator.remove();
         long i = p_127782_ - this.lastTick;
         long j = this.lastTick;
         this.lastTick = p_127782_;
         if (gametestevent.expectedDelay != null && gametestevent.expectedDelay != i) {
            this.parent.fail(new GameTestAssertException("Succeeded in invalid tick: expected " + (j + gametestevent.expectedDelay) + ", but current tick is " + p_127782_));
            break;
         }
      }

   }

   public class Condition {
      private static final long NOT_TRIGGERED = -1L;
      private long triggerTime = -1L;

      void trigger(long p_177584_) {
         if (this.triggerTime != -1L) {
            throw new IllegalStateException("Condition already triggered at " + this.triggerTime);
         } else {
            this.triggerTime = p_177584_;
         }
      }

      public void assertTriggeredThisTick() {
         long i = GameTestSequence.this.parent.getTick();
         if (this.triggerTime != i) {
            if (this.triggerTime == -1L) {
               throw new GameTestAssertException("Condition not triggered (t=" + i + ")");
            } else {
               throw new GameTestAssertException("Condition triggered at " + this.triggerTime + ", (t=" + i + ")");
            }
         }
      }
   }
}