package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldDownload extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public String downloadLink;
   public String resourcePackUrl;
   public String resourcePackHash;

   public static WorldDownload parse(String p_87725_) {
      JsonParser jsonparser = new JsonParser();
      JsonObject jsonobject = jsonparser.parse(p_87725_).getAsJsonObject();
      WorldDownload worlddownload = new WorldDownload();

      try {
         worlddownload.downloadLink = JsonUtils.getStringOr("downloadLink", jsonobject, "");
         worlddownload.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", jsonobject, "");
         worlddownload.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", jsonobject, "");
      } catch (Exception exception) {
         LOGGER.error("Could not parse WorldDownload: {}", (Object)exception.getMessage());
      }

      return worlddownload;
   }
}