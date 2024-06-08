package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RestoreTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Backup backup;
   private final long worldId;
   private final RealmsConfigureWorldScreen lastScreen;

   public RestoreTask(Backup p_90443_, long p_90444_, RealmsConfigureWorldScreen p_90445_) {
      this.backup = p_90443_;
      this.worldId = p_90444_;
      this.lastScreen = p_90445_;
   }

   public void run() {
      this.setTitle(new TranslatableComponent("mco.backup.restoring"));
      RealmsClient realmsclient = RealmsClient.create();
      int i = 0;

      while(i < 25) {
         try {
            if (this.aborted()) {
               return;
            }

            realmsclient.restoreWorld(this.worldId, this.backup.backupId);
            pause(1L);
            if (this.aborted()) {
               return;
            }

            setScreen(this.lastScreen.getNewScreen());
            return;
         } catch (RetryCallException retrycallexception) {
            if (this.aborted()) {
               return;
            }

            pause((long)retrycallexception.delaySeconds);
            ++i;
         } catch (RealmsServiceException realmsserviceexception) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't restore backup", (Throwable)realmsserviceexception);
            setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this.lastScreen));
            return;
         } catch (Exception exception) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't restore backup", (Throwable)exception);
            this.error(exception.getLocalizedMessage());
            return;
         }
      }

   }
}