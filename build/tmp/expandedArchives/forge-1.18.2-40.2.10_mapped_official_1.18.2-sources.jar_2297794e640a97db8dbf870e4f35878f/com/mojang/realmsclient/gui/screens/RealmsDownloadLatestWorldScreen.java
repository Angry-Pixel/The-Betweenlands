package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.Unit;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsDownloadLatestWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ReentrantLock DOWNLOAD_LOCK = new ReentrantLock();
   private final Screen lastScreen;
   private final WorldDownload worldDownload;
   private final Component downloadTitle;
   private final RateLimiter narrationRateLimiter;
   private Button cancelButton;
   private final String worldName;
   private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
   @Nullable
   private volatile Component errorMessage;
   private volatile Component status = new TranslatableComponent("mco.download.preparing");
   @Nullable
   private volatile String progress;
   private volatile boolean cancelled;
   private volatile boolean showDots = true;
   private volatile boolean finished;
   private volatile boolean extracting;
   @Nullable
   private Long previousWrittenBytes;
   @Nullable
   private Long previousTimeSnapshot;
   private long bytesPersSecond;
   private int animTick;
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private int dotIndex;
   private boolean checked;
   private final BooleanConsumer callback;

   public RealmsDownloadLatestWorldScreen(Screen p_88625_, WorldDownload p_88626_, String p_88627_, BooleanConsumer p_88628_) {
      super(NarratorChatListener.NO_TITLE);
      this.callback = p_88628_;
      this.lastScreen = p_88625_;
      this.worldName = p_88627_;
      this.worldDownload = p_88626_;
      this.downloadStatus = new RealmsDownloadLatestWorldScreen.DownloadStatus();
      this.downloadTitle = new TranslatableComponent("mco.download.title");
      this.narrationRateLimiter = RateLimiter.create((double)0.1F);
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.cancelButton = this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 42, 200, 20, CommonComponents.GUI_CANCEL, (p_88642_) -> {
         this.cancelled = true;
         this.backButtonClicked();
      }));
      this.checkDownloadSize();
   }

   private void checkDownloadSize() {
      if (!this.finished) {
         if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 5368709120L) {
            Component component = new TranslatableComponent("mco.download.confirmation.line1", Unit.humanReadable(5368709120L));
            Component component1 = new TranslatableComponent("mco.download.confirmation.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_88651_) -> {
               this.checked = true;
               this.minecraft.setScreen(this);
               this.downloadSave();
            }, RealmsLongConfirmationScreen.Type.Warning, component, component1, false));
         } else {
            this.downloadSave();
         }

      }
   }

   private long getContentLength(String p_88647_) {
      FileDownload filedownload = new FileDownload();
      return filedownload.contentLength(p_88647_);
   }

   public void tick() {
      super.tick();
      ++this.animTick;
      if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
         Component component = this.createProgressNarrationMessage();
         NarratorChatListener.INSTANCE.sayNow(component);
      }

   }

   private Component createProgressNarrationMessage() {
      List<Component> list = Lists.newArrayList();
      list.add(this.downloadTitle);
      list.add(this.status);
      if (this.progress != null) {
         list.add(new TextComponent(this.progress + "%"));
         list.add(new TextComponent(Unit.humanReadable(this.bytesPersSecond) + "/s"));
      }

      if (this.errorMessage != null) {
         list.add(this.errorMessage);
      }

      return CommonComponents.joinLines(list);
   }

   public boolean keyPressed(int p_88630_, int p_88631_, int p_88632_) {
      if (p_88630_ == 256) {
         this.cancelled = true;
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_88630_, p_88631_, p_88632_);
      }
   }

   private void backButtonClicked() {
      if (this.finished && this.callback != null && this.errorMessage == null) {
         this.callback.accept(true);
      }

      this.minecraft.setScreen(this.lastScreen);
   }

   public void render(PoseStack p_88634_, int p_88635_, int p_88636_, float p_88637_) {
      this.renderBackground(p_88634_);
      drawCenteredString(p_88634_, this.font, this.downloadTitle, this.width / 2, 20, 16777215);
      drawCenteredString(p_88634_, this.font, this.status, this.width / 2, 50, 16777215);
      if (this.showDots) {
         this.drawDots(p_88634_);
      }

      if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
         this.drawProgressBar(p_88634_);
         this.drawDownloadSpeed(p_88634_);
      }

      if (this.errorMessage != null) {
         drawCenteredString(p_88634_, this.font, this.errorMessage, this.width / 2, 110, 16711680);
      }

      super.render(p_88634_, p_88635_, p_88636_, p_88637_);
   }

   private void drawDots(PoseStack p_88645_) {
      int i = this.font.width(this.status);
      if (this.animTick % 10 == 0) {
         ++this.dotIndex;
      }

      this.font.draw(p_88645_, DOTS[this.dotIndex % DOTS.length], (float)(this.width / 2 + i / 2 + 5), 50.0F, 16777215);
   }

   private void drawProgressBar(PoseStack p_88649_) {
      double d0 = Math.min((double)this.downloadStatus.bytesWritten / (double)this.downloadStatus.totalBytes, 1.0D);
      this.progress = String.format(Locale.ROOT, "%.1f", d0 * 100.0D);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      double d1 = (double)(this.width / 2 - 100);
      double d2 = 0.5D;
      bufferbuilder.vertex(d1 - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      bufferbuilder.vertex(d1 + 200.0D * d0 + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      bufferbuilder.vertex(d1 + 200.0D * d0 + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      bufferbuilder.vertex(d1 - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      bufferbuilder.vertex(d1, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      bufferbuilder.vertex(d1 + 200.0D * d0, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      bufferbuilder.vertex(d1 + 200.0D * d0, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      bufferbuilder.vertex(d1, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      tesselator.end();
      RenderSystem.enableTexture();
      drawCenteredString(p_88649_, this.font, this.progress + " %", this.width / 2, 84, 16777215);
   }

   private void drawDownloadSpeed(PoseStack p_88654_) {
      if (this.animTick % 20 == 0) {
         if (this.previousWrittenBytes != null) {
            long i = Util.getMillis() - this.previousTimeSnapshot;
            if (i == 0L) {
               i = 1L;
            }

            this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / i;
            this.drawDownloadSpeed0(p_88654_, this.bytesPersSecond);
         }

         this.previousWrittenBytes = this.downloadStatus.bytesWritten;
         this.previousTimeSnapshot = Util.getMillis();
      } else {
         this.drawDownloadSpeed0(p_88654_, this.bytesPersSecond);
      }

   }

   private void drawDownloadSpeed0(PoseStack p_88639_, long p_88640_) {
      if (p_88640_ > 0L) {
         int i = this.font.width(this.progress);
         String s = "(" + Unit.humanReadable(p_88640_) + "/s)";
         this.font.draw(p_88639_, s, (float)(this.width / 2 + i / 2 + 15), 84.0F, 16777215);
      }

   }

   private void downloadSave() {
      (new Thread(() -> {
         try {
            if (DOWNLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
               if (this.cancelled) {
                  this.downloadCancelled();
                  return;
               }

               this.status = new TranslatableComponent("mco.download.downloading", this.worldName);
               FileDownload filedownload = new FileDownload();
               filedownload.contentLength(this.worldDownload.downloadLink);
               filedownload.download(this.worldDownload, this.worldName, this.downloadStatus, this.minecraft.getLevelSource());

               while(!filedownload.isFinished()) {
                  if (filedownload.isError()) {
                     filedownload.cancel();
                     this.errorMessage = new TranslatableComponent("mco.download.failed");
                     this.cancelButton.setMessage(CommonComponents.GUI_DONE);
                     return;
                  }

                  if (filedownload.isExtracting()) {
                     if (!this.extracting) {
                        this.status = new TranslatableComponent("mco.download.extracting");
                     }

                     this.extracting = true;
                  }

                  if (this.cancelled) {
                     filedownload.cancel();
                     this.downloadCancelled();
                     return;
                  }

                  try {
                     Thread.sleep(500L);
                  } catch (InterruptedException interruptedexception) {
                     LOGGER.error("Failed to check Realms backup download status");
                  }
               }

               this.finished = true;
               this.status = new TranslatableComponent("mco.download.done");
               this.cancelButton.setMessage(CommonComponents.GUI_DONE);
               return;
            }

            this.status = new TranslatableComponent("mco.download.failed");
         } catch (InterruptedException interruptedexception1) {
            LOGGER.error("Could not acquire upload lock");
            return;
         } catch (Exception exception) {
            this.errorMessage = new TranslatableComponent("mco.download.failed");
            exception.printStackTrace();
            return;
         } finally {
            if (!DOWNLOAD_LOCK.isHeldByCurrentThread()) {
               return;
            }

            DOWNLOAD_LOCK.unlock();
            this.showDots = false;
            this.finished = true;
         }

      })).start();
   }

   private void downloadCancelled() {
      this.status = new TranslatableComponent("mco.download.cancelled");
   }

   @OnlyIn(Dist.CLIENT)
   public static class DownloadStatus {
      public volatile long bytesWritten;
      public volatile long totalBytes;
   }
}