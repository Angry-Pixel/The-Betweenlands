package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class OpenServerTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final RealmsServer serverData;
   private final Screen returnScreen;
   private final boolean join;
   private final RealmsMainScreen mainScreen;
   private final Minecraft minecraft;

   public OpenServerTask(RealmsServer p_181344_, Screen p_181345_, RealmsMainScreen p_181346_, boolean p_181347_, Minecraft p_181348_) {
      this.serverData = p_181344_;
      this.returnScreen = p_181345_;
      this.join = p_181347_;
      this.mainScreen = p_181346_;
      this.minecraft = p_181348_;
   }

   public void run() {
      this.setTitle(new TranslatableComponent("mco.configure.world.opening"));
      RealmsClient realmsclient = RealmsClient.create();

      for(int i = 0; i < 25; ++i) {
         if (this.aborted()) {
            return;
         }

         try {
            boolean flag = realmsclient.open(this.serverData.id);
            if (flag) {
               this.minecraft.execute(() -> {
                  if (this.returnScreen instanceof RealmsConfigureWorldScreen) {
                     ((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
                  }

                  this.serverData.state = RealmsServer.State.OPEN;
                  if (this.join) {
                     this.mainScreen.play(this.serverData, this.returnScreen);
                  } else {
                     this.minecraft.setScreen(this.returnScreen);
                  }

               });
               break;
            }
         } catch (RetryCallException retrycallexception) {
            if (this.aborted()) {
               return;
            }

            pause((long)retrycallexception.delaySeconds);
         } catch (Exception exception) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Failed to open server", (Throwable)exception);
            this.error("Failed to open the server");
         }
      }

   }
}