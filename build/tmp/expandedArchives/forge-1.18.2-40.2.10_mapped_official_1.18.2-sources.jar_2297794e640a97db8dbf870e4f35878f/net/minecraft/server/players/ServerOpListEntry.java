package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;

public class ServerOpListEntry extends StoredUserEntry<GameProfile> {
   private final int level;
   private final boolean bypassesPlayerLimit;

   public ServerOpListEntry(GameProfile p_11360_, int p_11361_, boolean p_11362_) {
      super(p_11360_);
      this.level = p_11361_;
      this.bypassesPlayerLimit = p_11362_;
   }

   public ServerOpListEntry(JsonObject p_11358_) {
      super(createGameProfile(p_11358_));
      this.level = p_11358_.has("level") ? p_11358_.get("level").getAsInt() : 0;
      this.bypassesPlayerLimit = p_11358_.has("bypassesPlayerLimit") && p_11358_.get("bypassesPlayerLimit").getAsBoolean();
   }

   public int getLevel() {
      return this.level;
   }

   public boolean getBypassesPlayerLimit() {
      return this.bypassesPlayerLimit;
   }

   protected void serialize(JsonObject p_11365_) {
      if (this.getUser() != null) {
         p_11365_.addProperty("uuid", this.getUser().getId() == null ? "" : this.getUser().getId().toString());
         p_11365_.addProperty("name", this.getUser().getName());
         p_11365_.addProperty("level", this.level);
         p_11365_.addProperty("bypassesPlayerLimit", this.bypassesPlayerLimit);
      }
   }

   @Nullable
   private static GameProfile createGameProfile(JsonObject p_11368_) {
      if (p_11368_.has("uuid") && p_11368_.has("name")) {
         String s = p_11368_.get("uuid").getAsString();

         UUID uuid;
         try {
            uuid = UUID.fromString(s);
         } catch (Throwable throwable) {
            return null;
         }

         return new GameProfile(uuid, p_11368_.get("name").getAsString());
      } else {
         return null;
      }
   }
}