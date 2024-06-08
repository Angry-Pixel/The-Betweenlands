package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetActionBarTextPacket implements Packet<ClientGamePacketListener> {
   private final Component text;

   public ClientboundSetActionBarTextPacket(Component p_179203_) {
      this.text = p_179203_;
   }

   public ClientboundSetActionBarTextPacket(FriendlyByteBuf p_179201_) {
      this.text = p_179201_.readComponent();
   }

   public void write(FriendlyByteBuf p_179205_) {
      p_179205_.writeComponent(this.text);
   }

   public void handle(ClientGamePacketListener p_179209_) {
      p_179209_.setActionBarText(this);
   }

   public Component getText() {
      return this.text;
   }
}