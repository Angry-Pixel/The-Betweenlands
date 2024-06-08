package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class ChainedJsonException extends IOException {
   private final List<ChainedJsonException.Entry> entries = Lists.newArrayList();
   private final String message;

   public ChainedJsonException(String p_135902_) {
      this.entries.add(new ChainedJsonException.Entry());
      this.message = p_135902_;
   }

   public ChainedJsonException(String p_135904_, Throwable p_135905_) {
      super(p_135905_);
      this.entries.add(new ChainedJsonException.Entry());
      this.message = p_135904_;
   }

   public void prependJsonKey(String p_135909_) {
      this.entries.get(0).addJsonKey(p_135909_);
   }

   public void setFilenameAndFlush(String p_135911_) {
      (this.entries.get(0)).filename = p_135911_;
      this.entries.add(0, new ChainedJsonException.Entry());
   }

   public String getMessage() {
      return "Invalid " + this.entries.get(this.entries.size() - 1) + ": " + this.message;
   }

   public static ChainedJsonException forException(Exception p_135907_) {
      if (p_135907_ instanceof ChainedJsonException) {
         return (ChainedJsonException)p_135907_;
      } else {
         String s = p_135907_.getMessage();
         if (p_135907_ instanceof FileNotFoundException) {
            s = "File not found";
         }

         return new ChainedJsonException(s, p_135907_);
      }
   }

   public static class Entry {
      @Nullable
      String filename;
      private final List<String> jsonKeys = Lists.newArrayList();

      Entry() {
      }

      void addJsonKey(String p_135919_) {
         this.jsonKeys.add(0, p_135919_);
      }

      @Nullable
      public String getFilename() {
         return this.filename;
      }

      public String getJsonKeys() {
         return StringUtils.join(this.jsonKeys, "->");
      }

      public String toString() {
         if (this.filename != null) {
            return this.jsonKeys.isEmpty() ? this.filename : this.filename + " " + this.getJsonKeys();
         } else {
            return this.jsonKeys.isEmpty() ? "(Unknown file)" : "(Unknown file) " + this.getJsonKeys();
         }
      }
   }
}