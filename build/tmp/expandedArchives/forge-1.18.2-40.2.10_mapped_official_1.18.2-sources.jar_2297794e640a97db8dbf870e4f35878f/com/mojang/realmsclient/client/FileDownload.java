package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FileDownload {
   static final Logger LOGGER = LogUtils.getLogger();
   volatile boolean cancelled;
   volatile boolean finished;
   volatile boolean error;
   volatile boolean extracting;
   @Nullable
   private volatile File tempFile;
   volatile File resourcePackPath;
   @Nullable
   private volatile HttpGet request;
   @Nullable
   private Thread currentThread;
   private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
   private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public long contentLength(String p_86990_) {
      CloseableHttpClient closeablehttpclient = null;
      HttpGet httpget = null;

      long i;
      try {
         httpget = new HttpGet(p_86990_);
         closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
         CloseableHttpResponse closeablehttpresponse = closeablehttpclient.execute(httpget);
         return Long.parseLong(closeablehttpresponse.getFirstHeader("Content-Length").getValue());
      } catch (Throwable throwable) {
         LOGGER.error("Unable to get content length for download");
         i = 0L;
      } finally {
         if (httpget != null) {
            httpget.releaseConnection();
         }

         if (closeablehttpclient != null) {
            try {
               closeablehttpclient.close();
            } catch (IOException ioexception) {
               LOGGER.error("Could not close http client", (Throwable)ioexception);
            }
         }

      }

      return i;
   }

   public void download(WorldDownload p_86983_, String p_86984_, RealmsDownloadLatestWorldScreen.DownloadStatus p_86985_, LevelStorageSource p_86986_) {
      if (this.currentThread == null) {
         this.currentThread = new Thread(() -> {
            CloseableHttpClient closeablehttpclient = null;

            try {
               this.tempFile = File.createTempFile("backup", ".tar.gz");
               this.request = new HttpGet(p_86983_.downloadLink);
               closeablehttpclient = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
               HttpResponse httpresponse = closeablehttpclient.execute(this.request);
               p_86985_.totalBytes = Long.parseLong(httpresponse.getFirstHeader("Content-Length").getValue());
               if (httpresponse.getStatusLine().getStatusCode() == 200) {
                  OutputStream outputstream = new FileOutputStream(this.tempFile);
                  FileDownload.ProgressListener filedownload$progresslistener = new FileDownload.ProgressListener(p_86984_.trim(), this.tempFile, p_86986_, p_86985_);
                  FileDownload.DownloadCountingOutputStream filedownload$downloadcountingoutputstream = new FileDownload.DownloadCountingOutputStream(outputstream);
                  filedownload$downloadcountingoutputstream.setListener(filedownload$progresslistener);
                  IOUtils.copy(httpresponse.getEntity().getContent(), filedownload$downloadcountingoutputstream);
                  return;
               }

               this.error = true;
               this.request.abort();
            } catch (Exception exception1) {
               LOGGER.error("Caught exception while downloading: {}", (Object)exception1.getMessage());
               this.error = true;
               return;
            } finally {
               this.request.releaseConnection();
               if (this.tempFile != null) {
                  this.tempFile.delete();
               }

               if (!this.error) {
                  if (!p_86983_.resourcePackUrl.isEmpty() && !p_86983_.resourcePackHash.isEmpty()) {
                     try {
                        this.tempFile = File.createTempFile("resources", ".tar.gz");
                        this.request = new HttpGet(p_86983_.resourcePackUrl);
                        HttpResponse httpresponse1 = closeablehttpclient.execute(this.request);
                        p_86985_.totalBytes = Long.parseLong(httpresponse1.getFirstHeader("Content-Length").getValue());
                        if (httpresponse1.getStatusLine().getStatusCode() != 200) {
                           this.error = true;
                           this.request.abort();
                           return;
                        }

                        OutputStream outputstream1 = new FileOutputStream(this.tempFile);
                        FileDownload.ResourcePackProgressListener filedownload$resourcepackprogresslistener = new FileDownload.ResourcePackProgressListener(this.tempFile, p_86985_, p_86983_);
                        FileDownload.DownloadCountingOutputStream filedownload$downloadcountingoutputstream1 = new FileDownload.DownloadCountingOutputStream(outputstream1);
                        filedownload$downloadcountingoutputstream1.setListener(filedownload$resourcepackprogresslistener);
                        IOUtils.copy(httpresponse1.getEntity().getContent(), filedownload$downloadcountingoutputstream1);
                     } catch (Exception exception) {
                        LOGGER.error("Caught exception while downloading: {}", (Object)exception.getMessage());
                        this.error = true;
                     } finally {
                        this.request.releaseConnection();
                        if (this.tempFile != null) {
                           this.tempFile.delete();
                        }

                     }
                  } else {
                     this.finished = true;
                  }
               }

               if (closeablehttpclient != null) {
                  try {
                     closeablehttpclient.close();
                  } catch (IOException ioexception) {
                     LOGGER.error("Failed to close Realms download client");
                  }
               }

            }

         });
         this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
         this.currentThread.start();
      }
   }

   public void cancel() {
      if (this.request != null) {
         this.request.abort();
      }

      if (this.tempFile != null) {
         this.tempFile.delete();
      }

      this.cancelled = true;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public boolean isError() {
      return this.error;
   }

   public boolean isExtracting() {
      return this.extracting;
   }

   public static String findAvailableFolderName(String p_87002_) {
      p_87002_ = p_87002_.replaceAll("[\\./\"]", "_");

      for(String s : INVALID_FILE_NAMES) {
         if (p_87002_.equalsIgnoreCase(s)) {
            p_87002_ = "_" + p_87002_ + "_";
         }
      }

      return p_87002_;
   }

   void untarGzipArchive(String p_86992_, @Nullable File p_86993_, LevelStorageSource p_86994_) throws IOException {
      Pattern pattern = Pattern.compile(".*-([0-9]+)$");
      int i = 1;

      for(char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         p_86992_ = p_86992_.replace(c0, '_');
      }

      if (StringUtils.isEmpty(p_86992_)) {
         p_86992_ = "Realm";
      }

      p_86992_ = findAvailableFolderName(p_86992_);

      try {
         for(LevelSummary levelsummary : p_86994_.getLevelList()) {
            if (levelsummary.getLevelId().toLowerCase(Locale.ROOT).startsWith(p_86992_.toLowerCase(Locale.ROOT))) {
               Matcher matcher = pattern.matcher(levelsummary.getLevelId());
               if (matcher.matches()) {
                  int j = Integer.parseInt(matcher.group(1));
                  if (j > i) {
                     i = j;
                  }
               } else {
                  ++i;
               }
            }
         }
      } catch (Exception exception1) {
         LOGGER.error("Error getting level list", (Throwable)exception1);
         this.error = true;
         return;
      }

      String s;
      if (p_86994_.isNewLevelIdAcceptable(p_86992_) && i <= 1) {
         s = p_86992_;
      } else {
         s = p_86992_ + (i == 1 ? "" : "-" + i);
         if (!p_86994_.isNewLevelIdAcceptable(s)) {
            boolean flag = false;

            while(!flag) {
               ++i;
               s = p_86992_ + (i == 1 ? "" : "-" + i);
               if (p_86994_.isNewLevelIdAcceptable(s)) {
                  flag = true;
               }
            }
         }
      }

      TarArchiveInputStream tararchiveinputstream = null;
      File file1 = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");

      try {
         file1.mkdir();
         tararchiveinputstream = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(p_86993_))));

         for(TarArchiveEntry tararchiveentry = tararchiveinputstream.getNextTarEntry(); tararchiveentry != null; tararchiveentry = tararchiveinputstream.getNextTarEntry()) {
            File file2 = new File(file1, tararchiveentry.getName().replace("world", s));
            if (tararchiveentry.isDirectory()) {
               file2.mkdirs();
            } else {
               file2.createNewFile();
               FileOutputStream fileoutputstream = new FileOutputStream(file2);

               try {
                  IOUtils.copy(tararchiveinputstream, fileoutputstream);
               } catch (Throwable throwable2) {
                  try {
                     fileoutputstream.close();
                  } catch (Throwable throwable1) {
                     throwable2.addSuppressed(throwable1);
                  }

                  throw throwable2;
               }

               fileoutputstream.close();
            }
         }
      } catch (Exception exception) {
         LOGGER.error("Error extracting world", (Throwable)exception);
         this.error = true;
      } finally {
         if (tararchiveinputstream != null) {
            tararchiveinputstream.close();
         }

         if (p_86993_ != null) {
            p_86993_.delete();
         }

         try {
            LevelStorageSource.LevelStorageAccess levelstoragesource$levelstorageaccess = p_86994_.createAccess(s);

            try {
               levelstoragesource$levelstorageaccess.renameLevel(s.trim());
               Path path = levelstoragesource$levelstorageaccess.getLevelPath(LevelResource.LEVEL_DATA_FILE);
               deletePlayerTag(path.toFile());
            } catch (Throwable throwable3) {
               if (levelstoragesource$levelstorageaccess != null) {
                  try {
                     levelstoragesource$levelstorageaccess.close();
                  } catch (Throwable throwable) {
                     throwable3.addSuppressed(throwable);
                  }
               }

               throw throwable3;
            }

            if (levelstoragesource$levelstorageaccess != null) {
               levelstoragesource$levelstorageaccess.close();
            }
         } catch (IOException ioexception) {
            LOGGER.error("Failed to rename unpacked realms level {}", s, ioexception);
         }

         this.resourcePackPath = new File(file1, s + File.separator + "resources.zip");
      }

   }

   private static void deletePlayerTag(File p_86988_) {
      if (p_86988_.exists()) {
         try {
            CompoundTag compoundtag = NbtIo.readCompressed(p_86988_);
            CompoundTag compoundtag1 = compoundtag.getCompound("Data");
            compoundtag1.remove("Player");
            NbtIo.writeCompressed(compoundtag, p_86988_);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   static class DownloadCountingOutputStream extends CountingOutputStream {
      @Nullable
      private ActionListener listener;

      public DownloadCountingOutputStream(OutputStream p_193509_) {
         super(p_193509_);
      }

      public void setListener(ActionListener p_87017_) {
         this.listener = p_87017_;
      }

      protected void afterWrite(int p_87019_) throws IOException {
         super.afterWrite(p_87019_);
         if (this.listener != null) {
            this.listener.actionPerformed(new ActionEvent(this, 0, (String)null));
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ProgressListener implements ActionListener {
      private final String worldName;
      private final File tempFile;
      private final LevelStorageSource levelStorageSource;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

      ProgressListener(String p_87027_, File p_87028_, LevelStorageSource p_87029_, RealmsDownloadLatestWorldScreen.DownloadStatus p_87030_) {
         this.worldName = p_87027_;
         this.tempFile = p_87028_;
         this.levelStorageSource = p_87029_;
         this.downloadStatus = p_87030_;
      }

      public void actionPerformed(ActionEvent p_87039_) {
         this.downloadStatus.bytesWritten = ((FileDownload.DownloadCountingOutputStream)p_87039_.getSource()).getByteCount();
         if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
            try {
               FileDownload.this.extracting = true;
               FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
            } catch (IOException ioexception) {
               FileDownload.LOGGER.error("Error extracting archive", (Throwable)ioexception);
               FileDownload.this.error = true;
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   class ResourcePackProgressListener implements ActionListener {
      private final File tempFile;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
      private final WorldDownload worldDownload;

      ResourcePackProgressListener(File p_87046_, RealmsDownloadLatestWorldScreen.DownloadStatus p_87047_, WorldDownload p_87048_) {
         this.tempFile = p_87046_;
         this.downloadStatus = p_87047_;
         this.worldDownload = p_87048_;
      }

      public void actionPerformed(ActionEvent p_87056_) {
         this.downloadStatus.bytesWritten = ((FileDownload.DownloadCountingOutputStream)p_87056_.getSource()).getByteCount();
         if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
            try {
               String s = Hashing.sha1().hashBytes(Files.toByteArray(this.tempFile)).toString();
               if (s.equals(this.worldDownload.resourcePackHash)) {
                  FileUtils.copyFile(this.tempFile, FileDownload.this.resourcePackPath);
                  FileDownload.this.finished = true;
               } else {
                  FileDownload.LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", this.worldDownload.resourcePackHash, s);
                  FileUtils.deleteQuietly(this.tempFile);
                  FileDownload.this.error = true;
               }
            } catch (IOException ioexception) {
               FileDownload.LOGGER.error("Error copying resourcepack file: {}", (Object)ioexception.getMessage());
               FileDownload.this.error = true;
            }
         }

      }
   }
}