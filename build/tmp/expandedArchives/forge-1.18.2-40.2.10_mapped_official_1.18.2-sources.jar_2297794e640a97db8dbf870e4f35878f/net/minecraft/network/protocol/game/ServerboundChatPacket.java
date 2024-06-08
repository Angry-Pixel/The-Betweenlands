package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundChatPacket implements Packet<ServerGamePacketListener> {
   private static final int MAX_MESSAGE_LENGTH = 256;
   private final String message;

   public ServerboundChatPacket(String p_133830_) {
      if (p_133830_.length() > 256) {
         p_133830_ = p_133830_.substring(0, 256);
      }

      this.message = p_133830_;
   }

   public ServerboundChatPacket(FriendlyByteBuf p_179545_) {
      this.message = p_179545_.readUtf(256);
   }

   public void write(FriendlyByteBuf p_133839_) {
      p_133839_.writeUtf(this.message);
   }

   public void handle(ServerGamePacketListener p_133836_) {
      p_133836_.handleChat(this);
   }

   public String getMessage() {
      return this.message;
   }
}