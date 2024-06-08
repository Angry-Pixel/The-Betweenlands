package com.mojang.realmsclient.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JsonUtils {
   public static String getStringOr(String p_90162_, JsonObject p_90163_, String p_90164_) {
      JsonElement jsonelement = p_90163_.get(p_90162_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_90164_ : jsonelement.getAsString();
      } else {
         return p_90164_;
      }
   }

   public static int getIntOr(String p_90154_, JsonObject p_90155_, int p_90156_) {
      JsonElement jsonelement = p_90155_.get(p_90154_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_90156_ : jsonelement.getAsInt();
      } else {
         return p_90156_;
      }
   }

   public static long getLongOr(String p_90158_, JsonObject p_90159_, long p_90160_) {
      JsonElement jsonelement = p_90159_.get(p_90158_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_90160_ : jsonelement.getAsLong();
      } else {
         return p_90160_;
      }
   }

   public static boolean getBooleanOr(String p_90166_, JsonObject p_90167_, boolean p_90168_) {
      JsonElement jsonelement = p_90167_.get(p_90166_);
      if (jsonelement != null) {
         return jsonelement.isJsonNull() ? p_90168_ : jsonelement.getAsBoolean();
      } else {
         return p_90168_;
      }
   }

   public static Date getDateOr(String p_90151_, JsonObject p_90152_) {
      JsonElement jsonelement = p_90152_.get(p_90151_);
      return jsonelement != null ? new Date(Long.parseLong(jsonelement.getAsString())) : new Date();
   }
}