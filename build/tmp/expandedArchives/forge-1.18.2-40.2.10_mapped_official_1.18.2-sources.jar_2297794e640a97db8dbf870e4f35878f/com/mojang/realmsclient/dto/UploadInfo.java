package com.mojang.realmsclient.dto;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UploadInfo extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String DEFAULT_SCHEMA = "http://";
   private static final int DEFAULT_PORT = 8080;
   private static final Pattern URI_SCHEMA_PATTERN = Pattern.compile("^[a-zA-Z][-a-zA-Z0-9+.]+:");
   private final boolean worldClosed;
   @Nullable
   private final String token;
   private final URI uploadEndpoint;

   private UploadInfo(boolean p_87693_, @Nullable String p_87694_, URI p_87695_) {
      this.worldClosed = p_87693_;
      this.token = p_87694_;
      this.uploadEndpoint = p_87695_;
   }

   @Nullable
   public static UploadInfo parse(String p_87701_) {
      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_87701_).getAsJsonObject();
         String s = JsonUtils.getStringOr("uploadEndpoint", jsonobject, (String)null);
         if (s != null) {
            int i = JsonUtils.getIntOr("port", jsonobject, -1);
            URI uri = assembleUri(s, i);
            if (uri != null) {
               boolean flag = JsonUtils.getBooleanOr("worldClosed", jsonobject, false);
               String s1 = JsonUtils.getStringOr("token", jsonobject, (String)null);
               return new UploadInfo(flag, s1, uri);
            }
         }
      } catch (Exception exception) {
         LOGGER.error("Could not parse UploadInfo: {}", (Object)exception.getMessage());
      }

      return null;
   }

   @Nullable
   @VisibleForTesting
   public static URI assembleUri(String p_87703_, int p_87704_) {
      Matcher matcher = URI_SCHEMA_PATTERN.matcher(p_87703_);
      String s = ensureEndpointSchema(p_87703_, matcher);

      try {
         URI uri = new URI(s);
         int i = selectPortOrDefault(p_87704_, uri.getPort());
         return i != uri.getPort() ? new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), i, uri.getPath(), uri.getQuery(), uri.getFragment()) : uri;
      } catch (URISyntaxException urisyntaxexception) {
         LOGGER.warn("Failed to parse URI {}", s, urisyntaxexception);
         return null;
      }
   }

   private static int selectPortOrDefault(int p_87698_, int p_87699_) {
      if (p_87698_ != -1) {
         return p_87698_;
      } else {
         return p_87699_ != -1 ? p_87699_ : 8080;
      }
   }

   private static String ensureEndpointSchema(String p_87706_, Matcher p_87707_) {
      return p_87707_.find() ? p_87706_ : "http://" + p_87706_;
   }

   public static String createRequest(@Nullable String p_87710_) {
      JsonObject jsonobject = new JsonObject();
      if (p_87710_ != null) {
         jsonobject.addProperty("token", p_87710_);
      }

      return jsonobject.toString();
   }

   @Nullable
   public String getToken() {
      return this.token;
   }

   public URI getUploadEndpoint() {
      return this.uploadEndpoint;
   }

   public boolean isWorldClosed() {
      return this.worldClosed;
   }
}