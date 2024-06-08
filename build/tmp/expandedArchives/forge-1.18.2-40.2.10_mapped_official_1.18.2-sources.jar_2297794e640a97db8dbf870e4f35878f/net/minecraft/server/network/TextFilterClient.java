package net.minecraft.server.network;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.thread.ProcessorMailbox;
import org.slf4j.Logger;

public class TextFilterClient implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ThreadFactory THREAD_FACTORY = (p_10148_) -> {
      Thread thread = new Thread(p_10148_);
      thread.setName("Chat-Filter-Worker-" + WORKER_COUNT.getAndIncrement());
      return thread;
   };
   private final URL chatEndpoint;
   final URL joinEndpoint;
   final URL leaveEndpoint;
   private final String authKey;
   private final int ruleId;
   private final String serverId;
   private final String roomId;
   final TextFilterClient.IgnoreStrategy chatIgnoreStrategy;
   final ExecutorService workerPool;

   private TextFilterClient(URL p_212236_, URL p_212237_, URL p_212238_, String p_212239_, int p_212240_, String p_212241_, String p_212242_, TextFilterClient.IgnoreStrategy p_212243_, int p_212244_) {
      this.authKey = p_212239_;
      this.ruleId = p_212240_;
      this.serverId = p_212241_;
      this.roomId = p_212242_;
      this.chatIgnoreStrategy = p_212243_;
      this.chatEndpoint = p_212236_;
      this.joinEndpoint = p_212237_;
      this.leaveEndpoint = p_212238_;
      this.workerPool = Executors.newFixedThreadPool(p_212244_, THREAD_FACTORY);
   }

   private static URL getEndpoint(URI p_212246_, @Nullable JsonObject p_212247_, String p_212248_, String p_212249_) throws MalformedURLException {
      String s = p_212247_ != null ? GsonHelper.getAsString(p_212247_, p_212248_, p_212249_) : p_212249_;
      return p_212246_.resolve("/" + s).toURL();
   }

   @Nullable
   public static TextFilterClient createFromConfig(String p_143737_) {
      if (Strings.isNullOrEmpty(p_143737_)) {
         return null;
      } else {
         try {
            JsonObject jsonobject = GsonHelper.parse(p_143737_);
            URI uri = new URI(GsonHelper.getAsString(jsonobject, "apiServer"));
            String s = GsonHelper.getAsString(jsonobject, "apiKey");
            if (s.isEmpty()) {
               throw new IllegalArgumentException("Missing API key");
            } else {
               int i = GsonHelper.getAsInt(jsonobject, "ruleId", 1);
               String s1 = GsonHelper.getAsString(jsonobject, "serverId", "");
               String s2 = GsonHelper.getAsString(jsonobject, "roomId", "Java:Chat");
               int j = GsonHelper.getAsInt(jsonobject, "hashesToDrop", -1);
               int k = GsonHelper.getAsInt(jsonobject, "maxConcurrentRequests", 7);
               JsonObject jsonobject1 = GsonHelper.getAsJsonObject(jsonobject, "endpoints", (JsonObject)null);
               URL url = getEndpoint(uri, jsonobject1, "chat", "v1/chat");
               URL url1 = getEndpoint(uri, jsonobject1, "join", "v1/join");
               URL url2 = getEndpoint(uri, jsonobject1, "leave", "v1/leave");
               TextFilterClient.IgnoreStrategy textfilterclient$ignorestrategy = TextFilterClient.IgnoreStrategy.select(j);
               return new TextFilterClient(url, url1, url2, Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.US_ASCII)), i, s1, s2, textfilterclient$ignorestrategy, k);
            }
         } catch (Exception exception) {
            LOGGER.warn("Failed to parse chat filter config {}", p_143737_, exception);
            return null;
         }
      }
   }

   void processJoinOrLeave(GameProfile p_10142_, URL p_10143_, Executor p_10144_) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("server", this.serverId);
      jsonobject.addProperty("room", this.roomId);
      jsonobject.addProperty("user_id", p_10142_.getId().toString());
      jsonobject.addProperty("user_display_name", p_10142_.getName());
      p_10144_.execute(() -> {
         try {
            this.processRequest(jsonobject, p_10143_);
         } catch (Exception exception) {
            LOGGER.warn("Failed to send join/leave packet to {} for player {}", p_10143_, p_10142_, exception);
         }

      });
   }

   CompletableFuture<TextFilter.FilteredText> requestMessageProcessing(GameProfile p_10137_, String p_10138_, TextFilterClient.IgnoreStrategy p_10139_, Executor p_10140_) {
      if (p_10138_.isEmpty()) {
         return CompletableFuture.completedFuture(TextFilter.FilteredText.EMPTY);
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("rule", this.ruleId);
         jsonobject.addProperty("server", this.serverId);
         jsonobject.addProperty("room", this.roomId);
         jsonobject.addProperty("player", p_10137_.getId().toString());
         jsonobject.addProperty("player_display_name", p_10137_.getName());
         jsonobject.addProperty("text", p_10138_);
         return CompletableFuture.supplyAsync(() -> {
            try {
               JsonObject jsonobject1 = this.processRequestResponse(jsonobject, this.chatEndpoint);
               boolean flag = GsonHelper.getAsBoolean(jsonobject1, "response", false);
               if (flag) {
                  return TextFilter.FilteredText.passThrough(p_10138_);
               } else {
                  String s = GsonHelper.getAsString(jsonobject1, "hashed", (String)null);
                  if (s == null) {
                     return TextFilter.FilteredText.fullyFiltered(p_10138_);
                  } else {
                     int i = GsonHelper.getAsJsonArray(jsonobject1, "hashes").size();
                     return p_10139_.shouldIgnore(s, i) ? TextFilter.FilteredText.fullyFiltered(p_10138_) : new TextFilter.FilteredText(p_10138_, s);
                  }
               }
            } catch (Exception exception) {
               LOGGER.warn("Failed to validate message '{}'", p_10138_, exception);
               return TextFilter.FilteredText.fullyFiltered(p_10138_);
            }
         }, p_10140_);
      }
   }

   public void close() {
      this.workerPool.shutdownNow();
   }

   private void drainStream(InputStream p_10146_) throws IOException {
      byte[] abyte = new byte[1024];

      while(p_10146_.read(abyte) != -1) {
      }

   }

   private JsonObject processRequestResponse(JsonObject p_10128_, URL p_10129_) throws IOException {
      HttpURLConnection httpurlconnection = this.makeRequest(p_10128_, p_10129_);
      InputStream inputstream = httpurlconnection.getInputStream();

      JsonObject jsonobject;
      label89: {
         try {
            if (httpurlconnection.getResponseCode() == 204) {
               jsonobject = new JsonObject();
               break label89;
            }

            try {
               jsonobject = Streams.parse(new JsonReader(new InputStreamReader(inputstream))).getAsJsonObject();
            } finally {
               this.drainStream(inputstream);
            }
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

         return jsonobject;
      }

      if (inputstream != null) {
         inputstream.close();
      }

      return jsonobject;
   }

   private void processRequest(JsonObject p_10152_, URL p_10153_) throws IOException {
      HttpURLConnection httpurlconnection = this.makeRequest(p_10152_, p_10153_);
      InputStream inputstream = httpurlconnection.getInputStream();

      try {
         this.drainStream(inputstream);
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

   }

   private HttpURLConnection makeRequest(JsonObject p_10157_, URL p_10158_) throws IOException {
      HttpURLConnection httpurlconnection = (HttpURLConnection)p_10158_.openConnection();
      httpurlconnection.setConnectTimeout(15000);
      httpurlconnection.setReadTimeout(2000);
      httpurlconnection.setUseCaches(false);
      httpurlconnection.setDoOutput(true);
      httpurlconnection.setDoInput(true);
      httpurlconnection.setRequestMethod("POST");
      httpurlconnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      httpurlconnection.setRequestProperty("Accept", "application/json");
      httpurlconnection.setRequestProperty("Authorization", "Basic " + this.authKey);
      httpurlconnection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getCurrentVersion().getName());
      OutputStreamWriter outputstreamwriter = new OutputStreamWriter(httpurlconnection.getOutputStream(), StandardCharsets.UTF_8);

      try {
         JsonWriter jsonwriter = new JsonWriter(outputstreamwriter);

         try {
            Streams.write(p_10157_, jsonwriter);
         } catch (Throwable throwable2) {
            try {
               jsonwriter.close();
            } catch (Throwable throwable1) {
               throwable2.addSuppressed(throwable1);
            }

            throw throwable2;
         }

         jsonwriter.close();
      } catch (Throwable throwable3) {
         try {
            outputstreamwriter.close();
         } catch (Throwable throwable) {
            throwable3.addSuppressed(throwable);
         }

         throw throwable3;
      }

      outputstreamwriter.close();
      int i = httpurlconnection.getResponseCode();
      if (i >= 200 && i < 300) {
         return httpurlconnection;
      } else {
         throw new TextFilterClient.RequestFailedException(i + " " + httpurlconnection.getResponseMessage());
      }
   }

   public TextFilter createContext(GameProfile p_10135_) {
      return new TextFilterClient.PlayerContext(p_10135_);
   }

   @FunctionalInterface
   public interface IgnoreStrategy {
      TextFilterClient.IgnoreStrategy NEVER_IGNORE = (p_10169_, p_10170_) -> {
         return false;
      };
      TextFilterClient.IgnoreStrategy IGNORE_FULLY_FILTERED = (p_10166_, p_10167_) -> {
         return p_10166_.length() == p_10167_;
      };

      static TextFilterClient.IgnoreStrategy ignoreOverThreshold(int p_143739_) {
         return (p_143742_, p_143743_) -> {
            return p_143743_ >= p_143739_;
         };
      }

      static TextFilterClient.IgnoreStrategy select(int p_143745_) {
         switch(p_143745_) {
         case -1:
            return NEVER_IGNORE;
         case 0:
            return IGNORE_FULLY_FILTERED;
         default:
            return ignoreOverThreshold(p_143745_);
         }
      }

      boolean shouldIgnore(String p_10172_, int p_10173_);
   }

   class PlayerContext implements TextFilter {
      private final GameProfile profile;
      private final Executor streamExecutor;

      PlayerContext(GameProfile p_10179_) {
         this.profile = p_10179_;
         ProcessorMailbox<Runnable> processormailbox = ProcessorMailbox.create(TextFilterClient.this.workerPool, "chat stream for " + p_10179_.getName());
         this.streamExecutor = processormailbox::tell;
      }

      public void join() {
         TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.joinEndpoint, this.streamExecutor);
      }

      public void leave() {
         TextFilterClient.this.processJoinOrLeave(this.profile, TextFilterClient.this.leaveEndpoint, this.streamExecutor);
      }

      public CompletableFuture<List<TextFilter.FilteredText>> processMessageBundle(List<String> p_10190_) {
         List<CompletableFuture<TextFilter.FilteredText>> list = p_10190_.stream().map((p_10195_) -> {
            return TextFilterClient.this.requestMessageProcessing(this.profile, p_10195_, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
         }).collect(ImmutableList.toImmutableList());
         return Util.sequenceFailFast(list).exceptionally((p_143747_) -> {
            return ImmutableList.of();
         });
      }

      public CompletableFuture<TextFilter.FilteredText> processStreamMessage(String p_10186_) {
         return TextFilterClient.this.requestMessageProcessing(this.profile, p_10186_, TextFilterClient.this.chatIgnoreStrategy, this.streamExecutor);
      }
   }

   public static class RequestFailedException extends RuntimeException {
      RequestFailedException(String p_10199_) {
         super(p_10199_);
      }
   }
}