package net.minecraft.server.rcon.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import org.slf4j.Logger;

public abstract class GenericThread implements Runnable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final int MAX_STOP_WAIT = 5;
   protected volatile boolean running;
   protected final String name;
   @Nullable
   protected Thread thread;

   protected GenericThread(String p_11522_) {
      this.name = p_11522_;
   }

   public synchronized boolean start() {
      if (this.running) {
         return true;
      } else {
         this.running = true;
         this.thread = new Thread(this, this.name + " #" + UNIQUE_THREAD_ID.incrementAndGet());
         this.thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
         this.thread.start();
         LOGGER.info("Thread {} started", (Object)this.name);
         return true;
      }
   }

   public synchronized void stop() {
      this.running = false;
      if (null != this.thread) {
         int i = 0;

         while(this.thread.isAlive()) {
            try {
               this.thread.join(1000L);
               ++i;
               if (i >= 5) {
                  LOGGER.warn("Waited {} seconds attempting force stop!", (int)i);
               } else if (this.thread.isAlive()) {
                  LOGGER.warn("Thread {} ({}) failed to exit after {} second(s)", this, this.thread.getState(), i, new Exception("Stack:"));
                  this.thread.interrupt();
               }
            } catch (InterruptedException interruptedexception) {
            }
         }

         LOGGER.info("Thread {} stopped", (Object)this.name);
         this.thread = null;
      }
   }

   public boolean isRunning() {
      return this.running;
   }
}