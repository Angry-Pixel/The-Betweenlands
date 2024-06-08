package com.mojang.realmsclient.gui.task;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoStartupDelay implements RestartDelayCalculator {
   public void markExecutionStart() {
   }

   public long getNextDelayMs() {
      return 0L;
   }
}