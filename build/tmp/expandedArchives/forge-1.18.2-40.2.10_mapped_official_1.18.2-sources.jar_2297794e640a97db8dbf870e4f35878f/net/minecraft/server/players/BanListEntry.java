package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public abstract class BanListEntry<T> extends StoredUserEntry<T> {
   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   public static final String EXPIRES_NEVER = "forever";
   protected final Date created;
   protected final String source;
   @Nullable
   protected final Date expires;
   protected final String reason;

   public BanListEntry(T p_10953_, @Nullable Date p_10954_, @Nullable String p_10955_, @Nullable Date p_10956_, @Nullable String p_10957_) {
      super(p_10953_);
      this.created = p_10954_ == null ? new Date() : p_10954_;
      this.source = p_10955_ == null ? "(Unknown)" : p_10955_;
      this.expires = p_10956_;
      this.reason = p_10957_ == null ? "Banned by an operator." : p_10957_;
   }

   protected BanListEntry(T p_10950_, JsonObject p_10951_) {
      super(p_10950_);

      Date date;
      try {
         date = p_10951_.has("created") ? DATE_FORMAT.parse(p_10951_.get("created").getAsString()) : new Date();
      } catch (ParseException parseexception1) {
         date = new Date();
      }

      this.created = date;
      this.source = p_10951_.has("source") ? p_10951_.get("source").getAsString() : "(Unknown)";

      Date date1;
      try {
         date1 = p_10951_.has("expires") ? DATE_FORMAT.parse(p_10951_.get("expires").getAsString()) : null;
      } catch (ParseException parseexception) {
         date1 = null;
      }

      this.expires = date1;
      this.reason = p_10951_.has("reason") ? p_10951_.get("reason").getAsString() : "Banned by an operator.";
   }

   public Date getCreated() {
      return this.created;
   }

   public String getSource() {
      return this.source;
   }

   @Nullable
   public Date getExpires() {
      return this.expires;
   }

   public String getReason() {
      return this.reason;
   }

   public abstract Component getDisplayName();

   boolean hasExpired() {
      return this.expires == null ? false : this.expires.before(new Date());
   }

   protected void serialize(JsonObject p_10959_) {
      p_10959_.addProperty("created", DATE_FORMAT.format(this.created));
      p_10959_.addProperty("source", this.source);
      p_10959_.addProperty("expires", this.expires == null ? "forever" : DATE_FORMAT.format(this.expires));
      p_10959_.addProperty("reason", this.reason);
   }
}