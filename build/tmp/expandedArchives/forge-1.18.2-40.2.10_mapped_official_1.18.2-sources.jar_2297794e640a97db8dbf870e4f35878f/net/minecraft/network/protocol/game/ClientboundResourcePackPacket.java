package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundResourcePackPacket implements Packet<ClientGamePacketListener> {
   public static final int MAX_HASH_LENGTH = 40;
   private final String url;
   private final String hash;
   private final boolean required;
   @Nullable
   private final Component prompt;

   public ClientboundResourcePackPacket(String p_179182_, String p_179183_, boolean p_179184_, @Nullable Component p_179185_) {
      if (p_179183_.length() > 40) {
         throw new IllegalArgumentException("Hash is too long (max 40, was " + p_179183_.length() + ")");
      } else {
         this.url = p_179182_;
         this.hash = p_179183_;
         this.required = p_179184_;
         this.prompt = p_179185_;
      }
   }

   public ClientboundResourcePackPacket(FriendlyByteBuf p_179187_) {
      this.url = p_179187_.readUtf();
      this.hash = p_179187_.readUtf(40);
      this.required = p_179187_.readBoolean();
      if (p_179187_.readBoolean()) {
         this.prompt = p_179187_.readComponent();
      } else {
         this.prompt = null;
      }

   }

   public void write(FriendlyByteBuf p_132926_) {
      p_132926_.writeUtf(this.url);
      p_132926_.writeUtf(this.hash);
      p_132926_.writeBoolean(this.required);
      if (this.prompt != null) {
         p_132926_.writeBoolean(true);
         p_132926_.writeComponent(this.prompt);
      } else {
         p_132926_.writeBoolean(false);
      }

   }

   public void handle(ClientGamePacketListener p_132923_) {
      p_132923_.handleResourcePack(this);
   }

   public String getUrl() {
      return this.url;
   }

   public String getHash() {
      return this.hash;
   }

   public boolean isRequired() {
      return this.required;
   }

   @Nullable
   public Component getPrompt() {
      return this.prompt;
   }
}