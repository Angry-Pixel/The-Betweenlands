package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DownloadTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long worldId;
   private final int slot;
   private final Screen lastScreen;
   private final String downloadName;

   public DownloadTask(long p_90320_, int p_90321_, String p_90322_, Screen p_90323_) {
      this.worldId = p_90320_;
      this.slot = p_90321_;
      this.lastScreen = p_90323_;
      this.downloadName = p_90322_;
   }

   public void run() {
      this.setTitle(new TranslatableComponent("mco.download.preparing"));
      RealmsClient realmsclient = RealmsClient.create();
      int i = 0;

      while(i < 25) {
         try {
            if (this.aborted()) {
               return;
            }

            WorldDownload worlddownload = realmsclient.requestDownloadInfo(this.worldId, this.slot);
            pause(1L);
            if (this.aborted()) {
               return;
            }

            setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, worlddownload, this.downloadName, (p_90325_) -> {
            }));
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

            LOGGER.error("Couldn't download world data");
            setScreen(new RealmsGenericErrorScreen(realmsserviceexception, this.lastScreen));
            return;
         } catch (Exception exception) {
            if (this.aborted()) {
               return;
            }

            LOGGER.error("Couldn't download world data", (Throwable)exception);
            this.error(exception.getLocalizedMessage());
            return;
         }
      }

   }
}