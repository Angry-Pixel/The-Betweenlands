package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;

public class ServerOpList extends StoredUserList<GameProfile, ServerOpListEntry> {
   public ServerOpList(File p_11345_) {
      super(p_11345_);
   }

   protected StoredUserEntry<GameProfile> createEntry(JsonObject p_11348_) {
      return new ServerOpListEntry(p_11348_);
   }

   public String[] getUserList() {
      return this.getEntries().stream().map(StoredUserEntry::getUser).filter(Objects::nonNull).map(GameProfile::getName).toArray((p_143997_) -> {
         return new String[p_143997_];
      });
   }

   public boolean canBypassPlayerLimit(GameProfile p_11352_) {
      ServerOpListEntry serveroplistentry = this.get(p_11352_);
      return serveroplistentry != null ? serveroplistentry.getBypassesPlayerLimit() : false;
   }

   protected String getKeyForUser(GameProfile p_11354_) {
      return p_11354_.getId().toString();
   }
}