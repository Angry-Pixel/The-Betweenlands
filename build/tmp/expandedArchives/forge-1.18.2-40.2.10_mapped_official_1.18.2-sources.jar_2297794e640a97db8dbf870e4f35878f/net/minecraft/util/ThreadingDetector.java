package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import org.slf4j.Logger;

public class ThreadingDetector {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String name;
   private final Semaphore lock = new Semaphore(1);
   private final Lock stackTraceLock = new ReentrantLock();
   @Nullable
   private volatile Thread threadThatFailedToAcquire;
   @Nullable
   private volatile ReportedException fullException;

   public ThreadingDetector(String p_199415_) {
      this.name = p_199415_;
   }

   public void checkAndLock() {
      boolean flag = false;

      try {
         this.stackTraceLock.lock();
         if (!this.lock.tryAcquire()) {
            this.threadThatFailedToAcquire = Thread.currentThread();
            flag = true;
            this.stackTraceLock.unlock();

            try {
               this.lock.acquire();
            } catch (InterruptedException interruptedexception) {
               Thread.currentThread().interrupt();
            }

            throw this.fullException;
         }
      } finally {
         if (!flag) {
            this.stackTraceLock.unlock();
         }

      }

   }

   public void checkAndUnlock() {
      try {
         this.stackTraceLock.lock();
         Thread thread = this.threadThatFailedToAcquire;
         if (thread != null) {
            ReportedException reportedexception = makeThreadingException(this.name, thread);
            this.fullException = reportedexception;
            this.lock.release();
            throw reportedexception;
         }

         this.lock.release();
      } finally {
         this.stackTraceLock.unlock();
      }

   }

   public static ReportedException makeThreadingException(String p_199418_, @Nullable Thread p_199419_) {
      String s = Stream.of(Thread.currentThread(), p_199419_).filter(Objects::nonNull).map(ThreadingDetector::stackTrace).collect(Collectors.joining("\n"));
      String s1 = "Accessing " + p_199418_ + " from multiple threads";
      CrashReport crashreport = new CrashReport(s1, new IllegalStateException(s1));
      CrashReportCategory crashreportcategory = crashreport.addCategory("Thread dumps");
      crashreportcategory.setDetail("Thread dumps", s);
      LOGGER.error("Thread dumps: \n" + s);
      return new ReportedException(crashreport);
   }

   private static String stackTrace(Thread p_199421_) {
      return p_199421_.getName() + ": \n\tat " + (String)Arrays.stream(p_199421_.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
   }
}