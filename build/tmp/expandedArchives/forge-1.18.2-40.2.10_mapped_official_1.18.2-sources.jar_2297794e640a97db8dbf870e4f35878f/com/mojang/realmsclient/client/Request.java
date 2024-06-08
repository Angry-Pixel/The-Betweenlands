package com.mojang.realmsclient.client;

import com.mojang.realmsclient.exception.RealmsHttpException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Request<T extends Request<T>> {
   protected HttpURLConnection connection;
   private boolean connected;
   protected String url;
   private static final int DEFAULT_READ_TIMEOUT = 60000;
   private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

   public Request(String p_87310_, int p_87311_, int p_87312_) {
      try {
         this.url = p_87310_;
         Proxy proxy = RealmsClientConfig.getProxy();
         if (proxy != null) {
            this.connection = (HttpURLConnection)(new URL(p_87310_)).openConnection(proxy);
         } else {
            this.connection = (HttpURLConnection)(new URL(p_87310_)).openConnection();
         }

         this.connection.setConnectTimeout(p_87311_);
         this.connection.setReadTimeout(p_87312_);
      } catch (MalformedURLException malformedurlexception) {
         throw new RealmsHttpException(malformedurlexception.getMessage(), malformedurlexception);
      } catch (IOException ioexception) {
         throw new RealmsHttpException(ioexception.getMessage(), ioexception);
      }
   }

   public void cookie(String p_87323_, String p_87324_) {
      cookie(this.connection, p_87323_, p_87324_);
   }

   public static void cookie(HttpURLConnection p_87336_, String p_87337_, String p_87338_) {
      String s = p_87336_.getRequestProperty("Cookie");
      if (s == null) {
         p_87336_.setRequestProperty("Cookie", p_87337_ + "=" + p_87338_);
      } else {
         p_87336_.setRequestProperty("Cookie", s + ";" + p_87337_ + "=" + p_87338_);
      }

   }

   public T header(String p_167286_, String p_167287_) {
      this.connection.addRequestProperty(p_167286_, p_167287_);
      return (T)this;
   }

   public int getRetryAfterHeader() {
      return getRetryAfterHeader(this.connection);
   }

   public static int getRetryAfterHeader(HttpURLConnection p_87331_) {
      String s = p_87331_.getHeaderField("Retry-After");

      try {
         return Integer.valueOf(s);
      } catch (Exception exception) {
         return 5;
      }
   }

   public int responseCode() {
      try {
         this.connect();
         return this.connection.getResponseCode();
      } catch (Exception exception) {
         throw new RealmsHttpException(exception.getMessage(), exception);
      }
   }

   public String text() {
      try {
         this.connect();
         String s;
         if (this.responseCode() >= 400) {
            s = this.read(this.connection.getErrorStream());
         } else {
            s = this.read(this.connection.getInputStream());
         }

         this.dispose();
         return s;
      } catch (IOException ioexception) {
         throw new RealmsHttpException(ioexception.getMessage(), ioexception);
      }
   }

   private String read(@Nullable InputStream p_87315_) throws IOException {
      if (p_87315_ == null) {
         return "";
      } else {
         InputStreamReader inputstreamreader = new InputStreamReader(p_87315_, StandardCharsets.UTF_8);
         StringBuilder stringbuilder = new StringBuilder();

         for(int i = inputstreamreader.read(); i != -1; i = inputstreamreader.read()) {
            stringbuilder.append((char)i);
         }

         return stringbuilder.toString();
      }
   }

   private void dispose() {
      byte[] abyte = new byte[1024];

      try {
         InputStream inputstream = this.connection.getInputStream();

         while(inputstream.read(abyte) > 0) {
         }

         inputstream.close();
         return;
      } catch (Exception exception) {
         try {
            InputStream inputstream1 = this.connection.getErrorStream();
            if (inputstream1 != null) {
               while(inputstream1.read(abyte) > 0) {
               }

               inputstream1.close();
               return;
            }
         } catch (IOException ioexception) {
            return;
         }
      } finally {
         if (this.connection != null) {
            this.connection.disconnect();
         }

      }

   }

   protected T connect() {
      if (this.connected) {
         return (T)this;
      } else {
         T t = this.doConnect();
         this.connected = true;
         return t;
      }
   }

   protected abstract T doConnect();

   public static Request<?> get(String p_87317_) {
      return new Request.Get(p_87317_, 5000, 60000);
   }

   public static Request<?> get(String p_87319_, int p_87320_, int p_87321_) {
      return new Request.Get(p_87319_, p_87320_, p_87321_);
   }

   public static Request<?> post(String p_87343_, String p_87344_) {
      return new Request.Post(p_87343_, p_87344_, 5000, 60000);
   }

   public static Request<?> post(String p_87326_, String p_87327_, int p_87328_, int p_87329_) {
      return new Request.Post(p_87326_, p_87327_, p_87328_, p_87329_);
   }

   public static Request<?> delete(String p_87341_) {
      return new Request.Delete(p_87341_, 5000, 60000);
   }

   public static Request<?> put(String p_87354_, String p_87355_) {
      return new Request.Put(p_87354_, p_87355_, 5000, 60000);
   }

   public static Request<?> put(String p_87346_, String p_87347_, int p_87348_, int p_87349_) {
      return new Request.Put(p_87346_, p_87347_, p_87348_, p_87349_);
   }

   public String getHeader(String p_87352_) {
      return getHeader(this.connection, p_87352_);
   }

   public static String getHeader(HttpURLConnection p_87333_, String p_87334_) {
      try {
         return p_87333_.getHeaderField(p_87334_);
      } catch (Exception exception) {
         return "";
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Delete extends Request<Request.Delete> {
      public Delete(String p_87359_, int p_87360_, int p_87361_) {
         super(p_87359_, p_87360_, p_87361_);
      }

      public Request.Delete doConnect() {
         try {
            this.connection.setDoOutput(true);
            this.connection.setRequestMethod("DELETE");
            this.connection.connect();
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Get extends Request<Request.Get> {
      public Get(String p_87365_, int p_87366_, int p_87367_) {
         super(p_87365_, p_87366_, p_87367_);
      }

      public Request.Get doConnect() {
         try {
            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("GET");
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Post extends Request<Request.Post> {
      private final String content;

      public Post(String p_87372_, String p_87373_, int p_87374_, int p_87375_) {
         super(p_87372_, p_87374_, p_87375_);
         this.content = p_87373_;
      }

      public Request.Post doConnect() {
         try {
            if (this.content != null) {
               this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("POST");
            OutputStream outputstream = this.connection.getOutputStream();
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream, "UTF-8");
            outputstreamwriter.write(this.content);
            outputstreamwriter.close();
            outputstream.flush();
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Put extends Request<Request.Put> {
      private final String content;

      public Put(String p_87380_, String p_87381_, int p_87382_, int p_87383_) {
         super(p_87380_, p_87382_, p_87383_);
         this.content = p_87381_;
      }

      public Request.Put doConnect() {
         try {
            if (this.content != null) {
               this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }

            this.connection.setDoOutput(true);
            this.connection.setDoInput(true);
            this.connection.setRequestMethod("PUT");
            OutputStream outputstream = this.connection.getOutputStream();
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream, "UTF-8");
            outputstreamwriter.write(this.content);
            outputstreamwriter.close();
            outputstream.flush();
            return this;
         } catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
         }
      }
   }
}