package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundDisconnectPacket implements Packet<ClientGamePacketListener> {
   private final Component reason;

   public ClientboundDisconnectPacket(Component p_132078_) {
      this.reason = p_132078_;
   }

   public ClientboundDisconnectPacket(FriendlyByteBuf p_178841_) {
      this.reason = p_178841_.readComponent();
   }

   public void write(FriendlyByteBuf p_132087_) {
      p_132087_.writeComponent(this.reason);
   }

   public void handle(ClientGamePacketListener p_132084_) {
      p_132084_.handleDisconnect(this);
   }

   public Component getReason() {
      return this.reason;
   }
}