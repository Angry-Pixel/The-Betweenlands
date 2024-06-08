package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;
import javax.annotation.Nullable;

public class IpBanList extends StoredUserList<String, IpBanListEntry> {
   public IpBanList(File p_11036_) {
      super(p_11036_);
   }

   protected StoredUserEntry<String> createEntry(JsonObject p_11038_) {
      return new IpBanListEntry(p_11038_);
   }

   public boolean isBanned(SocketAddress p_11042_) {
      String s = this.getIpFromAddress(p_11042_);
      return this.contains(s);
   }

   public boolean isBanned(String p_11040_) {
      return this.contains(p_11040_);
   }

   @Nullable
   public IpBanListEntry get(SocketAddress p_11044_) {
      String s = this.getIpFromAddress(p_11044_);
      return this.get(s);
   }

   private String getIpFromAddress(SocketAddress p_11046_) {
      String s = p_11046_.toString();
      if (s.contains("/")) {
         s = s.substring(s.indexOf(47) + 1);
      }

      if (s.contains(":")) {
         s = s.substring(0, s.indexOf(58));
      }

      return s;
   }
}