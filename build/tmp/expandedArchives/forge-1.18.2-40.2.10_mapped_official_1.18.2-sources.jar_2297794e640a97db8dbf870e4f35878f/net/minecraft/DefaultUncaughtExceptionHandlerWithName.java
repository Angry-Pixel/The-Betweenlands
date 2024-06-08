package net.minecraft;

import java.lang.Thread.UncaughtExceptionHandler;
import org.slf4j.Logger;

public class DefaultUncaughtExceptionHandlerWithName implements UncaughtExceptionHandler {
   private final Logger logger;

   public DefaultUncaughtExceptionHandlerWithName(Logger p_202578_) {
      this.logger = p_202578_;
   }

   public void uncaughtException(Thread p_131803_, Throwable p_131804_) {
      this.logger.error("Caught previously unhandled exception :");
      this.logger.error(p_131803_.getName(), p_131804_);
   }
}