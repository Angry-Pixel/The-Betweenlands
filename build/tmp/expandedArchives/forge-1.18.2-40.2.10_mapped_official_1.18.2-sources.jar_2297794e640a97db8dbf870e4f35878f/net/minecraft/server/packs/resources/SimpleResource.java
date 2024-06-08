package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

public class SimpleResource implements Resource {
   private final String sourceName;
   private final ResourceLocation location;
   private final InputStream resourceStream;
   private final InputStream metadataStream;
   private boolean triedMetadata;
   private JsonObject metadata;

   public SimpleResource(String p_10929_, ResourceLocation p_10930_, InputStream p_10931_, @Nullable InputStream p_10932_) {
      this.sourceName = p_10929_;
      this.location = p_10930_;
      this.resourceStream = p_10931_;
      this.metadataStream = p_10932_;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public InputStream getInputStream() {
      return this.resourceStream;
   }

   public boolean hasMetadata() {
      return this.metadataStream != null;
   }

   @Nullable
   public <T> T getMetadata(MetadataSectionSerializer<T> p_10935_) {
      if (!this.hasMetadata()) {
         return (T)null;
      } else {
         if (this.metadata == null && !this.triedMetadata) {
            this.triedMetadata = true;
            BufferedReader bufferedreader = null;

            try {
               bufferedreader = new BufferedReader(new InputStreamReader(this.metadataStream, StandardCharsets.UTF_8));
               this.metadata = GsonHelper.parse(bufferedreader);
            } finally {
               IOUtils.closeQuietly((Reader)bufferedreader);
            }
         }

         if (this.metadata == null) {
            return (T)null;
         } else {
            String s = p_10935_.getMetadataSectionName();
            return (T)(this.metadata.has(s) ? p_10935_.fromJson(GsonHelper.getAsJsonObject(this.metadata, s)) : null);
         }
      }
   }

   public String getSourceName() {
      return this.sourceName;
   }

   public boolean equals(Object p_10941_) {
      if (this == p_10941_) {
         return true;
      } else if (!(p_10941_ instanceof SimpleResource)) {
         return false;
      } else {
         SimpleResource simpleresource = (SimpleResource)p_10941_;
         if (this.location != null) {
            if (!this.location.equals(simpleresource.location)) {
               return false;
            }
         } else if (simpleresource.location != null) {
            return false;
         }

         if (this.sourceName != null) {
            if (!this.sourceName.equals(simpleresource.sourceName)) {
               return false;
            }
         } else if (simpleresource.sourceName != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int i = this.sourceName != null ? this.sourceName.hashCode() : 0;
      return 31 * i + (this.location != null ? this.location.hashCode() : 0);
   }

   public void close() throws IOException {
      this.resourceStream.close();
      if (this.metadataStream != null) {
         this.metadataStream.close();
      }

   }
}