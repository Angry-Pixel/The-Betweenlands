package com.mojang.realmsclient.gui.task;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface RestartDelayCalculator {
   void markExecutionStart();

   long getNextDelayMs();
}