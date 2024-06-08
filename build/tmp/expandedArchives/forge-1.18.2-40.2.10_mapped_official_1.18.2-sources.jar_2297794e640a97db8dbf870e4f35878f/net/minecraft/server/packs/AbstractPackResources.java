package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class AbstractPackResources implements PackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   public final File file;

   public AbstractPackResources(File p_10207_) {
      this.file = p_10207_;
   }

   private static String getPathFromLocation(PackType p_10227_, ResourceLocation p_10228_) {
      return String.format("%s/%s/%s", p_10227_.getDirectory(), p_10228_.getNamespace(), p_10228_.getPath());
   }

   protected static String getRelativePath(File p_10218_, File p_10219_) {
      return p_10218_.toURI().relativize(p_10219_.toURI()).getPath();
   }

   public InputStream getResource(PackType p_10210_, ResourceLocation p_10211_) throws IOException {
      return this.getResource(getPathFromLocation(p_10210_, p_10211_));
   }

   public boolean hasResource(PackType p_10222_, ResourceLocation p_10223_) {
      return this.hasResource(getPathFromLocation(p_10222_, p_10223_));
   }

   protected abstract InputStream getResource(String p_10220_) throws IOException;

   public InputStream getRootResource(String p_10225_) throws IOException {
      if (!p_10225_.contains("/") && !p_10225_.contains("\\")) {
         return this.getResource(p_10225_);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   protected abstract boolean hasResource(String p_10229_);

   protected void logWarning(String p_10231_) {
      LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", p_10231_, this.file);
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> p_10213_) throws IOException {
      InputStream inputstream = this.getResource("pack.mcmeta");

      Object object;
      try {
         object = getMetadataFromStream(p_10213_, inputstream);
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

      return (T)object;
   }

   @Nullable
   public static <T> T getMetadataFromStream(MetadataSectionSerializer<T> p_10215_, InputStream p_10216_) {
      JsonObject jsonobject;
      try {
         BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(p_10216_, StandardCharsets.UTF_8));

         try {
            jsonobject = GsonHelper.parse(bufferedreader);
         } catch (Throwable throwable1) {
            try {
               bufferedreader.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }

            throw throwable1;
         }

         bufferedreader.close();
      } catch (Exception exception1) {
         LOGGER.error("Couldn't load {} metadata", p_10215_.getMetadataSectionName(), exception1);
         return (T)null;
      }

      if (!jsonobject.has(p_10215_.getMetadataSectionName())) {
         return (T)null;
      } else {
         try {
            return p_10215_.fromJson(GsonHelper.getAsJsonObject(jsonobject, p_10215_.getMetadataSectionName()));
         } catch (Exception exception) {
            LOGGER.error("Couldn't load {} metadata", p_10215_.getMetadataSectionName(), exception);
            return (T)null;
         }
      }
   }

   public String getName() {
      return this.file.getName();
   }

   @Override
   public String toString()
   {
      return String.format(java.util.Locale.ROOT, "%s: %s", getClass().getName(), file.getPath());
   }
}
