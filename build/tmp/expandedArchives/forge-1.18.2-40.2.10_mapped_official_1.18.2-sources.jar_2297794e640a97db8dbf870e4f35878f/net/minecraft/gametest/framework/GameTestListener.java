package net.minecraft.gametest.framework;

public interface GameTestListener {
   void testStructureLoaded(GameTestInfo p_127651_);

   void testPassed(GameTestInfo p_177494_);

   void testFailed(GameTestInfo p_127652_);
}