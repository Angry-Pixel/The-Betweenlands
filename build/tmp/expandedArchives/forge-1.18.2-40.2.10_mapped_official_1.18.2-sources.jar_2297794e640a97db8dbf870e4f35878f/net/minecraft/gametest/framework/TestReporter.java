package net.minecraft.gametest.framework;

public interface TestReporter {
   void onTestFailed(GameTestInfo p_128100_);

   void onTestSuccess(GameTestInfo p_177831_);

   default void finish() {
   }
}