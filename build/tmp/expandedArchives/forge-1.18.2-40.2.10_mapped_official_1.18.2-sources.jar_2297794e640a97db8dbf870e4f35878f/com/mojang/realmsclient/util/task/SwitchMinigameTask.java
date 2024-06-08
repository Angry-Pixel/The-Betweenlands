package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SwitchMinigameTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long worldId;
   private final WorldTemplate worldTemplate;
   private final RealmsConfigureWorldScreen lastScreen;

   public SwitchMinigameTask(long p_90451_, WorldTemplate p_90452_, RealmsConfigureWorldScreen p_90453_) {
      this.worldId = p_90451_;
      this.worldTemplate = p_90452_;
      this.lastScreen = p_90453_;
   }

   public void run() {
      RealmsClient realmsclient = RealmsClient.create();
      this.setTitle(new TranslatableComponent("mco.minigame.world.starting.screen.title"));

      for(int i = 0; i < 25; ++i) {
         try {
            if (this.aborted()) {
               return;
            }

            if (realmsclient.putIntoMinigameMode(this.worldId, this.worldTemplate.id)) {
               setScreen(this.lastScreen);
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

            LOGGER.error("Couldn't start mini game!");
            this.error(exception.toString());
         }
      }

   }
}