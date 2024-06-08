package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class UserBanListEntry extends BanListEntry<GameProfile> {
   public UserBanListEntry(GameProfile p_11436_) {
      this(p_11436_, (Date)null, (String)null, (Date)null, (String)null);
   }

   public UserBanListEntry(GameProfile p_11438_, @Nullable Date p_11439_, @Nullable String p_11440_, @Nullable Date p_11441_, @Nullable String p_11442_) {
      super(p_11438_, p_11439_, p_11440_, p_11441_, p_11442_);
   }

   public UserBanListEntry(JsonObject p_11434_) {
      super(createGameProfile(p_11434_), p_11434_);
   }

   protected void serialize(JsonObject p_11444_) {
      if (this.getUser() != null) {
         p_11444_.addProperty("uuid", this.getUser().getId() == null ? "" : this.getUser().getId().toString());
         p_11444_.addProperty("name", this.getUser().getName());
         super.serialize(p_11444_);
      }
   }

   public Component getDisplayName() {
      GameProfile gameprofile = this.getUser();
      return new TextComponent(gameprofile.getName() != null ? gameprofile.getName() : Objects.toString(gameprofile.getId(), "(Unknown)"));
   }

   private static GameProfile createGameProfile(JsonObject p_11446_) {
      if (p_11446_.has("uuid") && p_11446_.has("name")) {
         String s = p_11446_.get("uuid").getAsString();

         UUID uuid;
         try {
            uuid = UUID.fromString(s);
         } catch (Throwable throwable) {
            return null;
         }

         return new GameProfile(uuid, p_11446_.get("name").getAsString());
      } else {
         return null;
      }
   }
}