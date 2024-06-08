package net.minecraft.gametest.framework;

class ExhaustedAttemptsException extends Throwable {
   public ExhaustedAttemptsException(int p_177039_, int p_177040_, GameTestInfo p_177041_) {
      super("Not enough successes: " + p_177040_ + " out of " + p_177039_ + " attempts. Required successes: " + p_177041_.requiredSuccesses() + ". max attempts: " + p_177041_.maxAttempts() + ".", p_177041_.getError());
   }
}