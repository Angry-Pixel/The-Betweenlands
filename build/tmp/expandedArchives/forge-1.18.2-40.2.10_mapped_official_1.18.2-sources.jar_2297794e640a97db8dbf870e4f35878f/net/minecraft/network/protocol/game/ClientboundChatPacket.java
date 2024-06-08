package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundChatPacket implements Packet<ClientGamePacketListener> {
   private final Component message;
   private final ChatType type;
   private final UUID sender;

   public ClientboundChatPacket(Component p_131826_, ChatType p_131827_, UUID p_131828_) {
      this.message = p_131826_;
      this.type = p_131827_;
      this.sender = p_131828_;
   }

   public ClientboundChatPacket(FriendlyByteBuf p_178776_) {
      this.message = p_178776_.readComponent();
      this.type = ChatType.getForIndex(p_178776_.readByte());
      this.sender = p_178776_.readUUID();
   }

   public void write(FriendlyByteBuf p_131838_) {
      p_131838_.writeComponent(this.message);
      p_131838_.writeByte(this.type.getIndex());
      p_131838_.writeUUID(this.sender);
   }

   public void handle(ClientGamePacketListener p_131835_) {
      p_131835_.handleChat(this);
   }

   public Component getMessage() {
      return this.message;
   }

   public ChatType getType() {
      return this.type;
   }

   public UUID getSender() {
      return this.sender;
   }

   public boolean isSkippable() {
      return true;
   }
}