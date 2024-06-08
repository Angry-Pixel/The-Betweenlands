package net.minecraft.util;

import net.minecraft.network.chat.Component;

public interface ProgressListener {
   void progressStartNoAbort(Component p_14212_);

   void progressStart(Component p_14213_);

   void progressStage(Component p_14214_);

   void progressStagePercentage(int p_14211_);

   void stop();
}