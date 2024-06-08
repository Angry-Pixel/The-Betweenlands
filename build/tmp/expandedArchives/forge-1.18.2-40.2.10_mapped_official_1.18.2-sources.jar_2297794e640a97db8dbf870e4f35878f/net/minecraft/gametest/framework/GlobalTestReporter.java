package net.minecraft.gametest.framework;

public class GlobalTestReporter {
   private static TestReporter DELEGATE = new LogTestReporter();

   public static void replaceWith(TestReporter p_177656_) {
      DELEGATE = p_177656_;
   }

   public static void onTestFailed(GameTestInfo p_177654_) {
      DELEGATE.onTestFailed(p_177654_);
   }

   public static void onTestSuccess(GameTestInfo p_177658_) {
      DELEGATE.onTestSuccess(p_177658_);
   }

   public static void finish() {
      DELEGATE.finish();
   }
}