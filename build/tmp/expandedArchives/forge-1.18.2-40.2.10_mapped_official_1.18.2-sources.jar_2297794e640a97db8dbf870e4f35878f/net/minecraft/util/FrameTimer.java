package net.minecraft.util;

public class FrameTimer {
   public static final int LOGGING_LENGTH = 240;
   private final long[] loggedTimes = new long[240];
   private int logStart;
   private int logLength;
   private int logEnd;

   public void logFrameDuration(long p_13756_) {
      this.loggedTimes[this.logEnd] = p_13756_;
      ++this.logEnd;
      if (this.logEnd == 240) {
         this.logEnd = 0;
      }

      if (this.logLength < 240) {
         this.logStart = 0;
         ++this.logLength;
      } else {
         this.logStart = this.wrapIndex(this.logEnd + 1);
      }

   }

   public long getAverageDuration(int p_144733_) {
      int i = (this.logStart + p_144733_) % 240;
      int j = this.logStart;

      long k;
      for(k = 0L; j != i; ++j) {
         k += this.loggedTimes[j];
      }

      return k / (long)p_144733_;
   }

   public int scaleAverageDurationTo(int p_144735_, int p_144736_) {
      return this.scaleSampleTo(this.getAverageDuration(p_144735_), p_144736_, 60);
   }

   public int scaleSampleTo(long p_13758_, int p_13759_, int p_13760_) {
      double d0 = (double)p_13758_ / (double)(1000000000L / (long)p_13760_);
      return (int)(d0 * (double)p_13759_);
   }

   public int getLogStart() {
      return this.logStart;
   }

   public int getLogEnd() {
      return this.logEnd;
   }

   public int wrapIndex(int p_13763_) {
      return p_13763_ % 240;
   }

   public long[] getLog() {
      return this.loggedTimes;
   }
}