package net.minecraft;

public class ReportedException extends RuntimeException {
   private final CrashReport report;

   public ReportedException(CrashReport p_134760_) {
      this.report = p_134760_;
   }

   public CrashReport getReport() {
      return this.report;
   }

   public Throwable getCause() {
      return this.report.getException();
   }

   public String getMessage() {
      return this.report.getTitle();
   }
}