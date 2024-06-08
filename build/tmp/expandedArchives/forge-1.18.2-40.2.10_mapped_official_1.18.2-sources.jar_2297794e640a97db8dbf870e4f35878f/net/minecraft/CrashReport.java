package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionException;
import net.minecraft.util.MemoryReserve;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class CrashReport {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String title;
   private final Throwable exception;
   private final List<CrashReportCategory> details = Lists.newArrayList();
   private File saveFile;
   private boolean trackingStackTrace = true;
   private StackTraceElement[] uncategorizedStackTrace = new StackTraceElement[0];
   private final SystemReport systemReport = new SystemReport();

   public CrashReport(String p_127509_, Throwable p_127510_) {
      this.title = p_127509_;
      this.exception = p_127510_;
   }

   public String getTitle() {
      return this.title;
   }

   public Throwable getException() {
      return this.exception;
   }

   public String getDetails() {
      StringBuilder stringbuilder = new StringBuilder();
      this.getDetails(stringbuilder);
      return stringbuilder.toString();
   }

   public void getDetails(StringBuilder p_127520_) {
      if ((this.uncategorizedStackTrace == null || this.uncategorizedStackTrace.length <= 0) && !this.details.isEmpty()) {
         this.uncategorizedStackTrace = ArrayUtils.subarray((StackTraceElement[])this.details.get(0).getStacktrace(), 0, 1);
      }

      if (this.uncategorizedStackTrace != null && this.uncategorizedStackTrace.length > 0) {
         p_127520_.append("-- Head --\n");
         p_127520_.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
         p_127520_.append("Stacktrace:");
         p_127520_.append(net.minecraftforge.logging.CrashReportExtender.generateEnhancedStackTrace(this.uncategorizedStackTrace));
      }

      for(CrashReportCategory crashreportcategory : this.details) {
         crashreportcategory.getDetails(p_127520_);
         p_127520_.append("\n\n");
      }

      net.minecraftforge.logging.CrashReportExtender.extendSystemReport(systemReport);
      this.systemReport.appendToCrashReportString(p_127520_);
   }

   public String getExceptionMessage() {
      StringWriter stringwriter = null;
      PrintWriter printwriter = null;
      Throwable throwable = this.exception;
      if (throwable.getMessage() == null) {
         if (throwable instanceof NullPointerException) {
            throwable = new NullPointerException(this.title);
         } else if (throwable instanceof StackOverflowError) {
            throwable = new StackOverflowError(this.title);
         } else if (throwable instanceof OutOfMemoryError) {
            throwable = new OutOfMemoryError(this.title);
         }

         throwable.setStackTrace(this.exception.getStackTrace());
      }

      return net.minecraftforge.logging.CrashReportExtender.generateEnhancedStackTrace(throwable);
   }

   public String getFriendlyReport() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("---- Minecraft Crash Report ----\n");
      net.minecraftforge.logging.CrashReportExtender.addCrashReportHeader(stringbuilder, this);
      stringbuilder.append("// ");
      stringbuilder.append(getErrorComment());
      stringbuilder.append("\n\n");
      stringbuilder.append("Time: ");
      stringbuilder.append((new SimpleDateFormat()).format(new Date()));
      stringbuilder.append("\n");
      stringbuilder.append("Description: ");
      stringbuilder.append(this.title);
      stringbuilder.append("\n\n");
      stringbuilder.append(this.getExceptionMessage());
      stringbuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int i = 0; i < 87; ++i) {
         stringbuilder.append("-");
      }

      stringbuilder.append("\n\n");
      this.getDetails(stringbuilder);
      return stringbuilder.toString();
   }

   public File getSaveFile() {
      return this.saveFile;
   }

   public boolean saveToFile(File p_127513_) {
      if (this.saveFile != null) {
         return false;
      } else {
         if (p_127513_.getParentFile() != null) {
            p_127513_.getParentFile().mkdirs();
         }

         Writer writer = null;

         boolean flag;
         try {
            writer = new OutputStreamWriter(new FileOutputStream(p_127513_), StandardCharsets.UTF_8);
            writer.write(this.getFriendlyReport());
            this.saveFile = p_127513_;
            return true;
         } catch (Throwable throwable) {
            LOGGER.error("Could not save crash report to {}", p_127513_, throwable);
            flag = false;
         } finally {
            IOUtils.closeQuietly(writer);
         }

         return flag;
      }
   }

   public SystemReport getSystemReport() {
      return this.systemReport;
   }

   public CrashReportCategory addCategory(String p_127515_) {
      return this.addCategory(p_127515_, 1);
   }

   public CrashReportCategory addCategory(String p_127517_, int p_127518_) {
      CrashReportCategory crashreportcategory = new CrashReportCategory(p_127517_);
      if (this.trackingStackTrace) {
         int i = crashreportcategory.fillInStackTrace(p_127518_);
         StackTraceElement[] astacktraceelement = this.exception.getStackTrace();
         StackTraceElement stacktraceelement = null;
         StackTraceElement stacktraceelement1 = null;
         int j = astacktraceelement.length - i;
         if (j < 0) {
            System.out.println("Negative index in crash report handler (" + astacktraceelement.length + "/" + i + ")");
         }

         if (astacktraceelement != null && 0 <= j && j < astacktraceelement.length) {
            stacktraceelement = astacktraceelement[j];
            if (astacktraceelement.length + 1 - i < astacktraceelement.length) {
               stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - i];
            }
         }

         this.trackingStackTrace = crashreportcategory.validateStackTrace(stacktraceelement, stacktraceelement1);
         if (astacktraceelement != null && astacktraceelement.length >= i && 0 <= j && j < astacktraceelement.length) {
            this.uncategorizedStackTrace = new StackTraceElement[j];
            System.arraycopy(astacktraceelement, 0, this.uncategorizedStackTrace, 0, this.uncategorizedStackTrace.length);
         } else {
            this.trackingStackTrace = false;
         }
      }

      this.details.add(crashreportcategory);
      return crashreportcategory;
   }

   private static String getErrorComment() {
      String[] astring = new String[]{"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

      try {
         return astring[(int)(Util.getNanos() % (long)astring.length)];
      } catch (Throwable throwable) {
         return "Witty comment unavailable :(";
      }
   }

   public static CrashReport forThrowable(Throwable p_127522_, String p_127523_) {
      while(p_127522_ instanceof CompletionException && p_127522_.getCause() != null) {
         p_127522_ = p_127522_.getCause();
      }

      CrashReport crashreport;
      if (p_127522_ instanceof ReportedException) {
         crashreport = ((ReportedException)p_127522_).getReport();
      } else {
         crashreport = new CrashReport(p_127523_, p_127522_);
      }

      return crashreport;
   }

   public static void preload() {
      MemoryReserve.allocate();
      (new CrashReport("Don't panic!", new Throwable())).getFriendlyReport();
   }
}
