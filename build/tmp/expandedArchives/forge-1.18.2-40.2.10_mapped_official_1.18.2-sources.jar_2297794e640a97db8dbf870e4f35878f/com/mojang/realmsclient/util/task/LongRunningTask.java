package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.gui.ErrorCallback;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class LongRunningTask implements ErrorCallback, Runnable {
   protected static final int NUMBER_OF_RETRIES = 25;
   private static final Logger LOGGER = LogUtils.getLogger();
   protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

   protected static void pause(long p_167656_) {
      try {
         Thread.sleep(p_167656_ * 1000L);
      } catch (InterruptedException interruptedexception) {
         Thread.currentThread().interrupt();
         LOGGER.error("", (Throwable)interruptedexception);
      }

   }

   public static void setScreen(Screen p_90406_) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.execute(() -> {
         minecraft.setScreen(p_90406_);
      });
   }

   public void setScreen(RealmsLongRunningMcoTaskScreen p_90401_) {
      this.longRunningMcoTaskScreen = p_90401_;
   }

   public void error(Component p_90408_) {
      this.longRunningMcoTaskScreen.error(p_90408_);
   }

   public void setTitle(Component p_90410_) {
      this.longRunningMcoTaskScreen.setTitle(p_90410_);
   }

   public boolean aborted() {
      return this.longRunningMcoTaskScreen.aborted();
   }

   public void tick() {
   }

   public void init() {
   }

   public void abortTask() {
   }
}