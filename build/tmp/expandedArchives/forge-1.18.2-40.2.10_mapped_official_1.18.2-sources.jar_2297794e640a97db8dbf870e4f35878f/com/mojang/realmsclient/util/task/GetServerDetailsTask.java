package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GetServerDetailsTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final RealmsServer server;
   private final Screen lastScreen;
   private final RealmsMainScreen mainScreen;
   private final ReentrantLock connectLock;

   public GetServerDetailsTask(RealmsMainScreen p_90332_, Screen p_90333_, RealmsServer p_90334_, ReentrantLock p_90335_) {
      this.lastScreen = p_90333_;
      this.mainScreen = p_90332_;
      this.server = p_90334_;
      this.connectLock = p_90335_;
   }

   public void run() {
      this.setTitle(new TranslatableComponent("mco.connect.connecting"));

      RealmsServerAddress realmsserveraddress;
      try {
         realmsserveraddress = this.fetchServerAddress();
      } catch (CancellationException cancellationexception) {
         LOGGER.info("User aborted connecting to realms");
         return;
      } catch (RealmsServiceException realmsserviceexception) {
         switch(realmsserviceexception.realmsErrorCodeOrDefault(-1)) {
         case 6002:
            setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
            return;
         case 6006:
            boolean flag1 = this.server.ownerUUID.equals(Minecraft.getInstance().getUser().getUuid());
            setScreen((Screen)(flag1 ? new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == RealmsServer.WorldType.MINIGAME) : new RealmsGenericErrorScreen(new TranslatableComponent("mco.brokenworld.nonowner.title"), new TranslatableComponent("mco.brokenworld.nonowner.error"), this.lastScreen)));
            return;
         default:
            this.error(realmsserviceexception.toString());
            LOGGER.error("Couldn't connect to world", (Throwable)realmsserviceexception);
            return;
         }
      } catch (TimeoutException timeoutexception) {
         this.error(new TranslatableComponent("mco.errorMessage.connectionFailure"));
         return;
      } catch (Exception exception) {
         LOGGER.error("Couldn't connect to world", (Throwable)exception);
         this.error(exception.getLocalizedMessage());
         return;
      }

      boolean flag = realmsserveraddress.resourcePackUrl != null && realmsserveraddress.resourcePackHash != null;
      Screen screen = (Screen)(flag ? this.resourcePackDownloadConfirmationScreen(realmsserveraddress, this::connectScreen) : this.connectScreen(realmsserveraddress));
      setScreen(screen);
   }

   private RealmsServerAddress fetchServerAddress() throws RealmsServiceException, TimeoutException, CancellationException {
      RealmsClient realmsclient = RealmsClient.create();

      for(int i = 0; i < 40; ++i) {
         if (this.aborted()) {
            throw new CancellationException();
         }

         try {
            return realmsclient.join(this.server.id);
         } catch (RetryCallException retrycallexception) {
            pause((long)retrycallexception.delaySeconds);
         }
      }

      throw new TimeoutException();
   }

   public RealmsLongRunningMcoTaskScreen connectScreen(RealmsServerAddress p_167638_) {
      return new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ConnectTask(this.lastScreen, this.server, p_167638_));
   }

   private RealmsLongConfirmationScreen resourcePackDownloadConfirmationScreen(RealmsServerAddress p_167640_, Function<RealmsServerAddress, Screen> p_167641_) {
      BooleanConsumer booleanconsumer = (p_167645_) -> {
         try {
            if (p_167645_) {
               this.scheduleResourcePackDownload(p_167640_).thenRun(() -> {
                  setScreen(p_167641_.apply(p_167640_));
               }).exceptionally((p_202341_) -> {
                  Minecraft.getInstance().getClientPackSource().clearServerPack();
                  LOGGER.error("Failed to download resource pack from {}", p_167640_, p_202341_);
                  setScreen(new RealmsGenericErrorScreen(new TextComponent("Failed to download resource pack!"), this.lastScreen));
                  return null;
               });
               return;
            }

            setScreen(this.lastScreen);
         } finally {
            if (this.connectLock.isHeldByCurrentThread()) {
               this.connectLock.unlock();
            }

         }

      };
      return new RealmsLongConfirmationScreen(booleanconsumer, RealmsLongConfirmationScreen.Type.Info, new TranslatableComponent("mco.configure.world.resourcepack.question.line1"), new TranslatableComponent("mco.configure.world.resourcepack.question.line2"), true);
   }

   private CompletableFuture<?> scheduleResourcePackDownload(RealmsServerAddress p_167652_) {
      try {
         return Minecraft.getInstance().getClientPackSource().downloadAndSelectResourcePack(p_167652_.resourcePackUrl, p_167652_.resourcePackHash, false);
      } catch (Exception exception) {
         CompletableFuture<Void> completablefuture = new CompletableFuture<>();
         completablefuture.completeExceptionally(exception);
         return completablefuture;
      }
   }
}