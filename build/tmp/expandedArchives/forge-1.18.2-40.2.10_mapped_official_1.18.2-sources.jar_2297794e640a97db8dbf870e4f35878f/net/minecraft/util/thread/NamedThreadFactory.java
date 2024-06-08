package net.minecraft.util.thread;

import com.mojang.logging.LogUtils;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;

public class NamedThreadFactory implements ThreadFactory {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ThreadGroup group;
   private final AtomicInteger threadNumber = new AtomicInteger(1);
   private final String namePrefix;

   public NamedThreadFactory(String p_146346_) {
      SecurityManager securitymanager = System.getSecurityManager();
      this.group = securitymanager != null ? securitymanager.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.namePrefix = p_146346_ + "-";
   }

   public Thread newThread(Runnable p_146352_) {
      Thread thread = new Thread(this.group, p_146352_, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
      thread.setUncaughtExceptionHandler((p_146349_, p_146350_) -> {
         LOGGER.error("Caught exception in thread {} from {}", p_146349_, p_146352_);
         LOGGER.error("", p_146350_);
      });
      if (thread.getPriority() != 5) {
         thread.setPriority(5);
      }

      return thread;
   }
}