package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldTemplate extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public String id = "";
   public String name = "";
   public String version = "";
   public String author = "";
   public String link = "";
   @Nullable
   public String image;
   public String trailer = "";
   public String recommendedPlayers = "";
   public WorldTemplate.WorldTemplateType type = WorldTemplate.WorldTemplateType.WORLD_TEMPLATE;

   public static WorldTemplate parse(JsonObject p_87739_) {
      WorldTemplate worldtemplate = new WorldTemplate();

      try {
         worldtemplate.id = JsonUtils.getStringOr("id", p_87739_, "");
         worldtemplate.name = JsonUtils.getStringOr("name", p_87739_, "");
         worldtemplate.version = JsonUtils.getStringOr("version", p_87739_, "");
         worldtemplate.author = JsonUtils.getStringOr("author", p_87739_, "");
         worldtemplate.link = JsonUtils.getStringOr("link", p_87739_, "");
         worldtemplate.image = JsonUtils.getStringOr("image", p_87739_, (String)null);
         worldtemplate.trailer = JsonUtils.getStringOr("trailer", p_87739_, "");
         worldtemplate.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", p_87739_, "");
         worldtemplate.type = WorldTemplate.WorldTemplateType.valueOf(JsonUtils.getStringOr("type", p_87739_, WorldTemplate.WorldTemplateType.WORLD_TEMPLATE.name()));
      } catch (Exception exception) {
         LOGGER.error("Could not parse WorldTemplate: {}", (Object)exception.getMessage());
      }

      return worldtemplate;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum WorldTemplateType {
      WORLD_TEMPLATE,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;
   }
}