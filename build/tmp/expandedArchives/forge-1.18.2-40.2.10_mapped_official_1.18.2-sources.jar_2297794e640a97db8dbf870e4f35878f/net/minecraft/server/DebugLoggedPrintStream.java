package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import org.slf4j.Logger;

public class DebugLoggedPrintStream extends LoggedPrintStream {
   private static final Logger LOGGER = LogUtils.getLogger();

   public DebugLoggedPrintStream(String p_135934_, OutputStream p_135935_) {
      super(p_135934_, p_135935_);
   }

   protected void logLine(String p_135937_) {
      StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
      StackTraceElement stacktraceelement = astacktraceelement[Math.min(3, astacktraceelement.length)];
      LOGGER.info("[{}]@.({}:{}): {}", this.name, stacktraceelement.getFileName(), stacktraceelement.getLineNumber(), p_135937_);
   }
}