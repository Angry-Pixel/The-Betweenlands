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
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ReentrantLock UPLOAD_LOCK = new ReentrantLock();
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private static final Component VERIFYING_TEXT = new TranslatableComponent("mco.upload.verifying");
   private final RealmsResetWorldScreen lastScreen;
   private final LevelSummary selectedLevel;
   private final long worldId;
   private final int slotId;
   private final UploadStatus uploadStatus;
   private final RateLimiter narrationRateLimiter;
   @Nullable
   private volatile Component[] errorMessage;
   private volatile Component status = new TranslatableComponent("mco.upload.preparing");
   private volatile String progress;
   private volatile boolean cancelled;
   private volatile boolean uploadFinished;
   private volatile boolean showDots = true;
   private volatile boolean uploadStarted;
   private Button backButton;
   private Button cancelButton;
   private int tickCount;
   @Nullable
   private Long previousWrittenBytes;
   @Nullable
   private Long previousTimeSnapshot;
   private long bytesPersSecond;
   private final Runnable callback;

   public RealmsUploadScreen(long p_90083_, int p_90084_, RealmsResetWorldScreen p_90085_, LevelSummary p_90086_, Runnable p_90087_) {
      super(NarratorChatListener.NO_TITLE);
      this.worldId = p_90083_;
      this.slotId = p_90084_;
      this.lastScreen = p_90085_;
      this.selectedLevel = p_90086_;
      this.uploadStatus = new UploadStatus();
      this.narrationRateLimiter = RateLimiter.create((double)0.1F);
      this.callback = p_90087_;
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.backButton = this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 42, 200, 20, CommonComponents.GUI_BACK, (p_90118_) -> {
         this.onBack();
      }));
      this.backButton.visible = false;
      this.cancelButton = this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 42, 200, 20, CommonComponents.GUI_CANCEL, (p_90104_) -> {
         this.onCancel();
      }));
      if (!this.uploadStarted) {
         if (this.lastScreen.slot == -1) {
            this.upload();
         } else {
            this.lastScreen.switchSlot(() -> {
               if (!this.uploadStarted) {
                  this.uploadStarted = true;
                  this.minecraft.setScreen(this);
                  this.upload();
               }

            });
         }
      }

   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onBack() {
      this.callback.run();
   }

   private void onCancel() {
      this.cancelled = true;
      this.minecraft.setScreen(this.lastScreen);
   }

   public boolean keyPressed(int p_90089_, int p_90090_, int p_90091_) {
      if (p_90089_ == 256) {
         if (this.showDots) {
            this.onCancel();
         } else {
            this.onBack();
         }

         return true;
      } else {
         return super.keyPressed(p_90089_, p_90090_, p_90091_);
      }
   }

   public void render(PoseStack p_90096_, int p_90097_, int p_90098_, float p_90099_) {
      this.renderBackground(p_90096_);
      if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten == this.uploadStatus.totalBytes) {
         this.status = VERIFYING_TEXT;
         this.cancelButton.active = false;
      }

      drawCenteredString(p_90096_, this.font, this.status, this.width / 2, 50, 16777215);
      if (this.showDots) {
         this.drawDots(p_90096_);
      }

      if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
         this.drawProgressBar(p_90096_);
         this.drawUploadSpeed(p_90096_);
      }

      if (this.errorMessage != null) {
         for(int i = 0; i < this.errorMessage.length; ++i) {
            drawCenteredString(p_90096_, this.font, this.errorMessage[i], this.width / 2, 110 + 12 * i, 16711680);
         }
      }

      super.render(p_90096_, p_90097_, p_90098_, p_90099_);
   }

   private void drawDots(PoseStack p_90116_) {
      int i = this.font.width(this.status);
      this.font.draw(p_90116_, DOTS[this.tickCount / 10 % DOTS.length], (float)(this.width / 2 + i / 2 + 5), 50.0F, 16777215);
   }

   private void drawProgressBar(PoseStack p_90122_) {
      double d0 = Math.min((double)this.uploadStatus.bytesWritten / (double)this.uploadStatus.totalBytes, 1.0D);
      this.progress = String.format(Locale.ROOT, "%.1f", d0 * 100.0D);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      double d1 = (double)(this.width / 2 - 100);
      double d2 = 0.5D;
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
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
      drawCenteredString(p_90122_, this.font, this.progress + " %", this.width / 2, 84, 16777215);
   }

   private void drawUploadSpeed(PoseStack p_90125_) {
      if (this.tickCount % 20 == 0) {
         if (this.previousWrittenBytes != null) {
            long i = Util.getMillis() - this.previousTimeSnapshot;
            if (i == 0L) {
               i = 1L;
            }

            this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / i;
            this.drawUploadSpeed0(p_90125_, this.bytesPersSecond);
         }

         this.previousWrittenBytes = this.uploadStatus.bytesWritten;
         this.previousTimeSnapshot = Util.getMillis();
      } else {
         this.drawUploadSpeed0(p_90125_, this.bytesPersSecond);
      }

   }

   private void drawUploadSpeed0(PoseStack p_90101_, long p_90102_) {
      if (p_90102_ > 0L) {
         int i = this.font.width(this.progress);
         String s = "(" + Unit.humanReadable(p_90102_) + "/s)";
         this.font.draw(p_90101_, s, (float)(this.width / 2 + i / 2 + 15), 84.0F, 16777215);
      }

   }

   public void tick() {
      super.tick();
      ++this.tickCount;
      if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
         Component component = this.createProgressNarrationMessage();
         NarratorChatListener.INSTANCE.sayNow(component);
      }

   }

   private Component createProgressNarrationMessage() {
      List<Component> list = Lists.newArrayList();
      list.add(this.status);
      if (this.progress != null) {
         list.add(new TextComponent(this.progress + "%"));
      }

      if (this.errorMessage != null) {
         list.addAll(Arrays.asList(this.errorMessage));
      }

      return CommonComponents.joinLines(list);
   }

   private void upload() {
      this.uploadStarted = true;
      (new Thread(() -> {
         File file1 = null;
         RealmsClient realmsclient = RealmsClient.create();
         long i = this.worldId;

         try {
            if (UPLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
               UploadInfo uploadinfo = null;

               for(int j = 0; j < 20; ++j) {
                  try {
                     if (this.cancelled) {
                        this.uploadCancelled();
                        return;
                     }

                     uploadinfo = realmsclient.requestUploadInfo(i, UploadTokenCache.get(i));
                     if (uploadinfo != null) {
                        break;
                     }
                  } catch (RetryCallException retrycallexception) {
                     Thread.sleep((long)(retrycallexception.delaySeconds * 1000));
                  }
               }

               if (uploadinfo == null) {
                  this.status = new TranslatableComponent("mco.upload.close.failure");
                  return;
               }

               UploadTokenCache.put(i, uploadinfo.getToken());
               if (!uploadinfo.isWorldClosed()) {
                  this.status = new TranslatableComponent("mco.upload.close.failure");
                  return;
               }

               if (this.cancelled) {
                  this.uploadCancelled();
                  return;
               }

               File file2 = new File(this.minecraft.gameDirectory.getAbsolutePath(), "saves");
               file1 = this.tarGzipArchive(new File(file2, this.selectedLevel.getLevelId()));
               if (this.cancelled) {
                  this.uploadCancelled();
                  return;
               }

               if (this.verify(file1)) {
                  this.status = new TranslatableComponent("mco.upload.uploading", this.selectedLevel.getLevelName());
                  FileUpload fileupload = new FileUpload(file1, this.worldId, this.slotId, uploadinfo, this.minecraft.getUser(), SharedConstants.getCurrentVersion().getName(), this.uploadStatus);
                  fileupload.upload((p_167557_) -> {
                     if (p_167557_.statusCode >= 200 && p_167557_.statusCode < 300) {
                        this.uploadFinished = true;
                        this.status = new TranslatableComponent("mco.upload.done");
                        this.backButton.setMessage(CommonComponents.GUI_DONE);
                        UploadTokenCache.invalidate(i);
                     } else if (p_167557_.statusCode == 400 && p_167557_.errorMessage != null) {
                        this.setErrorMessage(new TranslatableComponent("mco.upload.failed", p_167557_.errorMessage));
                     } else {
                        this.setErrorMessage(new TranslatableComponent("mco.upload.failed", p_167557_.statusCode));
                     }

                  });

                  while(!fileupload.isFinished()) {
                     if (this.cancelled) {
                        fileupload.cancel();
                        this.uploadCancelled();
                        return;
                     }

                     try {
                        Thread.sleep(500L);
                     } catch (InterruptedException interruptedexception) {
                        LOGGER.error("Failed to check Realms file upload status");
                     }
                  }

                  return;
               }

               long k = file1.length();
               Unit unit = Unit.getLargest(k);
               Unit unit1 = Unit.getLargest(5368709120L);
               if (Unit.humanReadable(k, unit).equals(Unit.humanReadable(5368709120L, unit1)) && unit != Unit.B) {
                  Unit unit2 = Unit.values()[unit.ordinal() - 1];
                  this.setErrorMessage(new TranslatableComponent("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()), new TranslatableComponent("mco.upload.size.failure.line2", Unit.humanReadable(k, unit2), Unit.humanReadable(5368709120L, unit2)));
                  return;
               }

               this.setErrorMessage(new TranslatableComponent("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()), new TranslatableComponent("mco.upload.size.failure.line2", Unit.humanReadable(k, unit), Unit.humanReadable(5368709120L, unit1)));
               return;
            }

            this.status = new TranslatableComponent("mco.upload.close.failure");
         } catch (IOException ioexception) {
            this.setErrorMessage(new TranslatableComponent("mco.upload.failed", ioexception.getMessage()));
            return;
         } catch (RealmsServiceException realmsserviceexception) {
            this.setErrorMessage(new TranslatableComponent("mco.upload.failed", realmsserviceexception.toString()));
            return;
         } catch (InterruptedException interruptedexception1) {
            LOGGER.error("Could not acquire upload lock");
            return;
         } finally {
            this.uploadFinished = true;
            if (UPLOAD_LOCK.isHeldByCurrentThread()) {
               UPLOAD_LOCK.unlock();
               this.showDots = false;
               this.backButton.visible = true;
               this.cancelButton.visible = false;
               if (file1 != null) {
                  LOGGER.debug("Deleting file {}", (Object)file1.getAbsolutePath());
                  file1.delete();
               }

            }

            return;
         }

      })).start();
   }

   private void setErrorMessage(Component... p_90113_) {
      this.errorMessage = p_90113_;
   }

   private void uploadCancelled() {
      this.status = new TranslatableComponent("mco.upload.cancelled");
      LOGGER.debug("Upload was cancelled");
   }

   private boolean verify(File p_90106_) {
      return p_90106_.length() < 5368709120L;
   }

   private File tarGzipArchive(File p_90120_) throws IOException {
      TarArchiveOutputStream tararchiveoutputstream = null;

      File file2;
      try {
         File file1 = File.createTempFile("realms-upload-file", ".tar.gz");
         tararchiveoutputstream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file1)));
         tararchiveoutputstream.setLongFileMode(3);
         this.addFileToTarGz(tararchiveoutputstream, p_90120_.getAbsolutePath(), "world", true);
         tararchiveoutputstream.finish();
         file2 = file1;
      } finally {
         if (tararchiveoutputstream != null) {
            tararchiveoutputstream.close();
         }

      }

      return file2;
   }

   private void addFileToTarGz(TarArchiveOutputStream p_90108_, String p_90109_, String p_90110_, boolean p_90111_) throws IOException {
      if (!this.cancelled) {
         File file1 = new File(p_90109_);
         String s = p_90111_ ? p_90110_ : p_90110_ + file1.getName();
         TarArchiveEntry tararchiveentry = new TarArchiveEntry(file1, s);
         p_90108_.putArchiveEntry(tararchiveentry);
         if (file1.isFile()) {
            IOUtils.copy(new FileInputStream(file1), p_90108_);
            p_90108_.closeArchiveEntry();
         } else {
            p_90108_.closeArchiveEntry();
            File[] afile = file1.listFiles();
            if (afile != null) {
               for(File file2 : afile) {
                  this.addFileToTarGz(p_90108_, file2.getAbsolutePath(), s + "/", false);
               }
            }
         }

      }
   }
}