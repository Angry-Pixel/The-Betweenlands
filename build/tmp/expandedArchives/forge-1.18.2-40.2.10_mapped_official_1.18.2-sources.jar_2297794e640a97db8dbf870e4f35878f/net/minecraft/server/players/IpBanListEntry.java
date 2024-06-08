package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class IpBanListEntry extends BanListEntry<String> {
   public IpBanListEntry(String p_11050_) {
      this(p_11050_, (Date)null, (String)null, (Date)null, (String)null);
   }

   public IpBanListEntry(String p_11052_, @Nullable Date p_11053_, @Nullable String p_11054_, @Nullable Date p_11055_, @Nullable String p_11056_) {
      super(p_11052_, p_11053_, p_11054_, p_11055_, p_11056_);
   }

   public Component getDisplayName() {
      return new TextComponent(String.valueOf(this.getUser()));
   }

   public IpBanListEntry(JsonObject p_11048_) {
      super(createIpInfo(p_11048_), p_11048_);
   }

   private static String createIpInfo(JsonObject p_11060_) {
      return p_11060_.has("ip") ? p_11060_.get("ip").getAsString() : null;
   }

   protected void serialize(JsonObject p_11058_) {
      if (this.getUser() != null) {
         p_11058_.addProperty("ip", this.getUser());
         super.serialize(p_11058_);
      }
   }
}