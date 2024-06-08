package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.exception.RetryCallException;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SwitchSlotTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final long worldId;
   private final int slot;
   private final Runnable callback;

   public SwitchSlotTask(long p_90459_, int p_90460_, Runnable p_90461_) {
      this.worldId = p_90459_;
      this.slot = p_90460_;
      this.callback = p_90461_;
   }

   public void run() {
      RealmsClient realmsclient = RealmsClient.create();
      this.setTitle(new TranslatableComponent("mco.minigame.world.slot.screen.title"));

      for(int i = 0; i < 25; ++i) {
         try {
            if (this.aborted()) {
               return;
            }

            if (realmsclient.switchSlot(this.worldId, this.slot)) {
               this.callback.run();
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

            LOGGER.error("Couldn't switch world!");
            this.error(exception.toString());
         }
      }

   }
}