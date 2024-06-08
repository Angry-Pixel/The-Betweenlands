package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.SharedConstants;
import org.slf4j.Logger;

public class Eula {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path file;
   private final boolean agreed;

   public Eula(Path p_135943_) {
      this.file = p_135943_;
      this.agreed = SharedConstants.IS_RUNNING_IN_IDE || net.minecraftforge.gametest.ForgeGameTestHooks.isGametestServer() || this.readFile(); // Forge: Automatically agree to EULA for gametest servers to aid CI
   }

   private boolean readFile() {
      try {
         InputStream inputstream = Files.newInputStream(this.file);

         boolean flag;
         try {
            Properties properties = new Properties();
            properties.load(inputstream);
            flag = Boolean.parseBoolean(properties.getProperty("eula", "false"));
         } catch (Throwable throwable1) {
            if (inputstream != null) {
               try {
                  inputstream.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (inputstream != null) {
            inputstream.close();
         }

         return flag;
      } catch (Exception exception) {
         LOGGER.warn("Failed to load {}", (Object)this.file);
         this.saveDefaults();
         return false;
      }
   }

   public boolean hasAgreedToEULA() {
      return this.agreed;
   }

   private void saveDefaults() {
      if (!SharedConstants.IS_RUNNING_IN_IDE) {
         try {
            OutputStream outputstream = Files.newOutputStream(this.file);

            try {
               Properties properties = new Properties();
               properties.setProperty("eula", "false");
               properties.store(outputstream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
            } catch (Throwable throwable1) {
               if (outputstream != null) {
                  try {
                     outputstream.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (outputstream != null) {
               outputstream.close();
            }
         } catch (Exception exception) {
            LOGGER.warn("Failed to save {}", this.file, exception);
         }

      }
   }
}
